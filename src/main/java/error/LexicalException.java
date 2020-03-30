package error;

public class LexicalException extends RuntimeException {

    private final int row;
    private final int column;
    private final String lexeme;

    public LexicalException(int row, int column, String lexeme, Throwable cause) {
        super("Lexical error in " + row + ":" + column + ":" + lexeme, cause);
        this.row = row;
        this.column = column;
        this.lexeme = lexeme;
    }

    public LexicalException(int row, int column, String lexeme) {
        this(row, column, lexeme, null);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String getLexeme() {
        return lexeme;
    }

}
