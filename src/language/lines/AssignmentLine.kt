package language.lines

class AssignmentLine(fileName: String, lineNo: Int, val variable: String, val value: String): Line(fileName, lineNo) {

    override fun pseudocode(): String = "set $variable to $value"
}