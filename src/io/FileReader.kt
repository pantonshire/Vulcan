package io

import java.io.File

object FileReader {

    fun readTextFile(directory: String): Array<String> {
        val reader = File(directory).bufferedReader()
        val lines: MutableList<String> = mutableListOf()
        reader.readLines().asSequence().forEach { lines.add(it) }
        reader.close()
        return lines.toTypedArray()
    }
}