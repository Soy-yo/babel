package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class VarDeclarationNode extends DeclarationNode {

    private ExpressionNode initialValue;
    private boolean isConst;

    public VarDeclarationNode(IdGenerator id, LexicalUnit lexeme, Name name, ExpressionNode initialValue, boolean isConst) {
        super(id, lexeme, name);
        this.initialValue = initialValue;
        this.isConst = isConst;
    }

    public VarDeclarationNode(IdGenerator id, Name name, ExpressionNode initialValue, boolean isConst) {
        this(id, null, name, initialValue, isConst);
    }

    public VarDeclarationNode(IdGenerator id, LexicalUnit lexeme, Name name, ExpressionNode initialValue) {
        this(id, lexeme, name, initialValue, false);
    }

    public VarDeclarationNode(IdGenerator id, Name name, ExpressionNode initialValue) {
        this(id, null, name, initialValue);
    }

    public VarDeclarationNode(IdGenerator id, LexicalUnit lexeme, Name name) {
        this(id, lexeme, name, null);
    }

    public VarDeclarationNode(IdGenerator id, Name name) {
        this(id, null, name, null);
    }

    public static VarDeclarationNode fromConstructor(
            IdGenerator id,
            LexicalUnit lexeme,
            ConstructorCallExpressionNode constructor,
            String identifier) {
        Name name = new Name(identifier, constructor.getType());
        return new VarDeclarationNode(id, lexeme, name, constructor);
    }

    public static VarDeclarationNode fromConstructor(
        IdGenerator id,
        ConstructorCallExpressionNode constructor,
        String identifier) {
        return fromConstructor(id, null, constructor, identifier);
    }

    public ExpressionNode getInitialValue() {
        return initialValue;
    }

    public boolean isConst() {
        return isConst;
    }

    public VarDeclarationNode constant() {
        VarDeclarationNode result = new VarDeclarationNode(null, null, name, initialValue, true);
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
