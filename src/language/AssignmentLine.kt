package language

class AssignmentLine(lineNo: Int, val variable: String, val value: String): Line(lineNo) {

    override fun pseudocode(): String = "set $variable to $value"
}