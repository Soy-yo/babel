package syntactical.ast.visitors;

import syntactical.ast.*;

import java.io.*;

public class CodeGenerator implements Visitor {

  private final ASTNode root;
  private String file;
  private BufferedWriter writer;

  public CodeGenerator(ASTNode root, String file) {
    this.root = root;
    this.file = file;
    try {
      this.writer = new BufferedWriter(new FileWriter(file));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void visit(ProgramNode node) {
    for (DeclarationNode n : node.root()) {
      n.accept(this);
    }
  }

  @Override
  public void visit(VarDeclarationNode node) {

  }

  @Override
  public void visit(FunctionDeclarationNode node) {

  }

  @Override
  public void visit(ConstructorDeclarationNode node) {

  }

  @Override
  public void visit(ClassDeclarationNode node) {

  }

  @Override
  public void visit(BlockStatementNode node) {

  }

  @Override
  public void visit(VarDeclarationStatementNode node) {

  }

  @Override
  public void visit(AssignmentStatementNode node) {
    node.getTarget().accept(this);
    node.getValue().accept(this);
    Type type = node.getTarget().getType();
    issue("sto " + convertType(type) + ';');
  }

  @Override
  public void visit(FunctionCallStatementNode node) {

  }

  @Override
  public void visit(ReturnStatementNode node) {
    Type type = node.getReturnExpression().getType();
    issue("lod " + convertType(type) + " 0 0;");
  }

  @Override
  public void visit(IfElseStatementNode node) {
    if(node.getElsePart() != null) {
      node.getCondition().accept(this);
      issue("fjp l1;\n"); // TODO: do something with labels
      node.getIfBlock().accept(this);
      issue("ujp l2;\n");
      issueLabel("l1:");
      node.getElsePart().accept(this);
      issueLabel("l2:");
    } else {
      node.getCondition().accept(this);
      issue("fjp l;\n");
      node.getIfBlock().accept(this);
      issueLabel("l:");
    }

  }

  @Override
  public void visit(SwitchStatementNode node) {

  }

  @Override
  public void visit(WhileStatementNode node) {
    issueLabel("l1:"); // TODO: do something with labels
    node.getCondition().accept(this);
    issue("fjp l2;");
    node.getBlock().accept(this);
    issue("ujp l1;");
    issueLabel("l2:");
  }

  @Override
  public void visit(ForStatementNode node) {

  }

  @Override
  public void visit(PointExpressionNode node) {

  }

  @Override
  public void visit(ArrayAccessExpressionNode node) {

  }

  @Override
  public void visit(FunctionCallExpressionNode node) {

  }

  @Override
  public void visit(VariableExpressionNode node) {

  }

  @Override
  public void visit(ConstantExpressionNode node) {

  }

  @Override
  public void visit(ListConstructorExpressionNode node) {

  }

  @Override
  public void visit(AnonymousObjectConstructorExpressionNode node) {

  }

  @Override
  public void visit(ConstructorCallExpressionNode node) {

  }

  @Override
  public void visit(ErrorDeclarationNode node) {

  }

  @Override
  public void visit(ErrorStatementNode node) {

  }

  @Override
  public void visit(ErrorExpressionNode node) {

  }

  private void binaryOperatorInteger(String o1, String o2, String operator) {
    // TODO: generate constants for operators
    // TODO: probably each expression issues itself and we just issue the operator
    issue(o1 + ';');
    issue(o2 + ';');
    switch (operator) {
      case "+":
        issue("add i;");
        break;
      case "-":
        issue("sub i;");
        break;
      case "*":
        issue("mul i;");
        break;
      case "/":
        issue("div i;");
        break;
      case "==":
        issue("equ i;");
        break;
      case "!=":
        issue("neq i;");
        break;
      case "<=":
        issue("leq i;");
        break;
      case ">=":
        issue("geq i;");
        break;
      case "<":
        issue("les i;");
        break;
      case ">":
        issue("grt i;");
        break;
    }
  }

  private void binaryOperatorReal(String o1, String o2, String operator) {
    // TODO: generate constants for operators
    issue(o1 + ';');
    issue(o2 + ';');
    switch (operator) {
      case "+":
        issue("add r;");
        break;
      case "-":
        issue("sub r;");
        break;
      case "*":
        issue("mul r;");
        break;
      case "/":
        issue("div r;");
        break;
      case "==":
        issue("equ r;");
        break;
      case "!=":
        issue("neq r;");
        break;
      case "<=":
        issue("leq r;");
        break;
      case ">=":
        issue("geq r;");
        break;
      case "<":
        issue("les r;");
        break;
      case ">":
        issue("grt r;");
        break;
    }
  }

  private void binaryOperatorBool(String o1, String o2, String operator) {
    // TODO: generate constants for operators
    issue(o1 + ';');
    issue(o2 + ';');
    switch (operator) {
      case "&&":
        issue("and;");
        break;
      case "||":
        issue("or;");
      case "==":
        issue("equ b;");
        break;
      case "!=":
        issue("neq b;");
        break;
      case "<=":
        issue("leq b;");
        break;
      case ">=":
        issue("geq b;");
        break;
      case "<":
        issue("les b;");
        break;
      case ">":
        issue("grt b;");
        break;
    }
  }

  private void unaryOperatorInteger(String o1, String operator) {
    issue(o1 + ';');
    switch (operator) {
      case "-":
        issue("neg i;");
        break;
    }
  }

  private void unaryOperatorReal(String o1, String operator) {
    issue(o1 + ';');
    switch (operator) {
      case "-":
        issue("neg r;");
        break;
    }
  }

  private void unaryOperatorBool(String o1, String operator) {
    issue(o1 + ';');
    switch (operator) {
      case "!":
        issue("not;");
        break;
    }
  }

  private char convertType(Type type) {
    switch (type.getName()) {
      case "Int":
        return 'i';
      case "Bool":
        return 'b';
      case "Real":
        return 'r';
    }
    return 'a';
  }

  private void issue(String instruction) {
    try {
      writer.write(instruction);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void issueLabel(String label) {
    // TODO
  }
}
