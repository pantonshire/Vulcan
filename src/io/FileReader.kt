package io

import java.io.File

object FileReader {

    fun readTextFile(directory: String): Array<String> {
        val lines: MutableList<String> = mutableListOf()
        val file = File(directory)
        if(file.exists()) {
            val reader = file.bufferedReader()
            reader.readLines().asSequence().forEach { lines.add(it) }
            reader.close()
        }
        return lines.toTypedArray()
    }
}