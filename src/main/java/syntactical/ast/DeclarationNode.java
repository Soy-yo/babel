package syntactical.ast;

import syntactical.ast.visitors.Visitable;

import java.util.Objects;

// TODO check name case here?
public abstract class DeclarationNode implements ASTNode, Visitable {

    protected Name name;
    private DeclarationNode previous;
    private DeclarationNode next;

    public DeclarationNode(Name name) {
        this.name = name;
    }

    public Name getName() {
        return name;
    }

    public String getIdentifier() {
        return name.getIdentifier();
    }

    public String getType() {
        return name.getType();
    }

    public DeclarationNode getNext() {
        return next;
    }

    public DeclarationNode getPrevious() {
        return previous;
    }

    public DeclarationNode linkTo(DeclarationNode next) {
        checkNext(this);
        checkPrevious(next);
        this.next = next;
        next.previous = this;
        return this;
    }

    public DeclarationNode linkBefore(DeclarationNode node) {
        checkNext(this);
        checkPrevious(this);
        DeclarationNode p = node.previous;
        this.next = node;
        node.previous = this;
        this.previous = p;
        p.next = this;
        return this;
    }

    public DeclarationNode linkAfter(DeclarationNode node) {
        checkNext(this);
        checkPrevious(this);
        DeclarationNode n = node.next;
        this.next = n;
        node.next = this;
        this.previous = node;
        n.previous = this;
        return this;
    }

    private static void checkNext(DeclarationNode node) {
        if (node.next != null) {
            throw new IllegalArgumentException(node + " already has a next node");
        }
    }

    private static void checkPrevious(DeclarationNode node) {
        if (node.next != null) {
            throw new IllegalArgumentException(node + " already has a previous node");
        }
    }

    protected abstract String typeName();

    @Override
    public String toString() {
        return "DeclarationNode " + name + " (" + typeName() + ")";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        DeclarationNode node = (DeclarationNode) other;
        return Objects.equals(name, node.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
