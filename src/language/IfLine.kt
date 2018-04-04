package language

class IfLine(lineNo: Int, val condition: String): Line(lineNo) {

    override fun pseudocode(): String = "if $condition"
}