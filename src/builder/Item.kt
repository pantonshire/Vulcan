package builder

open class Item(val name: String,
                private val id: String,
                val texture: String,
                val description: String,
                val stackSize: Int,
                val shiny: Boolean,
                val burnTime: Int,
                val overrides: Map<String, String>
) {

    private fun defaultID(): String = name.replace(Regex("\\s+"), "_").toLowerCase()

    fun registryName(): String = if(id.isEmpty()) defaultID() else id

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