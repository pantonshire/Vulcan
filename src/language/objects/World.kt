package language.objects

import language.DataType

class World(name: String, java: String = name, cas: String? = null, mutable: Boolean = false): VulcanObject(DataType.WORLD, name, java, cas, mutable) {

    override val actions: Map<String, Int> = mapOf(
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
                    return "$java.getWorldInfo().setCleanWeatherTime($time * 20);¶" +
                            "$java.getWorldInfo().setRainTime(0);¶" +
                            "$java.getWorldInfo().setThunderTime(0);¶" +
                            "$java.getWorldInfo().setRaining(false);¶" +
                            "$java.getWorldInfo().setThundering(false);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            "rain" -> {
                if(parameters[0] == "for" && (parameters[2] == "seconds" || parameters[2] == "second")) {
                    val time = DataType.INTEGER.toJava(parameters[1], variables)
                    return "$java.getWorldInfo().setCleanWeatherTime(0);¶" +
                            "$java.getWorldInfo().setRainTime($time * 20);¶" +
                            "$java.getWorldInfo().setThunderTime(0);¶" +
                            "$java.getWorldInfo().setRaining(true);¶" +
                            "$java.getWorldInfo().setThundering(false);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            "thunder" -> {
                if(parameters[0] == "for" && (parameters[2] == "seconds" || parameters[2] == "second")) {
                    val time = DataType.INTEGER.toJava(parameters[1], variables)
                    return "$java.getWorldInfo().setCleanWeatherTime(0);¶" +
                            "$java.getWorldInfo().setRainTime($time * 20);¶" +
                            "$java.getWorldInfo().setThunderTime($time * 20);¶" +
                            "$java.getWorldInfo().setRaining(true);¶" +
                            "$java.getWorldInfo().setThundering(true);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }

            "explode" -> {
                if(parameters[0] == "with" && parameters[1] == "strength" && parameters[3] == "at") {
                    val strength = DataType.FLOAT.toJava(parameters[2], variables)
                    val position = DataType.VECTOR3.toJava(parameters[4], variables)
                    return "$java.createExplosion(null, $position.getX(), $position.getY(), $position.getZ(), $strength, true);"
                } else {
                    throw IllegalArgumentException("invalid syntax")
                }
            }
        }

        //Should only be called if the message is registered as valid, but has no case in the when statement
        throw IllegalArgumentException("unsupported message $message")
    }
}