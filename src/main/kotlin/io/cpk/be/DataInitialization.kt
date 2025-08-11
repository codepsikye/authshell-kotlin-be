package io.cpk.be

import io.cpk.be.basic.entity.AccessRight
import io.cpk.be.basic.entity.AppUser
import io.cpk.be.basic.entity.AppUserRole
import io.cpk.be.basic.entity.Center
import io.cpk.be.basic.entity.Org
import io.cpk.be.basic.entity.OrgType
import io.cpk.be.basic.entity.Role
import io.cpk.be.basic.repository.AccessRightRepository
import io.cpk.be.basic.repository.AppUserRepository
import io.cpk.be.basic.repository.AppUserRoleRepository
import io.cpk.be.basic.repository.CenterRepository
import io.cpk.be.basic.repository.OrgRepository
import io.cpk.be.basic.repository.OrgTypeRepository
import io.cpk.be.basic.repository.RoleRepository
import jakarta.annotation.PostConstruct
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.util.*
import java.util.regex.Pattern

@Service
@Transactional
class DataInitialization(
    private val accessRightRepository: AccessRightRepository,
    private val orgTypeRepository: OrgTypeRepository,
    private val orgRepository: OrgRepository,
    private val centerRepository: CenterRepository,
    private val roleRepository: RoleRepository,
    private val appUserRepository: AppUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val appUserRoleRepository: AppUserRoleRepository
) {
    /**
     * Startup bean method that scans all classes for @PreAuthorize annotations,
     * extracts access right strings, and creates them in the database if they don't exist.
     * Using PostConstruct to initialize access rights at application startup.
     */
    @PostConstruct
    fun initialize() {
        initializeAccessRights()
        initializeAdminOrgAndUser()
    }
    fun initializeAdminOrgAndUser() {
        if(orgRepository.count() > 0L) {
            return
        }
        val adminOrgType = orgTypeRepository.save(OrgType(name = "Admin", accessRight = emptyList(), orgConfigs = emptyMap()))
        val adminOrg = orgRepository.save(Org(name = "Admin", orgTypeName = "Admin", orgConfigs = emptyMap(), orgType = adminOrgType))
        val center = centerRepository.save(Center(name = "Admin", orgId = adminOrg.id, org = adminOrg))
        val allAccessRight = accessRightRepository.findAll()
        // Convert AccessRight objects to their names (strings)
        val accessRightNames = allAccessRight.map { it.name }
        val orgAdminRole = roleRepository.save(Role(orgId = adminOrg.id ?: 0, name = "OrgAdmin", accessRight = accessRightNames))
        val password = passwordEncoder.encode("admin123")
        val orgAdminUser = appUserRepository.save(
            AppUser(
                id = 1,
                org = adminOrg,
                username = "admin",
                fullname = "Administrator",
                email = "admin@cpk.co.zw",
                password = password,
                orgAdmin = true
            )
        )
        // Create AppUserRole with the correct ID fields and relationships
        val appUserRole = AppUserRole(
            userId = orgAdminUser.id,
            orgId = adminOrg.id ?: 0,
            centerId = center.id ?: 0,
            roleName = orgAdminRole.name,
            user = orgAdminUser,
            org = adminOrg,
            center = center,
            role = orgAdminRole
        )
        val savedRole = appUserRoleRepository.save(appUserRole)
    }
    fun initializeAccessRights() {
        val accessRights = mutableSetOf<String>()
        
        // Pattern to extract authority names from @PreAuthorize expressions
        val pattern = Pattern.compile("hasAuthority\\('([^']+)'\\)|hasAnyAuthority\\('([^']+)'\\)|hasAnyAuthority\\(\"([^\"]+)\"\\)|hasAuthority\\(\"([^\"]+)\"\\)")
        
        try {
            // Find all controller classes
            val controllerClasses = findControllerClasses()
            
            // Process each controller class
            controllerClasses.forEach { clazz ->
                // Check all methods in the class for @PreAuthorize annotations
                clazz.declaredMethods.forEach { method ->
                    method.getAnnotation(PreAuthorize::class.java)?.let { annotation ->
                        val value = annotation.value
                        
                        // Extract authority names using regex
                        val matcher = pattern.matcher(value)
                        if (matcher.find()) {
                            matcher.reset()
                            while (matcher.find()) {
                                // Try to get authority from any of the capture groups
                                val authority = matcher.group(1) ?: matcher.group(2) ?: matcher.group(3) ?: matcher.group(4)
                                if (!authority.isNullOrBlank()) {
                                    accessRights.add(authority)
                                }
                            }
                        }
                    }
                }
            }
            
            // Create access rights in the database if they don't exist
            accessRights.forEach { rightName ->
                if (accessRightRepository.findById(rightName).isEmpty) {
                    val accessRight = AccessRight(name = rightName)
                    accessRightRepository.save(accessRight)
                }
            }
        } catch (e: Exception) {
            // Exception handling without logging
        }
    }
    
    /**
     * Finds all controller classes in the application using reflection
     */
    private fun findControllerClasses(): List<Class<*>> {
        val controllerClasses = mutableListOf<Class<*>>()
        
        try {
            // Get the ClassLoader
            val classLoader = Thread.currentThread().contextClassLoader
            
            // Get all classes in the io.cpk.be package
            val packageName = "io.cpk.be"
            val packagePath = packageName.replace('.', '/')
            
            // Get all resources in the package
            val resources = classLoader.getResources(packagePath)
            
            // Process each resource
            while (resources.hasMoreElements()) {
                val resource = resources.nextElement()
                
                // If the resource is a file (directory)
                if (resource.protocol == "file") {
                    val directory = File(resource.file)
                    if (directory.exists()) {
                        // Find all class files in the directory and its subdirectories
                        findClassesInDirectory(directory, packageName, controllerClasses)
                    }
                }
            }
        } catch (e: Exception) {
            // Exception handling without logging
        }
        
        return controllerClasses
    }
    
    /**
     * Recursively finds all classes in a directory and its subdirectories
     */
    private fun findClassesInDirectory(directory: File, packageName: String, classes: MutableList<Class<*>>) {
        // Get all files in the directory
        val files = directory.listFiles() ?: return
        
        for (file in files) {
            // If the file is a directory, recursively process it
            if (file.isDirectory) {
                findClassesInDirectory(file, "$packageName.${file.name}", classes)
            } else if (file.name.endsWith(".class")) {
                // If the file is a class file, try to load it
                try {
                    val className = "$packageName.${file.name.substring(0, file.name.length - 6)}"
                    val clazz = Class.forName(className)
                    
                    // Only add controller classes
                    if (clazz.isAnnotationPresent(RestController::class.java) || 
                        clazz.isAnnotationPresent(Controller::class.java) ||
                        clazz.name.contains("Controller")) {
                        classes.add(clazz)
                    }
                } catch (e: Exception) {
                    // Exception handling without logging
                }
            }
        }
    }
}