package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class VarDeclarationNode extends DeclarationNode {

    private ExpressionNode initialValue;
    private boolean isConst;

    public VarDeclarationNode(
            Name name,
            ExpressionNode initialValue,
            boolean isConst
    ) {
        super(name);
        this.initialValue = initialValue;
        this.isConst = isConst;
    }

    public VarDeclarationNode(Name name, ExpressionNode initialValue) {
        this(name, initialValue, false);
    }

    public VarDeclarationNode(Name name) {
        this(name, null);
    }

    public static VarDeclarationNode fromConstructor(ConstructorCallExpressionNode constructor, String id) {
        Name name = new Name(id, constructor.getType());
        return new VarDeclarationNode(name, constructor);
    }

    public ExpressionNode getInitialValue() {
        return initialValue;
    }

    public boolean isConst() {
        return isConst;
    }

    public VarDeclarationNode constant() {
        return new VarDeclarationNode(name, initialValue, true);
    }

    public VarDeclarationStatementNode asStatement() {
        return new VarDeclarationStatementNode(this);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {initialValue:" + initialValue
                + ", const:" + isConst
                + "}";
    }

}
