package syntactical.ast.visitors;

import syntactical.ast.*;

public interface Visitor {

    void visit(ProgramNode node);

    void visit(VarDeclarationNode node);

    void visit(FunctionDeclarationNode node);

    void visit(ConstructorDeclarationNode node);

    void visit(ClassDeclarationNode node);

    void visit(BlockStatementNode node);

    void visit(VarDeclarationStatementNode node);

    void visit(AssignmentStatementNode node);

    void visit(FunctionCallStatementNode node);

    void visit(ReturnStatementNode node);

    void visit(IfElseStatementNode node);

    void visit(SwitchStatementNode node);

    void visit(WhileStatementNode node);

    void visit(ForStatementNode node);

    void visit(PointExpressionNode node);

    void visit(ArrayAccessExpressionNode node);

    void visit(FunctionCallExpressionNode node);

    void visit(VariableExpressionNode node);

    void visit(ConstantExpressionNode node);

    void visit(ListConstructorExpressionNode node);

    void visit(AnonymousObjectConstructorExpressionNode node);

    void visit(ConstructorCallExpressionNode node);

    void visit(ErrorDeclarationNode node);

    void visit(ErrorStatementNode node);

    void visit(ErrorExpressionNode node);

}
