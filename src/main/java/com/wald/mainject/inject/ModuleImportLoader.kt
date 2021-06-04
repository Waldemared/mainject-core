package com.wald.mainject.inject

import com.wald.mainject.module.Module
import com.wald.mainject.module.ModuleRef
import com.wald.mainject.module.ModulesSource


/**
 * @author vkosolapov
 * @since
 */
class ModuleImportLoader(private val registry: ModuleRegistry) {
    fun loadImports(modules: Collection<Module>) {
        modules.asSequence()
            .map(this::readImports)
            .flatten()
            .forEach(registry::register)
    }

    fun loadExternalImports(modules: Collection<Module>) {
        modules.asSequence()
            .map(this::readExternalImports)
            .flatten()
            .forEach(registry::register)
    }

    private fun readImports(module: Module) = module.imports
        .map { readImport(module, it) }

    private fun readImport(module: Module, moduleRef: ModuleRef) = runCatching {
            moduleRef.value
        }.onFailure {
            error("Failed to instantiate module of the ${moduleRef.clazz} class")
        }.getOrThrow()

    private fun readExternalImports(module: Module) = module.importedSources.asSequence()
        .map { readExternalImport(module, it) }
        .flatten()
        .toSet()

    private fun readExternalImport(module: Module, source: ModulesSource) = runCatching {
        source.read()
    }.onFailure {
        error("Failed to read imports of ${module.name} module from $source source")
    }.getOrThrow()
}