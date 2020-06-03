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

  }

  @Override
  public void visit(FunctionCallStatementNode node) {

  }

  @Override
  public void visit(ReturnStatementNode node) {

  }

  @Override
  public void visit(IfElseStatementNode node) {

  }

  @Override
  public void visit(SwitchStatementNode node) {

  }

  @Override
  public void visit(WhileStatementNode node) {

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

  public void issue(String instruction) {
    try {
      writer.write(instruction);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
