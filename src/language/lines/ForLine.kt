package language.lines

class ForLine(fileName: String, lineNo: Int, val loops: String, val counter: String?): Line(fileName, lineNo) {

    override fun pseudocode(): String = "for $loops times"
}