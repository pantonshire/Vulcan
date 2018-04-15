package parser

import builder.VCException
import language.Behaviour
import language.Behaviours
import language.DataType
import language.lines.*
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

                        val actionAndArgs: MutableList<String> = mutableListOf()
                        splitActionArguments(operation.second.trim()).asSequence()
                                .mapTo(actionAndArgs, { rawVariable: String -> parseVariableV2(rawVariable) })

//                        val actionAndArgs = splitActionArguments(parseVariableV2(operation.second))
                        val action = actionAndArgs[0]
                        val args = actionAndArgs.subList(1, actionAndArgs.size).toTypedArray()

                        ActionLine(
                                fileName,
                                lineNo,
                                parseVariableV2(operation.first),
                                action,
                                args
                        )
                    }

                    //Assignment (set x to y)
                    else {
                        SetLine(
                                fileName,
                                lineNo,
                                parseVariableV2(operation.first),
                                parseVariableV2(operation.second)
                        )
                    }

                } else {
                    throw VCException(fileName, lineNo, "invalid syntax")
                }
            }

            //If statements and while loops
            "if", "while" -> {
                val suffix = if(firstWord == "if") "then" else "do"
                if(finalParse.endsWith(suffix)) {

                    val condition = parseVariableV2(finalParse.removeSuffix(suffix).trim())

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
                        throw VCException(fileName, lineNo, "invalid syntax: no boolean condition is provided")
                    }

                } else {
                    throw VCException(fileName, lineNo, "invalid syntax")
                }
            }

            //Else if and else
            "otherwise" -> {
                if(finalParse.startsWith("if") && finalParse.endsWith("then")) {
                    val condition = parseVariableV2(finalParse.removePrefix("if").removeSuffix("then").trim())
                    if(condition.isNotEmpty()) {
                        return ElseIfLine(fileName, lineNo, condition)
                    } else {
                        throw VCException(fileName, lineNo, "invalid syntax: no boolean condition is provided")
                    }
                } else if(finalParse.isEmpty()) {
                    return ElseLine(fileName, lineNo)
                } else {
                    throw VCException(fileName, lineNo, "invalid syntax")
                }
            }

            //For loop
            "repeat" -> {
                //For loop with implicit counter
                val suffix = "times"
                if(finalParse.endsWith(suffix)) {
                    val loops = parseVariableV2(finalParse.removeSuffix(suffix).trim())
                    if(loops.isNotEmpty()) {
                        return ForLine(fileName, lineNo, loops, null)
                    } else {
                        throw VCException(fileName, lineNo, "invalid syntax: number of loops is not specified")
                    }
                }
                //For loop with explicit counter
                else {
                    val counterSeparator = "using a counter variable called"
                    val split = splitByFirst(finalParse, counterSeparator)
                    if(split.first.isNotEmpty() && split.first.endsWith(suffix) && split.second.isNotEmpty()) {
                        val loops = parseVariableV2(split.first.removeSuffix(suffix).trim())
                        if(loops.isNotEmpty()) {
                            return ForLine(fileName, lineNo, loops, split.second)
                        } else {
                            throw VCException(fileName, lineNo, "invalid syntax: number of loops is not specified")
                        }
                    } else {
                        throw VCException(fileName, lineNo, "invalid syntax")
                    }
                }
            }

            //Terminator
            "end" -> {
                if(finalParse.isNotEmpty()) {
                    return TerminatorLine(fileName, lineNo, finalParse)
                } else {
                    throw VCException(fileName, lineNo, "invalid syntax: terminator type is not specified")
                }
            }

            //Declaration
            "new" -> {
                val split1 = splitByFirst(finalParse, "=", whitespace = false)
                val value = parseVariableV2(split1.second).trim()
                val split2 = splitByFirst(split1.first, "called")
                val name = split2.second
                val mutability = when {
                    split2.first.endsWith("variable") -> "variable"
                    split2.first.endsWith("constant") -> "constant"
                    else -> throw VCException(fileName, lineNo, "invalid syntax: must be either variable or constant")
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
                    throw VCException(fileName, lineNo, "invalid syntax")
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
                        throw VCException(fileName, lineNo, "invalid syntax: \"$behaviour\" is not a valid behaviour")
                    }

                }
                //Invalid line
                else {
                    throw VCException(fileName, lineNo, "invalid syntax")
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


    @Deprecated("Does not support subtraction, will match strings")
    private fun parseVariable(rawVariable: String): String {
        return rawVariable
                //Field references
                .replace(Regex("(\'s)(\\s+)"), ".")
                //Less than
                .replace(Regex("(\\s*)(<)(\\s*)"), "<")
                .replace(Regex("(\\s+)(is less than|is smaller than)(\\s+)"), "<")
                //Greater than
                .replace(Regex("(\\s*)(>)(\\s*)"), ">")
                .replace(Regex("(\\s+)(is more than|is greater than)(\\s+)"), ">")
                //Not equal
                .replace(Regex("(\\s+)(isn\'t equal to|does not equal)(\\s+)"), "!=")
                //Equal
                .replace(Regex("(\\s+)(is equal to|equals)(\\s+)"), "==")
                //And not
                .replace(Regex("(\\s+)(and)(\\s+)(not)(\\s+)"), "&&!!")
                //Or not
                .replace(Regex("(\\s+)(or)(\\s+)(not)(\\s+)"), "||!!")
                //Not
                .replace(Regex("(\\s+|^)(not)(\\s+)"), "!!")
                //And
                .replace(Regex("(\\s+)(and)(\\s+)"), "&&")
                //Or
                .replace(Regex("(\\s+)(or)(\\s+)"), "||")
                //Addition
                .replace(Regex("(\\s*)(\\+)(\\s*)"), "++")
                //Subtraction
                //TODO: Currently breaks negative numbers
//                .replace(Regex("(\\s*)(-)(\\s*)"), "--")
                //Multiplication
                .replace(Regex("(\\s*)(\\*)(\\s*)"), "**")
                //Division
                .replace(Regex("(\\s*)(/)(\\s*)"), "//")
                .replace(Regex("(\\s*)(÷)(\\s*)"), "//")
                //Powers
                .replace(Regex("(\\s*)(\\^)(\\s*)"), "^^")
                //Modulo
                .replace(Regex("(\\s+)(mod)(\\s+)"), "%%")
                //Remove unnecessary whitespace
                .trim()
    }


    fun parseVariableV2(rawVariable: String): String {

        var parsed = ""
        var currentPart = ""
        var quote = false
        var last: Char? = null

        rawVariable.asSequence().forEach {
            if(it == '\"' || it == '“' || it == '”') {
                quote = !quote
            }

            if(!it.isWhitespace() || currentPart.isNotEmpty()) {
                currentPart += it
            }

            if(!quote) {

                val newPart = when {
                    //Field references
                    currentPart.endsWith("\'s ")                -> "${currentPart.removeSuffix("\'s ")}."
                    //Less than
                    currentPart.endsWith(" is less than ")      -> "${currentPart.removeSuffix(" is less than ")}<"
                    currentPart.endsWith(" is smaller than ")   -> "${currentPart.removeSuffix(" is smaller than ")}<"
                    it == '<'                                         -> "${currentPart.removeSuffix("<").trim()}<"
                    //Greater than
                    currentPart.endsWith(" is greater than ")   -> "${currentPart.removeSuffix(" is greater than ")}>"
                    currentPart.endsWith(" is more than ")      -> "${currentPart.removeSuffix(" is more than ")}>"
                    it == '>'                                         -> "${currentPart.removeSuffix(">").trim()}>"
                    //Not equal
                    currentPart.endsWith(" is not equal to ")   -> "${currentPart.removeSuffix(" is not equal to ")}!="
                    currentPart.endsWith(" does not equal ")    -> "${currentPart.removeSuffix(" does not equal ")}!="
                    //Equal
                    currentPart.endsWith(" is equal to ")       -> "${currentPart.removeSuffix(" is equal to ")}=="
                    currentPart.endsWith(" equals ")            -> "${currentPart.removeSuffix(" equals ")}=="
                    //And
                    currentPart.endsWith(" and ")               -> "${currentPart.removeSuffix(" and ")}&&"
                    //Or
                    currentPart.endsWith(" or ")                -> "${currentPart.removeSuffix(" or ")}||"
                    //Not
                    currentPart == "not "                             -> "!!"
                    currentPart == "not("                             -> "!!("
                    //Addition
                    it == '+'                                         -> "${currentPart.removeSuffix("+").trim()}++"
                    //Multiplication
                    it == '*'                                         -> "${currentPart.removeSuffix("*").trim()}**"
                    //Division
                    it == '/'                                         -> "${currentPart.removeSuffix("/").trim()}//"
                    //Powers
                    it == '^'                                         -> "${currentPart.removeSuffix("^").trim()}^^"
                    //Modulo
                    currentPart.endsWith(" mod ")               -> "${currentPart.removeSuffix(" mod ")}%%"
                    //Subtraction
                    it == '-'
                        && last != null
                        && (last!!.isDigit()
                            || last!!.isLetter()
                            || last == ')'
                            || last == '_')                           -> "${currentPart.removeSuffix("-").trim()}--"
                    //Anything else
                    else                                              -> ""
                }.trim()

                if(newPart.isNotEmpty()) {
//                    println("PART: \"$currentPart\" -> \"$newPart\"")
                    parsed += newPart
                    currentPart = ""
                    last = null
                }

            }

            if(!it.isWhitespace()) {
                last = it
            }
        }

        parsed += currentPart.trim()
        return parsed.trim()
    }

}