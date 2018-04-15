package builder

class VCException(fileName: String, lineNo: Int, message: String):
        RuntimeException("Error in $fileName${if(lineNo > 0) " on line ${lineNo + 1}" else ""}: $message")