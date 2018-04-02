package parser

import application.UIHandler
import language.*
import language.objects.*
import utils.VulcanUtils

object VulcanParser {

    private fun initialParse(unparsed: String): String {
        return unparsed
                .replace("\'s ", ".")             // Simplify field references
                .replace(Regex("(//)(.*)"), "")  // Remove comments
                .trim()                                             // Remove unnecessary whitespace

    }

    fun parseLine(fileName: String, lineNo: Int, raw: String, validEvents: Array<Behaviour>): Line {
        //Initial parse
        val initialParsed = initialParse(raw)
        //Split by whitespace
        val words = split(initialParsed)

        //Ignore blank lines
        if(words.isEmpty()) {
            return BlankLine(lineNo)
        }

        val eventNameMap = Behaviours.toNameMap(validEvents)

        return when(getWord(words, 0)) {
            "set" -> {
                if(words.size == 4 && words[2] == "to") {
                    SetLine(lineNo, words[1], words[3])
                } else {
                    throwError(fileName, lineNo, "invalid syntax for setting a variable")
                    BlankLine(lineNo)
                }
            }

            "new" -> {
                if(words.size == 6 && words[4] == "=") {
                    //Data type
                    var type: DataType? = null
                    for(possibleType in DataType.values()) {
                        if(words[1] == possibleType.typeName) {
                            type = possibleType
                            break
                        }
                    }

                    if(type == null) {
                        throwError(fileName, lineNo, "${words[1]} is not a valid type")
                    }

                    //Mutability (variable or constant)
                    var mutable = false
                    if(words[2] == "variable" || words[2] == "constant") {
                        mutable = words[2] == "variable"
                    } else {
                        throwError(fileName, lineNo, "invalid syntax: must be either a variable or a constant")
                    }

                    //Ensure variable name is not a keyword
                    if(!VulcanUtils.isValidVariableName(words[3])) {
                        throwError(fileName, lineNo, "${words[3]} cannot be used as a variable name")
                    }

                    //New variable object
                    val newVariable: VulcanObject? = when(type) {
                        DataType.BOOLEAN  ->   VulcanBoolean(words[3], mutable)
                        DataType.STRING   ->   VulcanString(words[3], mutable)
                        DataType.INTEGER  ->   VulcanInteger(words[3], mutable)
                        DataType.FLOAT    ->   VulcanDecimal(words[3], mutable)
                        DataType.VECTOR3  ->   null
                        DataType.ENTITY   ->   LivingEntity(words[3], mutable)
                        DataType.PLAYER   ->   Player(words[3], mutable)
                        DataType.WORLD    ->   null
                        else              ->   null
                    }

                    if(newVariable == null) {
                        throwError(fileName, lineNo, "declaring new ${type?.name} variables is not yet supported")
                    }

                    //Return new line object
                    DeclarationLine(lineNo, newVariable!!, words[5])

                } else {
                    throwError(fileName, lineNo, "invalid syntax for declaring a variable")
                    BlankLine(lineNo)
                }
            }

//            "assign" -> {
//                if(words.size == 4 && words[2] == "to") {
//                    AssignmentLine(lineNo, words[1], words[3])
//                } else {
//                    throwError(fileName, lineNo, "invalid syntax for assigning a variable")
//                    BlankLine(lineNo)
//                }
//            }

            "tell" -> {
                if(words.size >= 4 && words[2] == "to") {
                    val args: MutableList<String> = mutableListOf()
                    for(word in 4 until words.size) {
                        args += words[word]
                    }
                    ActionLine(lineNo, words[1], words[3], args.toTypedArray())
                } else {
                    throwError(fileName, lineNo, "invalid syntax for telling an object to perform an action")
                    BlankLine(lineNo)
                }
            }

            "attributes:" -> ConstructorLine(lineNo)

            in eventNameMap -> BehaviourLine(lineNo, eventNameMap[words[0]]!!)

            else -> BlankLine(lineNo)
        }
    }

    private fun getWord(words: List<String>, index: Int): String = if(words.size > index) words[index] else "nil"

    private fun split(raw: String): List<String> {
        val list: MutableList<String> = mutableListOf()
        var currentString = ""
        var quote = false
        var squareBrackets = 0

        raw.asSequence().forEach {
            if(!it.isWhitespace() || quote || squareBrackets > 0) {
                currentString += it
                when(it) {
                    '\"' -> quote = !quote
                    '['  -> squareBrackets += 1
                    ']'  -> squareBrackets -= 1
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

    private fun throwError(fileName: String, lineNo: Int, message: String) {
        throw IllegalArgumentException("Error in $fileName on line ${lineNo + 1}: $message")
    }
}