package language

import language.objects.VulcanObject

class Event(val name: String, vararg parameters: VulcanObject) {

    val parameterNames = parameters

    fun functionName() = name + ":"
}