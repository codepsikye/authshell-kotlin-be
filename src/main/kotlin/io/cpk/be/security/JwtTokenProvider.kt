package io.cpk.be.security

import io.cpk.be.basic.repository.AppUserRepository
import io.cpk.be.basic.repository.AppUserRoleRepository
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Service
class JwtTokenProvider(
    private val appUserRepository: AppUserRepository,
    private val appUserRoleRepository: AppUserRoleRepository
) {

    @Value("\${jwt.secret:mySecretKey123456789012345678901234567890}")
    private lateinit var jwtSecret: String

    @Value("\${jwt.expiration:86400000}") // 24 hours in milliseconds
    private var jwtExpirationInMs: Long = 86400000

    @Value("\${jwt.refresh-expiration:604800000}") // 7 days in milliseconds
    private var jwtRefreshExpirationInMs: Long = 604800000

    private fun getSigningKey(): Key {
        val keyBytes = jwtSecret.toByteArray()
        return SecretKeySpec(keyBytes, SignatureAlgorithm.HS512.jcaName)
    }

    fun generateToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as CustomUserDetails
        return generateTokenFromUsername(userPrincipal.username, userPrincipal.orgId, userPrincipal.centerId)
    }

    fun generateTokenFromUsername(username: String): String {
        // Get the user's orgId from the repository
        val appUser = appUserRepository.findByUsername(username).orElseThrow {
            RuntimeException("User not found with username: $username")
        }

        // Check if user has a unique centerId
        val centerId = if (appUserRoleRepository.hasUniqueCenterId(appUser.id)) {
            appUserRoleRepository.getUniqueCenterId(appUser.id)
        } else {
            null
        }

        return generateTokenFromUsername(username, appUser.orgId, centerId)
    }

    fun generateTokenFromUsername(username: String, orgId: Int): String {
        // Get the user to get their ID for repository calls
        val appUser = appUserRepository.findByUsername(username).orElseThrow {
            RuntimeException("User not found with username: $username")
        }
        
        // Check if user has a unique centerId
        val centerId = if (appUserRoleRepository.hasUniqueCenterId(appUser.id)) {
            appUserRoleRepository.getUniqueCenterId(appUser.id)
        } else {
            null
        }

        return generateTokenFromUsername(username, orgId, centerId)
    }

    fun generateTokenFromUsername(username: String, orgId: Int, centerId: Int?): String {
        val expiryDate = Date(Date().time + jwtExpirationInMs)

        return Jwts.builder()
            .subject(username)
            .claim("orgId", orgId)
            .claim("centerId", centerId)
            .issuedAt(Date())
            .expiration(expiryDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact()
    }

    fun generateRefreshToken(username: String): String {
        // Get the user's orgId from the repository
        val appUser = appUserRepository.findByUsername(username).orElseThrow {
            RuntimeException("User not found with username: $username")
        }

        // Check if user has a unique centerId
        val centerId = if (appUserRoleRepository.hasUniqueCenterId(appUser.id)) {
            appUserRoleRepository.getUniqueCenterId(appUser.id)
        } else {
            null
        }

        return generateRefreshToken(username, appUser.orgId, centerId)
    }

    fun generateRefreshToken(username: String, orgId: Int): String {
        // Get the user to get their ID for repository calls
        val appUser = appUserRepository.findByUsername(username).orElseThrow {
            RuntimeException("User not found with username: $username")
        }
        
        // Check if user has a unique centerId
        val centerId = if (appUserRoleRepository.hasUniqueCenterId(appUser.id)) {
            appUserRoleRepository.getUniqueCenterId(appUser.id)
        } else {
            null
        }

        return generateRefreshToken(username, orgId, centerId)
    }

    fun generateRefreshToken(username: String, orgId: Int, centerId: Int?): String {
        val expiryDate = Date(Date().time + jwtRefreshExpirationInMs)

        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .claim("type", "refresh")
            .claim("orgId", orgId)
            .claim("centerId", centerId)
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact()
    }

    fun getUsernameFromToken(token: String): String {
        val claims = Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body

        return claims.subject
    }

    fun getOrgIdFromToken(token: String): Int {
        val claims = Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body

        return claims.get("orgId", Integer::class.java).toInt()
    }

    fun getCenterIdFromToken(token: String): Int? {
        val claims = Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body

        return claims.get("centerId", Integer::class.java)?.toInt()
    }

    fun validateToken(authToken: String): Boolean {
        try {
            Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken)
            return true
        } catch (ex: SecurityException) {
            println("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            println("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            println("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            println("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            println("JWT claims string is empty")
        }
        return false
    }

    fun isRefreshToken(token: String): Boolean {
        try {
            val claims =
                Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body

            return "refresh" == claims["type"]
        } catch (ex: Exception) {
            return false
        }
    }

    fun getExpirationDateFromToken(token: String): Date? {
        try {
            val claims =
                Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token).body

            return claims.expiration
        } catch (ex: Exception) {
            return null
        }
    }
}
