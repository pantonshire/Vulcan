package builder

import language.*
import language.objects.VulcanObject

abstract class Builder(val fileName: String, type: String, val lines: Array<Line>, vararg globalObjectsVararg: VulcanObject) {

    /** All of the Vulcan Objects that can be referenced from anywhere. May include "self". */
    val globalObjects: Map<String, VulcanObject>

    val validBehaviours = Behaviours.getValidBehaviourNames(type)
    val validBehaviourNameMap: Map<String, Behaviour>
    var context = "default"
    val behaviourContent: HashMap<String, MutableList<String>> = hashMapOf()

    init {
        //Make map of valid contexts with their name as a key
        val map = hashMapOf<String, Behaviour>()
        Behaviours.getValidBehaviours(type).asSequence().forEach { map[it.name] = it }
        validBehaviourNameMap = map

        //Initialise behaviour content map
        validBehaviours.asSequence().forEach {
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

    protected abstract fun processLine(line: Line)

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

    protected fun updateContext(line: Line) {
        if(line is ConstructorLine) {
            context = "constructor"
        } else if(line is BehaviourLine) {
            val event = line.behaviour.name
            if(event in validBehaviours) {
                context = event
            } else {
                line.throwError(fileName,"unrecognised behaviour \"$event\"")
            }
        }
    }

    //* Returns all of the Vulcan Objects that can be referenced in the current behaviour. */
    protected fun getAllVisibleObjects(currentEvent: Behaviour): Map<String, VulcanObject> = globalObjects + currentEvent.parameters
}