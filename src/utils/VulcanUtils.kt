package utils

import language.DataType
import language.KEYWORDS
import language.objects.VulcanObject

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

    fun getVariable(name: String, visibleVariables: Map<String, VulcanObject>): VulcanObject? {
        val parts: Array<String> = name.split(".").toTypedArray()
        return when {
            parts.isEmpty() -> null
            parts.size == 1 -> visibleVariables[name]
            else -> {
                val root = visibleVariables[parts[0]]
                if(root == null) {
                    null
                } else {
                    getField(root, parts.copyOfRange(1, parts.size))
                }
            }
        }
    }

    private fun getField(container: VulcanObject, parts: Array<String>): VulcanObject? {
        if(parts.isEmpty()) {
            return null
        }

        val root: VulcanObject? = container.getFields()[parts[0]]

        return when {
            parts.size == 1 -> root
            root == null -> null
            else -> getField(root, parts.copyOfRange(1, parts.size))
        }
    }

    fun inferType(value: String, variables: Map<String, VulcanObject>): DataType {
        DataType.values().asSequence().forEach {
            try {
                it.toJava(value, variables)
                if(it == DataType.INTEGER) {
                    return DataType.FLOAT
                }
                return it
            } catch(exception: IllegalArgumentException) {
                //Not the correct data type
            }
        }

        throw IllegalArgumentException("failed to infer type of \"$value\"")
    }

    fun isValidVariableName(name: String): Boolean {
        if(name in KEYWORDS) {
            return false
        }

        var firstCharacter = true
        var containsLetter = false

        name.asSequence().forEach {
            val valid = it.isLetter() || (!firstCharacter && (it == '_' || it.isDigit()))
            if(!valid) {
                return false
            }

            if(!containsLetter && it.isLetter()) {
                containsLetter = true
            }

            //Numbers and underscores are only not allowed on the first pass
            if(firstCharacter) {
                firstCharacter = false
            }
        }

        return containsLetter
    }
}
