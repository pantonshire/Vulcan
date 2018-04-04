package language

import language.objects.VulcanObject
import utils.VulcanUtils

enum class DataType(val typeName: String, val javaTypeName: String) {

    BOOLEAN     ("boolean", "boolean"),
    STRING      ("string", "String"),
    INTEGER     ("integer", "int"),
    FLOAT       ("decimal", "float"),
    VECTOR3     ("vector", "BlockPos"),
    ENTITY      ("entity", "EntityLivingBase"),
    PLAYER      ("player", "EntityPlayer"),
    WORLD       ("world", "World"),
    //TODO: Add support for the following
    POTION      ("effect", "PotionEffect"),
    ITEM        ("item", "Item"),
    BLOCK       ("block", "Block")
    ;

    private val vectorPrefixes: Array<String> = arrayOf("x:", "y:", "z:")

    fun toJava(value: String, variables: Map<String, VulcanObject>): String {
        //Arithmetic
        if(this == INTEGER || this == FLOAT) {

        }

        //Boolean expressions (world's messiest code)
        //TODO: CLEAN THIS UP
        //TODO: && and || are the wrong way around
        //TODO: Brackets e.g. (a or b) and (i < j)
        if(this == BOOLEAN) {
            //and
            val splitAnd = value.split("&&")
            if(splitAnd.size > 1) {
                var statementJava = "("
                splitAnd.asSequence().forEach {
                    if(statementJava.length > 1) {
                        statementJava += " && "
                    }
                    statementJava += toJava(it.trim(), variables)
                }
                statementJava += ")"
                return statementJava
            } else {
                //or
                val splitOr = value.split("||")
                if(splitOr.size > 1) {
                    var statementJava = "("
                    splitOr.asSequence().forEach {
                        if(statementJava.length > 1) {
                            statementJava += " || "
                        }
                        statementJava += toJava(it.trim(), variables)
                    }
                    statementJava += ")"
                    return statementJava
                } else {
                    //isn't
                    val splitIsNot = value.split("!=")
                    if(splitIsNot.size == 2) {
                        val typeA = VulcanUtils.inferType(splitIsNot[0], variables)
                        val typeB = VulcanUtils.inferType(splitIsNot[1], variables)
                        if(typeA.comparableWith(typeB) && typeB.comparableWith(typeA)) {
                            return "(${splitIsNot[0]} != ${splitIsNot[1]})"
                        } else {
                            throw IllegalArgumentException("cannot compare ${typeA.typeName} with ${typeB.typeName}")
                        }
                    } else if(splitIsNot.size > 2) {
                        throw IllegalArgumentException("invalid syntax")
                    } else {
                        //is
                        val splitIs = value.split("==")
                        if(splitIs.size == 2) {
                            val typeA = VulcanUtils.inferType(splitIs[0], variables)
                            val typeB = VulcanUtils.inferType(splitIs[1], variables)
                            if(typeA.comparableWith(typeB) && typeB.comparableWith(typeA)) {
                                return "(${splitIs[0]} == ${splitIs[1]})"
                            } else {
                                throw IllegalArgumentException("cannot compare ${typeA.typeName} with ${typeB.typeName}")
                            }
                        } else if(splitIs.size > 2) {
                            throw IllegalArgumentException("invalid syntax")
                        } else {
                            //less than
                            val splitLess = value.split("<")
                            if(splitLess.size == 2) {
                                val typeA = VulcanUtils.inferType(splitLess[0], variables)
                                val typeB = VulcanUtils.inferType(splitLess[1], variables)
                                if(typeA.isNumerical() && typeB.isNumerical()) {
                                    return "(${splitLess[0]} < ${splitLess[1]})"
                                } else {
                                    throw IllegalArgumentException("left and right hand side of < must both be numerical (integers or decimals)")
                                }
                            } else if(splitLess.size > 2) {
                                throw IllegalArgumentException("invalid syntax")
                            } else {
                                //greater than
                                val splitGreater = value.split(">")
                                if(splitLess.size == 2) {
                                    val typeA = VulcanUtils.inferType(splitGreater[0], variables)
                                    val typeB = VulcanUtils.inferType(splitGreater[1], variables)
                                    if(typeA.isNumerical() && typeB.isNumerical()) {
                                        return "(${splitGreater[0]} > ${splitGreater[1]})"
                                    } else {
                                        throw IllegalArgumentException("left and right hand side of > must both be numerical (integers or decimals)")
                                    }
                                } else if(splitGreater.size > 2) {
                                    throw IllegalArgumentException("invalid syntax")
                                }
                            }
                        }
                    }
                }
            }
        }

        //Search for variable names and check data type
        val variable = VulcanUtils.getVariable(value, variables)
        if(variable != null) {
            return if(variable.type == this) {
                variable.java
            } else if(variable.type == INTEGER && this == FLOAT) {
                "((float)${variable.java})"
            } else if(variable.type == FLOAT && this == INTEGER) {
                "((int)${variable.java})"
            } else if(variable.type == PLAYER && this == ENTITY) {
                "((EntityLivingBase)${variable.java})"
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

            /** Syntax: [x:32, y:12.5, z:-34.6]
             * The "x:", "y:" and "z:" are optional and not case-sensitive.
             * They must be in the correct order.
             * All whitespace is ignored. */
            VECTOR3 -> {
                if(value.startsWith("[") && value.endsWith("]")) {
                    val coordinatesIn = value
                            .substring(1, value.length - 1)
                            .replace(Regex("\\s+"), "")
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

    fun comparableWith(other: DataType): Boolean {
        return this == other || when(this) {
            INTEGER     -> other == FLOAT
            FLOAT       -> other == INTEGER
            ENTITY      -> other == PLAYER
            PLAYER      -> other == ENTITY
            else        -> false
        }
    }

    fun isNumerical(): Boolean = this == INTEGER || this == FLOAT

    private fun typeError(value: String): String = "$value is not a valid $typeName"
}

