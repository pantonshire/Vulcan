package language.lines

import language.objects.VulcanObject

class DeclarationLine(fileName: String, lineNo: Int, val variable: VulcanObject, val initialValue: String): Line(fileName, lineNo) {

    override fun pseudocode(): String = "${if(variable.mutable) "mutable" else "immutable"} ${variable.type.typeName} ${variable.name} = $initialValue"
}