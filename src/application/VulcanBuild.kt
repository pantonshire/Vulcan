package application

import builder.ItemBuilder
import builder.ModBuilder
import console.VConsole
import io.Directories
import io.FileReader
import language.BlankLine
import language.Events
import language.Line
import parser.VulcanParser
import java.io.File

object VulcanBuild {

    fun build(sourceDirectory: String) {
        try {
            val source = Directories.parseExternalDirectory(sourceDirectory)
            val sourceFiles = FileReader.getFilesInFolder(source)
            val vmod = "settings.vmod"

            if(sourceFiles.contains(vmod)) {
                val settings = FileReader.readTextFile(Directories.getDirectory(source, vmod))
                ModBuilder.setModSettings(settings)
            } else {
                if(UIHandler.confirm("No settings.vmod file was found. Generate the mod anyway?")) {
                    ModBuilder.setDefaultSettings()
                } else {
                    return
                }
            }

            var files = 0

            sourceFiles.asSequence().filter { it.endsWith(".vlcn") }.forEach {
                parseVulcanFile(Directories.getDirectory(source, it))
                files += 1
            }

            if(files == 0) {
                UIHandler.error("No vlcn files were found.")
                return
            }

            ModBuilder.build()
            UIHandler.message("Successfully built mod ($files vlcn file(s) found).")

        } catch(exception: IllegalArgumentException) {
            UIHandler.error(exception.message ?: "No error description was provided :(")
        }
    }

    private fun parseVulcanFile(vulcanFileDirectory: String) {
        val directory = vulcanFileDirectory.split(File.separator)
        var fileName = ""
        if(directory.isNotEmpty()) {
            fileName = directory[directory.size - 1]
        }

        var lineNo = 0
        var validEvents = Events.none
        var type = ""
        val lineList: MutableList<Line> = mutableListOf()

        FileReader.readTextFile(vulcanFileDirectory).forEach {
            if(lineNo == 0) {
                val words = it.split(Regex("\\s+"))
                if(words.size == 2 && words[0] == "type:") {
                    type = words[1].toLowerCase().trim()
                    validEvents = Events.getValidEvents(type)
                }
            } else {
                val line = VulcanParser.parseLine(lineNo, it, validEvents)
                if(line !is BlankLine) {
                    lineList += line
                }
            }
            ++lineNo
        }

        val lines = lineList.toTypedArray()

        when(type) {
            "item" -> ItemBuilder(fileName, lines).build()
            else -> UIHandler.error("Unrecognised type for $fileName: \"$type\". This file will be skipped.")
        }
    }
}