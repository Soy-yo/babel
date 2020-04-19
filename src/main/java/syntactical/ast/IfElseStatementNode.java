package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class IfElseStatementNode extends StatementNode {

    private ExpressionNode condition;
    private BlockStatementNode ifBlock;
    private BlockStatementNode elseBlock;

    public IfElseStatementNode(ExpressionNode condition, BlockStatementNode ifBlock, BlockStatementNode elseBlock) {
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public BlockStatementNode getIfBlock() {
        return ifBlock;
    }

    public BlockStatementNode getElseBlock() {
        return elseBlock;
    }

    @Override
    public String toString() {
        return super.toString()
                + " {condition:" + condition
                + ", hasElse:" + (elseBlock != null)
                + "}";
    }

}
