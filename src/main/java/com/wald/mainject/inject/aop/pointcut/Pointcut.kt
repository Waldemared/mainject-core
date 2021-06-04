package com.wald.mainject.inject.aop

import kotlin.reflect.KClass
import kotlin.reflect.KFunction


/**
 * @author vkosolapov
 * @since
 */
interface Pointcut {
    fun check(clazz: KClass<*>, method: KFunction<*>): Boolean
}