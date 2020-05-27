package syntactical.ast.visitors;

import lexical.LexicalUnit;
import syntactical.ast.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SymbolTableCreator implements Visitor {

    private static final Type FORM = new Type(new LexicalUnit("Form"));
    private static final String THIS = "this";
    private static final String ARRAY = "Array";

    private final ASTNode root;
    private final SymbolTable symbolTable;

    // TODO probablemente en las expresiones comprobar que las variables que usan estén ya declaradas
    // es decir, visitar lo que contenga expresiones y comprobar que las variables ya estén en la tabla de símbolos
    // pero cuidado con las funciones y los accesos a atributos, que pueden ser del tipo [var]() o [var].[var]

    // quizá tener funciones visit especiales para las expresiones en función de si son el nodo principal o uno interno
    // p.e. x() llama al visit normal de nodo de llamada a funcion, pero para x se llama a otro visit privado y así no
    // comprueba que exista una variable x, sino una función x ???

    // TODO probablemente guardar info de los tipos y funciones que se van encontrando para comprobar al final
    // ya que en principio no debería haber obligación de declarar las clases y las funciones en un orden concreto

    // TODO aquí sólo se comprueba que las cosas existan y hará falta otro Visitor que compruebe tipos una vez todo existe

    // TODO caso especial para Array<?> al comprobar tipos

    public SymbolTableCreator(ASTNode root) {
        this.root = root;
        this.symbolTable = new SymbolTable();
    }

    private void initializeTable() {
        // TODO add default classes and methods (Int, Form, Array<?>, Int._plus, etc)
    }

    public SymbolTable create() {
        root.accept(this);
        return symbolTable;
    }

    @Override
    public void visit(ProgramNode node) {
        for (DeclarationNode n : node.root()) {
            n.accept(this);
        }
    }

    @Override
    public void visit(VarDeclarationNode node) {
        if (!symbolTable.putVariable(node.getIdentifier(), node.getType())) {
            // Variable already existed in current scope !!
            // TODO check errors
        }
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        List<Type> types = node.getParameters().stream()
                .map(VarDeclarationNode::getType)
                .collect(Collectors.toList());
        if (!symbolTable.putFunction(node.getIdentifier(), types, node.getType())) {
            // Function already existed !!
            // TODO check errors
        }
        node.getCode().accept(this);
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        // TODO get current class name and set the type of the constructor
        // node.setType( :( );
        visit((FunctionDeclarationNode) node);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        if (symbolTable.existsClassScope(node.getType())) {
            // Class already existed !!
            // TODO check errors
        }
        symbolTable.openClassScope(node.getId(), node.getType());
        node.getContentRoot().accept(this);
        symbolTable.closeScope();
    }

    @Override
    public void visit(BlockStatementNode node) {
        symbolTable.openScope(node.getId());
        for (StatementNode n : node) {
            n.accept(this);
        }
        symbolTable.closeScope();
    }

    @Override
    public void visit(VarDeclarationStatementNode node) {
        node.asDeclaration().accept(this);
    }

    @Override
    public void visit(AssignmentStatementNode node) {
        node.getDesignableExpression().accept(this);
        node.getValue().accept(this);
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        node.asExpression().accept(this);
    }

    @Override
    public void visit(ReturnStatementNode node) {
        node.getReturnExpression().accept(this);
    }

    @Override
    public void visit(IfElseStatementNode node) {
        node.getCondition().accept(this);
        node.getIfBlock().accept(this);
        if (node.getElsePart() != null) {
            node.getElsePart().accept(this);
        }
    }

    @Override
    public void visit(SwitchStatementNode node) {
        node.getSwitchExpression().accept(this);
        for (Map.Entry<ConstantExpressionNode, StatementNode> e : node.getCases().entrySet()) {
            e.getKey().accept(this);
            e.getValue().accept(this);
        }
    }

    @Override
    public void visit(WhileStatementNode node) {
        node.getCondition().accept(this);
        node.getBlock().accept(this);
    }

    @Override
    public void visit(ForStatementNode node) {
        node.getVariable().accept(this);
        node.getIterable().accept(this);
        node.getBlock().accept(this);
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
        // Probably nothing to do - type already set
    }

    @Override
    public void visit(ListConstructorExpressionNode node) {
        for (ExpressionNode n : node.getElements()) {
            n.accept(this);
        }
    }

    @Override
    public void visit(AnonymousObjectConstructorExpressionNode node) {
        symbolTable.openScope(node.getId());
        for (DeclarationNode n : node.getFields()) {
            n.accept(this);
        }
        symbolTable.closeScope();
    }

    @Override
    public void visit(ConstructorCallExpressionNode node) {
        // TODO check constructor's type exists (or save it to check at the end)
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

}
