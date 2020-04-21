package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class AssignmentStatementNode extends StatementNode {

    private Designator target;
    private ExpressionNode value;

    public AssignmentStatementNode(Designator target, ExpressionNode value) {
        this.target = target;
        this.value = value;
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
