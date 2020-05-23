package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class WhileStatementNode extends StatementNode {

    private ExpressionNode condition;
    private BlockStatementNode block;

    public WhileStatementNode(IdGenerator id, LexicalUnit lexeme, ExpressionNode condition, BlockStatementNode block) {
        super(id, lexeme);
        this.condition = condition;
        this.block = block;
    }

    public WhileStatementNode(IdGenerator id, ExpressionNode condition, BlockStatementNode block) {
        this(id, null, condition, block);
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
