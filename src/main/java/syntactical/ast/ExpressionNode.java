package syntactical.ast;

import lexical.LexicalUnit;

public abstract class ExpressionNode extends ASTNode {

    protected Type type;

    public ExpressionNode(IdGenerator id, LexicalUnit lexeme, Type type) {
        super(id, lexeme);
        this.type = type;
    }

    public ExpressionNode(IdGenerator id, Type type) {
        this(id, null, type);
    }

    public ExpressionNode(IdGenerator id) {
        this(id, null, null);
    }

    public ExpressionNode(IdGenerator id, LexicalUnit lexeme) {
        this(id, lexeme, null);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
