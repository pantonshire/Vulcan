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

    println(VulcanParserV3.parseVariableV2("not foo and not(baa + 200 < 7 % 10^5.5) or lorem does not equal 70"))
}
