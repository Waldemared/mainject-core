package com.wald.mainject.inject.lifecylce


/**
 * @author vkosolapov
 * @since
 */
data class StaticInitializer<T : Any>(val initializer: (T) -> Unit, val componentFilter: (T) -> Boolean = { true })