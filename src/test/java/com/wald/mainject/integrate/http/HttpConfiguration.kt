package com.wald.mainject.integrate.http

import java.net.ProxySelector


/**
 * @author vkosolapov
 * @since
 */
data class HttpConfiguration(val proxy: ProxySelector?, val defaultTimeout: Long)