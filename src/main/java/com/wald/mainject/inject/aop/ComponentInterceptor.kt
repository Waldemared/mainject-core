package com.wald.mainject.inject.lifecylce

import com.wald.mainject.inject.Component
import com.wald.mainject.inject.aop.*
import com.wald.mainject.inject.aop.advice.InterceptorClass
import com.wald.mainject.inject.aop.advice.InterceptorInstance
import com.wald.mainject.inject.aop.advice.InterceptorRef
import com.wald.mainject.inject.aop.advice.Invocation
import com.wald.mainject.inject.aop.pointcut.InterceptedMethods
import com.wald.mainject.inject.aop.proxy.DynamicProxyProvider
import com.wald.mainject.inject.definition.AspectDefinition
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions


/**
 * @author vkosolapov
 * @since
 */
interface ComponentInterceptor {
    fun <T: Any, R : Any> invoke(context: Invocation<T, R>): R
}

fun g() {
    fun resolveInterceptor(clazz: KClass<out Interceptor>): Interceptor {
        TODO()
    }

    fun resolve(interceptorRef: InterceptorRef): Interceptor {
        return when (interceptorRef) {
            is InterceptorInstance -> interceptorRef.instance
            is InterceptorClass -> {
                val clazz = interceptorRef.clazz
                resolveInterceptor(clazz)
            }
        }
    }

    val components = mutableListOf<Component<*>>()
    val aspectDefinitions = mutableListOf<AspectDefinition>()
    for (component in components) {

            for (aspectDefinition in aspectDefinitions) {
                for (method in component.clazz.functions) {
                    val methods = mutableSetOf<KFunction<*>>()
                    if (aspectDefinition.pointcut.check(component.clazz, method)) {
                        methods += method
                    }
                    val interceptor = resolve(aspectDefinition.advice)
                    component.interceptors += InterceptedMethods(methods, interceptor)
            }
        }
    }

    fun wrapInterceptor(component: Component<*>, intercepted: InterceptedMethods) {
        val clazz = component.clazz as KClass<Any>
        val instance = component.instance!! as KFunction<Any>
        DynamicProxyProvider.getInstance().proxyFor(clazz, instance, intercepted)
    }

    val component = components.firstOrNull()!!
}