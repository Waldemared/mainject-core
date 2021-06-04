package com.wald.mainject.inject.extension

import kotlin.reflect.KFunction


/**
 * @author vkosolapov
 * @since
 */
interface LifecycleCallbacksResolver {

    fun isInitMethod(method: KFunction<Unit>): Boolean

    fun isDestroyMethod(method: KFunction<Unit>): Boolean
}