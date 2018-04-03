package language.objects

import language.DataType

class VulcanString(name: String, java: String = name, mutable: Boolean = false): VulcanObject(DataType.STRING, name, java, mutable) {

    override val actions: Map<String, Int> = mapOf(
            Pair("trim", 0),
            Pair("remove", 1),
            Pair("replace", 3)
    )

    override fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String {
        when(message) {

            "trim" -> return "$java = $java.trim();"

            "remove" -> {
                val removeString = type.toJava(parameters[1], variables)
                return "$java = $java.replace($removeString, \"\");"
            }

            "replace" -> {
                if(parameters[1] == "with") {
                    val oldString = type.toJava(parameters[0], variables)
                    val newString = type.toJava(parameters[2], variables)
                    return "$java = $java.replace($oldString, $newString);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

        }

        //Should only be called if the message is registered as valid, but has no case in the when statement
        throw IllegalArgumentException("unsupported message $message")
    }
}