package builder

import language.Line

class BuildError(val line: Line, val description: String) {

    fun getErrorLog() = "Error on line ${line.lineNo + 1}: $description"
}