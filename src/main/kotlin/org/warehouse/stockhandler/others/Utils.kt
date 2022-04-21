package org.warehouse.stockhandler.others

import org.apache.logging.log4j.util.Strings
import java.io.File

object Utils {
    private const val classpathPrexix: String = "classpath:"
    private const val filepathPrexix: String = "file:"

    fun fileFromPath(path: String): File {
        if (path.trim().startsWith(classpathPrexix, true)) {
            val clearPath = path.replace(classpathPrexix, Strings.EMPTY)
            return File(javaClass.getResource(clearPath)?.toURI() ?: throw Exception())
        }
        return File(path.replace(filepathPrexix, Strings.EMPTY))
    }

}