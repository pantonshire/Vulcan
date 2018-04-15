package builder

import language.BooleanAttribute
import language.FloatAttribute
import language.IntegerAttribute
import language.StringAttribute
import language.lines.Line

class BlockBuilder(fileName: String, lines: Array<Line>): Builder(fileName,"block", lines) {

    private val walkedOn = "public void onEntityWalk(World world, BlockPos _position, Entity _entityUnsanitised)"
    private val destroyed = "public void breakBlock(World world, BlockPos _position, IBlockState _state)"
    private val rightClicked = "public boolean onBlockActivated(World world, BlockPos _position, IBlockState _state, EntityPlayer player, EnumHand _hand, EnumFacing _facing, float _hitX, float _hitY, float _hitZ)"
    private val placed = "public void onBlockPlacedBy(World world, BlockPos _position, IBlockState _state, EntityLivingBase placer, ItemStack _stack)"

    //Strings
    private val name                    = StringAttribute(this, "name", "")
    private val id                      = StringAttribute(this, "id", "")
    private val texture                 = StringAttribute(this, "texture", "")
    private val tool                    = StringAttribute(this, "tool", "")
    //Booleans
    private val unbreakable             = BooleanAttribute(this, "unbreakable", false)
    private val destroyedByExplosion    = BooleanAttribute(this, "fragile", false)
    private val flammable               = BooleanAttribute(this, "flammable", false)
    private val burnForever             = BooleanAttribute(this, "burn_forever", false)
    private val gravity                 = BooleanAttribute(this, "gravity", false)
    //Integers
    private val redstoneSignal          = IntegerAttribute(this, "redstone_signal", 0)
    private val harvestLevel            = IntegerAttribute(this, "tool_level", 0)
    //Floats
    private val hardness                = FloatAttribute(this, "hardness", 1.0)
    private val resistance              = FloatAttribute(this, "resistance", 1.0)
    private val slipperiness            = FloatAttribute(this, "slipperiness", 0.0)
    private val light                   = FloatAttribute(this, "light", 0.0)

    override fun validateAttributes() {
        if(name.get().isEmpty()) {
            throw VCException(fileName, -1, "no name was provided")
        }

        if(id.get().isNotEmpty()) {
            id.get().asSequence().forEach {
                if(it.isWhitespace()) {
                    throw VCException(fileName, -1, "whitespace characters are not allowed in the id")
                } else if(it.isUpperCase()) {
                    throw VCException(fileName, -1, "upper-case characters are not allowed in the id")
                } else if(!it.isLetter() && !it.isDigit() && it != '_' && it != '-') {
                    throw VCException(fileName, -1, "the character \"$it\" is not allowed in the id")
                }
            }
        }
    }

    override fun passToNext() {
        ModCompiler.instance.registerBlock(Block(

                name                 .get(),
                id                   .get(),
                texture              .get(),
                hardness             .get(),
                resistance           .get(),
                unbreakable          .get(),
                destroyedByExplosion .get(),
                flammable            .get(),
                burnForever          .get(),
                redstoneSignal       .get(),
                slipperiness         .get(),
                light                .get(),
                tool                 .get(),
                harvestLevel         .get(),
                gravity              .get(),

                makeOverrideMap()
        ))
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
                    "walked_on" -> overrides[walkedOn] = "if(_entityUnsanitised instanceof EntityLivingBase) {¶EntityLivingBase entity = (EntityLivingBase)_entityUnsanitised;¶$content¶}"
                    "destroyed" -> overrides[destroyed] = "$content¶super.breakBlock(world, _position, _state);"
                    "right_clicked" -> overrides[rightClicked] = "$content¶return true;"
                    "placed" -> overrides[placed] = content
                }
            }
        }

        return overrides.toMap()
    }
}