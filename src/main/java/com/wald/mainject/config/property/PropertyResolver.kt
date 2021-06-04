package com.wald.mainject.config.property

/**
 * @author vkosolapov
 * @since
 */
interface PropertyResolver {
    fun applies(propertyValue: String): Boolean
    fun resolve(propertyValue: String): Any?
}