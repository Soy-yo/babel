package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class IfElseStatementNode extends StatementNode {

    private ExpressionNode condition;
    private BlockStatementNode ifBlock;
    private StatementNode elsePart;

    public IfElseStatementNode(IdGenerator id, ExpressionNode condition, BlockStatementNode ifBlock, StatementNode elsePart) {
        super(id);
        if (elsePart != null && !(elsePart instanceof BlockStatementNode)
                && !(elsePart instanceof IfElseStatementNode)) {
            throw new IllegalArgumentException("The else part of an if statement must be either a block or another if");
        }
        this.condition = condition;
        this.ifBlock = ifBlock;
        this.elsePart = elsePart;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public BlockStatementNode getIfBlock() {
        return ifBlock;
    }

    public StatementNode getElsePart() {
        return elsePart;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {condition:" + condition
                + ", hasElse:" + (elsePart != null)
                + "}";
    }

}
