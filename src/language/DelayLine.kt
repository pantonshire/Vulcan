package language

class DelayLine(fileName: String, lineNo: Int, val delay: String): Line(fileName, lineNo) {

    override fun pseudocode(): String = "delay $delay"
}