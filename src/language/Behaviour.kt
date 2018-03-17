package language

import language.objects.VulcanObject

class Behaviour(val name: String, vararg parameterList: VulcanObject) {

    val parameters: Map<String, VulcanObject>

    init {
        val mutableParameterMap: HashMap<String, VulcanObject> = hashMapOf()
        parameterList.asSequence().forEach { mutableParameterMap[it.name] = it }
        parameters = mutableParameterMap.toMap()
    }
}