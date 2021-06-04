package com.wald.mainject.inject.aop.pointcut

import com.wald.mainject.inject.aop.Interceptor
import kotlin.reflect.KFunction

data class InterceptedMethods(val methods: Collection<KFunction<*>>, val advice: Interceptor)
