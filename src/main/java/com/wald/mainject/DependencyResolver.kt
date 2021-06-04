package com.wald.mainject

import com.wald.mainject.inject.Component
import com.wald.mainject.inject.definition.BeanDefinition
import java.util.*
import kotlin.reflect.KCallable


/**
 * @author vkosolapov
 * @since
 */
interface DependencyResolver {
    fun resolve(callable: KCallable<*>): List<Component<*>?> = resolve(callable, ResolutionContext())
    fun resolve(callable: KCallable<*>, resolutionContext: ResolutionContext): List<Component<*>?>
}

class ResolutionContext(val constructingComponents: Deque<BeanDefinition<*>> = LinkedList()) {
    fun pushDefinition(definition: BeanDefinition<*>) = constructingComponents.push(definition)
    fun popDefinition() = constructingComponents.pop()
    fun inConstruction(definition: BeanDefinition<*>) = definition in constructingComponents
}