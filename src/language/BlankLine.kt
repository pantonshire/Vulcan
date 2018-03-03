package language

class BlankLine(lineNo: Int): Line(lineNo) {

    override fun pseudocode(): String = "-"
}