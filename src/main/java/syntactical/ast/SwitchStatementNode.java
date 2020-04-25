package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.HashMap;
import java.util.Map;

public class SwitchStatementNode extends StatementNode {

  private ExpressionNode variable;
  private Map<ExpressionNode, StatementNode> cases; //TODO change for actual Node

  public SwitchStatementNode(ExpressionNode variable,
                             Map<ExpressionNode,StatementNode> cases) {
    this.variable = variable;
    this.cases = new HashMap<>(cases);
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public ExpressionNode getVariable() {
    return variable;
  }

  public Map<ExpressionNode, StatementNode> getAll() {return cases;}

  public Iterable<StatementNode> getBlocks() {
    return cases.values();
  }

  public Iterable<ExpressionNode> getCases() {
    return cases.keySet();
  }

  public int getNumCases() {
    return cases.size();
  }

  @Override
  public String toString() {
    return super.toString()
        + " {variable:" + variable
        + ", numCases:" + (cases.size())
        + "}";
  }
}
