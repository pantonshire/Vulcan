package language

class TerminatorLine(lineNo: Int, val type: String): Line(lineNo) {

    override fun pseudocode(): String = "end $type"
}