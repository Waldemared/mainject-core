package com.wald.mainject.inject

import com.wald.mainject.*
import com.wald.mainject.inject.definition.BeanDefinition
import com.wald.mainject.inject.extension.Extensions
import com.wald.mainject.inject.lifecylce.PreInitializer
import com.wald.mainject.inject.utils.isPublic
import java.util.*
import javax.inject.Provider
import kotlin.reflect.*
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure


/**
 * @author vkosolapov
 * @since
 */
class ComponentDefinitionProcessor(private val mainModule: MainModule,
                                   private val extensions: Extensions) {
    private val componentRegistry: ComponentRegistry = ComponentRegistry()
    private val dependencyResolver: DependencyResolver = BaseDependencyResolver(
        descriptorResolver = extensions.dependencyResolver,
        componentDefinitions = mainModule.componentDefinitions,
        components = componentRegistry
    ) { beanDefinition, resolutionContext -> createComponent(beanDefinition, resolutionContext) }
    private val injectableMembersResolver = InjectableMembersResolver(
        propertySelector = extensions.propertySelector,
        setterSelector = extensions.setterSelector
    )
    private val providerComposer: ProviderComposer = LayeredProviderFactory()

    fun process(): ComponentRegistry {
        for (definition in mainModule.componentDefinitions.beanDefinitions) {
            if (definition !in componentRegistry) {
                createComponent(definition, ResolutionContext(ArrayDeque()))
            }
        }

        return componentRegistry
    }

    private fun validate(definition: BeanDefinition<*>) {

    }

    private fun createComponent(definition: BeanDefinition<*>, resolutionContext: ResolutionContext): Component<*> {
        val component = ApplicationComponent(
            definition = definition as BeanDefinition<Any>,
            primary = definition.primary,
            scope = definition.scope
        )

        val constructor = definition.constructionMethod ?: resolveFactoryMethod(component) as KFunction<Any>
        val dependencies = dependencyResolver.resolve(constructor, resolutionContext)
        component.constructor = InjectionPoint(constructor, dependencies)

        mainModule.preInitializers.forEach { component.preInitializers += it as PreInitializer<Any> }

        component.initializers += resolveInitializers(definition)
        component.destructors += resolveDestructors(definition)

        injectableMembersResolver.resolve(definition.type.jvmErasure)
            .map { InjectionPoint(it, dependencyResolver.resolve(it, resolutionContext)) }
            .forEach { component.members.add(it) }

        component.provider = providerComposer.create(component) as Provider<Any>

        return component
    }

    private fun resolveFactoryMethod(component: Component<*>): KFunction<*> {
        val componentClass = component.definition.type.jvmErasure
        val availableConstructors = componentClass.constructors.filter { it.isPublic }
        val constructor = extensions.constructorResolver.resolve(
            ownerClass = componentClass as KClass<Any>,
            proposedConstructors = availableConstructors
        )

        if (constructor !in availableConstructors) {
            error("Chosen constructor is not allowed. Class: ${componentClass.simpleName}")
        }

        return constructor
    }

    private fun resolveInitializers(definition: BeanDefinition<*>): Set<KFunction<Unit>> {
        return definition.type.jvmErasure
            .allowedLifecycleFunctions
            .filter(extensions.lifecycleCallbacksResolver::isInitMethod)
            .toSet()
    }

    private fun resolveDestructors(definition: BeanDefinition<*>): Set<KFunction<Unit>> {
        return definition.type.jvmErasure
            .allowedLifecycleFunctions
            .filter(extensions.lifecycleCallbacksResolver::isDestroyMethod)
            .toSet()
    }

    private val KClass<*>.allowedLifecycleFunctions: Sequence<KFunction<Unit>>
        get() {
            return memberFunctions.asSequence()
                .filter { it.isPublic }
                .filter { it.valueParameters.all(KParameter::isOptional) }
                .filter { it.returnType.jvmErasure == Unit::class }
                .map { it as KFunction<Unit> }
        }
}