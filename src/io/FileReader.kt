package io

import application.UIHandler
import java.io.File
import java.io.InputStreamReader
import java.io.BufferedReader
import java.lang.Exception


object FileReader {

    fun readTextFile(directory: String): Array<String> {
        val lines: MutableList<String> = mutableListOf()
        val file = File(directory)
        if(file.exists()) {
            val reader = file.bufferedReader()
            reader.readLines().asSequence().forEach { lines.add(it) }
            reader.close()
        } else {
            UIHandler.error("File not found: $directory")
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

    fun readResourceFile(name: String): Array<String> {
        val lines: MutableList<String> = mutableListOf()
        try {
            val inputStream = javaClass.getResourceAsStream(name)
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.readLines().asSequence().forEach { lines.add(it) }
            reader.close()
        } catch(exception: Exception) {
            UIHandler.error("Error reading internal file $name")
        }

        return lines.toTypedArray()
    }
}