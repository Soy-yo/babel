package syntactical.ast.visitors;

import error.SemanticException;
import syntactical.OperatorOverloadConstants;
import syntactical.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SymbolTableInitializer implements Visitor {

    private final ProgramNode root;
    private final SymbolTable symbolTable;
    private final Map<DeclarationNode, String> fileErrorHandling;
    private String currentFile;
    private String currentClass;
    private int errors;

    protected SymbolTableInitializer(ProgramNode root,
                                     SymbolTable symbolTable,
                                     Map<DeclarationNode, String> fileErrorHandling) {
        this.root = root;
        this.symbolTable = symbolTable;
        this.fileErrorHandling = fileErrorHandling;
        this.currentFile = null;
        this.currentClass = null;
        this.errors = 0;
    }

    protected int start() {
        root.accept(this);
        return errors;
    }

    @Override
    public void visit(ProgramNode node) {
        for (DeclarationNode n : node.root()) {
            // Update current file if this node is the first node of that file
            if (fileErrorHandling.containsKey(n)) {
                currentFile = fileErrorHandling.get(n);
            }
            n.accept(this);
        }
    }

    @Override
    public void visit(VarDeclarationNode node) {
        Type type = node.getType();
        if (SymbolTableCreator.ARRAY_TYPE.equals(type)) {
            if (type.depth() == 0) {
                error(node, "Must give parametric type on Array");
            } else if (type.contains(SymbolTableCreator.FORM)) {
                error(node, "Arrays of Forms not allowed");
            }
        }
        if (!symbolTable.putVariable(node.getId(), node.getIdentifier(), type, node.isConst())) {
            error(node, "Variable already defined in this scope");
        }
        // Generate scope for object definition
        if (SymbolTableCreator.FORM.equals(type)) {
            ExpressionNode initialValue = node.getInitialValue();
            if (initialValue instanceof AnonymousObjectConstructorExpressionNode) {
                initialValue.accept(this);
            } else {
                ASTNode errorReceiver = initialValue == null ? node : initialValue;
                error(errorReceiver, "Expected an anonymous object definition");
            }
        }
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        Type returnType = node.getType();
        if (SymbolTableCreator.ARRAY_TYPE.equals(returnType) && returnType.depth() == 0) {
            error(node, "Must give parametric type on Array");
        } else if (returnType.contains(SymbolTableCreator.FORM)) {
            error(node, "Form object cannot be returned by a function/method");
        }
        List<Type> parameterTypes = new ArrayList<>();
        for (VarDeclarationNode p : node.getParameters()) {
            Type t = p.getType();
            if (SymbolTableCreator.ARRAY_TYPE.equals(t) && t.depth() == 0) {
                error(p, "Must give parametric type on Array");
            } else if (t.contains(SymbolTableCreator.FORM)) {
                error(p, "Functions/methods cannot have Form objects as parameters");
            }
            parameterTypes.add(t);
        }
        String functionName = node.getIdentifier();
        if (!checkOperatorOverloading(functionName, parameterTypes, returnType)) {
            error(node, "Trying to overload " + functionName + " with wrong parameter or return types");
        }
        if (!symbolTable.putFunction(node.getId(), functionName, parameterTypes, returnType)) {
            error(node, "Function/method already defined in this scope");
        }
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        Type type = new Type(currentClass);
        node.setType(type);
        visit((FunctionDeclarationNode) node);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        Type type = node.getType();
        if (symbolTable.existsClassScope(type)) {
            error(node, "Class already defined");
        }
        symbolTable.createClassScope(node.getId(), type);
        currentClass = type.getName();
        if (node.getContentRoot() != null) {
            for (DeclarationNode n : node.getContentRoot()) {
                n.accept(this);
            }
        }
        currentClass = null;
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
        if (currentClass == null) {
            return true;
        }
        switch (functionName) {
            // if 1 param then it is of the same type as this and returns bool
            case OperatorOverloadConstants._GE:
            case OperatorOverloadConstants._GT:
            case OperatorOverloadConstants._LE:
            case OperatorOverloadConstants._LT:
            case OperatorOverloadConstants._EQUALS:
                return parameterTypes.size() != 1 || currentClass.equals(parameterTypes.get(0).getName()) &&
                        SymbolTableCreator.BOOL.equals(returnType);
            // if 1 param then it is of the same type as this and returns Array<[type of this]>
            case OperatorOverloadConstants._TO:
                return parameterTypes.size() != 1 || currentClass.equals(parameterTypes.get(0).getName()) &&
                        new Type(SymbolTableCreator.ARRAY, new Type(currentClass)).equals(returnType);
        }
        return true;
    }

    private void error(ASTNode node, String message) {
        errors++;
        SemanticException error = new SemanticException(currentFile, node.getLexeme(), message);
        System.err.println("[ERROR] " + error.getMessage());
    }

}
