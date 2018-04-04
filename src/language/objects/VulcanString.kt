package language.objects

import language.DataType

class VulcanString(name: String, java: String = name, cas: String? = null, mutable: Boolean = false): VulcanObject(DataType.STRING, name, java, cas, mutable) {

    override val actions: Map<String, Int> = mapOf(
            Pair("trim", 0),
            Pair("remove", 1),
            Pair("replace", 3)
    )

    override fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String {
        when(message) {

            "trim" -> {
                if(mutable) {
                    return "$java = $java.trim();"
                } else {
                    throw IllegalArgumentException("cannot tell $name to trim since it is immutable")
                }
            }

            "remove" -> {
                if(mutable) {
                    val removeString = type.toJava(parameters[1], variables)
                    return "$java = $java.replace($removeString, \"\");"
                } else {
                    throw IllegalArgumentException("cannot tell $name to remove since it is immutable")
                }
            }

            "replace" -> {
                if(mutable) {
                    if (parameters[1] == "with") {
                        val oldString = type.toJava(parameters[0], variables)
                        val newString = type.toJava(parameters[2], variables)
                        return "$java = $java.replace($oldString, $newString);"
                    } else {
                        throw IllegalArgumentException("invalid syntax")
                    }
                } else {
                    throw IllegalArgumentException("cannot tell $name to replace since it is immutable")
                }
            }

        }

        //Should only be called if the message is registered as valid, but has no case in the when statement
        throw IllegalArgumentException("unsupported message $message")
    }
}