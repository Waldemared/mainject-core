package com.wald.mainject.config.property.resolver

import com.wald.mainject.config.property.PropertyResolver

/**
 * @author vkosolapov
 * @since
 */
abstract class CachingPropertyResolver : PropertyResolver {
    private val cachedValues: MutableMap<String, Any> = HashMap()

    override fun resolve(propertyValue: String): Any? {
        if (cachedValues[propertyValue] != null) {
            return cachedValues[propertyValue]
        }

        val resolvedValue = doResolve(propertyValue)

        if (resolvedValue != null) {
            cachedValues[propertyValue] = resolvedValue
        }

        return resolvedValue;
    }

    protected abstract fun doResolve(propertyValue: String): Any?
}