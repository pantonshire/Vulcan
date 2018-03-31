package builder

open class Item(val name: String, val texture: String, val description: String, val stackSize: Int, val shiny: Boolean, val burnTime: Int, val overrides: Map<String, String>) {

    fun registryName(): String = name.replace(" ", "_").toLowerCase()

    open fun toJava(): String {
        var java = "VulcanMod.items.addItem(\"${registryName()}\", new VulcanItem(\"$description\", $stackSize, $shiny, $burnTime)"
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