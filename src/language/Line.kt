package language

abstract class Line(val lineNo: Int) {

    abstract fun pseudocode(): String

    fun throwError(fileName: String, message: String) {
        throw IllegalArgumentException("Error in $fileName on line ${lineNo + 1}: $message")
    }
}