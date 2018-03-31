package builder

import language.*
import language.objects.VulcanObject
import utils.VulcanUtils

class FoodBuilder(fileName: String, lines: Array<Line>): Builder(fileName,"food", lines) {

    private val eaten = "protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player)"
    private val update = "public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected)"
    private val hitEntity = "public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)"

    private var name: String = "???"
    private var description: String = ""
    private var stackSize: Int = 64
    private var foodValue: Int = 1
    private var saturation: Double = 0.3
    private var meat: Boolean = false
    private var eatTime: Int = 32
    private var shiny: Boolean = false
    private var burnTime: Int = 0
    private var texture: String = ""

    override fun passToNext() {
        ModBuilder.registerItem(Food(name, texture, description, stackSize, shiny, burnTime, foodValue, saturation, meat, eatTime, makeOverrideMap()))
    }

    override fun processLine(line: Line) {
        if(context == "constructor") {
            if(line is SetLine) {
                when(line.field) {
                    "name" -> {
                        if(VulcanUtils.isValidInputString(line.value)) {
                            name = VulcanUtils.sanitiseInputString(line.value)
                        } else {
                            line.throwError(fileName, "${line.value} is not a valid string")
                        }
                    }

                    "texture" -> {
                        if(VulcanUtils.isValidInputString(line.value)) {
                            texture = VulcanUtils.sanitiseInputString(line.value).removeSuffix(".png")
                        } else {
                            line.throwError(fileName, "${line.value} is not a valid string")
                        }
                    }

                    "description" -> {
                        if(VulcanUtils.isValidInputString(line.value)) {
                            description = VulcanUtils.sanitiseInputString(line.value)
                        } else {
                            line.throwError(fileName, "${line.value} is not a valid string")
                        }
                    }

                    "stack" -> {
                        try {
                            val size = line.value.toInt()
                            stackSize = size
                        } catch(exception: NumberFormatException) {
                            line.throwError(fileName, "${line.value} is not a valid integer")
                        }
                    }

                    "shiny" -> {
                        if(line.value == "true" || line.value == "false") {
                            shiny = line.value == "true"
                        } else {
                            line.throwError(fileName, "${line.value} is not a valid boolean")
                        }
                    }

                    "burn_time" -> {
                        try {
                            val time = line.value.toInt()
                            burnTime = time
                        } catch(exception: NumberFormatException) {
                            line.throwError(fileName, "${line.value} is not a valid integer")
                        }
                    }

                    "heal_amount" -> {
                        try {
                            val value = line.value.toInt()
                            foodValue = value
                        } catch(exception: NumberFormatException) {
                            line.throwError(fileName, "${line.value} is not a valid integer")
                        }
                    }

                    "saturation" -> {
                        try {
                            val value = line.value.toDouble()
                            saturation = value
                        } catch(exception: NumberFormatException) {
                            line.throwError(fileName, "${line.value} is not a valid floating-point number")
                        }
                    }

                    "meat" -> {
                        if(line.value == "true" || line.value == "false") {
                            meat = line.value == "true"
                        } else {
                            line.throwError(fileName, "${line.value} is not a valid boolean")
                        }
                    }

                    "eat_time" -> {
                        try {
                            val time = line.value.toInt()
                            eatTime = time
                        } catch(exception: NumberFormatException) {
                            line.throwError(fileName, "${line.value} is not a valid integer")
                        }
                    }
                }
            }
        }

        else if(context in validBehaviours) {
            val behaviour = validBehaviours[context]
            if(behaviour != null) {
                val visibleObjects: Map<String, VulcanObject> = getAllVisibleObjects(behaviour)

                if(line is ActionLine) {
                    if(visibleObjects.containsKey(line.target)) {
                        val target = visibleObjects[line.target]!!
                        if(target.isValidMessage(line.method)) {
                            var javaFunctionCall = ""
                            try {
                                javaFunctionCall = target.messageToJava(line.method, line.arguments, behaviour.parameters)
                            } catch(exception: IllegalArgumentException) {
                                line.throwError(fileName,exception.message ?: "no error message was provided")
                            }

                            if(javaFunctionCall.isNotEmpty()) {
                               behaviourContent[context]?.add(javaFunctionCall)
                            }
                        } else {
                            line.throwError(fileName,"invalid message \"${line.method}\"")
                        }
                    } else {
                        line.throwError(fileName,"invalid target for message \"${line.target}\"")
                    }
                }
            }
        }
    }

    /** Returns a map for method overrides for the java item object.
     * The key is the method declaration line and the value is the method content. */
    private fun makeOverrideMap(): Map<String, String> {
        val overrides: HashMap<String, String> = hashMapOf()
        behaviourContent.asSequence().forEach {
            var content = ""
            it.value.asSequence().forEach {
                if(content.isNotEmpty()) {
                    content += "¶"
                }
                content += it
            }

            if(content.isNotEmpty()) {
                when(it.key) {
                    "eaten" -> overrides[eaten] = content
                    "held" -> overrides[update] = "if(selected && entity instanceof EntityPlayer) {¶EntityPlayer player = (EntityPlayer)entity;¶$content¶}"
                    "hit_entity" -> overrides[hitEntity] = "$content¶return true;"
                }
            }
        }

        return overrides.toMap()
    }
}