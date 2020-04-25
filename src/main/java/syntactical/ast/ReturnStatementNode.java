package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class ReturnStatementNode extends StatementNode {

    private ExpressionNode returnExpression;

    public ReturnStatementNode(ExpressionNode returnExpression) {
        this.returnExpression = returnExpression;
    }

    public ReturnStatementNode() {
        this(ConstantExpressionNode.ofNothing());
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
