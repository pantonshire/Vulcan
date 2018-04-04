package language

class ForLine(lineNo: Int, val loops: String): Line(lineNo) {

    override fun pseudocode(): String = "for $loops times"
}