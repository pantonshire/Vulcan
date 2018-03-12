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

fun main(args: Array<String>) {

    build("Desktop/TestInput")

}

private fun build(sourceDirectory: String) {
    val source = Directories.parseExternalDirectory(sourceDirectory)
    val sourceFiles = FileReader.getFilesInFolder(source)
    val vmod = "settings.vmod"

    if(sourceFiles.contains(vmod)) {
        val settings = FileReader.readTextFile(Directories.getDirectory(source, vmod))
        ModBuilder.setModSettings(settings)
    } else {
        VConsole.out("Warning: no settings.vmod file was found. This file is necessary for setting your mod\'s name and id. Default values will be used.")
    }

    sourceFiles.asSequence().filter { it.endsWith(".vlcn") }.forEach {
        parseVulcanFile(Directories.getDirectory(source, it))
    }

    ModBuilder.build()
    VConsole.out("Successfully built mod!")
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
        else -> VConsole.out("Unrecognised type: \"$type\". Skipping...")
    }
}