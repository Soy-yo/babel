package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class FunctionCallStatementNode extends StatementNode {

    private FunctionCallExpressionNode function;

    protected FunctionCallStatementNode(FunctionCallExpressionNode function) {
        super(null);
        this.id = function.id;
        this.function = function;
    }

    public FunctionCallExpressionNode asExpression() {
        return function;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString() + " - " + function.toString();
    }

}
