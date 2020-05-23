package syntactical.ast;

import lexical.LexicalUnit;

public abstract class DeclarationNode extends QueueableNode<DeclarationNode> {

    protected Name name;

    public DeclarationNode(IdGenerator id, LexicalUnit lexeme, Name name) {
        super(id, lexeme);
        this.name = name;
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
