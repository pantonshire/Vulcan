package language

abstract class VulcanObject(val name: String) {

    abstract val validMessages: Map<String, Int>

    fun isValidMessage(message: String): Boolean = message in validMessages.keys

    internal abstract fun convertMessage(message: String, parameters: Array<String>, others: Array<VulcanObject>): String

    fun messageToJava(message: String, parameters: Array<String>, others: Array<VulcanObject>): String {
        if(!isValidMessage(message)) { throw IllegalArgumentException("unrecognised message $message") }
        if(parameters.size != validMessages[message]) { throw IllegalArgumentException("improper usage of $message") }
        return convertMessage(message, parameters, others)
    }

    //Player object
    class Player(name: String): VulcanObject(name) {
        override val validMessages: Map<String, Int> = mapOf(
                Pair("jump", 0), //Make player jump
                Pair("take", 2), //Damage player
                Pair("burn", 3), //Set player on fire
                Pair("teleport", 2), //Set player's position
                Pair("breathe", 0), //Restore player's air
                Pair("swing", 2), //Swing arm
                Pair("heal", 2), //Recover health
                Pair("die", 0), //Take unblockable fatal damage
                Pair("mount", 1) //Ride an entity
        )

        override fun convertMessage(message: String, parameters: Array<String>, others: Array<VulcanObject>): String {
            when(message) {
                "jump" -> return "if(${name}.onGround){ ${name}.jump(); }"

                "take" -> {
                    if(parameters[1] == "damage") {
                        try {
                            val damage = parameters[0].toInt()
                            return "MessageUtils.attack($name, $damage);"
                        } catch(exception: NumberFormatException) {
                            throw IllegalArgumentException("${parameters[0]} is not a valid number")
                        }
                    } else {
                        throw IllegalArgumentException("invalid syntax")
                    }
                }

                "burn" -> {
                    if(parameters[0] == "for" && (parameters[2] == "seconds" || parameters[2] == "second")) {
                        try {
                            val time = parameters[1].toInt()
                            return "${name}.setFire($time);"
                        } catch(exception: NumberFormatException) {
                            throw IllegalArgumentException("${parameters[1]} is not a valid number")
                        }
                    } else {
                        throw IllegalArgumentException("invalid syntax")
                    }
                }

                "breathe" -> return "${name}.setAir(300);"

                "swing" -> {
                    if((parameters[0] == "left" || parameters[0] == "right") && parameters[1] == "arm") {
                        val mainHand = parameters[0] == "right"
                        return "MessageUtils.swingArm($name, $mainHand);"
                    } else {
                        throw IllegalArgumentException("invalid syntax")
                    }
                }

                "heal" -> {
                    if(parameters[1] == "health") {
                        try {
                            val amount = parameters[0].toInt()
                            return "${name}.heal($amount);"
                        } catch(exception: NumberFormatException) {
                            throw IllegalArgumentException("${parameters[0]} is not a valid number")
                        }
                    } else {
                        throw IllegalArgumentException("invalid syntax")
                    }
                }

                "die" -> return "MessageUtils.kill($name);"
            }

            throw IllegalArgumentException("unsupported message $message")
        }
    }
}