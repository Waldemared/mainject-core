package com.wald.mainject.inject

import com.wald.mainject.DependencyResolver
import com.wald.mainject.ResolutionContext
import com.wald.mainject.inject.definition.BeanDefinition
import com.wald.mainject.inject.definition.PlainComponentKey
import com.wald.mainject.inject.extension.DependencyDescriptorResolver
import com.wald.mainject.inject.lifecylce.PreInitializer
import kotlin.reflect.KCallable
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure


/**
 * @author vkosolapov
 * @since
 */
class BaseDependencyResolver(private var descriptorResolver: DependencyDescriptorResolver,
                             private var componentDefinitions: BeanDefinitionRegistry,
                             private var components: ComponentRegistry,
                             private var missingComponentFinder: (BeanDefinition<*>, ResolutionContext) -> Component<*>?)
    : DependencyResolver {

    override fun resolve(callable: KCallable<*>, resolutionContext: ResolutionContext): List<Component<*>?> {
        val parameters = callable.parameters
        val descriptors = parameters.map(descriptorResolver::resolve)
        val dependencies = descriptors.asSequence()
            .onEach(this::validateDescriptor)
            .map { resolveDependency(it, resolutionContext) }
            .toList()

        return dependencies
    }

    private fun validateDescriptor(dependencyDescriptor: DependencyDescriptor) {
        val dependencyClass = dependencyDescriptor.type.jvmErasure
        nonApplicableDependencyTypes.forEach {
            if (dependencyClass.isSubclassOf(it)) {
                throw error("Components of ${it.simpleName} class are not allowed")
            }
        }
    }

    private fun resolveDependency(descriptor: DependencyDescriptor,
                                  resolutionContext: ResolutionContext): Component<*>? {
        val key = PlainComponentKey(type = descriptor.type, qualifiers = descriptor.qualifiers.toList())
        val candidateDefinitions = componentDefinitions[key]
        if (candidateDefinitions.isEmpty()) {
            if (!descriptor.optional) {
                error("Missing bean definition satisfying $key")
            }
            return null
        }
        val dependencyDefinition = candidateDefinitions.first()
        if (dependencyDefinition in components) {
            return components[dependencyDefinition]!!
        }

        if (resolutionContext.inConstruction(dependencyDefinition)) {
            error("Detected a circular dependency during object graph creation. " +
                    "Construction stack: ${resolutionContext.constructingComponents}")
        }

        resolutionContext.pushDefinition(dependencyDefinition)
        val dependency = missingComponentFinder(dependencyDefinition, resolutionContext)
        resolutionContext.popDefinition()

        return dependency
    }

    private val nonApplicableDependencyTypes = setOf(
        PreInitializer::class
    )
}