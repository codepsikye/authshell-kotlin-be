package io.cpk.be

import io.cpk.be.basic.entity.AccessRight
import io.cpk.be.basic.repository.AccessRightRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController
import java.util.*

class DataInitializationTest {
    private lateinit var accessRightRepository: AccessRightRepository
    private lateinit var dataInitialization: DataInitialization

    private lateinit var orgTypeRepository: io.cpk.be.basic.repository.OrgTypeRepository
    private lateinit var orgRepository: io.cpk.be.basic.repository.OrgRepository
    private lateinit var centerRepository: io.cpk.be.basic.repository.CenterRepository
    private lateinit var roleRepository: io.cpk.be.basic.repository.RoleRepository
    private lateinit var appUserRepository: io.cpk.be.basic.repository.AppUserRepository
    private lateinit var passwordEncoder: org.springframework.security.crypto.password.PasswordEncoder
    private lateinit var appUserRoleRepository: io.cpk.be.basic.repository.AppUserRoleRepository

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
    fun `should initialize access rights from PreAuthorize annotations`() {
        // Given
        // Mock repository behavior for existing and non-existing rights
        every { accessRightRepository.findById("test_right_1") } returns Optional.empty()
        every { accessRightRepository.findById("test_right_2") } returns Optional.empty()
        every { accessRightRepository.findById("test_right_3") } returns Optional.empty()
        every { accessRightRepository.findById("test_right_4") } returns Optional.empty()
        every { accessRightRepository.save(any<AccessRight>()) } returnsArgument 0
        
        // Mock the findControllerClasses method to return our test controller
        val spyInitialization = spyk(dataInitialization)
        
        // Create a test controller class with annotations
        @RestController
        class TestController {
            @PreAuthorize("hasAuthority('test_right_1')")
            fun method1() {}
            
            @PreAuthorize("hasAuthority('test_right_2') or hasAnyAuthority('test_right_3')")
            fun method2() {}
            
            @PreAuthorize("hasAuthority('test_right_4') and #id == authentication.principal.orgId")
            fun method3() {}
        }
        
        // Mock the findControllerClasses method to return our test controller
        every { spyInitialization["findControllerClasses"]() } returns listOf(TestController::class.java)
        
        // When
        spyInitialization.initializeAccessRights()
        
        // Then
        verify { accessRightRepository.findById("test_right_1") }
        verify { accessRightRepository.findById("test_right_2") }
        verify { accessRightRepository.findById("test_right_3") }
        verify { accessRightRepository.findById("test_right_4") }
        verify { accessRightRepository.save(match { it.name == "test_right_1" }) }
        verify { accessRightRepository.save(match { it.name == "test_right_2" }) }
        verify { accessRightRepository.save(match { it.name == "test_right_3" }) }
        verify { accessRightRepository.save(match { it.name == "test_right_4" }) }
    }
}