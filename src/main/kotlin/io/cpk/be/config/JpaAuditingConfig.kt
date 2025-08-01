package io.cpk.be.config

import io.cpk.be.security.CustomUserDetails
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder
import java.util.*

/**
 * Configuration class for JPA auditing.
 * Enables JPA auditing and provides an implementation of AuditorAware to get the current username.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
class JpaAuditingConfig {

    /**
     * Provides the current username for auditing purposes.
     * If the user is authenticated, returns the username from the security context.
     * Otherwise, returns "system" as the default value.
     */
    @Bean
    fun auditorProvider(): AuditorAware<String> {
        return AuditorAware {
            val authentication = SecurityContextHolder.getContext().authentication
            
            if (authentication != null && authentication.isAuthenticated) {
                when (val principal = authentication.principal) {
                    is CustomUserDetails -> Optional.of(principal.username)
                    is String -> Optional.of(principal)
                    else -> Optional.of("system")
                }
            } else {
                Optional.of("system")
            }
        }
    }
}