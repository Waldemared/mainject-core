package com.wald.mainject.inject.aop.advice

import kotlin.reflect.KClass
import kotlin.reflect.KFunction

/**
 * Base AOP join point
 */
data class Invocation<T : Any, R : Any>(val clazz: KClass<T>,
                                        val target: T,
                                        val method: KFunction<R>,
                                        val arguments: Array<out Any>) {
    fun invoke(): R {
        return method.call(target, *arguments)
    }
}
