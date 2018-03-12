package parser

import language.*

object VulcanParser {

    fun parseLine(lineNo: Int, raw: String, validEvents: Array<Event>): Line {
        val words = split(raw)

        if(words.isEmpty()) {
            return BlankLine(lineNo)
        }

        val eventNameMap = Events.toNameMap(validEvents)

        return when(getWord(words, 0)) {
            "set" -> {
                if(words.size == 4 && words[2] == "to") {
                    SetLine(lineNo, words[1], words[3])
                } else {
                    BlankLine(lineNo)
                }
            }

            "tell" -> {
                if(words.size >= 4 && words[2] == "to") {
                    val args: MutableList<String> = mutableListOf()
                    for(word in 4 until words.size) {
                        args += words[word]
                    }
                    MessageLine(lineNo, words[1], words[3], args.toTypedArray())
                } else {
                    BlankLine(lineNo)
                }
            }

            "attributes:" -> ConstructorLine(lineNo)

            in eventNameMap -> EventLine(lineNo, eventNameMap[words[0]]!!)

            else -> BlankLine(lineNo)
        }
    }

    private fun getWord(words: List<String>, index: Int): String = if(words.size > index) words[index] else "nil"

    private fun split(raw: String): List<String> {
        val list: MutableList<String> = mutableListOf()
        var currentString = ""
        var quote = false
        raw.asSequence().forEach {
            if(quote || it != ' ') {
                currentString += it
                if(it == '\"' || it == '\'') {
                    quote = !quote
                }
            } else if(currentString.isNotEmpty()) {
                list += currentString
                currentString = ""
            }
        }
        if(currentString.isNotEmpty()) {
            list += currentString
        }

        return list
    }
}