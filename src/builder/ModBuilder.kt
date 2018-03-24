package builder

import application.UIHandler
import io.Directories
import io.FileReader
import io.FileWriter
import java.io.File

object ModBuilder {

    var modID = "my_vulcan_mod"
    var modName = "My Vulcan Mod"
    var version = "no version provided"
    var inputPath = ""
    var outputPath = ""
    private val items: MutableList<Item> = mutableListOf()
    private val blocks: MutableList<Block> = mutableListOf()

    var src = "java${File.separator}com${File.separator}$modID"
    var assets = "resources${File.separator}assets${File.separator}$modID"

    fun setDefaultSettings() {
        modID = "my_vulcan_mod"
        modName = "My Vulcan Mod"
        version = "no version provided"
        outputPath = ""
    }

    fun setModSettings(lines: Array<String>) {
        lines.asSequence().forEach {
            val split = it.split(":")
            if(split.size == 2) {
                val value = split[1].trim()
                when(split[0].trim().toLowerCase()) {
                    "id" -> modID = value
                    "name" -> modName = value
                    "version" -> version = value
                    "output" -> outputPath = Directories.parseExternalDirectory(value)
                }
            }
        }

        src = "$outputPath${File.separator}java${File.separator}com${File.separator}$modID"
        assets = "$outputPath${File.separator}resources${File.separator}assets${File.separator}$modID"
    }

    fun registerItem(item: Item) {
        items += item
    }

    fun registerBlock(block: Block) {
        blocks += block
    }

    fun build() {
        if(modID.isEmpty()) {
            throw IllegalArgumentException("Build failed: invalid mod ID")
        }

        if(!Directories.exists(outputPath)) {
            throw IllegalArgumentException("Build failed: output path does not exist")
        }

        projectSkeleton()
        modItemsFile()
        modBlocksFile()
        javaFileFromTemplate("VulcanMod")
        javaFileFromTemplate("RegistryManager")
        javaFileFromTemplate("ItemManager")
        javaFileFromTemplate("BlockManager")
        javaFileFromTemplate("VulcanItem")
        javaFileFromTemplate("VulcanBlock")
        javaFileFromTemplate("MessageUtils")
        itemModelFiles()
        blockModelFiles()
        textures()
        langFile()
    }

    /** Create all necessary directories */
    private fun projectSkeleton() {
        //Source directories
        Directories.mkdir(*src.split(File.separator).toTypedArray())

        //Assets directories
        Directories.mkdir(*assets.split(File.separator).toTypedArray())
        Directories.mkdir(assets, "lang")
        Directories.mkdir(assets, "blockstates")
        Directories.mkdir(assets, "models", "item")
        Directories.mkdir(assets, "models", "block")
        Directories.mkdir(assets, "textures", "item")
        Directories.mkdir(assets, "textures", "block")
    }

    private fun javaFileFromTemplate(name: String) {
        val templateLines = FileReader.readResourceFile("/templates/$name.txt")
        val javaFileLines: MutableList<String> = mutableListOf()
        templateLines.asSequence().forEach {
            javaFileLines += it.replace("~MODID~", modID).replace("~NAME~", modName).replace("~VERSION~", version)
        }
        FileWriter.writeFile(Directories.getDirectory(src, "$name.java"), *javaFileLines.toTypedArray())
    }

    private fun modItemsFile() {
        var content: String = "package com.$modID;¶¶" +
                        "import net.minecraft.world.World;¶" +
                        "import net.minecraft.util.EnumHand;¶" +
                        "import net.minecraft.util.ActionResult;¶" +
                        "import net.minecraft.item.ItemStack;¶" +
                        "import net.minecraft.entity.Entity;¶" +
                        "import net.minecraft.entity.EntityLivingBase;¶" +
                        "import net.minecraft.entity.player.EntityPlayer;¶" +
                        "¶public final class ModItems {¶public static void makeItems() {"

        items.asSequence().forEach {
            content += "¶" + it.toJava()
        }
        content += "¶}¶}"
        FileWriter.writeFile(Directories.getDirectory(src, "ModItems.java"), content)
    }

