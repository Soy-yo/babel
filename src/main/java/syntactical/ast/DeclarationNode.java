package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.Defaults;

public abstract class DeclarationNode extends QueueableNode<DeclarationNode> {

    private static final Type CHAR_ARRAY = new Type(Defaults.ARRAY_STR, Defaults.CHAR);

    protected Name name;

    public DeclarationNode(IdGenerator id, LexicalUnit lexeme, Name name) {
        super(id, lexeme);
        this.name = name.getType() != null &&
                !(this instanceof ClassDeclarationNode) &&
                name.getType().realEquals(Defaults.STRING) ?
                new Name(name.getIdentifierLexicalUnit(), CHAR_ARRAY) : name;
    }

    public DeclarationNode(IdGenerator id, Name name) {
        this(id, null, name);
    }

    public Name getName() {
        return name;
    }

    public String getIdentifier() {
        return name.getIdentifier();
    }

    public Type getType() {
        return name.getType();
    }

    @Override
    protected final DeclarationNode self() {
        return this;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + name;
    }

}
