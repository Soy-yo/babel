package error;

public class LexicalException extends RuntimeException {

    private final String file;
    private final int row;
    private final int column;
    private final String lexeme;

    public LexicalException(String file, int row, int column, String lexeme, Throwable cause) {
        super("Lexical error in " + file + ":" + row + ":" + column + ":" + lexeme, cause);
        this.file = file;
        this.row = row;
        this.column = column;
        this.lexeme = lexeme;
    }

    public LexicalException(String file, int row, int column, String lexeme) {
        this(file, row, column, lexeme, null);
    }

    public String getFile() {
        return file;
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
