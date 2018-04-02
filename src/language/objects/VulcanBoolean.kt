package language.objects

import language.DataType

class VulcanBoolean(name: String, mutable: Boolean = false): VulcanObject(DataType.BOOLEAN, name, mutable) {

    override val validMessages: Map<String, Int> = mapOf(
            Pair("flip", 0)     //Flip boolean state
    )

    override fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String {
        when(message) {
            "flip" -> return "$name = !$name;"
        }

        throw IllegalArgumentException("unsupported message $message")
    }
}