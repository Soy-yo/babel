package syntactical.ast.visitors;

import error.SemanticException;
import syntactical.Defaults;
import syntactical.OperatorOverloadConstants;
import syntactical.ast.*;

import java.util.*;
import java.util.stream.Collectors;

public class SymbolTableCreator implements Visitor {

    private final ProgramNode root;
    private final Map<DeclarationNode, String> fileErrorHandling;
    private final SymbolTable symbolTable;
    private String currentFile;
    private String currentClass;
    private Type currentFunctionType;
    private int currentFormDepth;
    private Deque<Integer> pointRecPath;
    private int pointRecDepth;

    private int errors;

    public SymbolTableCreator(ProgramNode root, Map<DeclarationNode, String> fileErrorHandling) {
        this.root = root;
        this.fileErrorHandling = fileErrorHandling;
        this.symbolTable = new SymbolTable();
        this.currentFile = null;
        this.currentClass = null;
        this.currentFunctionType = null;
        this.currentFormDepth = 0;
        this.pointRecPath = null;
        this.pointRecDepth = 0;
        initializeTable();
    }

    private void initializeTable() {
        // _ID(T x, S y) = dir(x) == dir(y) <- we can ignore types here
        symbolTable.putFunction(Defaults.IDENTITY_ID, OperatorOverloadConstants._ID,
                Arrays.asList(Type.WILDCARD, Type.WILDCARD), Defaults.BOOL);
        addIntegers();
        addForms();
        addReals();
        addBools();
        addChars();
        addVoid();
        addArrays();
        addStrings();
        SymbolTableInitializer initializer = new SymbolTableInitializer(root, symbolTable, fileErrorHandling);
        errors = initializer.start();
    }

    public void addIntegers() {
        symbolTable.createClassScope(Defaults.Int.ID, Defaults.INT);
        List<Type> params = Collections.singletonList(Defaults.INT);
        symbolTable.putFunction(Defaults.Int.PLUS_ID, OperatorOverloadConstants._PLUS, params, Defaults.INT);
        symbolTable.putFunction(Defaults.Int.MINUS_ID, OperatorOverloadConstants._MINUS, params, Defaults.INT);
        symbolTable.putFunction(Defaults.Int.MULT_ID, OperatorOverloadConstants._MULT, params, Defaults.INT);
        symbolTable.putFunction(Defaults.Int.DIV_ID, OperatorOverloadConstants._DIV, params, Defaults.INT);
        symbolTable.putFunction(Defaults.Int.MOD_ID, OperatorOverloadConstants._MOD, params, Defaults.INT);
        symbolTable.putFunction(Defaults.Int.GE_ID, OperatorOverloadConstants._GE, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Int.GT_ID, OperatorOverloadConstants._GT, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Int.LE_ID, OperatorOverloadConstants._LE, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Int.LT_ID, OperatorOverloadConstants._LT, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Int.UNARY_PLUS_ID, OperatorOverloadConstants._PLUS, new ArrayList<>(),
                Defaults.INT);
        symbolTable.putFunction(Defaults.Int.UNARY_MINUS_ID, OperatorOverloadConstants._MINUS, new ArrayList<>(),
                Defaults.INT);
        symbolTable.putFunction(Defaults.Int.EQUALS_ID, OperatorOverloadConstants._EQUALS, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Int.TO_ID, OperatorOverloadConstants._TO, params, new Type(Defaults.ARRAY_STR,
                Defaults.INT));
        symbolTable.closeScope();
    }

    public void addForms() {
        symbolTable.createClassScope(Defaults.Form.ID, Defaults.FORM);
        List<Type> params = Collections.singletonList(Defaults.FORM);
        symbolTable.putFunction(Defaults.Form.EQUALS_ID, OperatorOverloadConstants._EQUALS, params, Defaults.BOOL);
        symbolTable.closeScope();
    }

