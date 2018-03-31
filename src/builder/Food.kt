package builder

class Food(name: String, texture: String, description: String, stackSize: Int, shiny: Boolean,
           burnTime: Int, val foodValue: Int, val saturation: Double, val meat: Boolean, val eatTime: Int,
           overrides: Map<String, String>): Item(name, texture, description, stackSize, shiny, burnTime, overrides) {

    override fun toJava(): String {
        var java = "VulcanMod.items.addItem(\"${registryName()}\", new VulcanFood(\"$description\", $foodValue, $saturation, $meat, $eatTime, $stackSize, $shiny, $burnTime)"
        if(overrides.isNotEmpty()) {
            java += " {¶"
            overrides.keys.asSequence().forEach {
                java += "@Override¶$it {¶${overrides[it]}¶}"
            }
            java += "¶}"
        }
        java += ");"
        return java
    }
}