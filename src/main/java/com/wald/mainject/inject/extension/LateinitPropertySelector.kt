package com.wald.mainject.inject.extension

import kotlin.reflect.KMutableProperty


/**
 * @author vkosolapov
 * @since
 */
interface LateinitPropertySelector {
    fun isInjectable(property: KMutableProperty<*>): Boolean
}