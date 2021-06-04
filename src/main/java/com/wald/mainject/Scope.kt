package com.wald.mainject

import java.lang.ref.ReferenceQueue
import java.util.*
import javax.inject.Provider


/**
 * @author vkosolapov
 * @since
 */
interface Scope {
    fun <T> wrap(beanCreator: Provider<T>, onDestroy: (T) -> Unit) : Provider<T>
    fun <T> destroyAll()
}

object DefaultScope : Scope {
    override fun <T> wrap(beanCreator: Provider<T>, onDestroy: (T) -> Unit): Provider<T> {
        return beanCreator
    }

    override fun <T> destroyAll() {

    }
}

object SingletonScope : Scope {
    private val singletones: MutableSet<SingletonBeanProvider<*>> = mutableSetOf()

    override fun <T> wrap(beanCreator: Provider<T>, onDestroy: (T) -> Unit): Provider<T> {
        return SingletonBeanProvider<T>(beanCreator, onDestroy).apply { singletones += this }
    }

    override fun <T> destroyAll() {
        singletones.forEach { it.destroy() }
    }

    class SingletonBeanProvider<T>(val beanCreator: Provider<T>, private val onDestroy: (T) -> Unit) : Provider<T> {
        @Volatile
        private var resolvedInstance: T? = null

        override fun get(): T {
            if (resolvedInstance != null) {
                return resolvedInstance!!
            }

            val newInstance = beanCreator.get() ?: error("Unexpected null value from $beanCreator provider")

            resolvedInstance = newInstance
            return newInstance
        }

        fun destroy() {
            resolvedInstance?.let(onDestroy)
        }
    }
}