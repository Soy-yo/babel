package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.HashMap;
import java.util.Map;

public class SwitchStatementNode extends StatementNode {

    private ExpressionNode switchExpression;
    private Map<ConstantExpressionNode, StatementNode> cases;

    public SwitchStatementNode(
            IdGenerator id,
            ExpressionNode switchExpression,
            Map<ConstantExpressionNode, StatementNode> cases) {
        super(id, null);
        this.switchExpression = switchExpression;
        this.cases = new HashMap<>(cases);
    }

    public ExpressionNode getSwitchExpression() {
        return switchExpression;
    }

    public Map<ConstantExpressionNode, StatementNode> getCases() {
        return cases;
    }

    public Iterable<ConstantExpressionNode> getConstants() {
        return cases.keySet();
    }

    public Iterable<StatementNode> getStatements() {
        return cases.values();
    }

    public int getNumCases() {
        return cases.size();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {variable:" + switchExpression
                + ", numCases:" + cases.size()
                + "}";
    }

}
