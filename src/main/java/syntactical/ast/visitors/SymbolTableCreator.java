package syntactical.ast.visitors;

import error.SemanticException;
import syntactical.OperatorOverloadConstants;
import syntactical.ast.*;

import java.util.*;
import java.util.stream.Collectors;

public class SymbolTableCreator implements Visitor {

    // TODO probably move some of this constants to another class (yes, definitely)
    protected static final Type FORM = new Type("Form");
    protected static final Type INT = new Type("Int");
    protected static final Type REAL = new Type("Real");
    protected static final Type BOOL = new Type("Bool");
    protected static final Type CHAR = new Type("Char");
    protected static final Type VOID = new Type("Void");
    protected static final Type STRING = new Type("String");
    protected static final Type DEFAULT = new Type("~Default");
    protected static final String ARRAY = "Array";
    protected static final Type ARRAY_TYPE = new Type(ARRAY, Type.WILDCARD);
    protected static final String THIS = "this";
    protected static final String ARRAY_SIZE = "size";

    private static final int INT_ID = 0;
    private static final int REAL_ID = 1;
    private static final int BOOL_ID = 2;
    private static final int CHAR_ID = 3;
    private static final int VOID_ID = 4;
    private static final int FORM_ID = 5;
    private static final int ARRAY_ID = 6;
    private static final int STRING_ID = 7;

    private static final int OFFSET = 15;

    private final ProgramNode root;
    private final SymbolTable symbolTable;
    private String currentClass;
    private int errors;

    // TODO add a map (first declaration node of file -> filename) so we can show which file contains errors

    // TODO I think that restoring scopes won't work if we are inside a class
    // make some relation between class scopes and their ids

    public SymbolTableCreator(ProgramNode root) {
        this.root = root;
        this.symbolTable = new SymbolTable();
        this.currentClass = null;
        initializeTable();
    }

    private void initializeTable() {
        addIntegers();
        addForms();
        addReals();
        addBools();
        addChars();
        addVoid();
        addArrays();
        addStrings();
        SymbolTableInitializer initializer = new SymbolTableInitializer(root, symbolTable);
        errors = initializer.start();
    }

