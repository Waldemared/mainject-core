package com.wald.mainject.inject

import com.wald.mainject.inject.extension.Extensions
import com.wald.mainject.module.Module


/**
 * @author vkosolapov
 * @since
 */
data class ContainerBindings(val modules: Set<Module>, val extensions: Extensions)