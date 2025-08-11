package io.cpk.be.basic.service

import io.cpk.be.basic.dto.AppUserDto
import io.cpk.be.basic.entity.AppUser
import io.cpk.be.basic.mapper.AppUserMapper
import io.cpk.be.basic.repository.AppUserRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Unit tests for AppUserService
 */
class AppUserServiceTest {

    private val appUserRepository = mockk<AppUserRepository>()
    private val appUserMapper = mockk<AppUserMapper>()
    private val passwordEncoder = mockk<PasswordEncoder>()

    private lateinit var appUserService: AppUserService
    private lateinit var appUser: AppUser
    private lateinit var appUserDto: AppUserDto

    @BeforeEach
    fun setUp() {
        appUserService = AppUserService(appUserRepository, appUserMapper, passwordEncoder)

        appUser = AppUser.create(
            id = 123,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            orgAdmin = false,
            password = "encoded_password"
        )

        appUserDto = AppUserDto(
            id = 123,
            username = "testuser",
            fullname = "Test User",
            email = "test@example.com",
            orgId = 1,
            orgAdmin = false,
            password = "password123"
        )
    }

    @Test
    fun `should create app user successfully`() {
        // Given
        val createDto = appUserDto.copy(id = 0)
        val entityToSave = appUser.copy(id = 0, password = null)
        val savedEntity = appUser

        every { appUserMapper.toEntity(createDto) } returns entityToSave
        every { passwordEncoder.encode("password123") } returns "encoded_password"
        every { appUserRepository.save(any()) } returns savedEntity
        every { appUserMapper.toDto(savedEntity) } returns appUserDto

        // When
        val result = appUserService.create(createDto)

        // Then
        assertEquals(appUserDto, result)
        verify { appUserMapper.toEntity(createDto) }
        verify { passwordEncoder.encode("password123") }
        verify { appUserRepository.save(any()) }
        verify { appUserMapper.toDto(savedEntity) }
    }

    @Test
    fun `should find all app users`() {
        // Given
        val appUsers = listOf(appUser)
        val appUserDtos = listOf(appUserDto)

        every { appUserRepository.findAll() } returns appUsers
        every { appUserMapper.toDto(appUser) } returns appUserDto

        // When
        val result = appUserService.findAll()

        // Then
        assertEquals(appUserDtos, result)
        verify { appUserRepository.findAll() }
        verify { appUserMapper.toDto(appUser) }
    }

    @Test
    fun `should find all app users by org id with pagination`() {
        // Given
        val orgId = 1
        val pageable = PageRequest.of(0, 10)
        val appUsers = listOf(appUser)
        val appUserDtos = listOf(appUserDto)
        val page = PageImpl(appUsers, pageable, appUsers.size.toLong())

        every { appUserRepository.findAllByOrgId(orgId, pageable) } returns page
        every { appUserMapper.toDto(appUser) } returns appUserDto

        // When
        val result = appUserService.findAll(orgId, pageable)

        // Then
        assertEquals(appUserDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { appUserRepository.findAllByOrgId(orgId, pageable) }
        verify { appUserMapper.toDto(appUser) }
    }

    @Test
    fun `should find all app users by org id and fullname with pagination`() {
        // Given
        val orgId = 1
        val fullname = "Test"
        val pageable = PageRequest.of(0, 10)
        val appUsers = listOf(appUser)
        val appUserDtos = listOf(appUserDto)
        val page = PageImpl(appUsers, pageable, appUsers.size.toLong())

        every { appUserRepository.findAllByOrgIdAndFullnameContaining(orgId, fullname, pageable) } returns page
        every { appUserMapper.toDto(appUser) } returns appUserDto

        // When
        val result = appUserService.findAll(orgId, fullname, pageable)

        // Then
        assertEquals(appUserDtos, result.content)
        assertEquals(1, result.totalElements)
        verify { appUserRepository.findAllByOrgIdAndFullnameContaining(orgId, fullname, pageable) }
        verify { appUserMapper.toDto(appUser) }
    }

    @Test
    fun `should find app user by id successfully`() {
        // Given
        every { appUserRepository.findById(123) } returns Optional.of(appUser)
        every { appUserMapper.toDto(appUser) } returns appUserDto

        // When
        val result = appUserService.findById(123)

        // Then
        assertEquals(appUserDto, result)
        verify { appUserRepository.findById(123) }
        verify { appUserMapper.toDto(appUser) }
    }

    @Test
    fun `should return null when app user not found by id`() {
        // Given
        every { appUserRepository.findById(999) } returns Optional.empty()

        // When
        val result = appUserService.findById(999)

        // Then
        assertNull(result)
        verify { appUserRepository.findById(999) }
        verify(exactly = 0) { appUserMapper.toDto(any()) }
    }

    @Test
    fun `should update app user successfully`() {
        // Given
        val updateDto = appUserDto.copy(fullname = "Updated User", password = "new_password")
        val existingEntity = appUser
        val entityToUpdate = appUser.copy(fullname = "Updated User", password = null)
        val updatedEntity = appUser.copy(fullname = "Updated User", password = "encoded_new_password")
        val resultDto = appUserDto.copy(fullname = "Updated User")

        every { appUserRepository.findById(123) } returns Optional.of(existingEntity)
        every { appUserMapper.toEntity(updateDto) } returns entityToUpdate
        every { passwordEncoder.encode("new_password") } returns "encoded_new_password"
        every { appUserRepository.save(any()) } returns updatedEntity
        every { appUserMapper.toDto(updatedEntity) } returns resultDto

        // When
        val result = appUserService.update(123, updateDto)

        // Then
        assertEquals(resultDto, result)
        verify { appUserRepository.findById(123) }
        verify { appUserMapper.toEntity(updateDto) }
        verify { passwordEncoder.encode("new_password") }
        verify { appUserRepository.save(any()) }
        verify { appUserMapper.toDto(updatedEntity) }
    }

    @Test
    fun `should throw exception when updating non-existent app user`() {
        // Given
        every { appUserRepository.findById(999) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<RuntimeException> {
            appUserService.update(999, appUserDto)
        }
        assertEquals("AppUser not found", exception.message)
        verify { appUserRepository.findById(999) }
        verify(exactly = 0) { appUserRepository.save(any()) }
    }

    @Test
    fun `should delete app user successfully`() {
        // Given
        every { appUserRepository.deleteById(123) } just runs

        // When
        appUserService.delete(123)

        // Then
        verify { appUserRepository.deleteById(123) }
    }

    @Test
    fun `should update app user with null password successfully`() {
        // Given
        val updateDto = appUserDto.copy(fullname = "Updated User", password = null)
        val existingEntity = appUser
        val entityToUpdate = appUser.copy(fullname = "Updated User", password = null)
        val updatedEntity = appUser.copy(fullname = "Updated User", password = "encoded_password") // Keeps existing password
        val resultDto = appUserDto.copy(fullname = "Updated User", password = null)

        every { appUserRepository.findById(123) } returns Optional.of(existingEntity)
        every { appUserMapper.toEntity(updateDto) } returns entityToUpdate
        every { appUserRepository.save(any()) } returns updatedEntity
        every { appUserMapper.toDto(updatedEntity) } returns resultDto

        // When
        val result = appUserService.update(123, updateDto)

        // Then
        assertEquals(resultDto, result)
        verify { appUserRepository.findById(123) }
        verify { appUserMapper.toEntity(updateDto) }
        verify(exactly = 0) { passwordEncoder.encode(any()) } // Password encoder should not be called
        verify { appUserRepository.save(any()) }
        verify { appUserMapper.toDto(updatedEntity) }
    }
}