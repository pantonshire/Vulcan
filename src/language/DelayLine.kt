package language

class DelayLine(lineNo: Int, val delay: String): Line(lineNo) {

    override fun pseudocode(): String = "delay $delay"
}