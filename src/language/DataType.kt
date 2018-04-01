package language

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

    fun parse(value: String): Any {
        when(this) {
            BOOLEAN -> {
                if(value == "true" || value == "false") {
                    return value == "true"
                }
            }

            STRING -> {
                if(VulcanUtils.isValidInputString(value)) {
                    return VulcanUtils.sanitiseInputString(value)
                }
            }

            INTEGER -> {
                try {
                    return value.toInt()
                } catch(exception: NumberFormatException) {
                    throw IllegalArgumentException(typeError(value))
                }
            }

            FLOAT -> {
                try {
                    return value.toDouble()
                } catch(exception: NumberFormatException) {
                    throw IllegalArgumentException(typeError(value))
                }
            }

            VECTOR3 -> {
                if(value.startsWith("[") && value.endsWith("]")) {
                    val coordinates = value.substring(1, value.length - 1).split(",")
                    if(coordinates.size == 3) {
                        val castCoordinates: Array<Double> = arrayOf(0.0, 0.0, 0.0)
                        try {
                            for(i in 0..2) {
                                castCoordinates[i] = coordinates[i].trim().toDouble()
                            }
                        } catch(exception: NumberFormatException) {
                            throw IllegalArgumentException(typeError(value))
                        }

                        return Triple(castCoordinates[0], castCoordinates[1], castCoordinates[2])
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

