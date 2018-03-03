package io

import java.io.File

class JavaWriter {

    fun writeFile(directory: String, content: String) {
        val file = File(directory)
        val writer = file.bufferedWriter()
        content.split("¶").asSequence().forEach {
            writer.write(it)
            writer.newLine()
        }
        writer.close()
    }
}