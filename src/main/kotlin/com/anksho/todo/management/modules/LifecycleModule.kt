package com.anksho.todo.management.modules

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
import org.slf4j.LoggerFactory

object LifecycleModule {
    private val logger = LoggerFactory.getLogger(LifecycleModule::class.java)

    fun Application.configureLifecycle() {
        monitor.subscribe(ApplicationStarted) {
            logger.info("TODO Management Server STARTED.")
        }
        monitor.subscribe(ApplicationStopped) {
            logger.info("TODO Management Server is SHUTTING DOWN...")
            // Release resources and unsubscribe from events
            monitor.unsubscribe(ApplicationStarted) {}
            monitor.unsubscribe(ApplicationStopped) {}
            logger.info("TODO Management Server is STOPPED.")
        }
    }
}