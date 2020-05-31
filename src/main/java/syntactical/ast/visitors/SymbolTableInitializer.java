package syntactical.ast.visitors;

import error.SemanticException;
import syntactical.OperatorOverloadConstants;
import syntactical.ast.*;

import java.util.ArrayList;
import java.util.List;

public class SymbolTableInitializer implements Visitor {

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
        if (SymbolTableCreator.FORM.equals(type)) {
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
        if (SymbolTableCreator.FORM.equals(returnType)) {
            error(node, "Form object cannot be returned by a function/method");
        }
        List<Type> parameterTypes = new ArrayList<>();
        for (VarDeclarationNode p : node.getParameters()) {
            Type t = p.getType();
            if (SymbolTableCreator.FORM.equals(t)) {
                error(p, "Functions/methods cannot have Form objects as parameters");
            }
            parameterTypes.add(t);
        }
        String functionName = node.getIdentifier();
        if (checkOperatorOverloading(functionName, parameterTypes, returnType)) {

        }
        if (!symbolTable.putFunction(functionName, parameterTypes, returnType)) {
            error(node, "Function/method already defined in this scope");
        }
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        Type type = new Type(symbolTable.getCurrentScopeName());
        node.setType(type);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        if (symbolTable.existsClassScope(node.getType())) {
            error(node, "Class already defined");
        }
        symbolTable.createClassScope(node.getId(), node.getType());
        if (node.getContentRoot() != null) {
            for (DeclarationNode n : node.getContentRoot()) {
                n.accept(this);
            }
        }
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

    private boolean checkOperatorOverloading(String functionName, List<Type> parameterTypes, Type returnType) {
        String className = symbolTable.getCurrentScopeName();
        if (className == null) {
            return true;
        }
        switch (functionName) {
            // 0 or 1 params, any return type
            case OperatorOverloadConstants._PLUS:
            case OperatorOverloadConstants._MINUS:
                // TODO check
                return parameterTypes.isEmpty() || parameterTypes.size() == 1;
            // 1 param, any return type
            case OperatorOverloadConstants._MULT:
            case OperatorOverloadConstants._DIV:
            case OperatorOverloadConstants._MOD:
            case OperatorOverloadConstants._AND:
            case OperatorOverloadConstants._OR:
                // TODO check
                return parameterTypes.size() == 1;
            // 0 params, any return type
            case OperatorOverloadConstants._NOT:
                // TODO check
                return parameterTypes.isEmpty();
            // 1 param of the same type as this, returns bool
            case OperatorOverloadConstants._GE:
            case OperatorOverloadConstants._GT:
            case OperatorOverloadConstants._LE:
            case OperatorOverloadConstants._LT:
            case OperatorOverloadConstants._EQUALS:
                return parameterTypes.size() == 1 && className.equals(parameterTypes.get(0).getName()) &&
                        SymbolTableCreator.BOOL.equals(returnType);
            // can't overload
            case OperatorOverloadConstants._NEQ:
                // TODO should be x._equals(y)._not()?
            case OperatorOverloadConstants._TO:
            case OperatorOverloadConstants._ID:
                return false;
        }
        return true;
    }

    private void error(ASTNode node, String message) {
        errors++;
        SemanticException error = new SemanticException(node.getLexeme(), message);
        System.err.println("[ERROR] " + error.getMessage());
    }

}
