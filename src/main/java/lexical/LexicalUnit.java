package lexical;

import java_cup.runtime.Symbol;

public class LexicalUnit extends Symbol {

    private final int row;
    private final int column;

    public LexicalUnit(int row, int column, int lexicalClass, String lexeme) {
        super(lexicalClass, lexeme);
        this.row = row;
        this.column = column;
    }

    public int lexicalClass() {
        return sym;
    }

    public String value() {
        return (String) value;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return super.toString() + ": " + value();
    }

}
