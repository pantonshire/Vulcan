package language

class IfLine(fileName: String, lineNo: Int, val condition: String): Line(fileName, lineNo) {

    override fun pseudocode(): String = "if $condition"
}