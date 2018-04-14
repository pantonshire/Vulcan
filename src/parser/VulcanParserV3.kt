package parser

import application.UIHandler
import language.*
import language.objects.*

object VulcanParserV3 {


    fun parseLine(fileName: String, lineNo: Int, raw: String, validBehaviours: Array<Behaviour>): Line {

        //Parse 0: remove comments
        val parse0 = raw.replace(Regex("(//)(.*)"), "")
        //Parse 1: remove prefixing and suffixing whitespace
        val parse1 = parse0.trim()
        //Get first word, used to identify what type of line this is
        val firstWord = parse1.split(Regex("\\s+"))[0].trim()
        //Parse 2: remove first word and trim whitespace
        val parse2 = parse1.removePrefix(firstWord).trim()
        //Parse 3: compress whitespace
        val finalParse = parse2.replace(Regex("\\s+"), " ")

        when(firstWord) {

            //Actions and assignment
            "tell", "set" -> {
                val operation = splitByFirst(finalParse, "to")
                if(operation.first.isNotEmpty() && operation.second.isNotEmpty()) {

                    //Actions (tell x to y)
                    return if(firstWord == "tell") {
                        val actionAndArgs = splitActionArguments(parseVariable(operation.second))
                        val action = actionAndArgs[0]
                        val args = actionAndArgs.subList(1, actionAndArgs.size).toTypedArray()

                        ActionLine(
                                fileName,
                                lineNo,
                                parseVariable(operation.first),
                                action,
                                args
                        )
                    }

                    //Assignment (set x to y)
                    else {
                        SetLine(
                                fileName,
                                lineNo,
                                parseVariable(operation.first),
                                parseVariable(operation.second)
                        )
                    }

                } else {
                    throw VCException(fileName, lineNo, "Invalid syntax")
                }
            }

            //If statements and while loops
            "if", "while" -> {
                val suffix = if(firstWord == "if") "then" else "do"
                if(finalParse.endsWith(suffix)) {

                    val condition = parseVariable(finalParse.removeSuffix(suffix).trim())

                    if(condition.isNotEmpty()) {

                        //If statement
                        return if(firstWord == "if") {
                            IfLine(
                                    fileName,
                                    lineNo,
                                    condition
                            )
                        }

                        //While loop
                        else {
                            WhileLine(
                                    fileName,
                                    lineNo,
                                    condition
                            )
                        }

                    } else {
                        throw VCException(fileName, lineNo, "Invalid syntax")
                    }

                } else {
                    throw VCException(fileName, lineNo, "Invalid syntax")
                }
            }

            //Else if and else
            "otherwise" -> {
                if(finalParse.startsWith("if") && finalParse.endsWith("then")) {
                    val condition = parseVariable(finalParse.removePrefix("if").removeSuffix("then").trim())
                    if(condition.isNotEmpty()) {
                        return ElseIfLine(fileName, lineNo, condition)
                    } else {
                        throw VCException(fileName, lineNo, "Invalid syntax")
                    }
                } else if(finalParse.isEmpty()) {
                    return ElseLine(fileName, lineNo)
                } else {
                    throw VCException(fileName, lineNo, "Invalid syntax")
                }
            }

            //For loop
            "repeat" -> {
                //For loop with implicit counter
                val suffix = "times"
                if(finalParse.endsWith(suffix)) {
                    val loops = parseVariable(finalParse.removeSuffix(suffix).trim())
                    if(loops.isNotEmpty()) {
                        return ForLine(fileName, lineNo, loops, null)
                    } else {
                        throw VCException(fileName, lineNo, "Invalid syntax")
                    }
                }
                //For loop with explicit counter
                else {
                    val counterSeparator = "using a counter variable called"
                    val split = splitByFirst(finalParse, counterSeparator)
                    if(split.first.isNotEmpty() && split.first.endsWith(suffix) && split.second.isNotEmpty()) {
                        val loops = parseVariable(split.first.removeSuffix(suffix).trim())
                        if(loops.isNotEmpty()) {
                            return ForLine(fileName, lineNo, loops, split.second)
                        } else {
                            throw VCException(fileName, lineNo, "Invalid syntax")
                        }
                    } else {
                        throw VCException(fileName, lineNo, "Invalid syntax")
                    }
                }
            }

            //Terminator
            "end" -> {
                if(finalParse.isNotEmpty()) {
                    return TerminatorLine(fileName, lineNo, finalParse)
                } else {
                    throw VCException(fileName, lineNo, "Invalid syntax")
                }
            }

            //Declaration
            "new" -> {
                val split1 = splitByFirst(finalParse, "=", whitespace = false)
                val value = parseVariable(split1.second).trim()
//                UIHandler.message("\"$value\"")
                val split2 = splitByFirst(split1.first, "called")
                val name = split2.second
                val mutability = when {
                    split2.first.endsWith("variable") -> "variable"
                    split2.first.endsWith("constant") -> "constant"
                    else -> throw VCException(fileName, lineNo, "Invalid syntax")
                }
                val mutable = mutability == "variable"
                val typeName = split2.first.removeSuffix(mutability).trim()

                if(typeName.isNotEmpty() && name.isNotEmpty() && value.isNotEmpty()) {

                    //Work out the data type
                    var type: DataType? = null
                    for(possibleType in DataType.values()) {
                        if(typeName == possibleType.typeName) {
                            type = possibleType
                            break
                        }
                    }

                    if(type == null) {
                        throw VCException(fileName, lineNo, "${typeName[1]} is not a valid type")
                    }

                    //Make a new VulcanObject
                    val newVariable: VulcanObject = when(type) {
                        DataType.BOOLEAN  -> VulcanBoolean(name, mutable = mutable)
                        DataType.STRING   -> VulcanString(name, mutable = mutable)
                        DataType.INTEGER  -> VulcanInteger(name, mutable = mutable)
                        DataType.FLOAT    -> VulcanDecimal(name, mutable = mutable)
                        DataType.VECTOR3  -> VulcanVector3(name, mutable = mutable)
                        DataType.ENTITY   -> LivingEntity(name, mutable = mutable)
                        DataType.PLAYER   -> Player(name, mutable = mutable)
                        DataType.WORLD    -> World(name, mutable = mutable)
                        else              ->   null
                    } ?: throw VCException(fileName, lineNo, "declaring new ${type.typeName} variables is not yet supported")

                    return DeclarationLine(
                            fileName,
                            lineNo,
                            newVariable,
                            value
                    )

                } else {
                    throw VCException(fileName, lineNo, "Invalid syntax")
                }
            }

            else -> {
                //Blank lines
                if(firstWord.isEmpty() && finalParse.isEmpty()) {
                    return BlankLine(fileName, lineNo)
                }
                //Behaviours and constructor
                else if(firstWord.endsWith(":") && finalParse.isEmpty()) {

                    val behaviour = firstWord.removeSuffix(":").trim()
                    val behaviourNames = Behaviours.toNameMap(validBehaviours)

                    return if(behaviour == "attributes") {
                        ConstructorLine(fileName, lineNo)
                    } else if(behaviour in behaviourNames) {
                        BehaviourLine(fileName, lineNo, behaviourNames[behaviour]!!)
                    } else {
                        throw VCException(fileName, lineNo, "Invalid syntax")
                    }

                }
                //Invalid line
                else {
                    throw VCException(fileName, lineNo, "Invalid syntax")
                }
            }

        }
    }


