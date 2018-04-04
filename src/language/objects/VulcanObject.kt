package language.objects

import language.DataType

abstract class VulcanObject(val type: DataType, val name: String, val java: String = name, val customAssignmentSyntax: String? = null, val mutable: Boolean = false) {

    abstract val actions: Map<String, Int>

    //Which block of code this variable is contained to
    var depth: Int = 0

    fun getFields(): Map<String, VulcanObject> {
        val fields: HashMap<String, VulcanObject> = hashMapOf()
        val fieldsArray = FieldManager.getFields(this)
        fieldsArray.asSequence().forEach {
            fields[it.name] = it
        }
        return fields.toMap()
    }

    fun isValidMessage(message: String): Boolean = message in actions.keys

    protected abstract fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String

    fun messageToJava(message: String, parameters: Array<String>, others: Map<String, VulcanObject>): String {
        if(!isValidMessage(message)) { throw IllegalArgumentException("unrecognised message $message") }
        if(parameters.size != actions[message]) { throw IllegalArgumentException("improper usage of $message") }
        return convertMessage(message, parameters, others)
    }
}