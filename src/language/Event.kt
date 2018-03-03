package language

class Event(val name: String, vararg parameters: String) {

    val parameterNames = parameters

    fun functionName() = name + ":"
}