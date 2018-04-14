package language.lines

import language.Behaviour

class BehaviourLine(fileName: String, lineNo: Int, val behaviour: Behaviour): Line(fileName, lineNo) {

    override fun pseudocode(): String = "override function ${behaviour.name}"
}