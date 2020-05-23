package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class ClassDeclarationNode extends DeclarationNode {

    private DeclarationNode contentRoot;

    public ClassDeclarationNode(IdGenerator id, LexicalUnit lexeme, String name, DeclarationNode contentRoot) {
        super(id, lexeme, new Name(name, new Type(name)));
        this.contentRoot = contentRoot;
    }

    public ClassDeclarationNode(IdGenerator id, String name, DeclarationNode contentRoot) {
        this(id, null, name, contentRoot);
    }

    public DeclarationNode getContentRoot() {
        return contentRoot;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
