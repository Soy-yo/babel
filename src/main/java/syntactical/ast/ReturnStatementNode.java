package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class ReturnStatementNode extends StatementNode {

    private ExpressionNode returnExpression;

    public ReturnStatementNode(IdGenerator id, LexicalUnit lexeme, ExpressionNode returnExpression) {
        super(id, lexeme);
        this.returnExpression = returnExpression;
    }

    public ReturnStatementNode(IdGenerator id, ExpressionNode returnExpression) {
        this(id, null, returnExpression);
    }

    public ReturnStatementNode(IdGenerator id, LexicalUnit lexeme) {
        this(id, lexeme, ConstantExpressionNode.ofNothing(id, lexeme));
    }

    public ReturnStatementNode(IdGenerator id) {
        this(id, null, ConstantExpressionNode.ofNothing(id, null));
    }

    public ExpressionNode getReturnExpression() {
        return returnExpression;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {return:" + returnExpression
                + "}";
    }

}
