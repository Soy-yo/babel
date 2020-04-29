package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class AssignmentStatementNode extends StatementNode {

    private Designator target;
    private ExpressionNode value;

    public AssignmentStatementNode(Designator target, ExpressionNode value) {
        this.target = target;
        this.value = value;
    }

    public static AssignmentStatementNode fromSyntacticSugar(Designator target, String operator, ExpressionNode value) {
        // TODO get designator expression (replace null)
        ExpressionNode left = ConstantExpressionNode.ofNull();
        return new AssignmentStatementNode(target, FunctionCallExpressionNode.operator(left, operator, value));
    }

    public ExpressionNode getValue() {
        return value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {target:" + target
                + ", value:" + value
                + "}";
    }

}
