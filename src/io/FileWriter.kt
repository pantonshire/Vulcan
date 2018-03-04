package io

import java.io.File

object FileWriter {

    fun writeFile(directory: String, content: String) {
        writeFile(directory, *content.split("¶").toTypedArray())
    }

    fun writeFile(directory: String, vararg content: String) {
        val file = File(directory)
        val writer = file.bufferedWriter()
        content.asSequence().forEach {
            val line = it.replace("»", "    ")
            writer.write(line)
            writer.newLine()
        }
        writer.close()
    }
}