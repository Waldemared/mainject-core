package com.wald.mainject.config.dsl

import com.wald.mainject.Scope
import com.wald.mainject.SingletonScope
import com.wald.mainject.inject.*
import com.wald.mainject.inject.definition.BeanDefinition
import com.wald.mainject.inject.definition.ComponentMethodBeanDefinition
import com.wald.mainject.inject.definition.ImplClassBeanDefinition
import com.wald.mainject.inject.qualify.hasAnnotation
import javax.inject.Provider
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberFunctions


/**
 * @author vkosolapov
 * @since
 */
class BeansDsl {
    private val beans: MutableList<BeanDefinition<*>> = mutableListOf()

    inline fun <reified T : Any> bean(beanSupply: BeanDsl<T>.() -> Unit = {}) {
        val bean = BeanDsl(T::class).apply(beanSupply).toImplClassDefinition()
        newBean(bean)
    }

    inline fun <reified T : Any> singleton(beanSupply: BeanDsl<T>.() -> Unit = {}) {
        val bean = BeanDsl(T::class).apply(beanSupply)
            .apply { scope(SingletonScope) }
            .toImplClassDefinition()

        newBean(bean)
    }

    inline fun <reified T : Provider<*>> provider(beanSupply: BeanDsl<T>.() -> Unit = {}) {
        bean(beanSupply)
    }

    inline fun <reified T : Any> definitions(beanSupply: BeanDsl<T>.() -> Unit = {}) {
        val beans = BeanDsl(T::class).apply(beanSupply).toComponentMethodsDefinitions()
        for (bean in beans) {
            newBean(bean)
        }
    }

    @PublishedApi
    internal fun newBean(bean: BeanDefinition<*>) {
        beans.add(bean)
    }

    fun toDefinit(): List<BeanDefinition<*>> {
        return beans
    }

    class BeanDsl<T : Any>(val clazz: KClass<T>) {
        private var primary: Boolean? = null
        private var name: String? = null
        private var qualifiers: MutableList<QualifierDescriptor>? = null
        private var scope: Scope? = null

        fun name(name: String) {
            this.name = name
        }

        fun primary(primary: Boolean = true) {
            this.primary = primary
        }

        fun qualified(vararg qualifiers: Annotation) {
            qualifiers(qualifiers.map { InstanceBasedQualifierDescriptor(it) })
        }

        inline fun <reified T : Annotation> qualified() {
            qualifiers(listOf(TypeBasedQualifierDescriptor(T::class)))
        }

        @PublishedApi
        internal fun qualifiers(qualifiers: Iterable<QualifierDescriptor>) {
            if (this.qualifiers == null) {
                this.qualifiers = mutableListOf<QualifierDescriptor>().apply { addAll(qualifiers) }
            } else {
                this.qualifiers!!.addAll(qualifiers)
            }
        }

        fun scope(scope: Scope) {
            this.scope = scope
        }

        @PublishedApi
        internal fun toImplClassDefinition(): BeanDefinition<T> {
            return ImplClassBeanDefinition(
                componentClass = clazz,
                scope = this.scope,
                constructor = null,
                primary = primary,
                qualifiers = qualifiers
            )
        }

        @PublishedApi
        internal fun toComponentMethodsDefinitions(): List<BeanDefinition<*>> {
            val componentDefinition = ImplClassBeanDefinition(
                componentClass = clazz,
                scope = SingletonScope,
                constructor = clazz.constructors.first { it.parameters.isEmpty() },
                primary = true,
                qualifiers = emptyList()
            )

            val methodDefinitions = clazz.memberFunctions.filter { it.hasAnnotation<Provides>() }
                .map { ComponentMethodBeanDefinition(method = it as KFunction<Any>) }

            return methodDefinitions + componentDefinition
        }
    }
}

fun beans(beanSupply: BeansDsl.() -> Unit): List<BeanDefinition<*>> {
    return BeansDsl().apply(beanSupply).toDefinit()
}