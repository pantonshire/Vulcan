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

    fun getFilesInFolder(directory: String): Array<String> {
        val folder = File(directory)
        val files: MutableList<String> = mutableListOf()

        if(folder.exists() && folder.isDirectory) {
            folder.listFiles().asSequence().filter { it.isFile }.mapTo(files, { it.name })
        }

        return files.toTypedArray()
    }
}