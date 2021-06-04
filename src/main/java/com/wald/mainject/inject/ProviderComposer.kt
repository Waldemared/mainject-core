package com.wald.mainject.inject

import javax.inject.Provider


/**
 * @author vkosolapov
 * @since
 */
interface ProviderComposer {
    fun create(component: Component<*>): Provider<*>
}