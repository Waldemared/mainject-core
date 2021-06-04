package com.wald.mainject.module.source.script

import com.wald.mainject.module.Module
import com.wald.mainject.module.source.FileConfigurationSource
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.Reader
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException

/**
 * @author vkosolapov
 * @since
 */
class ScriptConfigurationSource(configurationFile: File) : FileConfigurationSource(configurationFile) {
    private val scriptEngine: ScriptEngine

    init {
        val extension = getExtension(configurationFile)
        scriptEngine = SCRIPT_MANAGER.getEngineByExtension(extension)
            ?: error("Configuration file \"${configurationFile.absolutePath}\" cannot be read: " +
                    "No script engine found for files having extension $extension")
    }

    override fun read(): Set<Module> {
        return try {
            val reader: Reader = FileReader(super.configurationFile)
            setOf(scriptEngine.eval(reader) as Module)
        } catch (exception: FileNotFoundException) {
            throw RuntimeException(exception)
        } catch (exception: ScriptException) {
            exception.fillInStackTrace()
            throw RuntimeException(exception)
        }
    }

    private fun getExtension(file: File): String {
        val absolutePath = file.absolutePath
        val extensionStartIndex = absolutePath.lastIndexOf('.')
        return absolutePath.substring(extensionStartIndex + 1, absolutePath.length)
    }

    companion object {
        protected var SCRIPT_MANAGER = ScriptEngineManager()
    }
}