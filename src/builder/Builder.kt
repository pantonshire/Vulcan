package builder

import language.*

abstract class Builder(type: String, val lines: Array<Line>) {

    val validEvents = Events.getValidEventNames(type)
    val validEventNameMap: Map<String, Event>
    var context = "default"
    val eventContent: HashMap<String, MutableList<String>> = hashMapOf()
    val errors: MutableList<BuildError> = mutableListOf()

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
            errors += BuildError(line, "internal error (this is bad!)")
        }

        else when(context) {
            "default" -> {
                if(!(line is ConstructorLine || line is EventLine)) {
                    errors += BuildError(line, "no context defined")
                }
            }

            "constructor" -> {
                if(line is MessageLine) {
                    errors += BuildError(line, "cannot send messages in the current context")
                }
            }

            in validEvents -> {
                if(line is SetLine) {
                    errors += BuildError(line, "cannot set attributes in the current context")
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
                errors += BuildError(line, "unrecognised event \"$event\"")
            }
        }
    }
}