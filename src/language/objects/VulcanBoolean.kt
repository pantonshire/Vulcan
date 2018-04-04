package language.objects

import language.DataType

class VulcanBoolean(name: String, java: String = name, cas: String? = null, mutable: Boolean = false): VulcanObject(DataType.BOOLEAN, name, java, cas, mutable) {

    override val actions: Map<String, Int> = mapOf(
            Pair("flip", 0)     //Flip boolean state
    )

    override fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String {
        when(message) {
            "flip" -> {
                if(mutable) {
                    return "$java = !$java;"
                } else {
                    throw IllegalArgumentException("cannot tell $name to flip since it is immutable")
                }
            }
        }

        throw IllegalArgumentException("unsupported message $message")
    }
}