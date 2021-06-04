package com.wald.mainject.module

/**
 * @author vkosolapov
 * @since
 */
interface ModulesSource {
    fun read(): Set<Module>
}