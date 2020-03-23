package lexical;

import java_cup.runtime.Symbol;

public class LexicalUnit extends Symbol {

    private final String file;
    private final int row;
    private final int column;

    public LexicalUnit(String file, int row, int column, int lexicalClass, String lexeme) {
        super(lexicalClass, lexeme);
        this.file = file;
        this.row = row;
        this.column = column;
    }

    public int lexicalClass() {
        return sym;
    }

    public String value() {
        return (String) value;
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

}
