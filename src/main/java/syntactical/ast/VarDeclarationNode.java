package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class VarDeclarationNode extends DeclarationNode {

    private ExpressionNode initialValue;
    private boolean isConst;

    public VarDeclarationNode(IdGenerator id, Name name, ExpressionNode initialValue, boolean isConst) {
        super(id, name.getIdentifierLexicalUnit(), name);
        this.initialValue = initialValue;
        this.isConst = isConst;
    }

    public VarDeclarationNode(IdGenerator id, Name name, ExpressionNode initialValue) {
        this(id, name, initialValue, false);
    }

    public VarDeclarationNode(IdGenerator id, Name name) {
        this(id, name, null);
    }

    public static VarDeclarationNode fromConstructor(
            IdGenerator id,
            ConstructorCallExpressionNode constructor,
            LexicalUnit identifier) {
        Name name = new Name(identifier, constructor.getType());
        return new VarDeclarationNode(id, name, constructor);
    }

    public ExpressionNode getInitialValue() {
        return initialValue;
    }

    public boolean isConst() {
        return isConst;
    }

    public VarDeclarationNode constant() {
        VarDeclarationNode result = new VarDeclarationNode(null, name, initialValue, true);
        result.id = id;
        return result;
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
