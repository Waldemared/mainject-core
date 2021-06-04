package com.wald.mainject.inject.definition

import com.wald.mainject.DefaultScope
import com.wald.mainject.Scope
import com.wald.mainject.inject.Primary
import com.wald.mainject.inject.QualifierDescriptor
import com.wald.mainject.inject.qualify.hasAnnotation
import com.wald.mainject.inject.qualify.qualifiers
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.reflect


/**
 * Mutable definition of a container component that is registered by used.
 * Abstracts ways to register a component by providing the required data explicitly.
 *
 * @author vkosolapov
 * @since
 */
sealed class BeanDefinition<T : Any> {
    abstract val type: KType

    abstract val constructionMethod: KFunction<T>?

    abstract val profile: String?

    abstract val scope: Scope

    /**
     * Qualifiers of the component
     */
    abstract val qualifiers: List<QualifierDescriptor>

    /**
     * Whether the component is marked as primary, that is,
     * eligible for injection when several injection candidates found
     */
    abstract val primary: Boolean

    val key: ComponentKey = PlainComponentKey(this.type, this.qualifiers)
}

class ImplClassBeanDefinition<T : Any>(componentClass: KClass<T>,
                                       scope: Scope? = null,
                                       primary: Boolean? = null,
                                       qualifiers: List<QualifierDescriptor>? = null) : BeanDefinition<T>() {
    override val type: KType = componentClass.starProjectedType
    override val constructionMethod: KFunction<T>? = null
    override val scope: Scope = scope ?: DefaultScope
    override val qualifiers: List<QualifierDescriptor> = qualifiers ?: componentClass.qualifiers
    override val primary: Boolean = primary ?: componentClass.hasAnnotation<Primary>()
}

class InstanceBeanDefinition<T : Any>(instance: T,
                                      type: KType? = null,
                                      scope: Scope? = null,
                                      override val qualifiers: List<QualifierDescriptor> = emptyList(),
                                      override val primary: Boolean = false) : BeanDefinition<T>() {
    override val type: KType = type ?: instance::class.starProjectedType
    override val constructionMethod: KFunction<T> = { instance }.reflect()!!
    override val scope: Scope = scope ?: DefaultScope
}

class ExpressionComponentDefinition<T : Any>(instanceFactory: Function<T>,
                                             type: KType? = null,
                                             scope: Scope? = null,
                                             override val qualifiers: List<QualifierDescriptor> = emptyList(),
                                             override val primary: Boolean = false) : BeanDefinition<T>() {
    private val reflectedExpression = instanceFactory.reflect()!!
    override val type: KType = type ?: reflectedExpression.returnType
    override val constructionMethod: KFunction<T> = reflectedExpression
    override val scope: Scope = scope ?: DefaultScope
}

class ComponentMethodBeanDefinition<T : Any>(method: KFunction<T>,
                                             scope: Scope? = null,
                                             primary: Boolean? = null,
                                             qualifiers: List<QualifierDescriptor>? = null) : BeanDefinition<T>() {
    override val type: KType = method.returnType
    override val constructionMethod: KFunction<T> = method
    override val scope: Scope = scope ?: DefaultScope
    override val qualifiers: List<QualifierDescriptor> = qualifiers ?: type.jvmErasure.qualifiers
    override val primary: Boolean = primary ?: method.hasAnnotation<Primary>()
}