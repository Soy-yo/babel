package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class BlockStatementNode extends StatementNode {

    private StatementNode root;

    public BlockStatementNode(IdGenerator id, StatementNode root) {
        super(id);
        this.root = root;
    }

    public StatementNode root() {
        return root;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
