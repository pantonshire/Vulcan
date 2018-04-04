package language.objects

import language.DataType

class LivingEntity(name: String, java: String = name, cas: String? = null, mutable: Boolean = false): VulcanObject(DataType.ENTITY, name, java, cas, mutable) {

    override val actions: Map<String, Int> = mapOf(
            Pair("jump", 0),        //Make entity jump
            Pair("take", 2),        //Damage entity
            Pair("burn", 3),        //Set entity on fire
            Pair("teleport", 2),    //Set entity's position
            Pair("breathe", 0),     //Restore entity's air
            Pair("swing", 2),       //Swing arm
            Pair("heal", 2),        //Recover health
            Pair("die", 0),         //Take unblockable fatal damage
            Pair("ride", 1),        //Ride an entity
            Pair("explode", 3)      //Explode the entity
    )

    override fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String {
        when(message) {
            //Jump
            "jump" -> return "MessageUtils.makeJump($java);"

            //Damage
            "take" -> {
                if(parameters[1] == "damage") {
                    val damage = DataType.INTEGER.toJava(parameters[0], variables)
                    return "MessageUtils.attack($java, $damage);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Set fire
            "burn" -> {
                if(parameters[0] == "for" && (parameters[2] == "seconds" || parameters[2] == "second")) {
                    val time = DataType.INTEGER.toJava(parameters[1], variables)
                    return "$java.setFire($time);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Set the player's position
            "teleport" -> {
                if(parameters[0] == "to") {
                    val position = DataType.VECTOR3.toJava(parameters[1], variables)
                    return "$java.setPosition($position.getX(), $position.getY(), $position.getZ())"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Restore air
            "breathe" -> return "$java.setAir(300);"

            //Swing arm
            "swing" -> {
                if((parameters[0] == "left" || parameters[0] == "right") && parameters[1] == "arm") {
                    val mainHand = parameters[0] == "right"
                    return "MessageUtils.swingArm($java, $mainHand);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Restore health
            "heal" -> {
                if(parameters[1] == "health") {
                    val amount = DataType.INTEGER.toJava(parameters[0], variables)
                    return "$java.heal($amount);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            //Instant kill
            "die" -> return "MessageUtils.kill($java);"

            //Set riding entity
            "ride" -> {
                val target = DataType.ENTITY.toJava(parameters[0], variables)
                return "$java.startRiding($target, true);"
            }

            //Explode the entity (KABOOM!)
            "explode" -> {
                if(parameters[0] == "with" && parameters[1] == "strength") {
                    val strength = DataType.FLOAT.toJava(parameters[2], variables)
                    return "MessageUtils.explode($java, $strength);"
                }

                throw IllegalArgumentException("invalid syntax")
            }
        }

        throw IllegalArgumentException("unsupported message $message")
    }
}