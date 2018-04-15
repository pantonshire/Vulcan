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
    ITEM        ("item", "Item"),
    BLOCK       ("block", "Block"),
    //TODO: Add support for the following
    POTION      ("effect", "PotionEffect")
    ;

    private val vectorPrefixes: Array<String> = arrayOf("x:", "y:", "z:")

    fun toJava(value: String, variables: Map<String, VulcanObject>): String {

        //Arithmetic
        if(this.isNumerical()) {

            //Powers
            val splitPow = VulcanUtils.split(value, "^^")
            if(splitPow.size == 2) {

                val typeA = VulcanUtils.inferType(splitPow[0], variables)
                val typeB = VulcanUtils.inferType(splitPow[1], variables)

                if(typeA.isNumerical() && typeB.isNumerical()) {
                    val left = typeA.toJava(splitPow[0].trim(), variables)
                    val right = typeB.toJava(splitPow[1].trim(), variables)
                    return "Math.pow($left, $right)"
                }

                else {
                    throw IllegalArgumentException("cannot raise ${typeA.typeName} to ${typeB.typeName}")
                }
            } else if(splitPow.size > 2) {
                throw IllegalArgumentException("invalid syntax: cannot chain exponents")
            }

            //Multiplication
            val splitMply = VulcanUtils.split(value, "**")
            if(splitMply.size > 1) {
                var statementJava = ""
                splitMply.asSequence().forEach {
                    if(statementJava.isNotEmpty()) {
                        statementJava += " * "
                    }
                    statementJava += toJava(it.trim(), variables)
                }
                return statementJava
            }

            //Division
            val splitDiv = VulcanUtils.split(value, "//")
            if(splitDiv.size > 1) {
                var statementJava = ""
                splitDiv.asSequence().forEach {
                    if(statementJava.isNotEmpty()) {
                        statementJava += " / "
                    }
                    statementJava += toJava(it.trim(), variables)
                }
                return statementJava
            }

            //Modulo
            val splitMod = VulcanUtils.split(value, "%%")
            if(splitMod.size > 1) {
                var statementJava = "("
                splitMod.asSequence().forEach {
                    if(statementJava.length > 1) {
                        statementJava += " % "
                    }
                    statementJava += toJava(it.trim(), variables)
                }
                statementJava += ")"
                return statementJava
            }

            //Addition
            val splitAdd = VulcanUtils.split(value, "++")
            if(splitAdd.size > 1) {
                var statementJava = ""
                splitAdd.asSequence().forEach {
                    if(statementJava.isNotEmpty()) {
                        statementJava += " + "
                    }
                    statementJava += toJava(it.trim(), variables)
                }
                return statementJava
            }

            //Subtraction
            val splitSub = VulcanUtils.split(value, "++")
            if(splitSub.size > 1) {
                var statementJava = ""
                splitSub.asSequence().forEach {
                    if(statementJava.isNotEmpty()) {
                        statementJava += " - "
                    }
                    statementJava += toJava(it.trim(), variables)
                }
                return statementJava
            }

        }

        //Boolean expressions
        if(this == BOOLEAN) {

            //or
            val splitOr = VulcanUtils.split(value, "||")
            if (splitOr.size > 1) {
                var statementJava = ""
                splitOr.asSequence().forEach {
                    if (statementJava.isNotEmpty()) {
                        statementJava += " || "
                    }
                    statementJava += toJava(it.trim(), variables)
                }
                return statementJava
            }

            //and
            val splitAnd = VulcanUtils.split(value, "&&")
            if (splitAnd.size > 1) {
                var statementJava = ""
                splitAnd.asSequence().forEach {
                    if (statementJava.isNotEmpty()) {
                        statementJava += " && "
                    }
                    statementJava += toJava(it.trim(), variables)
                }
                return statementJava
            }

            //not
            val splitNot = VulcanUtils.split(value, "!!")
            if (splitNot.size == 2) {
                if(splitNot[0].isNotEmpty()) {
                    throw IllegalArgumentException("invalid syntax")
                }
                val statementJava = toJava(splitNot[1].trim(), variables)
                return "!($statementJava)"
            } else if (splitNot.size > 2) {
                throw IllegalArgumentException("invalid syntax")
            }

            //isn't
            val splitIsNot = VulcanUtils.split(value, "!=")
            if (splitIsNot.size == 2) {
                val typeA = VulcanUtils.inferType(splitIsNot[0], variables)
                val typeB = VulcanUtils.inferType(splitIsNot[1], variables)
                if (typeA.comparableWith(typeB) && typeB.comparableWith(typeA)) {
                    val left = typeA.toJava(splitIsNot[0].trim(), variables)
                    val right = typeB.toJava(splitIsNot[1].trim(), variables)
                    return "$left != $right"
                } else {
                    throw IllegalArgumentException("cannot compare ${typeA.typeName} with ${typeB.typeName}")
                }
            } else if (splitIsNot.size > 2) {
                throw IllegalArgumentException("invalid syntax")
            }

            //is
            val splitIs = VulcanUtils.split(value, "==")
            if (splitIs.size == 2) {
                val typeA = VulcanUtils.inferType(splitIs[0], variables)
                val typeB = VulcanUtils.inferType(splitIs[1], variables)
                if (typeA.comparableWith(typeB) && typeB.comparableWith(typeA)) {
                    val left = typeA.toJava(splitIs[0].trim(), variables)
                    val right = typeB.toJava(splitIs[1].trim(), variables)
                    return "$left == $right"
                } else {
                    throw IllegalArgumentException("cannot compare ${typeA.typeName} with ${typeB.typeName}")
                }
            } else if (splitIs.size > 2) {
                throw IllegalArgumentException("invalid syntax")
            }

            //less than
            val splitLess = VulcanUtils.split(value, "<")
            if (splitLess.size == 2) {
                val typeA = VulcanUtils.inferType(splitLess[0], variables)
                val typeB = VulcanUtils.inferType(splitLess[1], variables)
                if (typeA.isNumerical() && typeB.isNumerical()) {
                    val left = typeA.toJava(splitLess[0].trim(), variables)
                    val right = typeB.toJava(splitLess[1].trim(), variables)
                    return "$left < $right"
                } else {
                    throw IllegalArgumentException("left and right hand side of < must both be numerical (integers or decimals)")
                }
            } else if (splitLess.size > 2) {
                throw IllegalArgumentException("invalid syntax")
            }

            //greater than
            val splitGreater = VulcanUtils.split(value, ">")
            if (splitGreater.size == 2) {
                val typeA = VulcanUtils.inferType(splitGreater[0], variables)
                val typeB = VulcanUtils.inferType(splitGreater[1], variables)
                if (typeA.isNumerical() && typeB.isNumerical()) {
                    val left = typeA.toJava(splitGreater[0].trim(), variables)
                    val right = typeB.toJava(splitGreater[1].trim(), variables)
                    return "$left > $right"
                } else {
                    throw IllegalArgumentException("left and right hand side of > must both be numerical (integers or decimals)")
                }
            } else if (splitGreater.size > 2) {
                throw IllegalArgumentException("invalid syntax")
            }
        }

        //Parentheses
        if(value.startsWith("(") && value.endsWith(")")) {
            val contentJava = toJava(value.removePrefix("(").removeSuffix(")").trim(), variables)
            return "($contentJava)"
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
                throw IllegalArgumentException("cannot convert ${variable.type.typeName} to $typeName")
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

