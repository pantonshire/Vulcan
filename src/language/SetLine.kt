package language

class SetLine(fileName: String, lineNo: Int, val field: String, val value: String): Line(fileName, lineNo) {

    override fun pseudocode(): String = "$field = $value"
}