import utils.VulcanUtils

const val VERSION = "alpha 0.2.0"

fun main(args: Array<String>) {
//    if(args.isNotEmpty()) {
//        if(args[0] == "--version") {
//            System.out.println(VERSION)
//        }
//
//        else if(args[0] == "compile") {
//            //TODO: Get input and output directories
//            val inputDir = ""
//            val outputDir = ""
//            VulcanBuild.build(inputDir)
//        }
//    }

//    val inp = "not x"
//    val split = inp.replace(Regex("(\\s+|^)(not)(\\s+)"), "!!").split("!!")
//    println(split.size)

//    val inp = "if then == true then"
//    val split = VulcanParserV2.splitLine(inp)
//    split.asSequence().forEach {
//        println(it)
//    }

//    val inp = "tell \tself's player\t\tto    jump up in the air like they just don\'t care"
//    VulcanParserV3.splitLine(inp).asSequence().forEach {
//        println(it)
//    }

//    val inp = "repeat a million times using a counter variable called bob using"
//    val inp = "while condition == true do"
//    VulcanParserV3.splitLine(inp).asSequence().forEach {
//        println(it)
//    }

//    VulcanParserV3.splitActionArguments("explode with strength (10 + 2) at [x: 3, y: 5, z: -1208.6]").asSequence().forEach {
//        println("\"$it\"")
//    }

    VulcanUtils.split("foo+baa)+(7+", "+").asSequence().forEach {
        println("\"$it\"")
    }
}
