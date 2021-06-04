package com.wald.mainject.inject.aop

import com.wald.mainject.integrate.http.HttpClientBuilderProvider
import com.wald.mainject.integrate.http.HttpConfiguration
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import org.junit.jupiter.api.Test
import java.lang.reflect.Method
import java.net.http.HttpClient
import java.util.concurrent.Executors
import kotlin.reflect.full.superclasses
import kotlin.reflect.jvm.kotlinFunction


/**
 * @author vkosolapov
 * @since
 */
class CglibInvocationTests {
    @Test
    fun testInvocation() {
        val httpClient = HttpClientBuilderProvider(
            HttpConfiguration(proxy = null, defaultTimeout = 5),
            Executors.newSingleThreadScheduledExecutor()
        ).get()

        val enhancer = Enhancer()
        enhancer.setSuperclass(httpClient::class.superclasses.first().java)

        enhancer.setCallback(object : MethodInterceptor {
            override fun intercept(obj: Any, method: Method, args: Array<out Any>, proxy: MethodProxy): Any {
                println(this)
                println(obj.toString())
                println(method)
                println(args)
                println(proxy)
                println(method.kotlinFunction!!.call(obj, *args))
                println(proxy.invoke(obj, args))
                return proxy.invoke(obj, args)
            }
        })

        (enhancer.create() as HttpClient).followRedirects()
        //enhancer.setCallbackFilter(CallbackHelper())
    }
}