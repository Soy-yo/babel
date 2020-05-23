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

    protected ConstantExpressionNode(IdGenerator id, int value, String representation, Type type) {
        super(id, type);
        this.value = value;
        this.representation = representation;
    }

    protected ConstantExpressionNode(IdGenerator id, int n) {
        this(id, n, "" + n, INT);
    }

    protected ConstantExpressionNode(IdGenerator id, float x) {
        this(id, Float.floatToIntBits(x), "" + x, REAL);
    }

    protected ConstantExpressionNode(IdGenerator id, boolean b) {
        this(id, b ? 0b1 : 0b0, "" + b, BOOL);
    }

    protected ConstantExpressionNode(IdGenerator id, char c) {
        this(id, c, "" + c, CHAR);
    }

    public static ConstantExpressionNode fromInt(IdGenerator id, String n) {
        int v = n.contains("b") ? Integer.valueOf(n.substring(2), 2) : Integer.decode(n);
        return new ConstantExpressionNode(id, v);
    }

    public static ConstantExpressionNode fromFloat(IdGenerator id, String x) {
        return new ConstantExpressionNode(id, Float.parseFloat(x));
    }

    public static ConstantExpressionNode fromBoolean(IdGenerator id, String b) {
        return new ConstantExpressionNode(id, Boolean.parseBoolean(b));
    }

    public static ConstantExpressionNode fromChar(IdGenerator id, String c) {
        return new ConstantExpressionNode(id, charValue(c), c.substring(1, c.length() - 1), CHAR);
    }

    public static ConstantExpressionNode ofNothing(IdGenerator id) {
        return new ConstantExpressionNode(id, 0b0, "nothing", VOID);
    }

    public static ConstantExpressionNode ofNull(IdGenerator id) {
        return new ConstantExpressionNode(id, 0b0, "null", null);
    }

    public static ConstantExpressionNode special(IdGenerator id, String representation, Type type) {
        return new ConstantExpressionNode(id, 0b0, representation, type);
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
