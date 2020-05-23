package syntactical.ast;

public abstract class ExpressionNode extends ASTNode {

    protected Type type;

    public ExpressionNode(IdGenerator id, Type type) {
        super(id);
        this.type = type;
    }

    public ExpressionNode(IdGenerator id) {
        this(id, null);
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
