package application

import builder.BlockBuilder
import builder.FoodBuilder
import builder.ItemBuilder
import builder.ModCompiler
import io.Directories
import io.FileReader
import language.BlankLine
import language.Behaviours
import language.Line
import language.VCException
import parser.VulcanParser
import parser.VulcanParserV3
import java.io.File

object VulcanBuild {

    var stacktrace = true
    var parserVersion = 3

    fun build(sourceDirectory: String) {
        try {
            //Create new compiler instance
            ModCompiler.newInstance()
            //Parse input source directory
            val source = Directories.parseExternalDirectory(sourceDirectory)
            //Set compiler input path
            ModCompiler.instance.inputPath = source
            //Get all source files
            val sourceFiles = FileReader.getFilesInFolder(source)
            //Mod settings file name
            val vmod = "settings.vmod"

            //Search for vmod file and update compiler settings
            if(sourceFiles.contains(vmod)) {
                val settings = FileReader.readTextFile(Directories.getDirectory(source, vmod))
                ModCompiler.instance.setModSettings(settings)
            } else {
                if(!UIHandler.confirm("No settings.vmod file was found. Generate the mod anyway?")) {
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

            ModCompiler.instance.compileMod()
            UIHandler.message("Successfully built mod ($files vlcn file(s) found).")

        } catch(exception: IllegalArgumentException) {
            UIHandler.error(exception.message ?: "No error description was provided :(")
            if(stacktrace) {
                exception.printStackTrace()
            }
        } catch(exception: VCException) {
            UIHandler.error(exception.message ?: "No error description was provided :(")
            if(stacktrace) {
                exception.printStackTrace()
            }
        }
    }

    private fun parseVulcanFile(vulcanFileDirectory: String) {
        val directory = vulcanFileDirectory.split(File.separator)
        var fileName = ""
        if(directory.isNotEmpty()) {
            fileName = directory[directory.size - 1]
        }

        var lineNo = 0
        var validEvents = Behaviours.none
        var type = ""
        val lineList: MutableList<Line> = mutableListOf()

        FileReader.readTextFile(vulcanFileDirectory).forEach {
            if(lineNo == 0) {
                val words = it.split(Regex("\\s+"))
                if(words.size == 2 && words[0] == "type:") {
                    type = words[1].toLowerCase().trim()
                    validEvents = Behaviours.getValidBehaviours(type)
                }
            } else {
                val line = when(parserVersion) {
                    1 ->    VulcanParser.parseLine(fileName, lineNo, it, validEvents)
                    3 ->    VulcanParserV3.parseLine(fileName, lineNo, it, validEvents)
                    else -> throw IllegalArgumentException("INVALID PARSER VERSION: $parserVersion")
                }

                if(line !is BlankLine) {
                    lineList += line
                }
            }
            ++lineNo
        }

        val lines = lineList.toTypedArray()

        var debugOut = ""
        lines.asSequence().forEach {
            if(debugOut.isNotEmpty()) {
                debugOut += "\n"
            }
            debugOut += "\"${it.pseudocode()}\""
        }
        UIHandler.message(debugOut)

        when(type) {
            "item" -> ItemBuilder(fileName, lines).build()
            "food" -> FoodBuilder(fileName, lines).build()
            "block" -> BlockBuilder(fileName, lines).build()
            else -> UIHandler.error("Unrecognised type for $fileName: \"$type\". This file will be skipped.")
        }
    }
}