package com.wald.mainject.inject.aop

import com.wald.mainject.inject.aop.advice.Invocation
import kotlin.reflect.KClass


/**
 *
 *
 * @author vkosolapov
 * @since
 * The AOP advice
 */
interface Interceptor {
    fun <T : Any, R : Any> process(invocation: Invocation<T, R>): R
}