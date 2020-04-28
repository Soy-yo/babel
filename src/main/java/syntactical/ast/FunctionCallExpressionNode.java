package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FunctionCallExpressionNode extends ExpressionNode {

  private ExpressionNode function;
  private List<ExpressionNode> arguments;

  public FunctionCallExpressionNode(ExpressionNode function, Collection<ExpressionNode> arguments) {
    this.function = function;
    this.arguments = new ArrayList<>(arguments);
  }

  public ExpressionNode getFunction() {
    return function;
  }

  public List<ExpressionNode> getArguments() {
    return arguments;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return super.toString()
        + " {function:" + function
        + ", arguments:" + arguments
        + "}";
  }
}
