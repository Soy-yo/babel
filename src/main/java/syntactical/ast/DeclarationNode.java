package syntactical.ast;

// TODO check name case here?
public abstract class DeclarationNode extends QueueableNode<DeclarationNode> {

    protected Name name;

    public DeclarationNode(Name name) {
        this.name = name;
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
