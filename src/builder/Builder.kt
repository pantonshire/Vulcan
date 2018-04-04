package builder

import language.*
import language.objects.VulcanObject
import utils.VulcanUtils

abstract class Builder(val fileName: String, type: String, val lines: Array<Line>, vararg defaultGlobalVariables: VulcanObject) {

    val attributes: HashMap<String, Attribute<Any>> = hashMapOf()

    /** All of the Vulcan Objects that can be referenced from anywhere. May include "self". */
    private val globalVariables: Map<String, VulcanObject>

    /** All of the local variables that can currently be referenced. */
    private val localVariables: HashMap<String, VulcanObject> = hashMapOf()

    private val validBehaviours: Map<String, Behaviour>
    private var context = "default"
    private val nest: MutableList<String> = mutableListOf() //All of the current nested statements. Nest.size will give the current depth
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

        //Make global variables map
        val mutableMap: HashMap<String, VulcanObject> = hashMapOf()
        defaultGlobalVariables.asSequence().forEach { mutableMap[it.name] = it }
        globalVariables = mutableMap.toMap()
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
            if(line.field in attributes) {
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
                val visibleVariables: Map<String, VulcanObject> = getVisibleVariables(behaviour)

                //Method calls
                if(line is ActionLine) {
                    val target = VulcanUtils.getVariable(line.target, visibleVariables)
                    if(target != null) {
                        if(target.isValidMessage(line.method)) {
                            var javaFunctionCall = ""
                            try {
                                //Convert line to java code
                                javaFunctionCall = target.messageToJava(line.method, line.arguments, visibleVariables)
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

                //Declaring variables
                else if(line is DeclarationLine) {
                    if(line.variable.name in visibleVariables) {
                        line.throwError(fileName, "another variable called ${line.variable.name} already exists")
                    } else {
                        try {

                            line.variable.depth = nest.size
                            localVariables[line.variable.name] = line.variable
                            val type = line.variable.type
                            val initialValueJava = type.toJava(line.initialValue, visibleVariables)
                            val prefix = if(!line.variable.mutable) "final " else ""
                            behaviourContent[context]?.add("$prefix${type.javaTypeName} ${line.variable.java} = $initialValueJava;")

                        } catch (exception: IllegalArgumentException) {
                            line.throwError(fileName, exception.message ?: "no error message provided")
                        }
                    }
                }

                //Assignment
                else if(line is SetLine) {
                    val variable = VulcanUtils.getVariable(line.field, visibleVariables)
                    if(variable != null) {
                        if(variable.mutable) {
                            try {
                                val value = variable.type.toJava(line.value, visibleVariables)
                                behaviourContent[context]?.add("${variable.java} = $value;")
                            } catch (exception: IllegalArgumentException) {
                                line.throwError(fileName, exception.message ?: "no error message provided")
                            }
                        } else {
                            line.throwError(fileName,"\"${line.field}\" is read-only; it cannot be reassigned")
                        }
                    } else {
                        line.throwError(fileName,"the variable \"${line.field}\" does not exist")
                    }
                }

                //If statements
                else if(line is IfLine) {
                    val condition = DataType.BOOLEAN.toJava(line.condition, visibleVariables)
                    behaviourContent[context]?.add("if($condition) {")
                    nest += "if"
                }

                //Terminators
                else if(line is TerminatorLine) {
                    if(nest.isNotEmpty() && line.type == nest.last()) {
                        behaviourContent[context]?.add("}")
                        nest.removeAt(nest.size - 1)
                        updateLocalVariables()
                    } else {
                        line.throwError(fileName, "invalid use of \"end\"")
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
//                if(line is SetLine) {
//                    line.throwError(fileName,"cannot set attributes in the current behaviour")
//                }
            }
        }
    }

    private fun updateContext(line: Line) {
        if(line is ConstructorLine) {
            context = "constructor"
        } else if(line is BehaviourLine) {
            val behaviour = line.behaviour.name
            if(behaviour in validBehaviours) {
                //Update context
                context = behaviour
                //Remove local variables from previous context
                localVariables.clear()
            } else {
                line.throwError(fileName,"unrecognised behaviour \"$behaviour\"")
            }
        }
    }

    //* Returns all of the Vulcan Objects that can be referenced in the current behaviour. */
    private fun getVisibleVariables(currentBehaviour: Behaviour): Map<String, VulcanObject> =
            globalVariables + localVariables + currentBehaviour.parameters

    private fun updateLocalVariables() {
        val depth = nest.size
        val toRemove: MutableList<String> = mutableListOf()
        localVariables.values.asSequence().forEach {
            if(it.depth > depth) {
                toRemove += it.name
            }
        }
        toRemove.asSequence().forEach {
            localVariables.remove(it)
        }
    }
}