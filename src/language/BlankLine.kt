package language

class BlankLine(fileName: String, lineNo: Int): Line(fileName, lineNo) {

    override fun pseudocode(): String = "-"
}