package language.objects

import language.DataType

class VulcanVector3(name: String, java: String = name, mutable: Boolean = false): VulcanObject(DataType.VECTOR3, name, java, mutable,

        VulcanDecimal   ("x",       "$java.getX()"       ),
        VulcanDecimal   ("y",       "$java.getY()"       ),
        VulcanDecimal   ("z",       "$java.getZ()"       )

) {

    override val actions: Map<String, Int> = mapOf(
            Pair("offset", 2)
    )

    override fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String {
        when(message) {
            "offset" -> {
                if(mutable) {
                    if (parameters[0] == "by") {
                        val offset = type.toJava(parameters[1], variables)
                        return "$java = $java.add($offset);"
                    } else {
                        throw IllegalArgumentException("invalid syntax")
                    }
                } else {
                    throw IllegalArgumentException("cannot tell $name to offset since it is immutable")
                }
            }
        }

        //Should only be called if the message is registered as valid, but has no case in the when statement
        throw IllegalArgumentException("unsupported message $message")
    }
}