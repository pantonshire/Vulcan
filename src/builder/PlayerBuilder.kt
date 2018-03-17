package builder

import language.*
import language.objects.Player
import language.objects.VulcanObject

class PlayerBuilder(fileName: String, lines: Array<Line>): Builder(fileName,"item", lines,
        Player("self")
        //TODO: world goes here
    ) {

    //TODO: Replace these with player events
    private val rightClick = "public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)"
    private val update = "public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected)"
    private val hitEntity = "public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)"

    private var maxHealth = 20
    private var attackDamage = 1
    private var jumpMultiplier = 1.0

    override fun passToNext() {
        //TODO: Pass data to mod builder
//        ModBuilder.registerItem(Item(name, texture, description, stackSize, shiny, makeOverrideMap()))
    }

    override fun processLine(line: Line) {
        if(context == "constructor") {
            if(line is SetLine) {
                when(line.field) {
                    "hearts" -> {
                        try {
                            val hearts = line.value.toInt()
                            maxHealth = hearts * 2
                        } catch(exception: NumberFormatException) {
                            line.throwError(fileName, "${line.value} is not a valid integer")
                        }
                    }

                    "strength" -> {
                        try {
                            val damage = line.value.toInt()
                            attackDamage = damage
                        } catch(exception: NumberFormatException) {
                            line.throwError(fileName, "${line.value} is not a valid integer")
                        }
                    }

                    "jump_height" -> {
                        try {
                            val height = line.value.toDouble()
                            jumpMultiplier = height
                        } catch(exception: NumberFormatException) {
                            line.throwError(fileName, "${line.value} is not a valid floating-point number")
                        }
                    }
                }
            }
        }

        else if(context in validBehaviours) {
            val event = validBehaviours[context]
            if(event != null) {
                val visibleObjects: Map<String, VulcanObject> = getAllVisibleObjects(event)

                if(line is ActionLine) {
                    if(visibleObjects.containsKey(line.target)) {
                        val target = visibleObjects[line.target]!!
                        if(target.isValidMessage(line.method)) {
                            var javaFunctionCall = ""
                            try {
                                javaFunctionCall = target.messageToJava(line.method, line.arguments, event.parameters)
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