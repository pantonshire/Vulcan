package builder

class Block(val name: String, val texture: String, val hardness: Double, val resistance: Double, val unbreakable: Boolean,
            val destroyedByExplosion: Boolean, val climbable: Boolean, val slipperiness: Double, val light: Double,
            val tool: String, val harvestLevel: Int, val overrides: Map<String, String>) {

    fun registryName(): String = name.replace(" ", "_").toLowerCase()

    fun toJava(): String {
        var java = "Vulcan.blocks.addBlock(\"${registryName()}\", new VulcanBlock($hardness, $resistance, $unbreakable, $slipperiness, $light, \"$tool\", $harvestLevel, $destroyedByExplosion, $climbable, null, 0, 0)"
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