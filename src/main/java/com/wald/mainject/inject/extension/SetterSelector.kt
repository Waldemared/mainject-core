package com.wald.mainject.inject.extension

import kotlin.reflect.KFunction


/**
 * @author vkosolapov
 * @since
 */
interface SetterSelector {
    fun isInjectable(setter: KFunction<*>): Boolean
}