package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class ConstantExpressionNode extends ExpressionNode {

    private static final Type INT = new Type("Int");
    private static final Type REAL = new Type("Real");
    private static final Type BOOL = new Type("Bool");
    private static final Type CHAR = new Type("Char");
    private static final Type VOID = new Type("Void");

    private int value;
    private String representation;

    protected ConstantExpressionNode(int value, String representation, Type type) {
        super(type);
        this.value = value;
        this.representation = representation;
    }

    protected ConstantExpressionNode(int n) {
        this(n, "" + n, INT);
    }

    protected ConstantExpressionNode(float x) {
        this(Float.floatToIntBits(x), "" + x, REAL);
    }

    protected ConstantExpressionNode(boolean b) {
        this(b ? 0b1 : 0b0, "" + b, BOOL);
    }

    protected ConstantExpressionNode(char c) {
        this(c, "" + c, CHAR);
    }

    public static ConstantExpressionNode fromInt(String n) {
        int v = n.contains("b") ? Integer.valueOf(n.substring(2), 2) : Integer.decode(n);
        return new ConstantExpressionNode(v);
    }

    public static ConstantExpressionNode fromFloat(String x) {
        return new ConstantExpressionNode(Float.parseFloat(x));
    }

    public static ConstantExpressionNode fromBoolean(String b) {
        return new ConstantExpressionNode(Boolean.parseBoolean(b));
    }

    public static ConstantExpressionNode fromChar(String c) {
        return new ConstantExpressionNode(charValue(c), c.substring(1, c.length() - 1), CHAR);
    }

    public static ConstantExpressionNode ofNothing() {
        return new ConstantExpressionNode(0b0, "nothing", VOID);
    }

    public static ConstantExpressionNode ofNull() {
        return new ConstantExpressionNode(0b0, "null", null);
    }

    public static ConstantExpressionNode special(String representation, Type type) {
        return new ConstantExpressionNode(0b0, representation, type);
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
