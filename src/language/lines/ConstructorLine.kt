package language.lines

class ConstructorLine(fileName: String, lineNo: Int): Line(fileName, lineNo) {

    override fun pseudocode(): String = "constructor"
}