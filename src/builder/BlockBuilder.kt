package builder

import language.*
import language.objects.VulcanObject
import utils.VulcanUtils

class BlockBuilder(fileName: String, lines: Array<Line>): Builder(fileName,"item", lines) {

    private val walkedOn = "public void onEntityWalk(World world, BlockPos position, Entity entityUnsanitised)"
    private val destroyed = "public void breakBlock(World world, BlockPos position, IBlockState state)"
    private val rightClicked = "public boolean onBlockActivated(World world, BlockPos position, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)"
    private val placed = "public void onBlockPlacedBy(World world, BlockPos position, IBlockState state, EntityLivingBase placer, ItemStack stack)"

    private var name = "???"
    private var hardness = 1.0
    private var resistance = 1.0
    private var unbreakable = false
    private var destroyedByExplosion = false
    private var climbable = false
    private var slipperiness = 0.0
    private var light = 0.0
    private var tool = ""
    private var harvestLevel = 0
    private var texture = ""

    override fun passToNext() {
        ModBuilder.registerBlock(Block(name, texture, hardness, resistance, unbreakable, destroyedByExplosion,
                climbable, slipperiness, light, tool, harvestLevel, makeOverrideMap()))
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

                    "hardness" -> {
                        try {
                            val value = line.value.toDouble()
                            hardness = value
                        } catch(exception: NumberFormatException) {
                            line.throwError(fileName, "${line.value} is not a valid floating-point number")
                        }
                    }

                    "resistance" -> {
                        try {
                            val value = line.value.toDouble()
                            resistance = value
                        } catch(exception: NumberFormatException) {
                            line.throwError(fileName, "${line.value} is not a valid floating-point number")
                        }
                    }

                    "unbreakable" -> {
                        if(line.value == "true" || line.value == "false") {
                            unbreakable = line.value == "true"
                        } else {
                            line.throwError(fileName, "${line.value} is not a valid boolean")
                        }
                    }

                    "fragile" -> {
                        if(line.value == "true" || line.value == "false") {
                            destroyedByExplosion = line.value == "true"
                        } else {
                            line.throwError(fileName, "${line.value} is not a valid boolean")
                        }
                    }

                    "climbable" -> {
                        if(line.value == "true" || line.value == "false") {
                            climbable = line.value == "true"
                        } else {
                            line.throwError(fileName, "${line.value} is not a valid boolean")
                        }
                    }

                    "slipperiness" -> {
                        try {
                            val value = line.value.toDouble()
                            slipperiness = value
                        } catch(exception: NumberFormatException) {
                            line.throwError(fileName, "${line.value} is not a valid floating-point number")
                        }
                    }

                    "light" -> {
                        try {
                            val value = line.value.toDouble()
                            light = value
                        } catch(exception: NumberFormatException) {
                            line.throwError(fileName, "${line.value} is not a valid floating-point number")
                        }
                    }

                    "tool" -> {
                        if(VulcanUtils.isValidInputString(line.value)) {
                            tool = VulcanUtils.sanitiseInputString(line.value)
                        } else {
                            line.throwError(fileName, "${line.value} is not a valid string")
                        }
                    }

                    "tool_level" -> {
                        try {
                            val value = line.value.toInt()
                            harvestLevel = value
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
                when (it.key) {
                    "walked_on" -> overrides[walkedOn] = "if(entityUnsanitised instanceof EntityLivingBase) {¶EntityLivingBase entity = (EntityLivingBase)entityUnsanitised;¶$content¶}"
                    "destroyed" -> overrides[destroyed] = "$content¶super.breakBlock(world, position, state);"
                    "right_clicked" -> overrides[rightClicked] = "$content¶return true;"
                    "placed" -> overrides[placed] = content
                }
            }
        }

        return overrides.toMap()
    }
}