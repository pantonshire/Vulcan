package language

class WhileLine(fileName: String, lineNo: Int, val condition: String): Line(fileName, lineNo) {

    override fun pseudocode(): String = "while $condition"
}