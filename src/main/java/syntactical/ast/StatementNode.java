package syntactical.ast;

import lexical.LexicalUnit;

public abstract class StatementNode extends QueueableNode<StatementNode> {

    public StatementNode(IdGenerator id, LexicalUnit lexeme) {
        super(id, lexeme);
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
