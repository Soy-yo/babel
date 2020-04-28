package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.beans.Expression;

public class OperatorsExpressionNode extends ExpressionNode {


  private String operator;
  private ExpressionNode first;
  private ExpressionNode last;

  public OperatorsExpressionNode(String operator, ExpressionNode first,
                                     ExpressionNode last) {
    this.operator = operator;
    this.first = first;
    this.last = last;
  }

  /* OperatorsExpressionNode(String operator, ExpressionNode first,
                                    ExpressionNode last, Type type) {
    super(type);
    this.operator = operator;
    this.first = first;
    this.last = last;
  }*/

  public String getOperator() { return this.operator; }
  public ExpressionNode getFirst() { return this.first; }
  public ExpressionNode getLast() { return this.last; }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  @Override
  public String toString() {
    return super.toString()
        + " {type:" + type
        + ", operator:" + operator
        + ", first:" + first
        + ", last:" + last
        + "}";
  }
}
