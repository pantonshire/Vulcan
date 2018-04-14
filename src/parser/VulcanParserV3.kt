package parser

object VulcanParserV3 {

    fun splitLine(raw: String): Array<String> {

        val parse1 = raw.trim()
        val firstWord = parse1.split(Regex("\\s+"))[0].trim()
        val parse2 = parse1.replace(Regex("\\s+"), " ")
        val finalParse = parse2.removePrefix(firstWord).trim()
//        val finalParse = replaceStrings(parse2)

        when(firstWord) {

            "tell", "set" -> {
                val operation = splitByFirst(finalParse, "to")
                if(operation.first.isNotEmpty() && operation.second.isNotEmpty()) {
                    return arrayOf(firstWord, operation.first, "to", operation.second)
                } else {
                    throw IllegalArgumentException("Invalid syntax")
                }
            }

            "if", "while" -> {
                val suffix = if(firstWord == "if") "then" else "do"
                if(finalParse.endsWith(suffix)) {
                    val condition = finalParse.removeSuffix(suffix).trim()
                    if(condition.isNotEmpty()) {
                        return arrayOf(firstWord, condition, suffix)
                    } else {
                        throw IllegalArgumentException("Invalid syntax")
                    }
                } else {
                    throw IllegalArgumentException("Invalid syntax")
                }
            }

            "otherwise" -> {
                if(finalParse.startsWith("if") && finalParse.endsWith("then")) {
                    val condition = finalParse.removePrefix("if").removeSuffix("then").trim()
                    if(condition.isNotEmpty()) {
                        return arrayOf(firstWord, "if", condition, "then")
                    } else {
                        throw IllegalArgumentException("Invalid syntax")
                    }
                } else if(finalParse.isEmpty()) {
                    return arrayOf(firstWord)
                } else {
                    throw IllegalArgumentException("Invalid syntax")
                }
            }

            "repeat" -> {
                //For loop with implicit counter
                val suffix = "times"
                if(finalParse.endsWith(suffix)) {
                    val loops = finalParse.removeSuffix(suffix).trim()
                    if(loops.isNotEmpty()) {
                        return arrayOf(firstWord, loops, suffix)
                    } else {
                        throw IllegalArgumentException("Invalid syntax")
                    }
                }
                //For loop with explicit counter
                else {
                    val counterSeparator = "using a counter variable called"
                    val split = splitByFirst(finalParse, counterSeparator)
                    if(split.first.isNotEmpty() && split.first.endsWith(suffix) && split.second.isNotEmpty()) {
                        val loops = split.first.removeSuffix(suffix).trim()
                        if(loops.isNotEmpty()) {
                            return arrayOf(firstWord, loops, suffix, counterSeparator, split.second)
                        } else {
                            throw IllegalArgumentException("Invalid syntax")
                        }
                    } else {
                        throw IllegalArgumentException("Invalid syntax")
                    }
                }
            }

            "end" -> {
                if(finalParse.isNotEmpty()) {
                    return arrayOf(firstWord, finalParse)
                } else {
                    throw IllegalArgumentException("Invalid syntax")
                }
            }

            "new" -> {
                val split1 = splitByFirst(finalParse, "=")
                val value = split1.second
                val split2 = splitByFirst(split1.first, "called")
                val name = split2.second
                val mutability = when {
                    split2.first.endsWith("variable") -> "variable"
                    split2.first.endsWith("constant") -> "constant"
                    else -> throw IllegalArgumentException("Invalid syntax")
                }
                val mutable = mutability == "variable"
                val type = split2.first.removeSuffix(mutability).trim()

                if(type.isNotEmpty() && name.isNotEmpty() && value.isNotEmpty()) {
                    return arrayOf(firstWord, type, mutability, "called", name, "=", value)
                } else {
                    throw IllegalArgumentException("Invalid syntax")
                }
            }

            else -> {
                if(firstWord.isEmpty() && finalParse.isEmpty()) {
                    return arrayOf()
                } else if(firstWord.endsWith(":") && finalParse.isEmpty()) {
                    val behaviour = firstWord.removeSuffix(":").trim()
                    return arrayOf(behaviour)
                } else {
                    throw IllegalArgumentException("Invalid syntax")
                }
            }

        }

        return arrayOf()
    }

    /** Split the string by the first occurrence of the given separator. */
    private fun splitByFirst(raw: String, separator: String): Pair<String, String> {
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

            if(!split && currentString.endsWith(separator) && !quote && vector == 0 && parenthesis == 0) {
                first = currentString.removeSuffix(separator).trim()
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

}