package language

import language.objects.VulcanObject

class DeclarationLine(lineNo: Int, val variable: VulcanObject, val initialValue: String): Line(lineNo) {

    override fun pseudocode(): String = "${if(variable.mutable) "mutable" else "immutable"} ${variable.type.name} ${variable.name} = $initialValue"
}