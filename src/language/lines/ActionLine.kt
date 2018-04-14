package language.lines

class ActionLine(fileName: String, lineNo: Int, val target: String, val method: String, val arguments: Array<String>): Line(fileName, lineNo) {

    override fun pseudocode(): String = "$target.$method${argumentsString()}"

    private fun argumentsString(): String {
        var argstr = ""
        arguments.asSequence().forEach {
            if(argstr.isNotEmpty()) {
                argstr += ", "
            }
            argstr += it
        }
        return "($argstr)"
    }
}