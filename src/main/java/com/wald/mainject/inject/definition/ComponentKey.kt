package com.wald.mainject.inject.definition

import com.wald.mainject.inject.InstanceBasedQualifierDescriptor
import com.wald.mainject.inject.QualifierDescriptor
import com.wald.mainject.inject.TypeBasedQualifierDescriptor
import com.wald.mainject.inject.qualify.AnnotationType
import kotlin.reflect.KType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.typeOf


/**
 * @author vkosolapov
 * @since
 */
interface ComponentKey {
    val type: KType

    val qualifiers: List<QualifierDescriptor>
}

class PlainComponentKey(override val type: KType,
                        override val qualifiers: List<QualifierDescriptor>) : ComponentKey {
    constructor(javaType: Class<*>, qualifiers: List<QualifierDescriptor>) : this(
        type = javaType.kotlin.starProjectedType,
        qualifiers = qualifiers
    )
}

fun ComponentKey.satisfies(specificationKey: ComponentKey) =
    type.isSubtypeOf(specificationKey.type) && qualifiers.containsAll(specificationKey.qualifiers)



@OptIn(ExperimentalStdlibApi::class)
inline fun <reified T : Any> keyOfType(builder: ComponentKeyBuilder.() -> Unit): ComponentKey {
    return ComponentKeyBuilder(typeOf<T>()).apply(builder).build()
}

class ComponentKeyBuilder(private var type: KType) {
    private var qualifiers = mutableListOf<QualifierDescriptor>()

    inline fun <reified T : Annotation> qualifiedWith() = qualifiedWith(T::class)

    fun qualifiedWith(qualifier: Annotation) = apply { qualifiers += InstanceBasedQualifierDescriptor(qualifier) }

    fun qualifiedWith(qualifierType: AnnotationType) = apply { qualifiers += TypeBasedQualifierDescriptor(qualifierType) }

    fun build() = PlainComponentKey(type, qualifiers)
}