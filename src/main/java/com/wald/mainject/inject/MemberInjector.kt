package com.wald.mainject.inject

import com.wald.mainject.DependencyResolver
import com.wald.mainject.InjectableMembersResolver
import java.util.logging.Logger


/**
 * @author vkosolapov
 * @since
 */
class MemberInjector(val safe: Boolean,
                     private val injectableMembersResolver: InjectableMembersResolver,
                     private val dependencyResolver: DependencyResolver) {
    fun accept(instance: Any) {
        val clazz = instance::class
        val members = injectableMembersResolver.resolve(clazz)
        val injectionPoints = members.map { InjectionPoint(it, dependencyResolver.resolve(it)) }
        injectionPoints.forEach { inject(it, instance) }
    }

    private fun inject(injectionPoint: InjectionPoint<*>, instance: Any) {
        runCatching {
            injectionPoint.inject(instance)
        }.onFailure {
            if (safe) {
                LOG.warning("Failed to inject value into ${injectionPoint.callable.name} member of $instance")
            } else {
                throw it
            }
        }.getOrNull()
    }

    companion object {
        private val LOG = Logger.getLogger(MemberInjector::class.qualifiedName)
    }
}