package syntactical.ast;

public abstract class ExpressionNode extends ASTNode {

    protected Type type;

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
