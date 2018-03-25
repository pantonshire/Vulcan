package builder

class Block(val name: String, val texture: String, val hardness: Double, val resistance: Double, val unbreakable: Boolean,
            val destroyedByExplosion: Boolean, val flammable: Boolean, val burnForever: Boolean, val redstoneSignal: Int,
            val slipperiness: Double, val light: Double, val tool: String, val harvestLevel: Int, val gravity: Boolean,
            val overrides: Map<String, String>) {

    fun registryName(): String = name.replace(" ", "_").toLowerCase()

    fun toJava(): String {
        val blockClass = if(gravity) "VulcanBlockFalling" else "VulcanBlock"
        var java = "VulcanMod.blocks.addBlock(\"${registryName()}\", new $blockClass($hardness, $resistance, $unbreakable, $slipperiness, $light, \"$tool\", $harvestLevel, $destroyedByExplosion, $flammable, $burnForever, $redstoneSignal, null, 0, 0)"
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