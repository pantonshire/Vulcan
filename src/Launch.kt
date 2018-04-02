import builder.ItemBuilder
import builder.ModCompiler
import console.VConsole
import io.Directories
import io.FileReader
import language.BlankLine
import language.Behaviours
import language.Line
import parser.VulcanParser
import java.io.File

fun main(args: Array<String>) {

    build("Desktop/TestInput")
//    val split = VulcanParser.split("\thello   world\thow      are\t\t\tyou today?")
//    split.asSequence().forEach {
//        println("->$it")
//    }

}

private fun build(sourceDirectory: String) {
    val source = Directories.parseExternalDirectory(sourceDirectory)
    val sourceFiles = FileReader.getFilesInFolder(source)
    val vmod = "settings.vmod"

    if(sourceFiles.contains(vmod)) {
        val settings = FileReader.readTextFile(Directories.getDirectory(source, vmod))
        ModCompiler.instance.setModSettings(settings)
    } else {
        VConsole.out("Warning: no settings.vmod file was found. This file is necessary for setting your mod\'s name and id. Default values will be used.")
    }

    sourceFiles.asSequence().filter { it.endsWith(".vlcn") }.forEach {
        parseVulcanFile(Directories.getDirectory(source, it))
    }

    ModCompiler.instance.compileMod()
    VConsole.out("Successfully built mod!")
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
            val line = VulcanParser.parseLine(fileName, lineNo, it, validEvents)
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
