package builder

import language.*
import language.objects.VulcanObject

abstract class Builder(val fileName: String, type: String, val lines: Array<Line>, vararg globalObjectsVararg: VulcanObject) {

    /** All of the Vulcan Objects that can be referenced from anywhere. May include "self". */
    val globalObjects: Map<String, VulcanObject>

    val validEvents = Events.getValidEventNames(type)
    val validEventNameMap: Map<String, Event>
    var context = "default"
    val eventContent: HashMap<String, MutableList<String>> = hashMapOf()

    init {
        //Make map of valid contexts with their name as a key
        val map = hashMapOf<String, Event>()
        Events.getValidEvents(type).asSequence().forEach { map[it.name] = it }
        validEventNameMap = map

        //Initialise event content map
        validEvents.asSequence().forEach {
            eventContent[it] = mutableListOf()
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
                if(!(line is ConstructorLine || line is EventLine)) {
                    line.throwError(fileName,"no context defined")
                }
            }

            "constructor" -> {
                if(line is MessageLine) {
                    line.throwError(fileName,"cannot send messages in the current context")
                }
            }

            in validEvents -> {
                if(line is SetLine) {
                    line.throwError(fileName,"cannot set attributes in the current context")
                }
            }
        }
    }

    protected fun updateContext(line: Line) {
        if(line is ConstructorLine) {
            context = "constructor"
        } else if(line is EventLine) {
            val event = line.event.name
            if(event in validEvents) {
                context = event
            } else {
                line.throwError(fileName,"unrecognised event \"$event\"")
            }
        }
    }

    //* Returns all of the Vulcan Objects that can be referenced in the current context. */
    protected fun getAllVisibleObjects(currentEvent: Event): Map<String, VulcanObject> = globalObjects + currentEvent.parameters
}