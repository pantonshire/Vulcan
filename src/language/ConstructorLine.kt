package language

class ConstructorLine(fileName: String, lineNo: Int): Line(fileName, lineNo) {

    override fun pseudocode(): String = "constructor"
}