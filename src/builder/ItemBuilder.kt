package builder

import language.Line
import language.SetLine

class ItemBuilder(lines: Array<Line>): Builder("item", lines) {

    private var name: String = "???"
    private var description: String = ""
    private var stackSize: Int = 64
    private var shiny: Boolean = false

    override fun build() {
        for(line in lines) {
            checkForErrors(line)
            updateContext(line)
            if(errors.isNotEmpty()) {
                println("Build failed with ${errors.size} error(s):")
                errors.asSequence().forEach {
                    println(it.getErrorLog())
                }
                break
            }

            processLine(line)
        }

        val item = Item(name, description, stackSize, shiny, mapOf(
                Pair("public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)", "player.jump()")
        )) //Add method overrides later

        ModBuilder.registerItem(item)

//        val java = item.toJava()
//        println()
//
//        var indentation = 0
//        java.split("¶").forEach {
//            var indentationString = ""
//            for(x in 0 until indentation) {
//                indentationString += "  "
//            }
//
//            println(indentationString + it)
//
//            for(char in it) {
//                when(char) {
//                    '{' -> ++indentation
//                    '}' -> --indentation
//                }
//            }
//        }
    }

    private fun processLine(line: Line) {
        if(context == "constructor") {
            if(line is SetLine) {
                when(line.field) {
                    "name" -> name = line.value.replace("\"", "")
                    "description" -> description = line.value.replace("\"", "")
                    "stackSize" -> stackSize = line.value.toInt()
                    "shiny" -> shiny = line.value.toBoolean()
                }
            }
        }
    }
}