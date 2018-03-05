package io

import java.io.File

object Directories {

    fun mkdir(vararg folders: String): Boolean {
        var path = ""
        folders.asSequence().forEach {
            if(path.isNotEmpty()) {
                path += File.separator
            }
            path += it
            val directory = File(path)
            if(!directory.exists() && !directory.mkdir()) {
                return false
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
        return home + separator + path.replace("/", separator).removePrefix(home)
    }
}