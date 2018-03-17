package language

class BehaviourLine(lineNo: Int, val behaviour: Behaviour): Line(lineNo) {

    override fun pseudocode(): String = "override function ${behaviour.name}"
}