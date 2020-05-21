package error;

import lexical.LexicalUnit;

public class SyntacticalException extends RuntimeException {

    private final LexicalUnit lexicalUnit;

    public SyntacticalException(LexicalUnit lexicalUnit, Throwable cause) {
        super("Unexpected element in " +
                +lexicalUnit.getRow() + ":"
                + lexicalUnit.getColumn() + ":"
                + lexicalUnit.value(), cause);
        this.lexicalUnit = lexicalUnit;
    }

    public SyntacticalException(LexicalUnit lexicalUnit) {
        this(lexicalUnit, null);
    }

    public LexicalUnit getLexicalUnit() {
        return lexicalUnit;
    }

}
