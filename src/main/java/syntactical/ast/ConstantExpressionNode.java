package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.Defaults;
import syntactical.ast.visitors.Visitor;

public class ConstantExpressionNode extends ExpressionNode implements Comparable {

    private int value;

    protected ConstantExpressionNode(IdGenerator id, LexicalUnit lexeme, int value, Type type) {
        super(id, lexeme, type);
        this.value = value;
    }

    protected ConstantExpressionNode(IdGenerator id, LexicalUnit lexeme, int n) {
        this(id, lexeme, n, Defaults.INT);
    }

    protected ConstantExpressionNode(IdGenerator id, LexicalUnit lexeme, float x) {
        this(id, lexeme, Float.floatToIntBits(x), Defaults.REAL);
    }

    protected ConstantExpressionNode(IdGenerator id, LexicalUnit lexeme, boolean b) {
        this(id, lexeme, b ? 0b1 : 0b0, Defaults.BOOL);
    }

    protected ConstantExpressionNode(IdGenerator id, LexicalUnit lexeme, char c) {
        this(id, lexeme, c, Defaults.CHAR);
    }

    public static ConstantExpressionNode fromInt(IdGenerator id, LexicalUnit lexeme) {
        String n = lexeme.lexeme();
        int v = n.contains("b") ? Integer.valueOf(n.substring(2), 2) : Integer.decode(n);
        return new ConstantExpressionNode(id, lexeme, v);
    }

    public static ConstantExpressionNode fromFloat(IdGenerator id, LexicalUnit lexeme) {
        return new ConstantExpressionNode(id, lexeme, Float.parseFloat(lexeme.lexeme()));
    }

    public static ConstantExpressionNode fromBoolean(IdGenerator id, LexicalUnit lexeme) {
        return new ConstantExpressionNode(id, lexeme, Boolean.parseBoolean(lexeme.lexeme()));
    }

    public static ConstantExpressionNode fromChar(IdGenerator id, LexicalUnit lexeme) {
        return new ConstantExpressionNode(id, lexeme, charValue(lexeme.lexeme()), Defaults.CHAR);
    }

    public static ConstantExpressionNode ofNothing(IdGenerator id, LexicalUnit lexeme) {
        return new ConstantExpressionNode(id, lexeme, 0b0, Defaults.VOID);
    }

    public static ConstantExpressionNode ofNull(IdGenerator id, LexicalUnit lexeme) {
        return new ConstantExpressionNode(id, lexeme, 0b0, Type.WILDCARD);
    }

    public static ConstantExpressionNode special(IdGenerator id, LexicalUnit lexeme, Type type) {
        return new ConstantExpressionNode(id, lexeme, 0b0, type);
    }

    private static char charValue(String c) {
        switch (c) {
            case "'\\t'":
                return '\t';
            case "'\\b'":
                return '\b';
            case "'\\n'":
                return '\n';
            case "'\\r'":
                return '\r';
            case "'\\f'":
                return '\f';
            case "'\\0'":
                return '\0';
            case "'\\\\'":
                return '\\';
            case "'\\''":
                return '\'';
        }
        return c.charAt(1);
    }

    public int getValue() {
        return value;
    }

    public String getBits() {
        return Integer.toString(value, 2);
    }

    public String getHex() {
        return Integer.toString(value, 16);
    }

    public String getRepresentation() {
        return lexeme.lexeme();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {type:" + type
                + ", value:" + getRepresentation()
                + ", bits:" + getBits()
                + "}";
    }

    @Override
    public int compareTo(Object o) {
        return this.value - ((ConstantExpressionNode)o).getValue();
    }
}
