package utils

//infix fun Boolean.xor(other: Boolean) = (this && !other) || (!this && other)

object VulcanUtils {

    fun sanitiseInputString(input: String): String =
            input.replace("\"", "").replace("\'", "\\" + "\'")

    fun isValidInputString(input: String): Boolean {
        if (input.first() != '\"' || input.last() != '\"') {
            return false
        }

        for (x in 1 until input.length - 1) {
            if (input[x] == '\"') {
                return false
            }
        }

        return true
    }
}
