package builder

class VCException(fileName: String, lineNo: Int, message: String): RuntimeException("Error in $fileName on line ${lineNo + 1}: $message")