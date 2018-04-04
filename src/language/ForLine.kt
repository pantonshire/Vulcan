package language

class ForLine(lineNo: Int, val loops: String, val counter: String?): Line(lineNo) {

    override fun pseudocode(): String = "for $loops times"
}