    public void addReals() {
        symbolTable.createClassScope(Defaults.Real.ID, Defaults.REAL);
        List<Type> params = Collections.singletonList(Defaults.REAL);
        symbolTable.putFunction(Defaults.Real.PLUS_ID, OperatorOverloadConstants._PLUS, params, Defaults.REAL);
        symbolTable.putFunction(Defaults.Real.MINUS_ID, OperatorOverloadConstants._MINUS, params, Defaults.REAL);
        symbolTable.putFunction(Defaults.Real.MULT_ID, OperatorOverloadConstants._MULT, params, Defaults.REAL);
        symbolTable.putFunction(Defaults.Real.DIV_ID, OperatorOverloadConstants._DIV, params, Defaults.REAL);
        symbolTable.putFunction(Defaults.Real.GE_ID, OperatorOverloadConstants._GE, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Real.GT_ID, OperatorOverloadConstants._GT, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Real.LE_ID, OperatorOverloadConstants._LE, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Real.LT_ID, OperatorOverloadConstants._LT, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Real.UNARY_PLUS_ID, OperatorOverloadConstants._PLUS, new ArrayList<>(),
                Defaults.REAL);
        symbolTable.putFunction(Defaults.Real.UNARY_MINUS_ID, OperatorOverloadConstants._MINUS, new ArrayList<>(),
                Defaults.REAL);
        symbolTable.putFunction(Defaults.Real.EQUALS_ID, OperatorOverloadConstants._EQUALS, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Real.TO_ID, OperatorOverloadConstants._TO, params, new Type(Defaults.ARRAY_STR,
                Defaults.REAL));
        symbolTable.closeScope();
    }

    public void addBools() {
        symbolTable.createClassScope(Defaults.Bool.ID, Defaults.BOOL);
        List<Type> params = Collections.singletonList(Defaults.BOOL);
        symbolTable.putFunction(Defaults.Bool.EQUALS_ID, OperatorOverloadConstants._EQUALS, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Bool.AND_ID, OperatorOverloadConstants._AND, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Bool.OR_ID, OperatorOverloadConstants._OR, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Bool.NOT_ID, OperatorOverloadConstants._NOT, new ArrayList<>(), Defaults.BOOL);
        symbolTable.closeScope();
    }

    public void addChars() {
        symbolTable.createClassScope(Defaults.Char.ID, Defaults.CHAR);
        List<Type> params = Collections.singletonList(Defaults.CHAR);
        symbolTable.putFunction(Defaults.Char.EQUALS_ID, OperatorOverloadConstants._EQUALS, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Char.GE_ID, OperatorOverloadConstants._GE, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Char.GT_ID, OperatorOverloadConstants._GT, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Char.LE_ID, OperatorOverloadConstants._LE, params, Defaults.BOOL);
        symbolTable.putFunction(Defaults.Char.LT_ID, OperatorOverloadConstants._LT, params, Defaults.BOOL);
        // TODO not sure about this last one, but it'll probably do
        symbolTable.putFunction(Defaults.Char.TO_ID, OperatorOverloadConstants._TO, params, new Type(Defaults.ARRAY_STR,
                Defaults.CHAR));
        symbolTable.closeScope();
    }

    public void addVoid() {
        symbolTable.createClassScope(Defaults.Void.ID, Defaults.VOID);
        symbolTable.closeScope();
    }

    public void addArrays() {
        symbolTable.createClassScope(Defaults.Array.ID, Defaults.ARRAY);
        List<Type> params = Collections.singletonList(Defaults.ARRAY);
        symbolTable.putVariable(Defaults.Array.SIZE_ID, Defaults.ARRAY_SIZE, Defaults.INT);
        symbolTable.putFunction(Defaults.Array.EQUALS_ID, OperatorOverloadConstants._EQUALS, params, Defaults.BOOL);
        params = Collections.singletonList(Defaults.INT);
        symbolTable.putFunction(Defaults.Array.CONSTRUCTOR_ID, "constructor", params, Defaults.ARRAY);
        symbolTable.closeScope();
    }

    public void addStrings() {
        symbolTable.createClassScope(Defaults.String.ID, Defaults.STRING);
        symbolTable.closeScope();
    }

    public SymbolTable create() {
        root.accept(this);
        return symbolTable;
    }

