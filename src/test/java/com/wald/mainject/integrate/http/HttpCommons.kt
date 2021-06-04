package com.wald.mainject.integrate.http

import com.wald.mainject.config.property.PropertySource
import com.wald.mainject.integrate.HttpClientArchetype
import com.wald.mainject.integrate.ProxyingHttpClientArchetype
import java.net.InetSocketAddress
import java.net.ProxySelector
import java.net.http.HttpClient
import java.time.Duration
import java.util.concurrent.Executor


/**
 * @author vkosolapov
 * @since
 */
class HttpCommons {
    fun configuration(properties: PropertySource): HttpConfiguration {
        val proxyUrl = properties.getString("http.proxy.url")
        val proxyPort = properties.getString("http.proxy.port").toInt()
        val defaultTimeout = properties.getString("http.defaultTimeout").toLong()
        val proxyAddr = InetSocketAddress(proxyUrl, proxyPort);

        return HttpConfiguration(ProxySelector.of(proxyAddr), defaultTimeout)
    }

    @HttpClientArchetype
    fun baseClientBuilder(httpConfig: HttpConfiguration, executor: Executor): HttpClient.Builder {
        return HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(httpConfig.defaultTimeout))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .executor(executor)
    }

    @ProxyingHttpClientArchetype
    fun proxyingClientBuilder(@HttpClientArchetype baseBuilder: HttpClient.Builder,
                              httpConfig: HttpConfiguration
    ): HttpClient.Builder {
        requireNotNull(httpConfig.proxy) { "Proxy settings are not configured" }
        return baseBuilder.proxy(httpConfig.proxy)
    }
}