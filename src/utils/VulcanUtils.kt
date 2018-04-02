package utils

import language.KEYWORDS

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

    fun isValidVariableName(name: String): Boolean {
        if(name in KEYWORDS) {
            return false
        }

        name.asSequence().forEach {
            if(!it.isLetter()) {
                return false
            }
        }

        return true
    }
}
