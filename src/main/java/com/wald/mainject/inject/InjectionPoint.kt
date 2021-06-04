package com.wald.mainject.inject

import com.wald.mainject.inject.qualify.hasAnnotationThat
import javax.inject.Provider
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KCallable
import kotlin.reflect.KParameter
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.valueParameters


/**
 * @author vkosolapov
 * @since
 */
class InjectionPoint<C : KCallable<*>>(val callable: C,
                                       private val properties: List<Component<*>?>) {
    fun callConstructor() : Any {
        val invocationParameters = constructParameters(callable.valueParameters)
        return callable.callBy(invocationParameters)!!
    }

    fun inject(injectable: Any) {
        val invocationParameters = mutableMapOf<KParameter, Any?>(callable.instanceParameter!! to injectable)
        invocationParameters += constructParameters(callable.valueParameters)
        callable.callBy(invocationParameters)
    }

    private fun constructParameters(invocationParameters: List<KParameter>): Map<KParameter, Any?> {
        val parameters = mutableMapOf<KParameter, Any?>()

        if (invocationParameters.size != properties.size) {
            error("Failed to inject dependencies. Wrong parameters number provided")
        }

        invocationParameters.forEachIndexed { index, param ->
            val component = properties[index]

            if (component == null) {
                if (param.isOptional) {
                    return@forEachIndexed
                }
                if (param.isNullable) {
                    parameters[param] = null
                    return@forEachIndexed
                }
                error("No value provided")
            }

            if (!component.definition.type.isSubtypeOf(param.type)) {
                error("Bad parameter type.")
            }

            if (component.definition.type.isSubtypeOf(Provider::class.starProjectedType)) {
                parameters[param] = component.provider
            } else {
                parameters[param] = component.provider!!.get()
            }
        }

        return parameters
    }

    private val KParameter.isNullable: Boolean
        get() = type.isMarkedNullable || callable.isAnnotatedAsNullable

    val KAnnotatedElement.isAnnotatedAsNullable: Boolean
        get() = this.hasAnnotationThat { it.annotationClass.simpleName == "Nullable" }
}