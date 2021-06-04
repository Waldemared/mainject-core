package com.wald.mainject.inject.lifecylce


/**
 * @author vkosolapov
 * @since
 */
open class BasePreInitializer<T : Any>(val initializer: (T) -> Unit,
                                       val componentFilter: (T) -> Boolean) : PreInitializer<T> {
    override fun apply(component: T) = initializer(component)
}