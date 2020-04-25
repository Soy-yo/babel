package syntactical.ast;


import syntactical.ast.visitors.Visitor;


public class WhileStatementNode extends StatementNode{

  private ExpressionNode condition;
  private BlockStatementNode block;

  public WhileStatementNode(ExpressionNode condition,
                             BlockStatementNode block) {
    this.condition = condition;
    this.block = block;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }

  public ExpressionNode getCondition() {
    return condition;
  }

  public BlockStatementNode getBlock() {return block;}

  @Override
  public String toString() {
    return super.toString()
        + " {condition:" + condition
        + "}";
  }
}