    public int errors() {
        return errors;
    }

    @Override
    public void visit(ProgramNode node) {
        if (node.root() != null) {
            for (DeclarationNode n : node.root()) {
                // Update current file if this node is the first node of that file
                if (fileErrorHandling.containsKey(n)) {
                    currentFile = fileErrorHandling.get(n);
                }
                n.accept(this);
            }
        }
    }

    @Override
    public void visit(VarDeclarationNode node) {
        Type type = node.getType();
        ExpressionNode initialValue = node.getInitialValue();
        if (initialValue != null) {
            if (Defaults.FORM.equals(type)) {
                if (initialValue instanceof AnonymousObjectConstructorExpressionNode) {
                    symbolTable.openScope(node.getId());
                    globalFormVisit((AnonymousObjectConstructorExpressionNode) initialValue);
                    symbolTable.closeScope();
                }
            } else {
                initialValue.accept(this);
                Type found = initialValue.getType();
                // If initial value was null set its type to the correct one
                if (found == Type.WILDCARD) {
                    checkNullOnPrimitive(found, type, initialValue);
                    initialValue.setType(type);
                } else if (found != null && !type.realEquals(found)) {
                    error(initialValue, "Expected " + type + ", but found " + found);
                }
            }
        }
    }

    private void globalFormVisit(AnonymousObjectConstructorExpressionNode node) {
        // Same as normal visit but it uses the Declaration method that doesn't put variables into symbol table
        if (node.getFields() != null) {
            currentFormDepth++;
            for (DeclarationNode n : node.getFields()) {
                n.accept(this);
            }
            currentFormDepth--;
        }
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        BlockStatementNode block = node.getCode();
        // Create block here to add all parameters to scope
        symbolTable.openScope(block.getId());
        if (currentClass != null) {
            // TODO make sure it's safe to set class id for "this"
            int thisId = symbolTable.getCurrentScopeId();
            symbolTable.putVariable(thisId, Defaults.THIS, new Type(currentClass));
        }
        for (VarDeclarationNode param : node.getParameters()) {
            symbolTable.putVariable(param.getId(), param.getIdentifier(), param.getType());
        }
        symbolTable.closeScope();
        currentFunctionType = node.getType();
        block.accept(this);
        currentFunctionType = null;
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
        if (node.root() != null) {
            for (StatementNode n : node.root()) {
                n.accept(this);
            }
        }
        symbolTable.closeScope();
    }

