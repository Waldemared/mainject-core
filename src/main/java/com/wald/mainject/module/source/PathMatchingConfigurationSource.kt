package com.wald.mainject.module.source

/**
 * @author vkosolapov
 * @since
 */
class PathMatchingConfigurationSource(protected val filePattern: String) {
    constructor() : this("*")
}