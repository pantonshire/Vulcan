package parser

object VulcanParserV3 {

    fun splitLine(raw: String): Array<String> {

        val parse1 = raw.trim()
        val firstWord = parse1.split(Regex("\\s+"))[0].trim()
        val finalParse = parse1.removePrefix(firstWord).trim()
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
                    return arrayOf(firstWord, condition, suffix)
                } else {
                    throw IllegalArgumentException("Invalid syntax")
                }
            }


        }

        return arrayOf()
    }

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