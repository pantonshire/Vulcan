package language

class MessageLine(lineNo: Int, val target: String, val method: String, val arguments: Array<String>): Line(lineNo) {

    override fun pseudocode(): String = "$target.$method()"
}