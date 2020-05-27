package lexical;

import java_cup.runtime.Symbol;

public class LexicalUnit extends Symbol {

    private final String lexeme;
    private final int row;
    private final int column;

    public LexicalUnit(int row, int column, int lexicalClass, String lexeme) {
        super(lexicalClass);
        this.value = this;
        this.lexeme = lexeme;
        this.row = row;
        this.column = column;
    }

    public LexicalUnit(String lexeme) {
        this(0, 0, 0, lexeme);
    }

    public int lexicalClass() {
        return sym;
    }

    public String lexeme() {
        return lexeme;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return super.toString() + ": " + lexeme();
    }

}
