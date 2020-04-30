package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.HashMap;
import java.util.Map;

public class SwitchStatementNode extends StatementNode {

    // TODO create case node ??

    private ExpressionNode switchExpression;
    private Map<ExpressionNode, StatementNode> cases;

    public SwitchStatementNode(ExpressionNode switchExpression,
                               Map<ExpressionNode, StatementNode> cases) {
        this.switchExpression = switchExpression;
        this.cases = new HashMap<>(cases);
    }

    public ExpressionNode getSwitchExpression() {
        return switchExpression;
    }

    public Map<ExpressionNode, StatementNode> getCases() {
        return cases;
    }

    public Iterable<ExpressionNode> getConstants() {
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
