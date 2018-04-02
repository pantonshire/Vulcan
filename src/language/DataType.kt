package language

import language.objects.VulcanObject
import utils.VulcanUtils

enum class DataType(val typeName: String) {

    BOOLEAN     ("boolean"),
    STRING      ("string"),
    INTEGER     ("integer"),
    FLOAT       ("decimal"),
    VECTOR3     ("vector"),
    ENTITY      ("entity"),
    PLAYER      ("player"),
    WORLD       ("world")
    ;

    val vectorPrefixes: Array<String> = arrayOf("x:", "y:", "z:")

    fun toJava(value: String, variables: Map<String, VulcanObject>): String {
        if(value in variables) {
            val variable = variables[value]!!
            return if(variable.type == this) {
                value
            } else if(variable.type == INTEGER && this == FLOAT) {
                "((float)$value)"
            } else if(variable.type == FLOAT && this == INTEGER) {
                "((int)$value)"
            } else if(variable.type == PLAYER && this == ENTITY) {
                "((EntityLivingBase)$value)"
            } else {
                throw IllegalArgumentException("cannot convert ${variable.type.name} to $name")
            }
        }

        when(this) {
            BOOLEAN -> {
                if(value == "true" || value == "false") {
                    return value
                }
            }

            STRING -> {
                if(VulcanUtils.isValidInputString(value)) {
                    return value
                }
            }

            INTEGER -> {
                try {
                    val asInt = value.toInt()
                    return asInt.toString()
                } catch(exception: NumberFormatException) {
                    throw IllegalArgumentException(typeError(value))
                }
            }

            FLOAT -> {
                try {
                    val asFloat = value.toFloat()
                    return "${asFloat}f"
                } catch(exception: NumberFormatException) {
                    throw IllegalArgumentException(typeError(value))
                }
            }

            VECTOR3 -> {
                if(value.startsWith("[") && value.endsWith("]")) {
                    val coordinatesIn = value
                            .substring(1, value.length - 1)
                            .replace(" ", "")
                            .split(",")

                    if(coordinatesIn.size == 3) {
                        val coordinates: Array<Float> = arrayOf(0f, 0f, 0f)

                        for(i in 0..2) {
                            var coordinate = coordinatesIn[i].trim()
                            val prefix = vectorPrefixes[i]
                            if(coordinate.startsWith(prefix, true)) {
                                coordinate = coordinate.substring(prefix.length)
                            }

                            try {
                                coordinates[i] = coordinate.toFloat()
                            } catch(exception: NumberFormatException) {
                                throw IllegalArgumentException(typeError(value))
                            }
                        }

                        return "(new BlockPos(${coordinates[0]}f, ${coordinates[1]}f, ${coordinates[2]}f))"
                    }
                }
            }

            else -> {
                throw IllegalArgumentException(typeError(value))
            }
        }

        throw IllegalArgumentException(typeError(value))
    }

    private fun typeError(value: String): String = "$value is not a valid $typeName"
}

