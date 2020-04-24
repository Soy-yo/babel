package syntactical.ast.visitors;

import syntactical.ast.*;

public interface Visitor {

    // TODO visit all types of nodes
    void visit(ProgramNode node);

    void visit(VarDeclarationNode node);

    void visit(FunctionDeclarationNode node);

    void visit(ClassDeclarationNode node);

    void visit(BlockStatementNode node);

    void visit(VarDeclarationStatementNode node);

    void visit(AssignmentStatementNode node);

    void visit(FunctionCallStatementNode node);

    void visit(IfElseStatementNode node);

    void visit(SwitchStatementNode node);

    void visit(ConstantExpressionNode node);

}
