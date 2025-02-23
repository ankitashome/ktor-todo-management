package com.anksho.todo.management

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.extensions.junitxml.JunitXmlReporter

class KotestConfig : AbstractProjectConfig() {
    override fun extensions(): List<Extension> = listOf(
        JunitXmlReporter(
            includeContainers = false,
            useTestPathAsName = true,
            outputDir = "kotest-reports",
        )
    )
}
