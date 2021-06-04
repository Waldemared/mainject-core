package com.wald.mainject.inject.aop.proxy

import com.wald.mainject.inject.aop.advice.Invocation
import com.wald.mainject.inject.aop.pointcut.InterceptedMethods
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction


/**
 * @author vkosolapov
 * @since
 */
class DynamicProxyProvider : ProxyFactory {
    override fun <T : Any> proxyFor(clazz: KClass<T>, target: T, intercepted: InterceptedMethods): Any {
        val invocation = InvocationHandler { _, method, args ->
            val kotlinMethod = method.kotlinFunction
            val inContext = Invocation(clazz, target, kotlinMethod as KFunction<Any>, args)

            if (kotlinMethod !in intercepted.methods) {
                return@InvocationHandler inContext.invoke()
            }

            return@InvocationHandler intercepted.advice.process(inContext)
        }

        return Proxy.newProxyInstance(clazz.java.classLoader, clazz.java.interfaces, invocation)
    }

    companion object {
        private val instance = DynamicProxyProvider()
        fun getInstance() = instance
    }
}