package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import javax.swing.plaf.nimbus.State;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class SwitchStatementNode extends StatementNode {

    private ExpressionNode switchExpression;
    private Map<ConstantExpressionNode, StatementNode> cases;
    private StatementNode def;

    public SwitchStatementNode(
            IdGenerator id,
            ExpressionNode switchExpression,
            Map<ConstantExpressionNode, StatementNode> cases, StatementNode def) {
        super(id, null);
        this.switchExpression = switchExpression;
        this.cases = new TreeMap<>(cases);
        this.def = def;
    }

    public SwitchStatementNode(
        IdGenerator id,
        ExpressionNode switchExpression,
        Map<ConstantExpressionNode, StatementNode> cases) {
        this(id, switchExpression, cases, null);
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

    public StatementNode getDef() {
        return def;
    }

    public int getNumCases() {
        return cases.size();
    }

    public Type getType() {
        return switchExpression.getType();
    }

    public boolean hasDefault() {
        return def != null;
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
