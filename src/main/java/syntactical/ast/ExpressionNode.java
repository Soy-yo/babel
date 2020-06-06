package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.Defaults;

public abstract class ExpressionNode extends ASTNode {

    private static final Type CHAR_ARRAY = new Type(Defaults.ARRAY_STR, Defaults.CHAR);

    protected Type type;

    public ExpressionNode(IdGenerator id, LexicalUnit lexeme, Type type) {
        super(id, lexeme);
        this.type = type != null && type.realEquals(Defaults.STRING) ? CHAR_ARRAY : type;
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
