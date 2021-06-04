package com.wald.mainject.inject

import com.wald.mainject.module.Module
import com.wald.mainject.module.ModuleRef


/**
 * @author vkosolapov
 * @since
 */
class ModuleRegistry {
    private val registeredModules: MutableMap<String, Module> = HashMap()

    val modules: Set<Module>
        get() = registeredModules.values.toSet()

    fun register(module: Module) {
        registeredModules.computeIfAbsent(module.name) { module }
    }

    fun override(baseModuleRef: ModuleRef, override: Module) {
        val baseModule = baseModuleRef.value
        registeredModules.compute(baseModule.name) { _, _ -> override }
    }

    fun exclude(baseModuleRef: ModuleRef) {}
}