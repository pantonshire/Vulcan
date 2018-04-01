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
                    val coordinates = value.substring(1, value.length - 1).split(",")
                    if(coordinates.size == 3) {
                        val castCoordinates: Array<Float> = arrayOf(0f, 0f, 0f)
                        try {
                            for(i in 0..2) {
                                castCoordinates[i] = coordinates[i].trim().toFloat()
                            }
                        } catch(exception: NumberFormatException) {
                            throw IllegalArgumentException(typeError(value))
                        }

                        return "(new BlockPos(${castCoordinates[0]}f, ${castCoordinates[1]}f, ${castCoordinates[2]}f))"
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

