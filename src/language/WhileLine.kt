package language

class WhileLine(lineNo: Int, val condition: String): Line(lineNo) {

    override fun pseudocode(): String = "while $condition"
}