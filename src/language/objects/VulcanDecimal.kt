package language.objects

import language.DataType

class VulcanDecimal(name: String, mutable: Boolean = false): VulcanObject(DataType.FLOAT, name, mutable) {

    override val validMessages: Map<String, Int> = mapOf(
            Pair("increase", 2),
            Pair("decrease", 2),
            Pair("negate", 0),
            Pair("round", 0)
    )

    override fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String {
        when(message) {
            "increase" -> {
                if(parameters[0] == "by") {
                    val amount = type.toJava(parameters[1], variables)
                    return "$name += $amount;"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            "decrease" -> {
                if(parameters[0] == "by") {
                    val amount = type.toJava(parameters[1], variables)
                    return "$name -= $amount;"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            "negate" -> return "$name = -$name;"

            "round" -> return "$name = Math.round($name);"
        }

        //Should only be called if the message is registered as valid, but has no case in the when statement
        throw IllegalArgumentException("unsupported message $message")
    }
}