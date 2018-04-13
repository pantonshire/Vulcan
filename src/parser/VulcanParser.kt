package parser

import language.*
import language.objects.*
import utils.VulcanUtils

object VulcanParser {

    private fun initialParse(unparsed: String): String {
        return unparsed
                //Field references
                .replace(Regex("(\'s)(\\s+)"), ".")
                //Less than
                .replace(Regex("(\\s*)(<|is less than)(\\s*)"), "<")
                //Greater than
                .replace(Regex("(\\s*)(>|is more than|is greater than)(\\s*)"), ">")
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
                //Remove comments
                .replace(Regex("(//)(.*)"), "")
                //Remove unnecessary whitespace
                .trim()

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
            //Setting attributes and assigning variables
            "set" -> {
                if(words.size == 4 && words[2] == "to") {
                    SetLine(lineNo, words[1], words[3])
                } else {
                    throwError(fileName, lineNo, "invalid syntax for setting a variable")
                    BlankLine(lineNo)
                }
            }

            //Declaring variables
            "new" -> {
                if(words.size == 7 && words[3] == "called" && words[5] == "=") {
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
                    if(!VulcanUtils.isValidVariableName(words[4])) {
                        throwError(fileName, lineNo, "${words[4]} cannot be used as a variable name")
                    }

                    //New variable object
                    val newVariable: VulcanObject? = when(type) {
                        DataType.BOOLEAN  ->   VulcanBoolean    (words[4], mutable=mutable)
                        DataType.STRING   ->   VulcanString     (words[4], mutable=mutable)
                        DataType.INTEGER  ->   VulcanInteger    (words[4], mutable=mutable)
                        DataType.FLOAT    ->   VulcanDecimal    (words[4], mutable=mutable)
                        DataType.VECTOR3  ->   VulcanVector3    (words[4], mutable=mutable)
                        DataType.ENTITY   ->   LivingEntity     (words[4], mutable=mutable)
                        DataType.PLAYER   ->   Player           (words[4], mutable=mutable)
                        DataType.WORLD    ->   World            (words[4], mutable=mutable)
                        else              ->   null
                    }

                    if(newVariable == null) {
                        throwError(fileName, lineNo, "declaring new ${type?.name} variables is not yet supported")
                    }

                    //Return new line object
                    DeclarationLine(lineNo, newVariable!!, words[6])

                } else {
                    throwError(fileName, lineNo, "invalid syntax for declaring a variable: $initialParsed")
                    BlankLine(lineNo)
                }
            }

            //Function calls
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

            //If statement
            "if" -> {
                if(words.size == 3 && words[2] == "then") {
                    IfLine(lineNo, words[1])
                } else {
                    throwError(fileName, lineNo, "invalid syntax for an if statement")
                    BlankLine(lineNo)
                }
            }

            //While loop
            "while" -> {
                if(words.size == 3 && words[2] == "do") {
                    WhileLine(lineNo, words[1])
                } else {
                    throwError(fileName, lineNo, "invalid syntax for a while loop")
                    BlankLine(lineNo)
                }
            }

            //For loop
            "repeat" -> {
                //Implicit counter variable
                if(words.size == 3 && words[2] == "times") {
                    ForLine(lineNo, words[1], null)
                }
                //Explicit counter variable
                else if(words.size == 8 && words[2] == "times" && words[3] == "using"
                        && words[4] == "counter" && words[5] == "variable" && words[6] == "called") {
                    ForLine(lineNo, words[1], words[7])
                } else {
                    throwError(fileName, lineNo, "invalid syntax for a repeat loop")
                    BlankLine(lineNo)
                }
            }

            //Terminator
            "end" -> {
                if(words.size == 2) {
                    TerminatorLine(lineNo, words[1])
                } else {
                    throwError(fileName, lineNo, "invalid syntax for a terminator statement")
                    BlankLine(lineNo)
                }
            }

            //Constructor
            "attributes:" -> ConstructorLine(lineNo)

            //Behaviours
            in eventNameMap -> BehaviourLine(lineNo, eventNameMap[words[0]]!!)

            //Invalid line
            else -> {
                throwError(fileName, lineNo, "invalid syntax")
                BlankLine(lineNo)
            }
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