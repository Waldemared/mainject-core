package com.wald.mainject.config.property.source

import com.wald.mainject.config.property.PropertySource

/**
 * Implementation of [PropertySource] that provides access to
 * system properties including runtime environment variables.
 *
 * @author vkosolapov
 * @since
 */
object RuntimeSystemProperties : PropertySource {
    override val properties: Map<String, Any>
        get() = System.getProperties().mapKeys { (name, _) -> name.toString() }

    override fun get(propertyKey: String): Any? = System.getProperty(propertyKey)

    override fun contains(propertyKey: String): Boolean = System.getProperties().containsKey(propertyKey)
}

object SystemEnvironmentProperties : PersistentPropertySource() {
    override fun doGetAll(): Map<String, Any> = System.getenv()
}