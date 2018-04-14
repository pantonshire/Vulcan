package builder

import language.*
import language.lines.Line

class ItemBuilder(fileName: String, lines: Array<Line>): Builder(fileName,"item", lines) {

    private val rightClick = "public ActionResult<ItemStack> onItemRightClick(World _world, EntityPlayer player, EnumHand _hand)"
    private val update = "public void onUpdate(ItemStack _stack, World _world, Entity _entity, int _slot, boolean _selected)"
    private val hitEntity = "public boolean hitEntity(ItemStack _stack, EntityLivingBase target, EntityLivingBase attacker)"

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
                    "right_click" -> overrides[rightClick] = "$content¶return super.onItemRightClick(_world, player, _hand);"
                    "held" -> overrides[update] = "if(_selected && _entity instanceof EntityPlayer) {¶EntityPlayer player = (EntityPlayer)_entity;¶$content¶}"
                    "hit_entity" -> overrides[hitEntity] = "$content¶return true;"
                }
            }
        }

        return overrides.toMap()
    }
}