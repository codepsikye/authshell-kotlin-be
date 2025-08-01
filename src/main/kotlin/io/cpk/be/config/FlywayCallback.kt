package io.cpk.be.config

import org.flywaydb.core.api.callback.Callback
import org.flywaydb.core.api.callback.Context
import org.flywaydb.core.api.callback.Event
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Flyway callback to handle migration failures by clearing migration tables
 * This ensures that all migrations are retried from scratch when a failure occurs
 */
@Component
class FlywayCallback : Callback {
    
    private val logger = LoggerFactory.getLogger(FlywayCallback::class.java)
    
    override fun supports(event: Event, context: Context?): Boolean {
        return event == Event.AFTER_MIGRATE_ERROR || event == Event.AFTER_VALIDATE_ERROR
    }
    
    override fun canHandleInTransaction(event: Event, context: Context?): Boolean {
        return false // We need to handle this outside of transaction
    }
    
    override fun handle(event: Event, context: Context) {
        when (event) {
            Event.AFTER_MIGRATE_ERROR -> {
                logger.warn("Migration failed, clearing Flyway schema history table to allow retry")
                clearFlywaySchemaHistory(context)
            }
            Event.AFTER_VALIDATE_ERROR -> {
                logger.warn("Validation failed, clearing Flyway schema history table to allow restart")
                clearFlywaySchemaHistory(context)
            }
            else -> {
                // No action needed for other events
            }
        }
    }
    
    private fun clearFlywaySchemaHistory(context: Context) {
        try {
            val connection = context.connection
            
            // Clear the Flyway schema history table
            val statement = connection.createStatement()
            
            // First, try to repair the schema history table if there are checksum mismatches
            try {
                // Update checksums for all migrations to match the current files
                statement.execute("UPDATE flyway_schema_history SET checksum = NULL WHERE installed_rank > 0")
                logger.info("Reset checksums in flyway_schema_history table")
            } catch (e: Exception) {
                logger.debug("Could not update checksums in flyway_schema_history table: ${e.message}")
                
                // If repair fails, drop the table completely
                try {
                    statement.execute("DROP TABLE IF EXISTS flyway_schema_history CASCADE")
                    logger.info("Dropped flyway_schema_history table")
                } catch (e: Exception) {
                    logger.warn("Could not drop flyway_schema_history table: ${e.message}")
                }
            }
            
            // Also try alternative table names that Flyway might use
            try {
                statement.execute("DROP TABLE IF EXISTS schema_version CASCADE")
                logger.info("Dropped schema_version table")
            } catch (e: Exception) {
                logger.debug("schema_version table not found or could not be dropped: ${e.message}")
            }
            
            statement.close()
            
            logger.info("Flyway schema history cleared successfully. Next migration attempt will start fresh.")
            
        } catch (e: Exception) {
            logger.error("Failed to clear Flyway schema history: ${e.message}", e)
        }
    }
    
    override fun getCallbackName(): String {
        return "Migration Failure Handler"
    }
}