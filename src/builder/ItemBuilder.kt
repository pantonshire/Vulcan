package builder

import language.*
import language.objects.VulcanObject
import utils.VulcanUtils

class ItemBuilder(fileName: String, lines: Array<Line>): Builder(fileName,"item", lines) {

    private val rightClick = "public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)"
    private val update = "public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected)"
    private val hitEntity = "public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)"

    private var name: String = "???"
    private var description: String = ""
    private var stackSize: Int = 64
    private var shiny: Boolean = false
    private var texture: String = ""

    override fun passToNext() {
        ModBuilder.registerItem(Item(name, texture, description, stackSize, shiny, makeOverrideMap()))
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
                when (it.key) {
                    "right_click" -> overrides[rightClick] = content + "¶return super.onItemRightClick(world, player, hand);"
                    "held" -> overrides[update] = "if(selected && entity instanceof EntityPlayer) {¶EntityPlayer player = (EntityPlayer)entity;¶$content¶}"
                    "hit_entity" -> overrides[hitEntity] = content + "¶return true;"
                }
            }
        }

        return overrides.toMap()
    }
}