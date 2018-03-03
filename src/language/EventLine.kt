package language

class EventLine(lineNo: Int, val event: Event): Line(lineNo) {

    override fun pseudocode(): String = "override function ${event.name}"
}