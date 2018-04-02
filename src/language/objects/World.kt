package language.objects

import language.DataType

class World(name: String, mutable: Boolean = false): VulcanObject(DataType.WORLD, name, mutable) {

    override val validMessages: Map<String, Int> = mapOf(
            Pair("sunshine", 3),
            Pair("rain", 3),
            Pair("thunder", 3),
            Pair("explode", 5)
    )

    override fun convertMessage(message: String, parameters: Array<String>, variables: Map<String, VulcanObject>): String {
        when(message) {
            "sunshine" -> {
                if(parameters[0] == "for" && (parameters[2] == "seconds" || parameters[2] == "second")) {
                    val time = DataType.INTEGER.toJava(parameters[1], variables)
                    return "$name.getWorldInfo().setCleanWeatherTime($time * 20);¶" +
                            "$name.getWorldInfo().setRainTime(0);¶" +
                            "$name.getWorldInfo().setThunderTime(0);¶" +
                            "$name.getWorldInfo().setRaining(false);¶" +
                            "$name.getWorldInfo().setThundering(false);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            "rain" -> {
                if(parameters[0] == "for" && (parameters[2] == "seconds" || parameters[2] == "second")) {
                    val time = DataType.INTEGER.toJava(parameters[1], variables)
                    return "$name.getWorldInfo().setCleanWeatherTime(0);¶" +
                            "$name.getWorldInfo().setRainTime($time * 20);¶" +
                            "$name.getWorldInfo().setThunderTime(0);¶" +
                            "$name.getWorldInfo().setRaining(true);¶" +
                            "$name.getWorldInfo().setThundering(false);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            "thunder" -> {
                if(parameters[0] == "for" && (parameters[2] == "seconds" || parameters[2] == "second")) {
                    val time = DataType.INTEGER.toJava(parameters[1], variables)
                    return "$name.getWorldInfo().setCleanWeatherTime(0);¶" +
                            "$name.getWorldInfo().setRainTime($time * 20);¶" +
                            "$name.getWorldInfo().setThunderTime($time * 20);¶" +
                            "$name.getWorldInfo().setRaining(true);¶" +
                            "$name.getWorldInfo().setThundering(true);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            "explode" -> {
                if(parameters[0] == "with" && parameters[1] == "strength" && parameters[3] == "at") {
                    val strength = DataType.FLOAT.toJava(parameters[2], variables)
                    val position = DataType.VECTOR3.toJava(parameters[4], variables)
                    return "$name.createExplosion(null, $position.getX(), $position.getY(), $position.getZ(), $strength, true);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }
        }

        //Should only be called if the message is registered as valid, but has no case in the when statement
        throw IllegalArgumentException("unsupported message $message")
    }
}