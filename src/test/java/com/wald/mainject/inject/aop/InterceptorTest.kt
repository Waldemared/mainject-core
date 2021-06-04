package com.wald.mainject.inject.aop

import com.wald.mainject.inject.aop.advice.Invocation
import com.wald.mainject.utils.testLogger
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import java.util.concurrent.TimeUnit
import kotlin.reflect.KFunction
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.measureTime
import kotlin.time.toDuration


/**
 * @author vkosolapov
 * @since
 */
class InterceptorTest {
    @Test
    fun `testInterception`() {
        val instance = TheC()
        val logger = testLogger()
        val proxy = interceptionmanager.wrap(TheC::class, instance) as TheI
        val result = proxy.method1(53)
        val result2 = proxy.method2("gewg")
        assertEquals(53, result)
    }
}

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Cacheable

data class InvocationCacheKey(val target: Any, val method: KFunction<*>, val arguments: Array<out Any>)

interface Cache<K : Any> {
    operator fun contains(key: K): Boolean

    operator fun get(key: K): Any
}

class LocalInvocationCache : Cache<InvocationCacheKey> {
    override fun contains(key: InvocationCacheKey): Boolean {
        TODO("Not yet implemented")
    }

    override fun get(key: InvocationCacheKey): Any {
        TODO("Not yet implemented")
    }
}


@Suppress("UNCHECKED_CAST", "unused")
class CachingComponentInterceptor(private val logger: Logger) : Interceptor {
    private val cache: Cache<InvocationCacheKey> = LocalInvocationCache()

    override fun <T : Any, R : Any> process(invocation: Invocation<T, R>): R {
        val cacheKey = InvocationCacheKey(invocation.target, invocation.method, invocation.arguments)

        if (cacheKey in cache) {
            logger.debug("Found a cached result of ${invocation.method.name} invocation")
            return cache[cacheKey] as R
        }

        return invocation.invoke()
    }
}

annotation class TrackTime(val warnTimeout: Int = 1800)

class TimeTrackingInterceptor(private val logger: Logger) : Interceptor {
    override fun <T : Any, R : Any> process(invocation: Invocation<T, R>): R {
        val startTime = LocalDateTime.now()
        try {
            return invocation.invoke()
        } finally {
            val duration = java.time.Duration.between(startTime, LocalDateTime.now())
            logger.debug("Method has been proceed in ${duration.get(ChronoUnit.MILLIS)}")
        }
    }
}


interface TheI {
    fun method1(intArgument: Int): Int

    @Cacheable
    fun method2(stringArgument: String): String
}

class TheC : TheI {
    override fun method1(intArgument: Int): Int {
        return intArgument
    }

    override fun method2(stringArgument: String): String {
        return stringArgument
    }
}
