package error;

import lexical.LexicalUnit;

public class SemanticException extends RuntimeException {

    private final LexicalUnit lexicalUnit;

    public SemanticException(LexicalUnit lexicalUnit, String message, Throwable cause) {
        super(message
                + lexicalUnit.getRow() + ":"
                + lexicalUnit.getColumn() + ":"
                + lexicalUnit.lexeme(), cause);
        this.lexicalUnit = lexicalUnit;
    }

    public SemanticException(LexicalUnit lexicalUnit, String message) {
        this(lexicalUnit, message, null);
    }

    public LexicalUnit getLexicalUnit() {
        return lexicalUnit;
    }

}
