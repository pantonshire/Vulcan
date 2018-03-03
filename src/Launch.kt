import io.JavaWriter
import language.BlankLine
import language.Events
import parser.VulcanParser
import java.io.File

fun main(args : Array<String>) {
    val reader = File("testdata${File.separator}banana.vlcn").bufferedReader()
    val parser = VulcanParser()
    var lineNo = 0
    var validEvents = Events.none

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
            }
        }
        ++lineNo //Always increment, as this is only used for referencing erroneous lines
    }
    reader.close()

//    JavaWriter().writeFile("testdata${File.separator}javatest.java",
//            "public class Test {¶   public static void main(String[] args) {¶" +
//                    "       System.out.println(\"Hello world!\");¶   }¶}")
}