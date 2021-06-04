package com.wald.mainject.inject.aop.proxy

import com.wald.mainject.inject.aop.pointcut.InterceptedMethods
import kotlin.reflect.KClass


/**
 * @author vkosolapov
 * @since
 */
interface ProxyFactory {
    fun <T : Any> proxyFor(clazz: KClass<T>, target: T, intercepted: InterceptedMethods): Any
}