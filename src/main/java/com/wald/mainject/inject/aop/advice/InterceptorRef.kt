package com.wald.mainject.inject.aop.advice

import com.wald.mainject.inject.aop.Interceptor
import kotlin.reflect.KClass

sealed class InterceptorRef(open val instance: Interceptor? = null,
                            open val clazz: KClass<out Interceptor>? = null)

class InterceptorInstance(override val instance: Interceptor) : InterceptorRef(instance = instance)

class InterceptorClass(override val clazz: KClass<out Interceptor>) : InterceptorRef(clazz = clazz)
