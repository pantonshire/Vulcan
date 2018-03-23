package language

import language.objects.VulcanObject

//TODO: Find a good way to specify the arguments of an action
class Action(val name: String, vararg arguments: Any) {

    val args = arguments

    fun checkArguments(argsIn: Array<String>, variables: Map<String, VulcanObject>) {
        if(args.size != argsIn.size) {
            throw IllegalArgumentException("\"$name\" action takes ${args.size} arguments but ${argsIn.size} were found")
        }

        for(x in 0 until argsIn.size) {
            when(args[x]) {
                is String -> {
                    if(argsIn[x] != args[x]) {
                        throw IllegalArgumentException("Expected the word \"${args[x]}\" but found \"${argsIn[x]}\"")
                    }
                }

                is VulcanObject -> {

                }
            }
        }
    }
}