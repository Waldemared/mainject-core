package com.wald.mainject.inject.lifecylce


/**
 * @author vkosolapov
 * @since
 */
interface PreInitializer<T : Any> {
    fun apply(component: T)
}