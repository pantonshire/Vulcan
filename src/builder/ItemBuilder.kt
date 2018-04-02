package builder

import language.*

class ItemBuilder(fileName: String, lines: Array<Line>): Builder(fileName,"item", lines) {

    private val rightClick = "public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)"
    private val update = "public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected)"
    private val hitEntity = "public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)"

    //Strings
    private var name            = StringAttribute(this, "name", "???")
    private var texture         = StringAttribute(this, "texture", "")
    private var description     = StringAttribute(this, "description", "")
    //Booleans
    private var shiny           = BooleanAttribute(this, "shiny", false)
    //Integers
    private var stackSize       = IntegerAttribute(this, "stack", 64)
    private var burnTime        = IntegerAttribute(this, "burn_time", 0)

    override fun passToNext() {
        ModCompiler.instance.registerItem(Item(

                name            .get(),
                texture         .get(),
                description     .get(),
                stackSize       .get(),
                shiny           .get(),
                burnTime        .get(),

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
                    "right_click" -> overrides[rightClick] = "$content¶return super.onItemRightClick(world, player, hand);"
                    "held" -> overrides[update] = "if(selected && entity instanceof EntityPlayer) {¶EntityPlayer player = (EntityPlayer)entity;¶$content¶}"
                    "hit_entity" -> overrides[hitEntity] = "$content¶return true;"
                }
            }
        }

        return overrides.toMap()
    }
}