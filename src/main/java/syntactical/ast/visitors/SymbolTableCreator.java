package syntactical.ast.visitors;

import syntactical.ast.*;

import java.util.List;
import java.util.stream.Collectors;

public class SymbolTableCreator implements Visitor {

    // TODO arroba por ejemplo
    private static final String SCOPE_ID_PREFIX = "@scope_";

    private final ASTNode root;
    private final SymbolTable symbolTable;
    private int nextId;

    // TODO probablemente en las expresiones comprobar que las variables que usan estén ya declaradas
    // es decir, visitar lo que contenga expresiones y comprobar que las variables ya estén en la tabla de símbolos
    // pero cuidado con las funciones y los accesos a atributos, que pueden ser del tipo [var]() o [var].[var]

    // quizá tener funciones visit especiales para las expresiones en función de si son el nodo principal o uno interno
    // p.e. x() llama al visit normal de nodo de llamada a funcion, pero para x se llama a otro visit privado y así no
    // comprueba que exista una variable x, sino una función x ???

    // TODO probablemente guardar info de los tipos y funciones que se van encontrando para comprobar al final
    // ya que en principio no debería haber obligación de declarar las clases y las funciones en un orden concreto

    // TODO aquí sólo se comprueba que las cosas existan y hará falta otro Visitor que compruebe tipos una vez todo existe

    public SymbolTableCreator(ASTNode root) {
        this.root = root;
        this.symbolTable = new SymbolTable();
        this.nextId = 0;
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
    public void visit(ClassDeclarationNode node) {
        if (!symbolTable.putClass(node.getType())) {
            // Class already existed !!
            // TODO check errors
        }
        // TODO quizá nombre concreto para las clases para poder encontrarlas mejor
        String scopeName = nextScopeId();
        //node.setId(scopeName);
        symbolTable.openScope(scopeName);
        node.getContentRoot().accept(this);
        symbolTable.closeScope();
    }

    @Override
    public void visit(BlockStatementNode node) {
        String scopeName = nextScopeId();
        //node.setId(scopeName);
        symbolTable.openScope(scopeName);
        node.accept(this);
        symbolTable.closeScope();
    }

    @Override
    public void visit(VarDeclarationStatementNode node) {
        node.asDeclaration().accept(this);
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

    private String nextScopeId() {
        String result = SCOPE_ID_PREFIX + nextId;
        nextId++;
        return result;
    }

}
