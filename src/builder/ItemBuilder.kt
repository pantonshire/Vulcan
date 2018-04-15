package builder

import language.BooleanAttribute
import language.IntegerAttribute
import language.StringAttribute
import language.lines.Line

class ItemBuilder(fileName: String, lines: Array<Line>): Builder(fileName,"item", lines) {

    private val rightClick = "public ActionResult<ItemStack> onItemRightClick(World _world, EntityPlayer player, EnumHand _hand)"
    private val update = "public void onUpdate(ItemStack _stack, World _world, Entity _entity, int _slot, boolean _selected)"
    private val hitEntity = "public boolean hitEntity(ItemStack _stack, EntityLivingBase target, EntityLivingBase attacker)"

    //Strings
    private val name            = StringAttribute(this, "name", "")
    private val id              = StringAttribute(this, "id", "")
    private val texture         = StringAttribute(this, "texture", "")
    private val description     = StringAttribute(this, "description", "")
    //Booleans
    private val shiny           = BooleanAttribute(this, "shiny", false)
    //Integers
    private val stackSize       = IntegerAttribute(this, "stack", 64)
    private val burnTime        = IntegerAttribute(this, "burn_time", 0)

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
        ModCompiler.instance.registerItem(Item(

                name            .get(),
                id              .get(),
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