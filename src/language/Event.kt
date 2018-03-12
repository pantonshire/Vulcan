package language

import language.objects.VulcanObject

class Event(val name: String, vararg eventParameters: VulcanObject) {

    val parameters: Map<String, VulcanObject>

    init {
        val mutableParameterMap: HashMap<String, VulcanObject> = hashMapOf()
        eventParameters.asSequence().forEach { mutableParameterMap[it.name] = it }
        parameters = mutableParameterMap.toMap()
    }

    fun functionName() = name + ":"
}