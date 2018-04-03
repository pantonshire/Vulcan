package language.objects

import language.DataType

class VulcanBoolean(name: String, java: String = name, mutable: Boolean = false): VulcanObject(DataType.BOOLEAN, name, java, mutable) {

    override val actions: Map<String, Int> = mapOf(
            Pair("flip", 0)     //Flip boolean state
    )

    override fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String {
        when(message) {
            "flip" -> return "$java = !$java;"
        }

        throw IllegalArgumentException("unsupported message $message")
    }
}