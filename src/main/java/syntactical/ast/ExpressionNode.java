package syntactical.ast;

public abstract class ExpressionNode extends ASTNode {

    protected Type type;

    public ExpressionNode(Type type) {
        this.type = type;
    }

    public ExpressionNode() {
        this(null);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