    @Override
    public void visit(VarDeclarationStatementNode node) {
        VarDeclarationNode declaration = node.asDeclaration();
        Type type = declaration.getType();
        if (Defaults.ARRAY.equals(type)) {
            if (type.depth() == 0) {
                error(node, "Must give parametric type on Array");
            } else if (type.contains(Defaults.FORM)) {
                error(node, "Arrays of Forms not allowed");
            }
        }
        ExpressionNode initialValue = declaration.getInitialValue();
        if (Defaults.FORM.equals(type)) {
            // Generate scope for object definition
            symbolTable.openScope(node.getId());
            if (initialValue instanceof AnonymousObjectConstructorExpressionNode) {
                initialValue.accept(this);
            } else {
                error(node, "Expected an anonymous object definition");
            }
            symbolTable.closeScope();
        } else {
            initialValue.accept(this);
            Type found = initialValue.getType();
            if (found == Type.WILDCARD) {
                checkNullOnPrimitive(found, type, initialValue);
                initialValue.setType(type);
            } else if (found != null && !type.realEquals(found)) {
                error(initialValue, "Expected " + type + ", but found " + found);
            }
        }
        // Put variable at the end so it cannot be used inside the initialization
        if (!symbolTable.putVariable(declaration.getId(), declaration.getIdentifier(), type, declaration.isConst())) {
            error(node, "Variable already defined in this scope");
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
                Variable v = symbolTable.getVariableById(target.getId());
                if (v != null && v.isConst) {
                    error(target, "Trying to assign a value to a constant variable");
                }
            }
            break;
            case FIELD: {
                VariableExpressionNode field = ((PointExpressionNode) designable).getField();
                Variable v = symbolTable.getVariableById(field.getId());
                if (v != null && v.isConst) {
                    error(designable, "Trying to assign a value to a constant field");
                }
            }
            break;
        }
        if (expected != null) {
            if (Defaults.FORM.equals(expected)) {
                error(designable, "Form objects cannot be reassigned");
            }
            if (found == Type.WILDCARD) {
                checkNullOnPrimitive(found, expected, value);
                value.setType(expected);
            } else if (found != null && !expected.realEquals(found)) {
                error(value, "Expected " + expected + ", but found " + found);
            }
        }
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
        if (found != null && !found.realEquals(currentFunctionType)) {
            if (Defaults.VOID.equals(found)) {
                error(node, "Missing return value");
            } else {
                error(expression, "Expected " + currentFunctionType + ", but found " + found);
            }
        }
        if (found == Type.WILDCARD) {
            checkNullOnPrimitive(found, currentFunctionType, expression);
            expression.setType(currentFunctionType);
        }
    }

    @Override
    public void visit(IfElseStatementNode node) {
        ExpressionNode condition = node.getCondition();
        condition.accept(this);
        Type found = condition.getType();
        if (found != null && !Defaults.BOOL.realEquals(found)) {
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
        if (type != null && !type.isPrimitive()) {
            error(target, "Expected a primitive type, but found " + type);
        }
        Set<Integer> seen = new HashSet<>();
        for (Map.Entry<ConstantExpressionNode, StatementNode> e : node.getCases().entrySet()) {
            ConstantExpressionNode key = e.getKey();
            key.accept(this);
            Type t = key.getType();
            if (t == Type.WILDCARD) {
                error(key, "null is not a valid key for a switch statement");
            } else if (Defaults.DEFAULT.realEquals(t)) {
                // Underscore: sets target's type
                key.setType(type);
                if (!seen.add(null)) {
                    error(key, "Repeated switch branch");
                }
            } else if (type != null && !type.realEquals(t)) {
                error(key, "Expected " + type + ", but found " + t);
            } else if (!seen.add(key.getValue())) {
                error(key, "Repeated switch branch");
            }
            e.getValue().accept(this);
        }
    }

    @Override
    public void visit(WhileStatementNode node) {
        ExpressionNode condition = node.getCondition();
        condition.accept(this);
        Type found = condition.getType();
        if (found != null && !Defaults.BOOL.realEquals(found)) {
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
        if (type != null && found != null) {
            if (found == Type.WILDCARD) {
                error(iterable, "null is not iterable");
            } else if (!found.equals(Defaults.ARRAY) || !type.equals(found.getParameter())) {
                error(iterable, "Expected " + Defaults.ARRAY_STR + "<" + type + ">, but found " + found);
            }
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
        if (hostType == Type.WILDCARD) {
            // Host is null so it doesn't have any fields
            error(host, "Can't apply point operator to null element");
            endPointRecursion();
            return;
        }
        VariableExpressionNode field = node.getField();
        String fieldName = field.get();
        if (Defaults.FORM.equals(hostType)) {
            if (host instanceof VariableExpressionNode) {
                // Variables don't get inside the scope
                pointRecPath = symbolTable.openPreviousScope(symbolTable.getVariableById(host.getId()).id);
                pointRecDepth = 1;
            }
            Variable v = symbolTable.getVariableHere(fieldName, field.getId());
            if (v == null) {
                error(field, "Form object does not have field " + fieldName);
            } else {
                field.setType(v.type);
                node.setType(v.type);
                if (Defaults.FORM.equals(v.type)) {
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
            if (Defaults.ARRAY.equals(hostType)) {
                // Array<?>.size is the only available property
                if (Defaults.ARRAY_SIZE.equals(fieldName)) {
                    // Go to Array<*> class
                    Deque<Integer> arrayPath = symbolTable.openClassScope(Defaults.ARRAY);
                    symbolTable.getVariableHere(Defaults.ARRAY_SIZE, field.getId());
                    // And then return again to where we were at the beginning
                    symbolTable.closeScope();
                    symbolTable.restoreScope(arrayPath);
                    field.setType(Defaults.INT);
                    node.setType(Defaults.INT);
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
                        if (Defaults.FORM.equals(v.type)) {
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
        if (pointRecPath == null) {
            return;
        }
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
        if (arrayType != null) {
            if (arrayType == Type.WILDCARD) {
                error(array, "Can't apply array operator to null element");
            } else if (!Defaults.ARRAY.equals(arrayType)) {
                error(array, "Expecting an Array, but found " + arrayType);
            }
            node.setType(arrayType.getParameter());
        }
        if (indexType != null && !Defaults.INT.realEquals(indexType)) {
            error(index, "Array indices must be integers, but found " + indexType);
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
                Type arg1 = argumentTypes.get(0);
                Type arg2 = argumentTypes.get(1);
                // Set correct types first so realEquals works
                if (arg1 == Type.WILDCARD) {
                    checkNullOnPrimitive(arg1, arg2, arguments.get(0));
                    arguments.get(0).setType(arg2);
                    arg1 = arg2;
                } else if (arg2 == Type.WILDCARD) {
                    checkNullOnPrimitive(arg2, arg1, arguments.get(1));
                    arguments.get(1).setType(arg1);
                    arg2 = arg1;
                }
                // Special case === - check types match (argumentTypes.size() must be 2)
                if (!arg1.realEquals(arg2)) {
                    error(node, "Identity operator === must be applied to objects of the same type, but found "
                            + argumentTypes.get(0) + " and " + argumentTypes.get(1));
                } else {
                    // Just register
                    symbolTable.getFunction(functionName, argumentTypes, node.getId());
                    ve.setType(Defaults.BOOL);
                    node.setType(Defaults.BOOL);
                }
            } else {
                Function f = symbolTable.getFunction(ve.get(), argumentTypes, node.getId());
                if (f == null) {
                    error(ve, "Couldn't find function " + functionName + " applied to arguments " +
                            argumentTypes);
                } else {
                    checkNullArguments(arguments, f.parameters);
                    ve.setType(f.returnType);
                    node.setType(f.returnType);
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
            if (receiverType == Type.WILDCARD) {
                // Host is null so it doesn't have any fields
                error(receiver, "Can't apply point operator to null element");
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
            if (Defaults.ARRAY.equals(receiverType)) {
                // Only method for arrays
                if (OperatorOverloadConstants._EQUALS.equals(functionName)) {
                    if (argumentTypes.size() != 1) {
                        error(ve, "Cannot apply " + functionName + " to " + receiver + " and " + argumentTypes);
                    } else if (receiverType.realEquals(argumentTypes.get(0))) {
                        error(node.getLexeme() == null ? ve : node,
                                "Cannot apply to " + receiver + " and " + argumentTypes.get(0));
                    } else {
                        // Ignore result as we already know which function it is
                        symbolTable.getFunctionHere(functionName, argumentTypes, node.getId());
                        ve.setType(Defaults.BOOL);
                        pe.setType(Defaults.BOOL);
                        node.setType(Defaults.BOOL);
                    }
                } else {
                    error(node.getLexeme() == null ? ve : node,
                            "Class " + receiverType + " does not have method " + functionName);
                }
            } else {
                Function f = symbolTable.getFunctionHere(functionName, argumentTypes, node.getId());
                if (f == null) {
                    error(node.getLexeme() == null ? ve : node, "Couldn't find method " + functionName +
                            " in class " + receiverType + " applied to arguments " + argumentTypes);
                } else {
                    checkNullArguments(arguments, f.parameters);
                    ve.setType(f.returnType);
                    pe.setType(f.returnType);
                    node.setType(f.returnType);
                }
            }
            symbolTable.closeScope();
            symbolTable.restoreScope(path);
        } else {
            error(function, "Not callable");
        }
    }

    private void checkNullArguments(List<ExpressionNode> found, Type[] expected) {
        // They have to be the same length
        for (int i = 0; i < expected.length; i++) {
            Type f = found.get(i).getType();
            if (f == Type.WILDCARD) {
                checkNullOnPrimitive(f, expected[i], found.get(i));
                found.get(i).setType(expected[i]);
            }
        }
    }

    @Override
    public void visit(VariableExpressionNode node) {
        String name = node.get();
        Deque<Integer> path = closeForm();
        Variable variable = symbolTable.getVariable(name, node.getId());
        if (variable == null) {
            error(node, "Undefined variable");
        } else {
            node.setType(variable.type);
        }
        // Restore Form info
        currentFormDepth = path.size();
        symbolTable.restoreScope(path);
    }

    private Deque<Integer> closeForm() {
        Deque<Integer> stack = new ArrayDeque<>();
        while (currentFormDepth > 0) {
            stack.push(symbolTable.getCurrentScopeId());
            symbolTable.closeScope();
            currentFormDepth--;
        }
        return stack;
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
        // Just to notify error on something if everything is null
        ExpressionNode firstNull = null;
        boolean allNulls = true;
        for (ExpressionNode n : elements) {
            n.accept(this);
            Type t = n.getType();
            if (t == Type.WILDCARD) {
                if (firstNull == null) {
                    firstNull = n;
                }
                if (parameter != null) {
                    checkNullOnPrimitive(t, parameter, n);
                    n.setType(parameter);
                }
                continue;
            } else {
                allNulls = false;
            }
            if (t != null) {
                if (parameter == null) {
                    parameter = t;
                } else if (!parameter.realEquals(t)) {
                    error(n, "Elements in list constructor are not of the same type: expected " + parameter +
                            ", but found " + t);
                }
            }
        }
        if (parameter == null && allNulls && firstNull != null) {
            error(firstNull, "Unknown type of array as all elements are null - " +
                    "use Array<*> constructor instead");
        } else {
            // Finally check any pending null
            for (ExpressionNode n : elements) {
                if (n.getType() == Type.WILDCARD) {
                    checkNullOnPrimitive(n.getType(), parameter, n);
                    n.setType(parameter);
                }
            }
            node.setType(new Type(Defaults.ARRAY_STR, parameter));
        }
    }

    @Override
    public void visit(AnonymousObjectConstructorExpressionNode node) {
        // Scope must already be open
        if (node.getFields() != null) {
            currentFormDepth++;
            for (DeclarationNode n : node.getFields()) {
                // Use Statement method as it's the one that adds variables to symbol table
                ((VarDeclarationNode) n).asStatement().accept(this);
            }
            currentFormDepth--;
        }
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
        if (Defaults.ARRAY.equals(type)) {
            // Array constructors may be Array<*>(Int, ..., Int) depending on dimensions
            int dimensions = type.depth();
            if (dimensions == 0) {
                error(node, "Must give parametric type on Array constructor");
            } else if (argumentTypes.size() == 0) {
                error(node, "Must give length of at least the first dimension of the array");
            } else if (argumentTypes.size() > dimensions) {
                error(node, "Given more arguments than array dimension on Array constructor");
            } else if (argumentTypes.stream().anyMatch(t -> !Defaults.INT.realEquals(t))) {
                error(node, "Array constructor arguments can only be integers");
            } else {
                // Just register, we already know the function exists
                symbolTable.getFunctionHere("constructor", Collections.singletonList(Defaults.INT), node.getId());
            }
        } else {
            Function f = symbolTable.getFunctionHere("constructor", argumentTypes, node.getId());
            if (f == null) {
                error(node, "Couldn't find constructor of class " + type +
                        " with the following argument types: " + argumentTypes);
            } else {
                checkNullArguments(node.getArguments(), f.parameters);
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
        SemanticException error = new SemanticException(currentFile, node.getLexeme(), message);
        System.err.println("[ERROR] " + error.getMessage());
    }

    private void checkNullOnPrimitive(Type possiblyNull, Type possiblyPrimitive, ASTNode node) {
        if (possiblyNull == Type.WILDCARD && possiblyPrimitive.isPrimitive()) {
            error(node, "Primitive type cannot be null");
        }
    }

}
