package language.objects

abstract class VulcanObject(val name: String) {

    abstract val validMessages: Map<String, Int>

    fun isValidMessage(message: String): Boolean = message in validMessages.keys

    internal abstract fun convertMessage(message: String, parameters: Array<String>, others: Array<VulcanObject>): String

    fun messageToJava(message: String, parameters: Array<String>, others: Array<VulcanObject>): String {
        if(!isValidMessage(message)) { throw IllegalArgumentException("unrecognised message $message") }
        if(parameters.size != validMessages[message]) { throw IllegalArgumentException("improper usage of $message") }
        return convertMessage(message, parameters, others)
    }
}