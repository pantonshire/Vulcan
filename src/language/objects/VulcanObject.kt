package language.objects

import language.DataType

abstract class VulcanObject(val type: DataType, val name: String, val java: String = name, val mutable: Boolean = false,
                            vararg fieldObjects: VulcanObject) {

    val fields: Map<String, VulcanObject>
    abstract val actions: Map<String, Int>

    init {
        val mutableFieldMap: HashMap<String, VulcanObject> = hashMapOf()
        fieldObjects.asSequence().forEach { mutableFieldMap[it.name] = it }
        fields = mutableFieldMap.toMap()
    }

    fun isValidMessage(message: String): Boolean = message in actions.keys

    protected abstract fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String

    fun messageToJava(message: String, parameters: Array<String>, others: Map<String, VulcanObject>): String {
        if(!isValidMessage(message)) { throw IllegalArgumentException("unrecognised message $message") }
        if(parameters.size != actions[message]) { throw IllegalArgumentException("improper usage of $message") }
        return convertMessage(message, parameters, others)
    }
}