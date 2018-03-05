package io

import java.io.File

object Directories {

    fun mkdir(vararg folders: String): Boolean {
        var separate = false
        var path = ""
        folders.asSequence().forEach {
            if(separate) {
                path += File.separator
            } else {
                separate = true
            }
            path += it
            if(path.isNotEmpty()) {
                val directory = File(path)
                if(!directory.exists()) {
                    if(!directory.mkdir()) {
                        return false
                    }
                }
            }
        }

        return true
    }

    fun getDirectory(vararg parts: String): String {
        var directory = ""
        parts.asSequence().forEach {
            if(directory.isNotEmpty()) {
                directory += File.separator
            }
            directory += it
        }
        return directory
    }

    fun parseExternalDirectory(path: String): String {
        val home = System.getProperty("user.home")
        val separator = File.separator
        return home + separator + path.replace("/", separator).removePrefix(home + separator)
    }

    fun exists(path: String): Boolean = File(path).exists()
}