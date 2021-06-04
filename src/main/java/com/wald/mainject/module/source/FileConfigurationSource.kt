package com.wald.mainject.module.source

import com.wald.mainject.module.ModulesSource
import java.io.File

/**
 * @author vkosolapov
 * @since
 */
abstract class FileConfigurationSource(protected val configurationFile: File) : ModulesSource