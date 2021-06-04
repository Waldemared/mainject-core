package com.wald.mainject.config.dsl

import com.wald.mainject.module.Module
import com.wald.mainject.module.ModuleBuilder


/**
 * @author vkosolapov
 * @since
 */
class ModuleDSL(val name: String) {
    private val builder = ModuleBuilder(name)

    fun beans(beansSupply: BeansDsl.() -> Unit) {
        BeansDsl().apply(beansSupply)
            .toDefinit()
            .forEach { builder.component(it) }
    }

    fun imports(importsSupply: ImportsDsl.() -> Unit) {
        val imports = ImportsDsl().apply(importsSupply).build()
        imports.imports.forEach { builder.import(it) }
        imports.sources.forEach { builder.moduleSource(it) }
    }

    fun properties(propertySupply: PropertiesDsl.() -> Unit) {
        val properties = PropertiesDsl().apply(propertySupply).build()
        properties.forEach { builder.properties(it) }
    }

    fun toModule() = builder.build()
}

class ModulesDsl() {
    private val modulesf = mutableListOf<Module>()

    fun module(name:String, modules: ModuleDSL.() -> Unit) {
        modulesf += ModuleDSL(name).apply(modules).toModule()
    }

    fun toModules(): List<Module> {
        return modulesf
    }
}

fun modules(modulesSupply: ModulesDsl.() -> Unit): Module {
    return com.wald.mainject.module.module("gege").build()
}

fun module(name: String, moduleSupply: ModuleDSL.() -> Unit): Module {
    return ModuleDSL(name).apply(moduleSupply).toModule()
}
