package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.HashMap;
import java.util.Map;

public class SwitchStatementNode extends StatementNode {

  private VarDeclarationNode variable;
  private Map<ExpressionNode,BlockStatementNode> cases; //TODO change for actual Node

  public SwitchStatementNode(VarDeclarationNode variable,
                             Map<ExpressionNode,BlockStatementNode> cases) {
    this.variable = variable;
    this.cases = new HashMap<>(cases);
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public VarDeclarationNode getVariable() {
    return variable;
  }

  public Map<ExpressionNode,BlockStatementNode> getAll() {return cases;}

  public Iterable<BlockStatementNode> getBlocks() {
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
