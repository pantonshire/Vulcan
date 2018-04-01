package builder

import language.*
import language.objects.VulcanObject

abstract class Builder(val fileName: String, type: String, val lines: Array<Line>, vararg globalObjectsVararg: VulcanObject) {

    val attributes: HashMap<String, Attribute<Any>> = hashMapOf()

    /** All of the Vulcan Objects that can be referenced from anywhere. May include "self". */
    val globalObjects: Map<String, VulcanObject>

    val validBehaviours: Map<String, Behaviour>
    var context = "default"
    val behaviourContent: HashMap<String, MutableList<String>> = hashMapOf()

    init {
        //Make map of valid contexts with their name as a key
        val map = hashMapOf<String, Behaviour>()
        Behaviours.getValidBehaviours(type).asSequence().forEach { map[it.name] = it }
        validBehaviours = map

        //Initialise behaviour content map
        validBehaviours.keys.asSequence().forEach {
            behaviourContent[it] = mutableListOf()
        }

        //Make global objects map
        val mutableObjectsMap: HashMap<String, VulcanObject> = hashMapOf()
        globalObjectsVararg.asSequence().forEach { mutableObjectsMap[it.name] = it }
        globalObjects = mutableObjectsMap.toMap()
    }

    fun build() {
        for(line in lines) {
            checkForErrors(line)
            updateContext(line)
            processLine(line)
        }

        passToNext()
    }

    abstract fun passToNext()

    private fun processLine(line: Line) {
        if(context == "constructor" && line is SetLine) {
            if (line.field in attributes) {
                try {
                    attributes[line.field]!!.set(line.value)
                } catch (exception: IllegalArgumentException) {
                    line.throwError(fileName, exception.message ?: "no error message provided")
                }
            }
        }

        else if(context in validBehaviours) {
            val behaviour = validBehaviours[context]
            if(behaviour != null) {
                val visibleObjects: Map<String, VulcanObject> = getAllVisibleObjects(behaviour)

                if(line is ActionLine) {
                    if(visibleObjects.containsKey(line.target)) {
                        val target = visibleObjects[line.target]!!
                        if(target.isValidMessage(line.method)) {
                            var javaFunctionCall = ""
                            try {
                                javaFunctionCall = target.messageToJava(line.method, line.arguments, behaviour.parameters)
                            } catch(exception: IllegalArgumentException) {
                                line.throwError(fileName,exception.message ?: "no error message was provided")
                            }

                            if(javaFunctionCall.isNotEmpty()) {
                                behaviourContent[context]?.add(javaFunctionCall)
                            }
                        } else {
                            line.throwError(fileName,"invalid message \"${line.method}\"")
                        }
                    } else {
                        line.throwError(fileName,"invalid target for message \"${line.target}\"")
                    }
                }
            }
        }
    }

    private fun checkForErrors(line: Line) {
        if(line is BlankLine) {
            line.throwError(fileName,"internal error (this is bad!)")
        }

        else when(context) {
            "default" -> {
                if(!(line is ConstructorLine || line is BehaviourLine)) {
                    line.throwError(fileName,"no behaviour defined")
                }
            }

            "constructor" -> {
                if(line is ActionLine) {
                    line.throwError(fileName,"cannot send messages in the current behaviour")
                }
            }

            in validBehaviours -> {
                if(line is SetLine) {
                    line.throwError(fileName,"cannot set attributes in the current behaviour")
                }
            }
        }
    }

    private fun updateContext(line: Line) {
        if(line is ConstructorLine) {
            context = "constructor"
        } else if(line is BehaviourLine) {
            val behaviour = line.behaviour.name
            if(behaviour in validBehaviours) {
                context = behaviour
            } else {
                line.throwError(fileName,"unrecognised behaviour \"$behaviour\"")
            }
        }
    }

    //* Returns all of the Vulcan Objects that can be referenced in the current behaviour. */
    protected fun getAllVisibleObjects(currentEvent: Behaviour): Map<String, VulcanObject> = globalObjects + currentEvent.parameters
}