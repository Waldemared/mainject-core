package com.wald.mainject.integrate

import com.wald.mainject.config.property.PropertySource
import com.wald.mainject.inject.lifecylce.PreInitializer
import java.io.FileReader
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Path


/**
 * @author vkosolapov
 * @since
 */
class ModulesTest {
}

object JsonParser {
    fun <T> parse(json: String) : T {
        error("e")
    }

    fun <T> parse(reader: Reader) : T {
        error("e")
    }
}

interface ConfigurationManager {
    fun <T> load(configId: String): T
}

class FileConfigurationManager(properties: PropertySource) : ConfigurationManager {
    private val defaultConfigDir = properties["DEFAULT_CONFIG_DIR"]

    override fun <T> load(configId: String): T {
        val configFilePath = Path.of("$defaultConfigDir/$configId")
        if (Files.exists(configFilePath)) {
            val configReader = FileReader(configFilePath.toFile())
            return JsonParser.parse(configReader)
        }

        throw error("No configuration found for ...")
    }
}

interface ConfigurationAware<T : Any> {
    val configId: String

    var config : T
}

class ConfigurationAwarePreInitializer<T>(val configurationManager: ConfigurationManager)
    : PreInitializer<T> where T : ConfigurationAware<*> {

    override fun apply(component: T) {
        val config = configurationManager.load<Any>(component.configId)
        component.config = config as Nothing
    }
}