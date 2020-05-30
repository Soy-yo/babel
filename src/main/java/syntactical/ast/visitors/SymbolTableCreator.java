package syntactical.ast.visitors;

import lexical.LexicalUnit;
import syntactical.OperatorOverloadConstants;
import syntactical.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SymbolTableCreator implements Visitor {

    // TODO probably move some of this constants to another class
    private static final Type FORM = new Type(new LexicalUnit("Form"));
    private static final Type INT = new Type(new LexicalUnit("Int"));
    private static final String THIS = "this";
    private static final String ARRAY = "Array";
    private static final String ARRAY_SIZE = "size";
    private static final int INT_ID = 0;
    private static final int FORM_ID = 1;

    private final ProgramNode root;
    private final SymbolTable symbolTable;
    private int errors;

    // TODO caso especial para Array<?> al comprobar tipos

    public SymbolTableCreator(ProgramNode root) {
        this.root = root;
        this.symbolTable = new SymbolTable();
        initializeTable();
    }

    private void initializeTable() {
        symbolTable.createClassScope(INT_ID,INT);
        List<Type> params = new ArrayList<Type>();
        params.add(INT);
        symbolTable.putFunction(OperatorOverloadConstants._PLUS, params, INT);
        symbolTable.putFunction(OperatorOverloadConstants._MINUS, params, INT);
        symbolTable.putFunction(OperatorOverloadConstants._MULT, params, INT);
        symbolTable.putFunction(OperatorOverloadConstants._DIV, params, INT);
        symbolTable.putFunction(OperatorOverloadConstants._MOD, params, INT);
        symbolTable.createClassScope(FORM_ID, FORM);
        // TODO add default
        // classes and methods
        // (Int,
        // Form, Array<?>, Int._plus, etc)
        SymbolTableInitializer initializer = new SymbolTableInitializer(root, symbolTable);
        errors = initializer.start();
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
