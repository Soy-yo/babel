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

    protected static final int IDENTITY_OP_ID = 0;

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
    private String currentFunction;
    private Deque<Integer> pointRecPath;
    private int pointRecDepth;

    private int errors;

    // TODO add a map (first declaration node of file -> filename) so we can show which file contains errors

    public SymbolTableCreator(ProgramNode root) {
        this.root = root;
        this.symbolTable = new SymbolTable();
        this.currentClass = null;
        this.currentFunction = null;
        this.pointRecPath = null;
        this.pointRecDepth = 0;
        initializeTable();
    }

    private void initializeTable() {
        // _ID(T x, S y) = dir(x) == dir(y) <- we can ignore types here
        symbolTable.putFunction(IDENTITY_OP_ID, OperatorOverloadConstants._ID,
                Arrays.asList(Type.WILDCARD, Type.WILDCARD), BOOL);
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
        // +1 for identity operator
        symbolTable.createClassScope(INT_ID * OFFSET + 1, INT);
        List<Type> params = Collections.singletonList(INT);
        symbolTable.putFunction(INT_ID * OFFSET + 2, OperatorOverloadConstants._PLUS, params, INT);
        symbolTable.putFunction(INT_ID * OFFSET + 3, OperatorOverloadConstants._MINUS, params, INT);
        symbolTable.putFunction(INT_ID * OFFSET + 4, OperatorOverloadConstants._MULT, params, INT);
        symbolTable.putFunction(INT_ID * OFFSET + 5, OperatorOverloadConstants._DIV, params, INT);
        symbolTable.putFunction(INT_ID * OFFSET + 6, OperatorOverloadConstants._MOD, params, INT);
        symbolTable.putFunction(INT_ID * OFFSET + 7, OperatorOverloadConstants._GE, params, BOOL);
        symbolTable.putFunction(INT_ID * OFFSET + 8, OperatorOverloadConstants._GT, params, BOOL);
        symbolTable.putFunction(INT_ID * OFFSET + 9, OperatorOverloadConstants._LE, params, BOOL);
        symbolTable.putFunction(INT_ID * OFFSET + 10, OperatorOverloadConstants._LT, params, BOOL);
        symbolTable.putFunction(INT_ID * OFFSET + 11, OperatorOverloadConstants._PLUS, new ArrayList<>(), INT);
        symbolTable.putFunction(INT_ID * OFFSET + 12, OperatorOverloadConstants._MINUS, new ArrayList<>(), INT);
        symbolTable.putFunction(INT_ID * OFFSET + 13, OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(INT_ID * OFFSET + 14, OperatorOverloadConstants._TO, params, new Type(ARRAY, INT));
        symbolTable.closeScope();
    }

    public void addForms() {
        symbolTable.createClassScope(FORM_ID * OFFSET, FORM);
        List<Type> params = Collections.singletonList(FORM);
        symbolTable.putFunction(FORM_ID * OFFSET + 1, OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.closeScope();
    }

    public void addReals() {
        symbolTable.createClassScope(REAL_ID * OFFSET, REAL);
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
        symbolTable.putFunction(REAL_ID * OFFSET + 12, OperatorOverloadConstants._TO, params, new Type(ARRAY, REAL));
        symbolTable.closeScope();
    }

    public void addBools() {
        symbolTable.createClassScope(BOOL_ID * OFFSET, BOOL);
        List<Type> params = Collections.singletonList(BOOL);
        symbolTable.putFunction(BOOL_ID * OFFSET + 1, OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(BOOL_ID * OFFSET + 2, OperatorOverloadConstants._AND, params, BOOL);
        symbolTable.putFunction(BOOL_ID * OFFSET + 3, OperatorOverloadConstants._OR, params, BOOL);
        symbolTable.putFunction(BOOL_ID * OFFSET + 4, OperatorOverloadConstants._NOT, new ArrayList<>(), BOOL);
        symbolTable.closeScope();
    }

    public void addChars() {
        symbolTable.createClassScope(CHAR_ID * OFFSET, CHAR);
        List<Type> params = Collections.singletonList(CHAR);
        symbolTable.putFunction(CHAR_ID * OFFSET + 1, OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(CHAR_ID * OFFSET + 2, OperatorOverloadConstants._GE, params, BOOL);
        symbolTable.putFunction(CHAR_ID * OFFSET + 3, OperatorOverloadConstants._GT, params, BOOL);
        symbolTable.putFunction(CHAR_ID * OFFSET + 4, OperatorOverloadConstants._LE, params, BOOL);
        symbolTable.putFunction(CHAR_ID * OFFSET + 5, OperatorOverloadConstants._LT, params, BOOL);
        // TODO not sure about this last one, but it'll probably do
        symbolTable.putFunction(CHAR_ID * OFFSET + 6, OperatorOverloadConstants._TO, params, new Type(ARRAY, CHAR));
        symbolTable.closeScope();
    }

    public void addVoid() {
        symbolTable.createClassScope(VOID_ID * OFFSET, VOID);
        symbolTable.closeScope();
    }

    public void addArrays() {
        symbolTable.createClassScope(ARRAY_ID * OFFSET, ARRAY_TYPE);
        List<Type> params = Collections.singletonList(ARRAY_TYPE);
        symbolTable.putVariable(ARRAY_ID * OFFSET + 1, "size", INT);
        symbolTable.putFunction(ARRAY_ID * OFFSET + 2, OperatorOverloadConstants._EQUALS, params, BOOL);
        params = Collections.singletonList(INT);
        symbolTable.putFunction(ARRAY_ID * OFFSET + 3, "constructor", params, ARRAY_TYPE);
        symbolTable.closeScope();
    }

    public void addStrings() {
        symbolTable.createClassScope(STRING_ID * OFFSET, STRING);
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
        currentFunction = node.getType().getName();
        if (currentClass != null) {
            // TODO make sure it's safe to set class id for "this"
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
        if (ARRAY_TYPE.equals(type)) {
            if (type.depth() == 0) {
                error(node, "Must give parametric type on Array");
            } else if (type.contains(SymbolTableCreator.FORM)) {
                error(node, "Arrays of Forms not allowed");
            }
        }
        if (!symbolTable.putVariable(declaration.getId(), declaration.getIdentifier(), type, declaration.isConst())) {
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
        ExpressionNode designable = node.getDesignableExpression();
        ExpressionNode value = node.getValue();
        designable.accept(this);
        value.accept(this);
        Type expected = designable.getType();
        Type found = value.getType();
        Designator.AccessMethod method = node.getAccessMethod();
        switch (method) {
            case NONE: {
                ExpressionNode target = node.getTarget();
                Variable v = symbolTable.getVariable(target.getId());
                if (v != null && v.isConst) {
                    error(target, "Trying to assign a value to a constant variable");
                }
            }
            break;
            case FIELD: {
                VariableExpressionNode field = ((PointExpressionNode) designable).getField();
                Variable v = symbolTable.getVariable(field.getId());
                if (v != null && v.isConst) {
                    error(designable, "Trying to assign a value to a constant field");
                }
            }
            break;
        }
        if (expected != null) {
            if (FORM.equals(expected)) {
                error(designable, "Form objects cannot be reassigned");
            }
            if (found != null && !expected.equals(found)) {
                error(designable, "Expected " + expected + ", but found " + found);
            }
        }
    }

    private void checkAssignment(AssignmentStatementNode node) {
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        node.asExpression().accept(this);
    }

    @Override
    public void visit(ReturnStatementNode node) {
        ExpressionNode expression = node.getReturnExpression();
        expression.accept(this);
        Type found = expression.getType();
        if(found != null && !currentFunction.equals(found.getName())) {
            if (VOID.equals(found)) {
                error(node, "Missing return value");
            } else {
                error(expression, "Expected " + currentFunction + ", but found " + found);
            }
        }
        if(found == Type.WILDCARD && Type.isPrimitive(currentFunction)) {
            error(expression, "Primitive type cannot be null");
        }
        // TODO: in function calls a primitive cant be null
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
        VarDeclarationNode variable = node.getVariable();
        ExpressionNode iterable = node.getIterable();
        BlockStatementNode block = node.getBlock();
        // Create variable inside the loop scope
        symbolTable.openScope(block.getId());
        variable.asStatement().accept(this);
        symbolTable.closeScope();
        iterable.accept(this);
        Type type = variable.getType();
        Type found = iterable.getType();
        // Iterable must be of type Array<T> if variable is of type T
        if (type != null && found != null && (!found.equals(ARRAY_TYPE) || !type.equals(found.getParameter()))) {
            error(iterable, "Expected " + type + ", but found " + found);
        }
        block.accept(this);
    }

    @Override
    public void visit(PointExpressionNode node) {
        pointRecPath = null;
        pointRecDepth = 0;
        visitPointRecursive(node);
        endPointRecursion();
    }

    private void visitPointRecursive(PointExpressionNode node) {
        ExpressionNode host = node.getHost();
        if (host instanceof PointExpressionNode) {
            visitPointRecursive((PointExpressionNode) host);
        } else {
            host.accept(this);
        }
        Type hostType = host.getType();
        if (hostType == null) {
            // Couldn't set the type of the host so we won't know if it owns the field
            endPointRecursion();
            return;
        }
        VariableExpressionNode field = node.getField();
        String fieldName = field.get();
        if (FORM.equals(hostType)) {
            if (host instanceof VariableExpressionNode) {
                // Variables don't get inside the scope
                pointRecPath = symbolTable.openPreviousScope(symbolTable.getVariable(host.getId()).id);
                pointRecDepth = 1;
            }
            Variable v = symbolTable.getVariableHere(fieldName, field.getId());
            if (v == null) {
                error(field, "Form object does not have field " + fieldName);
            } else {
                field.setType(v.type);
                node.setType(v.type);
                if (FORM.equals(v.type)) {
                    // Get inside this Form as well; this scope must already exist
                    symbolTable.openScope(v.id);
                    pointRecDepth++;
                } else {
                    endPointRecursion();
                }
            }
        } else {
            // Stopped finding Forms: return to where we were at the beginning
            endPointRecursion();
            if (ARRAY_TYPE.equals(hostType)) {
                // Array<?>.size is the only available property
                if (ARRAY_SIZE.equals(fieldName)) {
                    // Go to Array<*> class
                    Deque<Integer> arrayPath = symbolTable.openClassScope(ARRAY_TYPE);
                    symbolTable.getVariableHere(ARRAY_SIZE, field.getId());
                    // And then return again to where we were at the beginning
                    symbolTable.closeScope();
                    symbolTable.restoreScope(arrayPath);
                    node.setType(INT);
                } else {
                    error(field, "Class " + hostType + " does not have field " + fieldName);
                }
            } else {
                if (currentClass != null && currentClass.equals(hostType.getName())) {
                    // Can only access a private field if we are already inside that class
                    // Reopen class as we might be inside a method
                    Deque<Integer> classPath = symbolTable.openClassScope(hostType);
                    Variable v = symbolTable.getVariableHere(fieldName, field.getId());
                    // And then return again to where we were at the beginning
                    symbolTable.closeScope();
                    symbolTable.restoreScope(classPath);
                    if (v == null) {
                        error(field, "Class " + hostType + " does not have field " + fieldName);
                    } else {
                        field.setType(v.type);
                        node.setType(v.type);
                        if (FORM.equals(v.type)) {
                            // Open Form's scope
                            pointRecPath = symbolTable.openPreviousScope(v.id);
                            pointRecDepth = 1;
                        }
                    }
                } else {
                    // TODO check if it actually exists or leave it like this?
                    error(field, "Field " + fieldName + " in class " + hostType +
                            " might be private - use getters or setters");
                }
            }
        }
    }

    private void endPointRecursion() {
        while (pointRecDepth > 0) {
            symbolTable.closeScope();
            pointRecDepth--;
        }
        symbolTable.restoreScope(pointRecPath);
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
            String functionName = ve.get();
            if (OperatorOverloadConstants._ID.equals(functionName)) {
                // Special case === - check types match (argumentTypes.size() must be 2)
                if (!argumentTypes.get(0).realEquals(argumentTypes.get(1))) {
                    error(node, "Identity operator === must be applied to objects of the same type, but found "
                            + argumentTypes.get(0) + " and " + argumentTypes.get(1));
                } else {
                    // Just register
                    symbolTable.getFunction(functionName, argumentTypes, node.getId());
                }
            } else {
                Function f = symbolTable.getFunction(ve.get(), argumentTypes, node.getId());
                if (f == null) {
                    error(node, "Couldn't find function " + functionName + " applied to arguments " +
                            argumentTypes.stream().map(Type::toString).collect(Collectors.joining(", ")));
                }
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
            if (path == null) {
                // Couldn't find a path to the class so it doesn't exist (probably error has already been notified (?))
                // Scope hasn't moved so we don't need to restore anything
                return;
            }
            VariableExpressionNode ve = pe.getField();
            String functionName = ve.get();
            if (ARRAY_TYPE.equals(receiverType)) {
                // Only method for arrays
                if (OperatorOverloadConstants._EQUALS.equals(functionName)) {
                    if (argumentTypes.size() != 1) {
                        error(ve, "Cannot apply " + functionName + " to " + receiver + " and " +
                                argumentTypes.stream().map(Type::toString).collect(Collectors.joining(", ")));
                    } else if (receiverType.realEquals(argumentTypes.get(0))) {
                        error(node.getLexeme() == null ? ve : node,
                                "Cannot apply to " + receiver + " and " + argumentTypes.get(0));
                    } else {
                        // Ignore result as we already know which function it is
                        symbolTable.getFunctionHere(functionName, argumentTypes, node.getId());
                    }
                } else {
                    error(node.getLexeme() == null ? ve : node,
                            "Class " + receiverType + " does not have method " + functionName);
                }
            } else {
                Function f = symbolTable.getFunctionHere(functionName, argumentTypes, node.getId());
                if (f == null) {
                    error(node.getLexeme() == null ? ve : node, "Couldn't find method " + functionName +
                            " in class" + receiverType + " applied to arguments " +
                            argumentTypes.stream().map(Type::toString).collect(Collectors.joining(", ")));
                }
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
                } else if (!parameter.realEquals(t)) {
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
        if (ARRAY_TYPE.equals(type)) {
            // Array constructors may be Array<*>(Int, ..., Int) depending on dimensions
            int dimensions = type.depth();
            if (dimensions == 0) {
                error(node, "Must give parametric type on Array constructor");
            } else if (argumentTypes.size() == 0) {
                error(node, "Must give length of at least the first dimension of the array");
            } else if (argumentTypes.size() > dimensions) {
                error(node, "Given more arguments than array dimension on Array constructor");
            } else if (argumentTypes.stream().anyMatch(t -> !INT.equals(t))) {
                error(node, "Array constructor arguments can only be integers");
            } else {
                // Just register, we already know the function exists
                symbolTable.getFunctionHere("constructor", Collections.singletonList(INT), node.getId());
            }
        } else {
            Function f = symbolTable.getFunctionHere("constructor", argumentTypes, node.getId());
            if (f == null) {
                error(node, "Couldn't find constructor of class " + type +
                        " with the following argument types: " +
                        argumentTypes.stream().map(Type::toString).collect(Collectors.joining(", ")));
            }
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
