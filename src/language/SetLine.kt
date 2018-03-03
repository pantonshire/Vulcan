package language

class SetLine(lineNo: Int, val field: String, val value: String): Line(lineNo) {

    override fun pseudocode(): String = "$field = $value"
}