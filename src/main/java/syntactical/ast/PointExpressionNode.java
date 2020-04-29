package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.beans.Expression;

public class PointExpressionNode extends ExpressionNode {


  private ExpressionNode host;
  private String field;

  public PointExpressionNode(ExpressionNode host, String field) {
    this.host = host;
    this.field = field;
  }

  public ExpressionNode getHost() { return this.host; }
  public String getField() { return this.field; }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return super.toString()
        + " {host:" + host
        + ", field:" + field
        + "}";
  }
}
