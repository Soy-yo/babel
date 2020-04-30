package syntactical.ast;

public class Designator {

    private ExpressionNode original;
    private ExpressionNode target;
    private ExpressionNode access;
    private AccessMethod method;

    private Designator(ExpressionNode original, ExpressionNode target, ExpressionNode access, AccessMethod method) {
        this.original = original;
        this.target = target;
        this.access = access;
        this.method = method;
    }

    public static Designator ofVar(VariableExpressionNode expression) {
        return new Designator(expression, expression, null, AccessMethod.NONE);
    }

    public static Designator ofField(PointExpressionNode expression) {
        return new Designator(expression, expression.getHost(), expression.getField(), AccessMethod.FIELD);
    }

    public static Designator ofArray(ArrayAccessExpressionNode expression) {
        return new Designator(expression, expression.getArray(), expression.getIndex(), AccessMethod.ARRAY);
    }

    public ExpressionNode getTarget() {
        return target;
    }

    public ExpressionNode getAccess() {
        return access;
    }

    public ExpressionNode compose() {
        return original;
    }

    public AccessMethod getAccessMethod() {
        return method;
    }

    @Override
    public String toString() {
        return target + (access == null ? "" : " @ " + access);
    }

    public enum AccessMethod {
        NONE, FIELD, ARRAY
    }

}
