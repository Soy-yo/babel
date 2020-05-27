package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitable;

public abstract class ASTNode implements Visitable {

    protected int id;
    protected LexicalUnit lexeme;

    protected ASTNode(IdGenerator id) {
        // Allow null so subclasses can override id value
        this.id = id == null ? -1 : id.get();
    }

    protected ASTNode(IdGenerator id, LexicalUnit lexeme) {
        this.id = id == null ? -1 : id.get();
        this.lexeme = lexeme;
    }

    public int getId() {
        return id;
    }

    public LexicalUnit getLexeme() { return lexeme; }

    @Override
    public String toString() {
        return "AST Node @" + id + " contains " + lexeme;
    }

}