    private fun modBlocksFile() {
        var content: String = "package com.$modID;¶¶" +
                "import net.minecraft.world.World;¶" +
                "import net.minecraft.util.EnumHand;¶" +
                "import net.minecraft.util.EnumFacing;¶" +
                "import net.minecraft.util.ActionResult;¶" +
                "import net.minecraft.item.ItemStack;¶" +
                "import net.minecraft.entity.Entity;¶" +
                "import net.minecraft.entity.EntityLivingBase;¶" +
                "import net.minecraft.entity.player.EntityPlayer;¶" +
                "import net.minecraft.block.state.IBlockState¶;" +
                "import net.minecraft.util.math.BlockPos;¶" +
                "¶public final class ModBlocks {¶public static void makeBlocks() {"

        blocks.asSequence().forEach {
            content += "¶" + it.toJava()
        }
        content += "¶}¶}"
        FileWriter.writeFile(Directories.getDirectory(src, "ModBlocks.java"), content)
    }

    private fun itemModelFiles() {
        val jsonContent = "{¶»\"parent\": \"item/generated\",¶»\"textures\": {¶»»\"layer0\": \"$modID:item/~TEXTURE~\"¶»}¶}"
        items.asSequence().forEach {
            FileWriter.writeFile(Directories.getDirectory(assets, "models", "item", "${it.registryName()}.json"),
                    jsonContent.replace("~TEXTURE~", if(it.texture.isNotEmpty()) it.texture else "unknown"))
        }
    }

    private fun blockModelFiles() {
        blocks.asSequence().forEach {
            val registryName = it.registryName()
            val textureName = if(it.texture.isNotEmpty()) it.texture else "unknown"

            val itemJson = "{¶»\"parent\": \"$modID:block/$registryName\"¶}"
            val blockJson = "{¶»\"parent\": \"block/cube_all\", \"textures\": {¶»»\"all\": \"$modID:block/$textureName\"¶»}¶}"
            val blockStateJson = "{¶»\"variants\": {¶»»\"normal\": {¶»»»\"model\": \"$modID:$registryName\"¶»»}¶»}¶}"

            FileWriter.writeFile(Directories.getDirectory(assets, "models", "item", "$registryName.json"), itemJson)
            FileWriter.writeFile(Directories.getDirectory(assets, "blockstates", "$registryName.json"), blockStateJson)
            FileWriter.writeFile(Directories.getDirectory(assets, "models", "block", "$registryName.json"), blockJson)
        }
    }

    private fun textures() {
        //Copy item textures
        items.asSequence().filter { it.texture.isNotEmpty() }.forEach {
            val texture = Directories.getDirectory(inputPath, "${it.texture}.png")
            if(Directories.exists(texture)) {
                Directories.copy(texture, Directories.getDirectory(assets, "textures", "item", it.texture + ".png"), true)
            } else {
                UIHandler.error("Could not find ${it.texture}.png")
            }
        }

        //Copy block textures
        blocks.asSequence().filter { it.texture.isNotEmpty() }.forEach {
            val texture = Directories.getDirectory(inputPath, "${it.texture}.png")
            if(Directories.exists(texture)) {
                Directories.copy(texture, Directories.getDirectory(assets, "textures", "block", it.texture + ".png"), true)
            } else {
                UIHandler.error("Could not find ${it.texture}.png")
            }
        }
    }

    private fun langFile() {
        var content = ""
        items.asSequence().forEach { content += "item.${it.registryName()}.name=${it.name}¶" }
        blocks.asSequence().forEach { content += "tile.${it.registryName()}.name=${it.name}¶" }
        content += "¶itemGroup.${modID}_items=$modName items"
        content += "¶itemGroup.${modID}_blocks=$modName blocks"
        FileWriter.writeFile(Directories.getDirectory(assets, "lang", "en_us.lang"), content)
    }
}