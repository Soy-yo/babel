package syntactical.ast.visitors;

import error.SemanticException;
import syntactical.OperatorOverloadConstants;
import syntactical.ast.*;

import java.util.*;

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
    // TODO ARRAY_TYPE isn't right, just provisional
    protected static final String ARRAY = "Array";
    protected static final Type ARRAY_TYPE = new Type(ARRAY, Type.WILDCARD);
    protected static final String THIS = "this";
    protected static final String ARRAY_SIZE = "size";
    // TODO I don't really know what the IDs should be
    private static final int INT_ID = 0;
    private static final int REAL_ID = 1;
    private static final int BOOL_ID = 2;
    private static final int CHAR_ID = 3;
    private static final int VOID_ID = 4;
    private static final int FORM_ID = 5;
    private static final int ARRAY_ID = 6;
    private static final int STRING_ID = 7;

    private final ProgramNode root;
    private final SymbolTable symbolTable;
    private int errors;

    // TODO caso especial para Array<?> al comprobar tipos illo si comentas en inglés comentas en
    //  inglés

    public SymbolTableCreator(ProgramNode root) {
        this.root = root;
        this.symbolTable = new SymbolTable();
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
        symbolTable.putFunction(OperatorOverloadConstants._PLUS, params, INT);
        symbolTable.putFunction(OperatorOverloadConstants._MINUS, params, INT);
        symbolTable.putFunction(OperatorOverloadConstants._MULT, params, INT);
        symbolTable.putFunction(OperatorOverloadConstants._DIV, params, INT);
        symbolTable.putFunction(OperatorOverloadConstants._MOD, params, INT);
        symbolTable.putFunction(OperatorOverloadConstants._GE, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._GT, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._LE, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._LT, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._PLUS, new ArrayList<>(), INT);
        symbolTable.putFunction(OperatorOverloadConstants._MINUS, new ArrayList<>(), INT);
        symbolTable.putFunction(OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._NEQ, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._ID, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._TO, params, new Type(ARRAY, INT));
        symbolTable.closeScope();
    }

    public void addForms() {
        symbolTable.createClassScope(FORM_ID, FORM);
        List<Type> params = Collections.singletonList(FORM);
        symbolTable.putFunction(OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._NEQ, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._ID, params, BOOL);
        symbolTable.closeScope();
    }

    public void addReals() {
        symbolTable.createClassScope(REAL_ID, REAL);
        List<Type> params = Collections.singletonList(REAL);
        symbolTable.putFunction(OperatorOverloadConstants._PLUS, params, REAL);
        symbolTable.putFunction(OperatorOverloadConstants._MINUS, params, REAL);
        symbolTable.putFunction(OperatorOverloadConstants._MULT, params, REAL);
        symbolTable.putFunction(OperatorOverloadConstants._DIV, params, REAL);
        symbolTable.putFunction(OperatorOverloadConstants._GE, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._GT, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._LE, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._LT, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._PLUS, new ArrayList<>(), REAL);
        symbolTable.putFunction(OperatorOverloadConstants._MINUS, new ArrayList<>(), REAL);
        symbolTable.putFunction(OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._NEQ, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._ID, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._TO, params, new Type(ARRAY, REAL));
        symbolTable.closeScope();
    }

    public void addBools() {
        symbolTable.createClassScope(BOOL_ID, BOOL);
        List<Type> params = Collections.singletonList(BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._NEQ, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._ID, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._AND, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._OR, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._NOT, new ArrayList<>(), BOOL);
        symbolTable.closeScope();
    }

    public void addChars() {
        symbolTable.createClassScope(CHAR_ID, CHAR);
        List<Type> params = Collections.singletonList(CHAR);
        symbolTable.putFunction(OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._NEQ, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._ID, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._GE, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._GT, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._LE, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._LT, params, BOOL);
        // TODO not sure about this last one, but it'll probably do
        symbolTable.putFunction(OperatorOverloadConstants._TO, params, new Type(ARRAY, CHAR));
        symbolTable.closeScope();
    }

    public void addVoid() {
        symbolTable.createClassScope(VOID_ID, VOID);
        symbolTable.closeScope();
    }

    public void addArrays() {
        symbolTable.createClassScope(ARRAY_ID, ARRAY_TYPE);
        symbolTable.putVariable("Size", INT);
        List<Type> params = new ArrayList<>();
        params.add(INT);
        symbolTable.putFunction(ARRAY, params, VOID);
        symbolTable.putFunction("Access", params, ARRAY_TYPE.getParameter());
        params.clear();
        params.add(ARRAY_TYPE);
        symbolTable.putFunction(OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._NEQ, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._ID, params, BOOL);
        symbolTable.closeScope();
    }

    public void addStrings() {
        symbolTable.createClassScope(STRING_ID, STRING);
        List<Type> params = new ArrayList<>();
        params.add(STRING);
        symbolTable.putFunction(OperatorOverloadConstants._EQUALS, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._NEQ, params, BOOL);
        symbolTable.putFunction(OperatorOverloadConstants._ID, params, BOOL);
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
        node.getCode().accept(this);
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        visit((FunctionDeclarationNode) node);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        symbolTable.openScope(node.getId());
        if (node.getContentRoot() != null) {
            for (DeclarationNode n : node.getContentRoot()) {
                n.accept(this);
            }
        }
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
        if (!symbolTable.putVariable(declaration.getIdentifier(), type)) {
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
        // If null then there were errors before that has already been notified
        if (expected != null && found != null && !expected.equals(found)) {
            error(designable, "Expected " + expected + ", but found " + found);
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
        // TODO check type (for the moment variable is T and iterable should be Array<T>)
        node.getVariable().accept(this);
        node.getIterable().accept(this);
        node.getBlock().accept(this);
    }

    @Override
    public void visit(PointExpressionNode node) {
        ExpressionNode host = node.getHost();
        host.accept(this);
        Type hostType = host.getType();
        if (hostType == null) {
            // TODO couldn't know host's type so we can't know this expression's type (check at the end)
        }
        VariableExpressionNode field = node.getField();
        if (FORM.equals(hostType)) {

        } else if (ARRAY.equals(hostType.getName())) {
            // Array<?>.size is the only available property
            if (ARRAY_SIZE.equals(field.get())) {
                node.setType(INT);
            } else {
                // TODO error: whatever field it is, Array<?> does not have it
            }
        } else {

        }
    }

    @Override
    public void visit(ArrayAccessExpressionNode node) {

    }

    @Override
    public void visit(FunctionCallExpressionNode node) {

    }

    @Override
    public void visit(VariableExpressionNode node) {
        String name = node.get();
        Type type = symbolTable.getVariable(name);
        if (type == null) {
            // TODO check at the end etc
        }
        node.setType(type);
    }

    @Override
    public void visit(ConstantExpressionNode node) {
        // Probably nothing to do - type already set
    }

    @Override
    public void visit(ListConstructorExpressionNode node) {
        Type parameter = null;
        List<ExpressionNode> elements = node.getElements();
        if (elements.isEmpty()) {
            // Empty list: array of any type
            // TODO others should check the unknown type or don't allow empty lists
            node.setType(ARRAY_TYPE);
        }
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

    private void error(ASTNode node, String message) {
        errors++;
        SemanticException error = new SemanticException(node.getLexeme(), message);
        System.err.println("[ERROR] " + error.getMessage());
    }

}
