package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class WhileStatementNode extends StatementNode {

    private ExpressionNode condition;
    private BlockStatementNode block;

    public WhileStatementNode(IdGenerator id, ExpressionNode condition, BlockStatementNode block) {
        super(id);
        this.condition = condition;
        this.block = block;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public BlockStatementNode getBlock() {
        return block;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {condition:" + condition
                + "}";
    }

}
