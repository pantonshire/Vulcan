import parser.VulcanParserV3

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

    println(VulcanParserV3.parseVariableV2("-(-12.4 - 18) < -4 or -12 <-3 and 8 + 12 ^ 10 mod 6 is not equal to -1"))
//    println(VulcanParserV3.parseVariableV2("[x: 0.5, y: 64, z: -0.5]"))
}
