package com.wald.mainject.inject.extension

import com.wald.mainject.inject.extension.impl.*
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.inject.Inject


/**
 * @author vkosolapov
 * @since
 */
sealed class Extensions {
    abstract val constructorResolver: ConstructorResolver
    abstract val propertySelector: LateinitPropertySelector
    abstract val setterSelector: SetterSelector
    abstract val dependencyResolver: DependencyDescriptorResolver
    abstract val lifecycleCallbacksResolver: LifecycleCallbacksResolver
}

data class MutableExtensions(
    override var constructorResolver: ConstructorResolver = AnnotationBasedConstructorResolver(Inject::class),
    override var propertySelector: LateinitPropertySelector = AnnotationBasedLateinitPropertySelector(Inject::class),
    override var setterSelector: SetterSelector = AnnotationBasedSetterSelector(Inject::class),
    override var dependencyResolver: DependencyDescriptorResolver = DefaultDependencyDescriptionResolver(),
    override var lifecycleCallbacksResolver: LifecycleCallbacksResolver = AnnotationBasedLifecycleCallbackResolver(
        initMethodMarkers = listOf(PostConstruct::class),
        destroyMethodMarkers = listOf(PreDestroy::class)
    )
) : Extensions()

data class PublicExtensions(override val constructorResolver: ConstructorResolver,
                            override val propertySelector: LateinitPropertySelector,
                            override val setterSelector: SetterSelector,
                            override val dependencyResolver: DependencyDescriptorResolver,
                            override val lifecycleCallbacksResolver: LifecycleCallbacksResolver) : Extensions()