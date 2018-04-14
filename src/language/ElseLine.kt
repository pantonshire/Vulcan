package language

class ElseLine(fileName: String, lineNo: Int): Line(fileName, lineNo) {

    override fun pseudocode(): String = "else"
}