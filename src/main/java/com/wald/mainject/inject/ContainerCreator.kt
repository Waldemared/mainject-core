package com.wald.mainject.inject

import com.wald.mainject.InjectableMembersResolver


/**
 * @author vkosolapov
 * @since
 */
class ContainerCreator(private val userBindings: ContainerBindings) {
    fun initializeContainer(): Container {
        val moduleRegistry = ModuleRegistry()
        loadModules(moduleRegistry)
        val mainModule = composeModules(moduleRegistry)
        val componentRegistry = buildComponentGraph(mainModule)
        val memberInjector = obtainMemberInjector(mainModule, componentRegistry)

        return Container(componentRegistry, memberInjector)
    }

    private fun loadModules(registry: ModuleRegistry) {
        val modules = userBindings.modules
        modules.forEach(registry::register)
        val importLoader: ModuleImportLoader = ModuleImportLoader(registry)
        importLoader.loadImports(modules)
        importLoader.loadExternalImports(modules)
    }

    private fun composeModules(registry: ModuleRegistry) = ModulesComposer(registry).compose()

    private fun buildComponentGraph(mainModule: MainModule): ComponentRegistry {
        return ComponentDefinitionProcessor(mainModule, userBindings.extensions).process()
    }

    private fun obtainMemberInjector(mainModule: MainModule, componentRegistry: ComponentRegistry): MemberInjector {
        val injectableMembersResolver = with(userBindings.extensions) {
            InjectableMembersResolver(propertySelector, setterSelector)
        }
        val memberDependencyResolver = BaseDependencyResolver(
            descriptorResolver = userBindings.extensions.dependencyResolver,
            componentDefinitions = mainModule.componentDefinitions,
            components = componentRegistry
        ) { _, _ -> null }

        return MemberInjector(safe = true,
            injectableMembersResolver = injectableMembersResolver,
            dependencyResolver = memberDependencyResolver)
    }
}