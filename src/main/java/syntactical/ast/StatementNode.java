package syntactical.ast;

public abstract class StatementNode extends QueueableNode<StatementNode> {

    public StatementNode(IdGenerator id) {
        super(id);
    }

    @Override
    protected final StatementNode self() {
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
