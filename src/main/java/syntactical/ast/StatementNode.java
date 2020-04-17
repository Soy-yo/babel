package syntactical.ast;

public abstract class StatementNode extends QueueableNode<StatementNode> {

    @Override
    protected StatementNode self() {
        return this;
    }

}