    /** Split the string by the first occurrence of the given separator. If whitespace is true, then there
     * must be a whitespace character following the separator. */
    private fun splitByFirst(raw: String, separator: String, whitespace: Boolean = true): Pair<String, String> {
        val actualSeparator = if(whitespace) " $separator " else separator
        var quote = false
        var vector = 0
        var parenthesis = 0
        var currentString = ""
        var split = false
        var first = ""
        var second = ""

        raw.asSequence().forEach {
            when(it) {
                '\"', '“', '”'  -> quote = !quote
                '['             -> ++vector
                ']'             -> --vector
                '('             -> ++parenthesis
                ')'             -> --parenthesis
            }

            currentString += it

            if(!split && currentString.endsWith(actualSeparator) && !quote && vector == 0 && parenthesis == 0) {
                first = currentString.removeSuffix(actualSeparator).trim()
                split = true
                currentString = ""
            }
        }

        if(!split) {
            first = raw.trim()
        } else {
            second = currentString.trim()
        }

        return Pair(first, second)
    }


    private fun splitActionArguments(raw: String): List<String> {
        val args: MutableList<String> = mutableListOf()
        var quote = false
        var vector = 0
        var parenthesis = 0
        var currentString = ""

        raw.asSequence().forEach {
            when(it) {
                '\"', '“', '”'  -> quote = !quote
                '['             -> ++vector
                ']'             -> --vector
                '('             -> ++parenthesis
                ')'             -> --parenthesis
            }

            if(it.isWhitespace() && currentString.trim().isNotEmpty() && !quote && vector == 0 && parenthesis == 0) {
                args += currentString.trim()
                currentString = ""
            } else {
                currentString += it
            }
        }

        if(currentString.trim().isNotEmpty()) {
            args += currentString.trim()
        }

        return args
    }


