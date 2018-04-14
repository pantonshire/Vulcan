package language

class ElseIfLine(fileName: String, lineNo: Int, val condition: String): Line(fileName, lineNo) {

    override fun pseudocode(): String = "else if $condition"
}