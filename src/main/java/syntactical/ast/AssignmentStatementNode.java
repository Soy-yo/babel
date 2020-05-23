package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class AssignmentStatementNode extends StatementNode {

    private Designator target;
    private ExpressionNode value;

    public AssignmentStatementNode(IdGenerator id, LexicalUnit lexeme, Designator target,
                                   ExpressionNode value) {
        super(id, lexeme);
        this.target = target;
        this.value = value;
    }

    public AssignmentStatementNode(IdGenerator id, Designator target, ExpressionNode value) {
        this(id, null, target, value);
    }

    public static AssignmentStatementNode fromSyntacticSugar(
            IdGenerator id,
            Designator target,
            String operator,
            ExpressionNode value) {
        ExpressionNode left = target.compose();
        return new AssignmentStatementNode(id, null, target,
            FunctionCallExpressionNode.operator(id, null,
            left, operator, value));
    }

    public ExpressionNode getTarget() {
        return target.getTarget();
    }

    public ExpressionNode getAccessExpression() {
        return target.getAccess();
    }

    public Designator.AccessMethod getAccessMethod() {
        return target.getAccessMethod();
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
