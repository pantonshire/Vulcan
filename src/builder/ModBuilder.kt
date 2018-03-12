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

    fun build() {
        if(modID.isEmpty()) {
            throw IllegalArgumentException("Build failed: invalid mod ID")
        }

        if(!Directories.exists(outputPath)) {
            throw IllegalArgumentException("Build failed: output path does not exist")
        }

        projectSkeleton()
        modItemsFile()
        javaFileFromTemplate("VulcanMod")
        javaFileFromTemplate("Vulcan")
        javaFileFromTemplate("RegistryManager")
        javaFileFromTemplate("ItemManager")
        javaFileFromTemplate("VulcanItem")
        javaFileFromTemplate("MessageUtils")
        itemModelFiles()
        itemTextures()
        langFile()
    }

    private fun projectSkeleton() {
        //Source directories
        Directories.mkdir(*src.split(File.separator).toTypedArray())

        //Assets directories
        Directories.mkdir(*assets.split(File.separator).toTypedArray())
        Directories.mkdir(assets, "lang")
        Directories.mkdir(assets, "models", "item")
        Directories.mkdir(assets, "textures", "item")
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
        var content: String = "package com.$modID;¶»¶" +
                        "import net.minecraft.world.World;¶" +
                        "import net.minecraft.util.EnumHand;¶" +
                        "import net.minecraft.util.ActionResult;¶" +
                        "import net.minecraft.item.ItemStack;¶" +
                        "import net.minecraft.entity.Entity;¶" +
                        "import net.minecraft.entity.EntityLivingBase;¶" +
                        "import net.minecraft.entity.player.EntityPlayer;¶" +
                        "»¶public final class ModItems {¶»public static void makeItems() {"

        items.asSequence().forEach {
            content += "¶" + it.toJava()
        }
        content += "¶»}¶}"
        FileWriter.writeFile(Directories.getDirectory(src, "ModItems.java"), content)
    }

    private fun itemModelFiles() {
        val jsonContent = "{¶»\"parent\": \"item/generated\",¶»\"textures\": {¶»»\"layer0\": \"$modID:item/~TEXTURE~\"¶»}¶}"
        items.asSequence().forEach {
            FileWriter.writeFile(Directories.getDirectory(assets, "models", "item", "${it.registryName()}.json"),
                    jsonContent.replace("~TEXTURE~", it.registryName()))
        }
    }

    private fun itemTextures() {
        items.asSequence().filter { it.texture.isNotEmpty() }.forEach {
            val texture = Directories.getDirectory(inputPath, "${it.texture}.png")
            if(Directories.exists(texture)) {
                Directories.copy(texture, Directories.getDirectory(assets, "textures", "item", it.texture + ".png"), true)
            } else {
                UIHandler.error("Could not find ${it.texture}.png")
            }
        }
    }

    private fun langFile() {
        var content = ""
        items.asSequence().forEach {
            content += "item.${it.registryName()}.name=${it.name}¶"
        }
        FileWriter.writeFile(Directories.getDirectory(assets, "lang", "en_us.lang"), content)
    }
}