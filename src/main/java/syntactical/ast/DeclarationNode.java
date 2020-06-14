package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.Defaults;

public abstract class DeclarationNode extends QueueableNode<DeclarationNode> {

    protected Name name;

    public DeclarationNode(IdGenerator id, LexicalUnit lexeme, Name name) {
        super(id, lexeme);
        this.name = fixName(name);
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

    private Name fixName(Name name) {
        if (name.getType() == null || this instanceof ClassDeclarationNode) {
            return name;
        }
        Type type = Defaults.fixType(name.getType());
        // If type wasn't modified reuse the name
        return type.realEquals(name.getType()) ? name : new Name(name.getIdentifierLexicalUnit(), type);
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
