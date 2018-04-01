package builder

import language.*
import language.objects.VulcanObject
import utils.VulcanUtils

class FoodBuilder(fileName: String, lines: Array<Line>): Builder(fileName,"food", lines) {

    private val eaten = "protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player)"
    private val update = "public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected)"
    private val hitEntity = "public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker)"

    //Strings
    private var name            = StringAttribute(this, "name", "???")
    private var texture         = StringAttribute(this, "texture", "")
    private var description     = StringAttribute(this, "description", "")
    //Booleans
    private var shiny           = BooleanAttribute(this, "shiny", false)
    private var meat            = BooleanAttribute(this, "meat", false)
    //Integers
    private var stackSize       = IntegerAttribute(this, "stack", 64)
    private var foodValue       = IntegerAttribute(this, "heal_amount", 1)
    private var eatTime         = IntegerAttribute(this, "eat_time", 32)
    private var burnTime        = IntegerAttribute(this, "burn_time", 0)
    //Floats
    private var saturation      = FloatAttribute(this, "saturation", 0.3)

    override fun passToNext() {
        ModBuilder.registerItem(Food(

                name            .get(),
                texture         .get(),
                description     .get(),
                stackSize       .get(),
                shiny           .get(),
                burnTime        .get(),
                foodValue       .get(),
                saturation      .get(),
                meat            .get(),
                eatTime         .get(),

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