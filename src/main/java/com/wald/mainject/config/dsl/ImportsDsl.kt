package com.wald.mainject.config.dsl

import com.wald.mainject.module.*
import kotlin.reflect.KClass


/**
 * @author vkosolapov
 * @since
 */
class ImportsDsl {
    private val imports: MutableSet<ModuleRef> = mutableSetOf()
    private val moduleSources: MutableSet<ModulesSource> = mutableSetOf()

    fun <T : Module> module(moduleClass: KClass<T>) {
        val import = ModuleClass(moduleClass)
        imports += import
    }

    inline fun <reified T : Module> module() {
        module(T::class)
    }

    fun source(modulesSource: ModulesSource) {
        moduleSources += modulesSource
    }

    fun build(): Imports {
        return Imports(imports, moduleSources)
    }

    data class Imports(val imports: Set<ModuleRef>, val sources: Set<ModulesSource>)
}