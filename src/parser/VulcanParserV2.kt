package parser

object VulcanParserV2 {

    val linePrefixes: Array<String> = arrayOf(
            "attributes:",
            "new",
            "set",
            "tell",
            "if",
            "while",
            "repeat",
            "end"
    )

    private val lineStructures: Map<String, Array<String>> = mapOf(
            Pair("new",     arrayOf("[1]", "[1]", "called", "[1]", "=", "[n]")),
            Pair("if",      arrayOf("[n]", "then"))
//            Pair("new",     "[1] variable|constant called [1] = [n]"),
//            Pair("set",     "[n] to [n]"),
//            Pair("tell",    "[n] to [n]"),
//            Pair("if",      "[n] then"),
//            Pair("while",   "[n] do"),
//            Pair("repeat",  "[n] times ?using counter variable called [n]")
    )


    fun splitLine(raw: String): List<String> {

        val commandWord = getCommandWord(raw)
        val structure = lineStructures[commandWord]

        if(structure != null) {

            val content = raw.removePrefix(commandWord).trim()
            val split: MutableList<String> = mutableListOf(commandWord)
            var currentWord = ""
            var segment = 0

            for(i in 0 until content.length) {
                var character = content[i]

                val currentStructurePart = getStructurePart(structure, segment)
                val target =
                        if(currentStructurePart.contains("[1]") || currentStructurePart.contains("[n]"))
                            getStructurePart(structure, segment + 1)
                        else currentStructurePart
                val lastCharacter = i == content.length - 1

                if(lastCharacter && !character.isWhitespace()) {
                    currentWord += character
                    character = ' '
                }

                if(character.isWhitespace()) {
                    if(target == "[1]" && currentWord.replace(Regex("\\s+"), "").isNotEmpty()) {
                        split += currentWord.trim()
                        currentWord = ""
                        ++segment
                    } else if(target != ">") {
                        if(currentWord.isNotEmpty() && currentWord.endsWith(target) && currentWord.removeSuffix(target).last().isWhitespace()) {
                            split += currentWord.removeSuffix(target).trim()
                            split += target
                            currentWord = ""
                            segment += 2
                        }
                    } else if(lastCharacter) { //Anything left over
                        split += currentWord.trim()
                        currentWord = ""
                    }

                    if(currentWord.isNotEmpty()) {
                        currentWord += " "
                    }

                } else {
                    if(currentStructurePart == ">") {
                        throw IllegalArgumentException("invalid syntax")
                    }

                    currentWord += character
                }
            }

            return split

        } else {

            //Attributes, behaviours and invalid command words
            return listOf()

        }
    }


    private fun getCommandWord(raw: String): String
            = raw.trim().split(Regex("\\s+"))[0].trim()


    private fun getStructurePart(structure: Array<String>, index: Int): String {
        return when {
            index < 0                   -> "<"
            index >= structure.size     -> ">"
            else                        -> structure[index]
        }
    }
}