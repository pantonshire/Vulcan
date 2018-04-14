package language

class TerminatorLine(fileName: String, lineNo: Int, val type: String): Line(fileName, lineNo) {

    override fun pseudocode(): String = "end $type"
}