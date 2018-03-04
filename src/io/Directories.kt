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
}