package builder

class Item(val name: String, val description: String, val stackSize: Int, val shiny: Boolean, val overrides: Map<String, String>) {

    fun registryName(): String = name.replace(" ", "_").toLowerCase()

    fun toJava(): String {
        var java = "»»Vulcan.items.addItem(\"${registryName()}\", new VulcanItem(\"$description\", $stackSize, $shiny)"
        if(overrides.isNotEmpty()) {
            java += " {¶"
            overrides.keys.asSequence().forEach {
                java += "»»»@Override¶»»»$it {¶»»»»${overrides[it]}¶»»»}"
            }
            java += "¶»»}"
        }
        java += ");"
        return java
    }
}