    private fun parseVariable(rawVariable: String): String {
        return rawVariable
                //Field references
                .replace(Regex("(\'s)(\\s+)"), ".")
                //Less than
                .replace(Regex("(\\s+)(is less than|is smaller than)(\\s+)"), "<")
                .replace(Regex("(\\s*)(<)(\\s*)"), "<")
                //Greater than
                .replace(Regex("(\\s+)(is more than|is greater than)(\\s+)"), ">")
                .replace(Regex("(\\s*)(>)(\\s*)"), "<")
                //Not equal
                .replace(Regex("(\\s+)(isn\'t equal to|does not equal)(\\s+)"), "!=")
                //Equal
                .replace(Regex("(\\s+)(is equal to|equals)(\\s+)"), "==")
                //And
                .replace(Regex("(\\s+)(and)(\\s+)"), "&&")
                //Or
                .replace(Regex("(\\s+)(or)(\\s+)"), "||")
                //Not
                .replace(Regex("(\\s+|^)(not)(\\s+)"), "!!")
                //Remove unnecessary whitespace
                .trim()
    }


//    fun splitLine(raw: String): Array<String> {
//
//        //Parse 1: remove prefixing and suffixing whitespace
//        val parse1 = raw.trim()
//        //Get first word, used to identify what type of line this is
//        val firstWord = parse1.split(Regex("\\s+"))[0].trim()
//        //Parse 2: remove first word and trim whitespace
//        val parse2 = parse1.removePrefix(firstWord).trim()
//        //Parse 3: compress whitespace
//        val finalParse = parse2.replace(Regex("\\s+"), " ")
//
//        when(firstWord) {
//
//            "tell", "set" -> {
//                val operation = splitByFirst(finalParse, "to")
//                if(operation.first.isNotEmpty() && operation.second.isNotEmpty()) {
//                    return arrayOf(firstWord, operation.first, "to", operation.second)
//                } else {
//                    throw IllegalArgumentException("Invalid syntax")
//                }
//            }
//
//            "if", "while" -> {
//                val suffix = if(firstWord == "if") "then" else "do"
//                if(finalParse.endsWith(suffix)) {
//                    val condition = finalParse.removeSuffix(suffix).trim()
//                    if(condition.isNotEmpty()) {
//                        return arrayOf(firstWord, condition, suffix)
//                    } else {
//                        throw IllegalArgumentException("Invalid syntax")
//                    }
//                } else {
//                    throw IllegalArgumentException("Invalid syntax")
//                }
//            }
//
//            "otherwise" -> {
//                if(finalParse.startsWith("if") && finalParse.endsWith("then")) {
//                    val condition = finalParse.removePrefix("if").removeSuffix("then").trim()
//                    if(condition.isNotEmpty()) {
//                        return arrayOf(firstWord, "if", condition, "then")
//                    } else {
//                        throw IllegalArgumentException("Invalid syntax")
//                    }
//                } else if(finalParse.isEmpty()) {
//                    return arrayOf(firstWord)
//                } else {
//                    throw IllegalArgumentException("Invalid syntax")
//                }
//            }
//
//            "repeat" -> {
//                //For loop with implicit counter
//                val suffix = "times"
//                if(finalParse.endsWith(suffix)) {
//                    val loops = finalParse.removeSuffix(suffix).trim()
//                    if(loops.isNotEmpty()) {
//                        return arrayOf(firstWord, loops, suffix)
//                    } else {
//                        throw IllegalArgumentException("Invalid syntax")
//                    }
//                }
//                //For loop with explicit counter
//                else {
//                    val counterSeparator = "using a counter variable called"
//                    val split = splitByFirst(finalParse, counterSeparator)
//                    if(split.first.isNotEmpty() && split.first.endsWith(suffix) && split.second.isNotEmpty()) {
//                        val loops = split.first.removeSuffix(suffix).trim()
//                        if(loops.isNotEmpty()) {
//                            return arrayOf(firstWord, loops, suffix, counterSeparator, split.second)
//                        } else {
//                            throw IllegalArgumentException("Invalid syntax")
//                        }
//                    } else {
//                        throw IllegalArgumentException("Invalid syntax")
//                    }
//                }
//            }
//
//            "end" -> {
//                if(finalParse.isNotEmpty()) {
//                    return arrayOf(firstWord, finalParse)
//                } else {
//                    throw IllegalArgumentException("Invalid syntax")
//                }
//            }
//
//            "new" -> {
//                val split1 = splitByFirst(finalParse, "=")
//                val value = split1.second
//                val split2 = splitByFirst(split1.first, "called")
//                val name = split2.second
//                val mutability = when {
//                    split2.first.endsWith("variable") -> "variable"
//                    split2.first.endsWith("constant") -> "constant"
//                    else -> throw IllegalArgumentException("Invalid syntax")
//                }
//                val mutable = mutability == "variable"
//                val type = split2.first.removeSuffix(mutability).trim()
//
//                if(type.isNotEmpty() && name.isNotEmpty() && value.isNotEmpty()) {
//                    return arrayOf(firstWord, type, mutability, "called", name, "=", value)
//                } else {
//                    throw IllegalArgumentException("Invalid syntax")
//                }
//            }
//
//            else -> {
//                if(firstWord.isEmpty() && finalParse.isEmpty()) {
//                    return arrayOf()
//                } else if(firstWord.endsWith(":") && finalParse.isEmpty()) {
//                    val behaviour = firstWord.removeSuffix(":").trim()
//                    return arrayOf(behaviour)
//                } else {
//                    throw IllegalArgumentException("Invalid syntax")
//                }
//            }
//
//        }
//
//        return arrayOf()
//    }
}