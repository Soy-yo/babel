package syntactical.ast;

import syntactical.ast.visitors.Visitable;

public abstract class ASTNode implements Visitable {

    protected int id;

    protected ASTNode(IdGenerator id) {
        // Allow null so subclasses can override id value
        this.id = id == null ? -1 : id.get();
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "AST Node @" + id;
    }

}
