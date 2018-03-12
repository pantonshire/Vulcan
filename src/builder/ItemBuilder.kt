package builder

import console.VConsole
import language.*

class ItemBuilder(lines: Array<Line>): Builder("item", lines) {

    private val rightClick = "public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)"
    private val update = "public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected)"
    private val hitEntity = "public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)"

    private var name: String = "???"
    private var description: String = ""
    private var stackSize: Int = 64
    private var shiny: Boolean = false

    override fun build() {
        for(line in lines) {
            checkForErrors(line)
            updateContext(line)
            if(errors.isNotEmpty()) {
                VConsole.out("Build failed with ${errors.size} error(s):")
                errors.asSequence().forEach {
                    VConsole.out(it.getErrorLog())
                }
                break
            }

            processLine(line)
        }

        ModBuilder.registerItem(Item(name, description, stackSize, shiny, makeOverrideMap()))
    }

    private fun processLine(line: Line) {
        if(context == "constructor") {
            if(line is SetLine) {
                when(line.field) {
                    "name" -> name = line.value.replace("\"", "")
                    "description" -> description = line.value.replace("\"", "")
                    "stackSize" -> stackSize = line.value.toInt()
                    "shiny" -> shiny = line.value.toBoolean()
                }
            }
        }

        else if(context in validEvents) {
            val event = validEventNameMap[context]
            if(event != null) {
                if(line is MessageLine) {
                    if(event.parameters.containsKey(line.target)) {
                        val target = event.parameters[line.target]!!
                        if(target.isValidMessage(line.method)) {
                            var javaFunctionCall = ""
                            try {
                                javaFunctionCall = target.messageToJava(line.method, line.arguments, event.parameters)
                            } catch(exception: IllegalArgumentException) {
                                errors += BuildError(line, exception.message ?: "no error message was provided")
                            }

                            if(javaFunctionCall.isNotEmpty()) {
                               eventContent[context]?.add(javaFunctionCall)
                            }
                        } else {
                            errors += BuildError(line, "invalid message \"${line.method}\"")
                        }
                    } else {
                        errors += BuildError(line, "invalid target for message \"${line.target}\"")
                    }
                }
            }
        }
    }

    private fun makeOverrideMap(): Map<String, String> {
        val overrides: HashMap<String, String> = hashMapOf()
        eventContent.asSequence().forEach {
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