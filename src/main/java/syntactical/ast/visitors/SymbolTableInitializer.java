package syntactical.ast.visitors;

import error.SemanticException;
import lexical.LexicalUnit;
import syntactical.ast.*;

import java.util.ArrayList;
import java.util.List;

public class SymbolTableInitializer implements Visitor {

    private static final Type FORM = new Type(new LexicalUnit("Form"));

    private final ProgramNode root;
    private final SymbolTable symbolTable;
    private int errors;

    protected SymbolTableInitializer(ProgramNode root, SymbolTable symbolTable) {
        this.root = root;
        this.symbolTable = symbolTable;
        this.errors = 0;
    }

    protected int start() {
        root.accept(this);
        return errors;
    }

    @Override
    public void visit(ProgramNode node) {
        for (DeclarationNode n : node.root()) {
            n.accept(this);
        }
    }

    @Override
    public void visit(VarDeclarationNode node) {
        Type type = node.getType();
        if (!symbolTable.putVariable(node.getIdentifier(), type)) {
            error(node, "Variable already defined in this scope");
        }
        // Generate scope for object definition
        if (FORM.equals(type)) {
            ExpressionNode initialValue = node.getInitialValue();
            if (initialValue instanceof AnonymousObjectConstructorExpressionNode) {
                initialValue.accept(this);
            } else {
                error(initialValue, "Expected an anonymous object definition");
            }
        }
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        Type returnType = node.getType();
        if (FORM.equals(returnType)) {
            error(node, "Form object cannot be returned by a function/method");
        }
        List<Type> parameterTypes = new ArrayList<>();
        for (VarDeclarationNode p : node.getParameters()) {
            Type t = p.getType();
            if (FORM.equals(t)) {
                error(p, "Functions/methods cannot have Form objects as parameters");
            }
            parameterTypes.add(t);
        }
        if (!symbolTable.putFunction(node.getIdentifier(), parameterTypes, returnType)) {
            error(node, "Function/method already defined in this scope");
        }
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        Type type = new Type(new LexicalUnit(symbolTable.getCurrentScopeName()));
        node.setType(type);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        if (symbolTable.existsClassScope(node.getType())) {
            error(node, "Class already defined");
        }
        symbolTable.createClassScope(node.getId(), node.getType());
        node.getContentRoot().accept(this);
        symbolTable.closeScope();
    }

    @Override
    public void visit(BlockStatementNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(VarDeclarationStatementNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(AssignmentStatementNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(ReturnStatementNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(IfElseStatementNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(SwitchStatementNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(WhileStatementNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(ForStatementNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(PointExpressionNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(ArrayAccessExpressionNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(FunctionCallExpressionNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(VariableExpressionNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(ConstantExpressionNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(ListConstructorExpressionNode node) {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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

    private void error(ASTNode node, String message) {
        errors++;
        SemanticException error = new SemanticException(node.getLexeme(), message);
        System.err.println("[ERROR] " + error.getMessage());
    }

}
