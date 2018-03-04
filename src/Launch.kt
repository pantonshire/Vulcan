import builder.ItemBuilder
import builder.ModBuilder
import language.BlankLine
import language.Events
import language.Line
import parser.VulcanParser
import java.io.File

fun main(args : Array<String>) {
    val reader = File("testdata${File.separator}banana.vlcn").bufferedReader()
    val parser = VulcanParser()
    var lineNo = 0
    var validEvents = Events.none
    val lineList: MutableList<Line> = mutableListOf()

    reader.readLines().asSequence().forEach {
        if(lineNo == 0) {
            val words = it.split(Regex("\\s+"))
            if(words.size == 2 && words[0] == "type:") {
                validEvents = Events.getValidEvents(words[1])
                println("filetype ${words[1]}")
            }
        } else {
            val line = parser.parseLine(lineNo, it, validEvents)
            if(line !is BlankLine) {
                println("Line ${line.lineNo + 1}: ${line.pseudocode()}")
                lineList += line
            }
        }
        ++lineNo //Always increment, as this is only used for referencing erroneous lines
    }
    reader.close()

    ItemBuilder(lineList.toTypedArray()).build()

    ModBuilder.build()
}