    public void addIntegers() {
        symbolTable.createClassScope(INT_ID, INT);
        List<Type> params = Collections.singletonList(INT);
        symbolTable.putFunction(INT_ID * OFFSET + 1, OperatorOverloadConstants._PLUS, params, INT);
        symbolTable.putFunction(INT_ID * OFFSET + 2, OperatorOverloadConstants._MINUS, params, INT);
        symbolTable.putFunction(INT_ID * OFFSET + 3, OperatorOverloadConstants._MULT, params, INT);
        symbolTable.putFunction(INT_ID * OFFSET + 4, OperatorOverloadConstants._DIV, params, INT);
        symbolTable.putFunction(INT_ID * OFFSET + 5, OperatorOverloadConstants._MOD, params, INT);
        symbolTable.putFunction(INT_ID * OFFSET + 6, OperatorOverloadConstants._GE, params, BOOL);
        symbolTable.putFunction(INT_ID * OFFSET + 7, OperatorOverloadConstants._GT, params, BOOL);
        symbolTable.putFunction(INT_ID * OFFSET + 8, OperatorOverloadConstants._LE, params, BOOL);
        symbolTable.putFunction(INT_ID * OFFSET + 9, OperatorOverloadConstants._LT, params, BOOL);
        symbolTable.putFunction(INT_ID * OFFSET + 10, OperatorOverloadConstants._PLUS, new ArrayList<>(), INT);
        symbolTable.putFunction(INT_ID * OFFSET + 11, OperatorOverloadConstants._MINUS, new ArrayList<>(), INT);
        symbolTable.putFunction(INT_ID * OFFSET + 12, OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(INT_ID * OFFSET + 13, OperatorOverloadConstants._ID, params, BOOL);
        symbolTable.putFunction(INT_ID * OFFSET + 14, OperatorOverloadConstants._TO, params, new Type(ARRAY, INT));
        symbolTable.closeScope();
    }

    public void addForms() {
        symbolTable.createClassScope(FORM_ID, FORM);
        List<Type> params = Collections.singletonList(FORM);
        symbolTable.putFunction(FORM_ID * OFFSET + 1, OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(FORM_ID * OFFSET + 2, OperatorOverloadConstants._ID, params, BOOL);
        symbolTable.closeScope();
    }

    public void addReals() {
        symbolTable.createClassScope(REAL_ID, REAL);
        List<Type> params = Collections.singletonList(REAL);
        symbolTable.putFunction(REAL_ID * OFFSET + 1, OperatorOverloadConstants._PLUS, params, REAL);
        symbolTable.putFunction(REAL_ID * OFFSET + 2, OperatorOverloadConstants._MINUS, params, REAL);
        symbolTable.putFunction(REAL_ID * OFFSET + 3, OperatorOverloadConstants._MULT, params, REAL);
        symbolTable.putFunction(REAL_ID * OFFSET + 4, OperatorOverloadConstants._DIV, params, REAL);
        symbolTable.putFunction(REAL_ID * OFFSET + 5, OperatorOverloadConstants._GE, params, BOOL);
        symbolTable.putFunction(REAL_ID * OFFSET + 6, OperatorOverloadConstants._GT, params, BOOL);
        symbolTable.putFunction(REAL_ID * OFFSET + 7, OperatorOverloadConstants._LE, params, BOOL);
        symbolTable.putFunction(REAL_ID * OFFSET + 8, OperatorOverloadConstants._LT, params, BOOL);
        symbolTable.putFunction(REAL_ID * OFFSET + 9, OperatorOverloadConstants._PLUS, new ArrayList<>(), REAL);
        symbolTable.putFunction(REAL_ID * OFFSET + 10, OperatorOverloadConstants._MINUS, new ArrayList<>(), REAL);
        symbolTable.putFunction(REAL_ID * OFFSET + 11, OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(REAL_ID * OFFSET + 12, OperatorOverloadConstants._ID, params, BOOL);
        symbolTable.putFunction(REAL_ID * OFFSET + 13, OperatorOverloadConstants._TO, params, new Type(ARRAY, REAL));
        symbolTable.closeScope();
    }

    public void addBools() {
        symbolTable.createClassScope(BOOL_ID, BOOL);
        List<Type> params = Collections.singletonList(BOOL);
        symbolTable.putFunction(BOOL_ID * OFFSET + 1, OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(BOOL_ID * OFFSET + 2, OperatorOverloadConstants._ID, params, BOOL);
        symbolTable.putFunction(BOOL_ID * OFFSET + 3, OperatorOverloadConstants._AND, params, BOOL);
        symbolTable.putFunction(BOOL_ID * OFFSET + 4, OperatorOverloadConstants._OR, params, BOOL);
        symbolTable.putFunction(BOOL_ID * OFFSET + 5, OperatorOverloadConstants._NOT, new ArrayList<>(), BOOL);
        symbolTable.closeScope();
    }

    public void addChars() {
        symbolTable.createClassScope(CHAR_ID, CHAR);
        List<Type> params = Collections.singletonList(CHAR);
        symbolTable.putFunction(CHAR_ID * OFFSET + 1, OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(CHAR_ID * OFFSET + 2, OperatorOverloadConstants._ID, params, BOOL);
        symbolTable.putFunction(CHAR_ID * OFFSET + 3, OperatorOverloadConstants._GE, params, BOOL);
        symbolTable.putFunction(CHAR_ID * OFFSET + 4, OperatorOverloadConstants._GT, params, BOOL);
        symbolTable.putFunction(CHAR_ID * OFFSET + 5, OperatorOverloadConstants._LE, params, BOOL);
        symbolTable.putFunction(CHAR_ID * OFFSET + 6, OperatorOverloadConstants._LT, params, BOOL);
        // TODO not sure about this last one, but it'll probably do
        symbolTable.putFunction(CHAR_ID * OFFSET + 7, OperatorOverloadConstants._TO, params, new Type(ARRAY, CHAR));
        symbolTable.closeScope();
    }

    public void addVoid() {
        symbolTable.createClassScope(VOID_ID, VOID);
        symbolTable.closeScope();
    }

    public void addArrays() {
        // TODO check parametric types when using these methods
        symbolTable.createClassScope(ARRAY_ID, ARRAY_TYPE);
        List<Type> params = Collections.singletonList(ARRAY_TYPE);
        symbolTable.putVariable(ARRAY_ID * OFFSET + 1, "size", INT);
        symbolTable.putFunction(ARRAY_ID * OFFSET + 2, OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(ARRAY_ID * OFFSET + 3, OperatorOverloadConstants._ID, params, BOOL);
        params = Collections.singletonList(INT);
        symbolTable.putFunction(ARRAY_ID * OFFSET + 4, "constructor", params, ARRAY_TYPE);
        symbolTable.closeScope();
    }

    public void addStrings() {
        symbolTable.createClassScope(STRING_ID, STRING);
        symbolTable.closeScope();
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
        // Form initial values should have already been checked
        if (!FORM.equals(node.getType()) && node.getInitialValue() != null) {
            node.getInitialValue().accept(this);
        }
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        BlockStatementNode block = node.getCode();
        // Create block here to add all parameters to scope
        symbolTable.openScope(node.getId());
        if (currentClass != null) {
            // TODO check if it's safe to set class id for "this"
            int thisId = symbolTable.getCurrentScopeId();
            symbolTable.putVariable(thisId, THIS, new Type(currentClass));
        }
        for (VarDeclarationNode param : node.getParameters()) {
            symbolTable.putVariable(param.getId(), param.getIdentifier(), param.getType());
        }
        symbolTable.closeScope();
        block.accept(this);
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        visit((FunctionDeclarationNode) node);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        symbolTable.openClassScope(node.getType());
        currentClass = node.getType().getName();
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
        symbolTable.openScope(node.getId());
        for (StatementNode n : node) {
            n.accept(this);
        }
        symbolTable.closeScope();
    }

    @Override
    public void visit(VarDeclarationStatementNode node) {
        VarDeclarationNode declaration = node.asDeclaration();
        Type type = declaration.getType();
        if (SymbolTableCreator.ARRAY_TYPE.equals(type) && type.contains(SymbolTableCreator.FORM)) {
            error(node, "Arrays of Forms not allowed");
        }
        if (!symbolTable.putVariable(declaration.getId(), declaration.getIdentifier(), type)) {
            error(node, "Variable already defined in this scope");
        }
        // Generate scope for object definition
        ExpressionNode initialValue = declaration.getInitialValue();
        if (FORM.equals(type)) {
            if (initialValue instanceof AnonymousObjectConstructorExpressionNode) {
                initialValue.accept(this);
            } else {
                error(initialValue, "Expected an anonymous object definition");
            }
        } else {
            initialValue.accept(this);
        }
    }

    @Override
    public void visit(AssignmentStatementNode node) {
        // TODO check const here or in a different visitor?
        ExpressionNode designable = node.getDesignableExpression();
        ExpressionNode value = node.getValue();
        designable.accept(this);
        value.accept(this);
        Type expected = designable.getType();
        Type found = value.getType();
        if (expected != null) {
            if (FORM.equals(expected)) {
                error(designable, "Form objects cannot be reassigned");
            }
            if (found != null && !expected.equals(found)) {
                error(designable, "Expected " + expected + ", but found " + found);
            }
        }
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        node.asExpression().accept(this);
    }

    @Override
    public void visit(ReturnStatementNode node) {
        node.getReturnExpression().accept(this);
        // TODO check return type matches function type (if "return;" them display error at return else at expression)
    }

    @Override
    public void visit(IfElseStatementNode node) {
        ExpressionNode condition = node.getCondition();
        condition.accept(this);
        Type found = condition.getType();
        if (found != null && !BOOL.equals(found)) {
            error(condition, "Expected Bool, but found " + found);
        }
        node.getIfBlock().accept(this);
        if (node.getElsePart() != null) {
            node.getElsePart().accept(this);
        }
    }

    @Override
    public void visit(SwitchStatementNode node) {
        ExpressionNode target = node.getSwitchExpression();
        target.accept(this);
        Type type = target.getType();
        if (type != null && !INT.equals(type) && !REAL.equals(type) && !BOOL.equals(type) && !CHAR.equals(type)) {
            error(target, "Expected a primitive type, but found " + type);
        }
        Set<Integer> seen = new HashSet<>();
        for (Map.Entry<ConstantExpressionNode, StatementNode> e : node.getCases().entrySet()) {
            ConstantExpressionNode key = e.getKey();
            key.accept(this);
            Type t = key.getType();
            // Underscore: sets target's type
            if (DEFAULT.equals(key.getType())) {
                key.setType(type);
                if (!seen.add(null)) {
                    error(key, "Repeated switch branch");
                }
            } else if (!seen.add(key.getValue())) {
                error(key, "Repeated switch branch");
            }
            if (type != null && !type.equals(t)) {
                error(key, "Expected " + type + ", but found " + t);
            }
            e.getValue().accept(this);
        }
    }

    @Override
    public void visit(WhileStatementNode node) {
        ExpressionNode condition = node.getCondition();
        condition.accept(this);
        Type found = condition.getType();
        if (found != null && !BOOL.equals(found)) {
            error(condition, "Expected Bool, but found " + found);
        }
        node.getBlock().accept(this);
    }

    @Override
    public void visit(ForStatementNode node) {
        // TODO "hide" variable to outer scope if possible (i.e. declare inside the for scope)
        VarDeclarationNode variable = node.getVariable();
        ExpressionNode iterable = node.getIterable();
        variable.asStatement().accept(this);
        iterable.accept(this);
        Type type = variable.getType();
        Type found = iterable.getType();
        // Iterable must be of type Array<T> if variable is of type T
        if (type != null && found != null && (!found.equals(ARRAY_TYPE) || !type.equals(found.getParameter()))) {
            error(iterable, "Expected " + type + ", but found " + found);
        }
        node.getBlock().accept(this);
    }

    @Override
    public void visit(PointExpressionNode node) {
        ExpressionNode host = node.getHost();
        host.accept(this);
        Type hostType = host.getType();
        if (hostType == null) {
            // Couldn't set the type of the host so we won't know if it owns the field
            return;
        }
        VariableExpressionNode field = node.getField();
        if (FORM.equals(hostType)) {
            // TODO somehow enter in scope and look for field there
        } else if (ARRAY_TYPE.equals(hostType)) {
            // Array<?>.size is the only available property
            if (ARRAY_SIZE.equals(field.get())) {
                node.setType(INT);
            } else {
                error(field, "Class " + hostType + " does not have field " + field.get());
            }
        } else {
            // TODO fix: if hostType == currentClass then check that field exists
            // notify error otherwise
            // TODO check if it actually exists or leave it like this?
            error(field, "Field " + field.get() + " in class " + hostType +
                    " might be private - use getters or setters");
        }
    }

    @Override
    public void visit(ArrayAccessExpressionNode node) {
        ExpressionNode array = node.getArray();
        ExpressionNode index = node.getIndex();
        array.accept(this);
        index.accept(this);
        Type arrayType = array.getType();
        Type indexType = index.getType();
        if (arrayType != null && !ARRAY_TYPE.equals(arrayType)) {
            error(array, "Expecting an Array, but found " + arrayType);
        }
        if (indexType != null && !INT.equals(indexType)) {
            error(index, "Array indices must be integers, but found" + indexType);
        }
        if (arrayType != null) {
            node.setType(arrayType.getParameter());
        }
    }

    @Override
    public void visit(FunctionCallExpressionNode node) {
        // TODO check parametric types on array operators
        // TODO special case for _id method
        ExpressionNode function = node.getFunction();
        List<ExpressionNode> arguments = node.getArguments();
        for (ExpressionNode arg : arguments) {
            arg.accept(this);
        }
        List<Type> argumentTypes = arguments.stream()
                .map(ExpressionNode::getType)
                .collect(Collectors.toList());
        if (argumentTypes.stream().anyMatch(Objects::isNull)) {
            return;
        }
        if (function instanceof VariableExpressionNode) {
            VariableExpressionNode ve = (VariableExpressionNode) function;
            Function f = symbolTable.getFunction(ve.get(), argumentTypes, ve.getId());
            if (f == null) {
                error(ve, "Couldn't find function " + ve.get() + " applied to arguments " +
                        argumentTypes.stream().map(Type::toString).collect(Collectors.joining(", ")));
            }
        } else if (function instanceof PointExpressionNode) {
            PointExpressionNode pe = (PointExpressionNode) function;
            ExpressionNode receiver = pe.getHost();
            receiver.accept(this);
            Type receiverType = receiver.getType();
            if (receiverType == null) {
                return;
            }
            Deque<Integer> path = symbolTable.openClassScope(receiverType);
            // TODO can path be null?
            VariableExpressionNode ve = pe.getField();
            Function f = symbolTable.getFunctionHere(ve.get(), argumentTypes, ve.getId());
            if (f == null) {
                error(ve, "Couldn't find method " + ve.get() +
                        " in class" + receiverType + " applied to arguments " +
                        argumentTypes.stream().map(Type::toString).collect(Collectors.joining(", ")));
            }
            symbolTable.closeScope();
            symbolTable.restoreScope(path);
        } else {
            error(function, "Not callable");
        }
    }

    @Override
    public void visit(VariableExpressionNode node) {
        String name = node.get();
        Variable variable = symbolTable.getVariable(name, node.getId());
        if (variable == null) {
            error(node, "Undefined variable");
        } else {
            node.setType(variable.type);
        }
    }

    @Override
    public void visit(ConstantExpressionNode node) {
        // Probably nothing to do - type already set
    }

    @Override
    public void visit(ListConstructorExpressionNode node) {
        if (node.getType() != null) {
            // Already set: is String or empty list for example
            return;
        }
        Type parameter = null;
        List<ExpressionNode> elements = node.getElements();
        for (ExpressionNode n : elements) {
            n.accept(this);
            Type t = n.getType();
            if (t != null) {
                if (parameter == null) {
                    parameter = t;
                } else if (!parameter.equals(t)) {
                    error(n, "Elements in list constructor are not of the same type: expected " + parameter +
                            ", but found " + t);
                }
            }
        }
        node.setType(new Type(ARRAY, parameter));
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
        Type type = node.getType();
        for (ExpressionNode e : node.getArguments()) {
            e.accept(this);
        }
        List<Type> argumentTypes = node.getArguments().stream()
                .map(ExpressionNode::getType)
                .collect(Collectors.toList());
        if (argumentTypes.stream().anyMatch(Objects::isNull)) {
            // Unrecognized argument type: don't try to find the the constructor as it won't exist
            return;
        }
        if (!symbolTable.existsClassScope(type)) {
            error(node, "Trying to call a constructor of a class that doesn't exist");
            return;
        }
        Deque<Integer> path = symbolTable.openClassScope(type);
        Function f = symbolTable.getFunctionHere("constructor", argumentTypes, node.getId());
        if (f == null) {
            error(node, "Couldn't find constructor of class " + type + " with the following argument types: " +
                    argumentTypes.stream().map(Type::toString).collect(Collectors.joining(", ")));
        }
        symbolTable.closeScope();
        symbolTable.restoreScope(path);
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
