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
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class AdminOrgInitializationTest {
    private lateinit var accessRightRepository: AccessRightRepository
    private lateinit var orgTypeRepository: OrgTypeRepository
    private lateinit var orgRepository: OrgRepository
    private lateinit var centerRepository: CenterRepository
    private lateinit var roleRepository: RoleRepository
    private lateinit var appUserRepository: AppUserRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var appUserRoleRepository: AppUserRoleRepository
    private lateinit var dataInitialization: DataInitialization

    @BeforeEach
    fun setUp() {
        accessRightRepository = mockk()
        orgTypeRepository = mockk()
        orgRepository = mockk()
        centerRepository = mockk()
        roleRepository = mockk()
        appUserRepository = mockk()
        passwordEncoder = mockk()
        appUserRoleRepository = mockk()
        
        dataInitialization = DataInitialization(
            accessRightRepository,
            orgTypeRepository,
            orgRepository,
            centerRepository,
            roleRepository,
            appUserRepository,
            passwordEncoder,
            appUserRoleRepository
        )
    }

    @Test
    fun `should initialize admin org and user with non-null orgId in Center`() {
        // Given
        val orgTypeSlot = slot<OrgType>()
        val orgSlot = slot<Org>()
        // We will verify Center orgId via argument matcher instead of slot
        // val centerSlot = slot<Center>()
        val roleSlot = slot<Role>()
        val userSlot = slot<AppUser>()
        val userRoleSlot = slot<AppUserRole>()
        
        val adminOrgType = OrgType(name = "Admin", accessRight = emptyList(), orgConfigs = emptyMap())
        val adminOrg = Org(id = 1, name = "Admin", orgTypeName = "Admin", orgConfigs = emptyMap(), orgType = adminOrgType)
        val adminCenter = Center(id = 1, name = "Admin", orgId = 1, org = adminOrg)
        val accessRights = listOf("right1", "right2")
        val adminRole = Role(orgId = 1, name = "OrgAdmin", accessRight = accessRights)
        
        every { orgTypeRepository.save(capture(orgTypeSlot)) } returns adminOrgType
        every { orgRepository.save(capture(orgSlot)) } returns adminOrg
        every { orgRepository.count() } returns 0
        every { centerRepository.save(any()) } returns adminCenter
        every { accessRightRepository.findAll() } returns accessRights.map { AccessRight(name = it) }
        every { roleRepository.save(capture(roleSlot)) } returns adminRole
        every { passwordEncoder.encode(any()) } returns "encoded_password"
        every { appUserRepository.save(capture(userSlot)) } returns AppUser(
            id = 1,
            org = adminOrg,
            username = "admin",
            fullname = "Administrator",
            email = "admin@cpk.co.zw",
            password = "encoded_password",
            orgAdmin = true
        )
        every { appUserRoleRepository.save(capture(userRoleSlot)) } returns AppUserRole(
            userId = 1,
            orgId = 1,
            centerId = 1,
            roleName = "OrgAdmin"
        )

        // When
        println("[DEBUG_LOG] Starting initializeAdminOrgAndUser test")
        dataInitialization.initializeAdminOrgAndUser()
        println("[DEBUG_LOG] After initializeAdminOrgAndUser call")

        // Then
        // Verify that Center was saved with non-null orgId via argument inspection
        verify {
            centerRepository.save(withArg { centerArg ->
                assertEquals(adminOrg.id, centerArg.orgId)
                assertNotNull(centerArg.orgId)
            })
        }
        
        // Verify all the expected repository calls
        verify { orgTypeRepository.save(any()) }
        verify { orgRepository.save(any()) }
        verify { accessRightRepository.findAll() }
        verify { roleRepository.save(any()) }
        verify { passwordEncoder.encode(any()) }
        verify { appUserRepository.save(any()) }
        verify { appUserRoleRepository.save(any()) }
    }
}