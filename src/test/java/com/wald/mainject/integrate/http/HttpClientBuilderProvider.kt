package com.wald.mainject.integrate.http

import java.net.http.HttpClient
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Provider


/**
 * @author vkosolapov
 * @since
 */
class HttpClientBuilderProvider(private val httpConfig: HttpConfiguration,
                                private val executor: Executor?) : Provider<HttpClient.Builder> {
    override fun get(): HttpClient.Builder {
        return HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(getDefaultTimeout())
            .followRedirects(HttpClient.Redirect.NORMAL)
            .executor(getExecutor())
    }

    private fun getDefaultTimeout(): Duration {
        return Duration.of(httpConfig.defaultTimeout, ChronoUnit.SECONDS)
    }

    private fun getExecutor(): Executor {
        if (executor != null) {
            return executor
        } else {
            return Executors.newFixedThreadPool(4)
        }
    }
}