package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class BlockStatementNode extends StatementNode {

    private StatementNode root;
    private String id;

    public BlockStatementNode(StatementNode root) {
        this.root = root;
        this.id = null;
    }

    public StatementNode root() {
        return root;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
