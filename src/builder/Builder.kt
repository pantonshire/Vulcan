package builder

import language.*

abstract class Builder(val fileName: String, type: String, val lines: Array<Line>) {

    val validEvents = Events.getValidEventNames(type)
    val validEventNameMap: Map<String, Event>
    var context = "default"
    val eventContent: HashMap<String, MutableList<String>> = hashMapOf()

    init {
        val map = hashMapOf<String, Event>()
        Events.getValidEvents(type).asSequence().forEach { map[it.name] = it }
        validEventNameMap = map

        validEvents.asSequence().forEach {
            eventContent[it] = mutableListOf()
        }
    }

    abstract fun build()

    internal fun checkForErrors(line: Line) {
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

    internal fun updateContext(line: Line) {
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
}