package syntactical.ast.visitors;

import syntactical.ast.*;

public interface Visitor {

    // TODO visit all types of nodes
    void visit(ProgramNode node);

    void visit(VarDeclarationNode node);

    void visit(FunctionDeclarationNode node);

    void visit(ConstructorDeclarationNode node);

    void visit(ClassDeclarationNode node);

    void visit(BlockNode node);

}
