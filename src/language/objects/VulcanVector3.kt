package language.objects

import language.DataType

class VulcanVector3(name: String, mutable: Boolean = false): VulcanObject(DataType.VECTOR3, name, mutable) {

    override val validMessages: Map<String, Int> = mapOf(
            Pair("offset", 2)
    )

    override fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String {
        when(message) {
            "offset" -> {
                if(parameters[0] == "by") {
                    val offset = type.toJava(parameters[1], variables)
                    return "$name = $name.add($offset);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }
        }

        //Should only be called if the message is registered as valid, but has no case in the when statement
        throw IllegalArgumentException("unsupported message $message")
    }
}