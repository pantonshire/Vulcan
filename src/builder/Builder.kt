package builder

import language.*

abstract class Builder(type: String, val lines: Array<Line>) {

    val validEvents = Events.getValidEventNames(type)
    var state = "default"
    val errors: MutableList<BuildError> = mutableListOf()

    abstract fun build()

    internal fun checkForErrors(line: Line): BuildError? {
        return if(line is BlankLine) BuildError(line, "internal compilation error (this is bad!)") else when(state) {
            "default" -> {
                if(!(line is ConstructorLine || line is EventLine)) {
                    BuildError(line, "no context defined")
                } else {
                    null
                }
            }

            "constructor" -> {
                if(line is MessageLine) {
                    BuildError(line, "cannot send messages in the current context")
                } else {
                    null
                }
            }

            in validEvents -> {
                if(line is SetLine) {
                    BuildError(line, "cannot set attributes in the current context")
                } else {
                    null
                }
            }

            else -> null
        }
    }
}