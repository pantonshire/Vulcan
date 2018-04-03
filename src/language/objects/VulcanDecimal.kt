package language.objects

import language.DataType

class VulcanDecimal(name: String, java: String = name, mutable: Boolean = false): VulcanObject(DataType.FLOAT, name, java, mutable,

        VulcanInteger   ("integer_part",       "((int)$java)"           ),
        VulcanDecimal   ("decimal_part",       "($java - (int)$java)"   )

) {

    override val actions: Map<String, Int> = mapOf(
            Pair("increase", 2),
            Pair("decrease", 2),
            Pair("negate", 0),
            Pair("round", 0)
    )

    override fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String {
        when(message) {
            "increase" -> {
                if(mutable) {
                    if (parameters[0] == "by") {
                        val amount = type.toJava(parameters[1], variables)
                        return "$java += $amount;"
                    } else {
                        throw IllegalArgumentException("invalid syntax")
                    }
                } else {
                    throw IllegalArgumentException("cannot tell $name to increase since it is immutable")
                }
            }

            "decrease" -> {
                if(mutable) {
                    if (parameters[0] == "by") {
                        val amount = type.toJava(parameters[1], variables)
                        return "$java -= $amount;"
                    } else {
                        throw IllegalArgumentException("invalid syntax")
                    }
                } else {
                    throw IllegalArgumentException("cannot tell $name to decrease since it is immutable")
                }
            }

            "negate" -> {
                if(mutable) {
                    return "$java = -$java;"
                } else {
                    throw IllegalArgumentException("cannot tell $name to negate since it is immutable")
                }
            }

            "round" -> {
                if(mutable) {
                    return "$java = Math.round($java);"
                } else {
                    throw IllegalArgumentException("cannot tell $name to round since it is immutable")
                }
            }
        }

        //Should only be called if the message is registered as valid, but has no case in the when statement
        throw IllegalArgumentException("unsupported message $message")
    }
}