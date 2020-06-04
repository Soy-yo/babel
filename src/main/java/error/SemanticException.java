package error;

import lexical.LexicalUnit;

public class SemanticException extends RuntimeException {

    private final String filename;
    private final LexicalUnit lexicalUnit;

    public SemanticException(String filename, LexicalUnit lexicalUnit, String message, Throwable cause) {
        super(filename + ":"
                + lexicalUnit.getRow() + ":"
                + lexicalUnit.getColumn() + ":"
                + lexicalUnit.lexeme() + " "
                + message, cause);
        this.filename = filename;
        this.lexicalUnit = lexicalUnit;
    }

    public SemanticException(String filename, LexicalUnit lexicalUnit, String message) {
        this(filename, lexicalUnit, message, null);
    }

    public String getFilename() {
        return filename;
    }

    public LexicalUnit getLexicalUnit() {
        return lexicalUnit;
    }

}
