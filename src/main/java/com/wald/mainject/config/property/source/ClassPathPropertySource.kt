package com.wald.mainject.config.property.source

import com.wald.mainject.config.property.PropertySource
import java.io.File
import java.nio.file.Files
import kotlin.script.experimental.jvm.util.classPathFromTypicalResourceUrls


/**
 * @author vkosolapov
 * @since
 */
class ClassPathPropertySource(val path: String) : PersistentPropertySource() {
    override fun doGetAll(): Map<String, Any> {
        val resource = ClassPathPropertySource::class.java.getResource(path).let { File(it!!.toURI()) }
        return PropertiesFile(resource).properties
    }
}