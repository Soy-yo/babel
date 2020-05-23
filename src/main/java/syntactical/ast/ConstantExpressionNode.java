package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class ConstantExpressionNode extends ExpressionNode {

    private static final Type INT = new Type("Int");
    private static final Type REAL = new Type("Real");
    private static final Type BOOL = new Type("Bool");
    private static final Type CHAR = new Type("Char");
    private static final Type VOID = new Type("Void");

    private int value;
    private String representation;

    protected ConstantExpressionNode(IdGenerator id, LexicalUnit lexeme, int value, String representation, Type type) {
        super(id, lexeme, type);
        this.value = value;
        this.representation = representation;
    }

    protected ConstantExpressionNode(IdGenerator id, LexicalUnit lexeme, int n) {
        this(id, lexeme, n, "" + n, INT);
    }

    protected ConstantExpressionNode(IdGenerator id, LexicalUnit lexeme, float x) {
        this(id, lexeme, Float.floatToIntBits(x), "" + x, REAL);
    }

    protected ConstantExpressionNode(IdGenerator id, LexicalUnit lexeme, boolean b) {
        this(id, lexeme, b ? 0b1 : 0b0, "" + b, BOOL);
    }

    protected ConstantExpressionNode(IdGenerator id, LexicalUnit lexeme, char c) {
        this(id, lexeme, c, "" + c, CHAR);
    }

    protected ConstantExpressionNode(IdGenerator id, int value, String representation, Type type) {
        super(id, null, type);
        this.value = value;
        this.representation = representation;
    }

    protected ConstantExpressionNode(IdGenerator id, int n) {
        this(id, null, n, "" + n, INT);
    }

    protected ConstantExpressionNode(IdGenerator id, float x) {
        this(id, null, Float.floatToIntBits(x), "" + x, REAL);
    }

    protected ConstantExpressionNode(IdGenerator id, boolean b) {
        this(id, null, b ? 0b1 : 0b0, "" + b, BOOL);
    }

    protected ConstantExpressionNode(IdGenerator id, char c) {
        this(id, null, c, "" + c, CHAR);
    }

    public static ConstantExpressionNode fromInt(IdGenerator id, LexicalUnit lexeme, String n) {
        int v = n.contains("b") ? Integer.valueOf(n.substring(2), 2) : Integer.decode(n);
        return new ConstantExpressionNode(id, lexeme, v);
    }

    public static ConstantExpressionNode fromFloat(IdGenerator id, LexicalUnit lexeme, String x) {
        return new ConstantExpressionNode(id, lexeme, Float.parseFloat(x));
    }

    public static ConstantExpressionNode fromBoolean(IdGenerator id, LexicalUnit lexeme, String b) {
        return new ConstantExpressionNode(id, lexeme, Boolean.parseBoolean(b));
    }

    public static ConstantExpressionNode fromChar(IdGenerator id, LexicalUnit lexeme, String c) {
        return new ConstantExpressionNode(id, lexeme, charValue(c), c.substring(1, c.length() - 1),
            CHAR);
    }

    public static ConstantExpressionNode ofNothing(IdGenerator id, LexicalUnit lexeme) {
        return new ConstantExpressionNode(id, lexeme, 0b0, "nothing", VOID);
    }

    public static ConstantExpressionNode ofNull(IdGenerator id, LexicalUnit lexeme) {
        return new ConstantExpressionNode(id, lexeme, 0b0, "null", null);
    }

    public static ConstantExpressionNode special(IdGenerator id, LexicalUnit lexeme,
                                                 String representation, Type type) {
        return new ConstantExpressionNode(id, lexeme, 0b0, representation, type);
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
        return representation;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {type:" + type
                + ", value:" + representation
                + ", bits:" + getBits()
                + "}";
    }

}
