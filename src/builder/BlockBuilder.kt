package builder

import language.*

class BlockBuilder(fileName: String, lines: Array<Line>): Builder(fileName,"block", lines) {

    private val walkedOn = "public void onEntityWalk(World world, BlockPos position, Entity entityUnsanitised)"
    private val destroyed = "public void breakBlock(World world, BlockPos position, IBlockState state)"
    private val rightClicked = "public boolean onBlockActivated(World world, BlockPos position, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)"
    private val placed = "public void onBlockPlacedBy(World world, BlockPos position, IBlockState state, EntityLivingBase placer, ItemStack stack)"

    //Strings
    private var name                    = StringAttribute(this, "name", "???")
    private var texture                 = StringAttribute(this, "texture", "")
    private var tool                    = StringAttribute(this, "tool", "")
    //Booleans
    private var unbreakable             = BooleanAttribute(this, "unbreakable", false)
    private var destroyedByExplosion    = BooleanAttribute(this, "fragile", false)
    private var flammable               = BooleanAttribute(this, "flammable", false)
    private var burnForever             = BooleanAttribute(this, "burn_forever", false)
    private var gravity                 = BooleanAttribute(this, "gravity", false)
    //Integers
    private var redstoneSignal          = IntegerAttribute(this, "redstone_signal", 0)
    private var harvestLevel            = IntegerAttribute(this, "tool_level", 0)
    //Floats
    private var hardness                = FloatAttribute(this, "hardness", 1.0)
    private var resistance              = FloatAttribute(this, "resistance", 1.0)
    private var slipperiness            = FloatAttribute(this, "slipperiness", 0.0)
    private var light                   = FloatAttribute(this, "light", 0.0)

    override fun passToNext() {
        ModCompiler.instance.registerBlock(Block(

                name                 .get(),
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