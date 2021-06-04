package com.wald.mainject.inject.utils

import kotlin.reflect.*


/**
 * @author vkosolapov
 * @since
 */
private data class SuperType(private val type: KType, val owner: Class) {
    val typeProjections: List<TypeProjection>
        get() = type.arguments.map { TypeProjection(it) }
    val asClass: Class
        get() = Class(type.classifier as KClass<*>, owner)

    fun isTypeOf(cls: KClass<*>) = type.classifier == cls
}

private data class TypeProjection(private val type: KTypeProjection) {
    val isClass: Boolean
        get() = type.type?.classifier is KClass<*>
    val isTemplate: Boolean
        get() = type.type?.classifier is KTypeParameter
    val asClass: KClass<*>
        get() = type.type?.classifier as KClass<*>
    val name: String?
        get() {
            var name: String? = null
            if (isClass)
                name = (type.type?.classifier as KClass<*>).simpleName
            else if (isTemplate)
                name = (type.type?.classifier as KTypeParameter).name
            return name
        }
}

private data class TypeParameter(private val type: KTypeParameter) {
    val name: String
        get() = type.name
}

private data class Class(private val type: KClass<*>, val root: Class? = null) {
    private val supertypes: List<SuperType>
        get() = type.supertypes.filter { it.classifier is KClass<*> && it.classifier != Any::class }
            .map { SuperType(it, this) }
    private val typeParameters: List<TypeParameter>
        get() = type.typeParameters.map { TypeParameter(it) }

    fun findInHierarchy(cls: KClass<*>): Class? {
        for (s in supertypes) {
            if (s.isTypeOf(cls)) {
                return s.asClass;
            } else {
                val s2 = s.asClass.findInHierarchy(cls)
                if (s2 != null)
                    return s2
            }
        }
        return null
    }

    private fun mapTemplateToClass(name: String): KClass<*>? {
        val tp = typeParameters.firstOrNull { it.name == name }
        if (tp != null && root != null) {
            val clsSuperType = root.supertypes.firstOrNull { it.isTypeOf(type) }
            if (clsSuperType != null) {
                val index = typeParameters.indexOf(tp)
                if (index >= 0) {
                    val projection = clsSuperType.typeProjections[index]
                    if (projection.isClass)
                        return projection.asClass
                    else if (projection.isTemplate) {
                        val projectionName = projection.name
                        if (projectionName != null) {
                            return clsSuperType.owner.mapTemplateToClass(projectionName)
                        }
                    }

                }
            }
        }
        return null
    }

    fun resolveAllTemplates(): Map<String, KClass<*>> {
        val paramNames = typeParameters.map { it.name }
        return paramNames.map { it to mapTemplateToClass(it) }.filter { it.second != null }
            .map { it.first to it.second!! }.toMap()
    }
}

fun KClass<*>.resolveTemplates(parent: KClass<*>): Map<String, KClass<*>>? =
    Class(this).findInHierarchy(parent)?.resolveAllTemplates()