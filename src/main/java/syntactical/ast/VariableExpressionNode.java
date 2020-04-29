package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class VariableExpressionNode extends ExpressionNode {

    private String variable;

    public VariableExpressionNode(String variable) {
        this.variable = variable;
    }

    public String get() {
        return variable;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {variable:" + variable
                + "}";
    }

}
