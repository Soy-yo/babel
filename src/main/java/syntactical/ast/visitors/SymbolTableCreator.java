package syntactical.ast.visitors;

import lexical.LexicalUnit;
import sun.tools.jstat.Operator;
import syntactical.OperatorOverloadConstants;
import syntactical.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SymbolTableCreator implements Visitor {

    // TODO probably move some of this constants to another class (yes, definitely)
    private static final Type FORM = new Type(new LexicalUnit("Form"));
    private static final Type INT = new Type(new LexicalUnit("Int"));
    private static final Type REAL = new Type(new LexicalUnit("Real"));
    private static final Type BOOL = new Type(new LexicalUnit("Bool"));
    private static final Type CHAR = new Type(new LexicalUnit("Char"));
    private static final Type VOID = new Type(new LexicalUnit("Void"));
    private static final Type STRING = new Type(new LexicalUnit("String"));
    // TODO ARRAY_TYPE isn't right, just provisional
    private static final Type ARRAY_TYPE = new Type(new LexicalUnit("Array"));
    private static final String THIS = "this";
    private static final String ARRAY = "Array";
    private static final String ARRAY_SIZE = "size";
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
      symbolTable.createClassScope(INT_ID,INT);
      List<Type> params = new ArrayList<>();
      params.add(INT);
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
      symbolTable.putFunction(OperatorOverloadConstants._TO, params, ARRAY_TYPE);
      // TODO tostring and maybe some others
      symbolTable.closeScope();
    }

    public void addForms() {
      symbolTable.createClassScope(FORM_ID, FORM);
      List<Type> params = new ArrayList<>();
      params.add(FORM);
      symbolTable.putFunction(OperatorOverloadConstants._EQUALS, params, BOOL);
      symbolTable.putFunction(OperatorOverloadConstants._NEQ, params, BOOL);
      symbolTable.putFunction(OperatorOverloadConstants._ID, params, BOOL);
      symbolTable.closeScope();
    }

    public void addReals() {
      symbolTable.createClassScope(REAL_ID,REAL);
      List<Type> params = new ArrayList<>();
      params.add(REAL);
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
      symbolTable.putFunction(OperatorOverloadConstants._TO, params, ARRAY_TYPE);
      symbolTable.closeScope();
    }

    public void addBools() {
      symbolTable.createClassScope(BOOL_ID, BOOL);
      List<Type> params = new ArrayList<>();
      params.add(BOOL);
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
      List<Type> params = new ArrayList<>();
      params.add(CHAR);
      symbolTable.putFunction(OperatorOverloadConstants._EQUALS, params, BOOL);
      symbolTable.putFunction(OperatorOverloadConstants._NEQ, params, BOOL);
      symbolTable.putFunction(OperatorOverloadConstants._ID, params, BOOL);
      symbolTable.putFunction(OperatorOverloadConstants._GE, params, BOOL);
      symbolTable.putFunction(OperatorOverloadConstants._GT, params, BOOL);
      symbolTable.putFunction(OperatorOverloadConstants._LE, params, BOOL);
      symbolTable.putFunction(OperatorOverloadConstants._LT, params, BOOL);
      // TODO not sure about this last one, but it'll probably do
      symbolTable.putFunction(OperatorOverloadConstants._TO, params, ARRAY_TYPE);
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
        // TODO empty list?
        Type parameter = null;
        for (ExpressionNode n : node.getElements()) {
            n.accept(this);
            Type t = n.getType();
            if (t == null) {
                // TODO check later
            } else if (parameter == null) {
                parameter = t;
            } else if (!parameter.equals(t)) {
                // TODO elements are not of the same type: notify error :(
            }
        }
        node.setType(new Type(new LexicalUnit(ARRAY), parameter));
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

}
