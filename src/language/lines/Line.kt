package language.lines

abstract class Line(val fileName: String, val lineNo: Int) {

    abstract fun pseudocode(): String

    fun throwError(message: String): Nothing {
        throw IllegalArgumentException("Error in $fileName on line ${lineNo + 1}: $message")
    }
}