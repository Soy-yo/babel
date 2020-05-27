package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class ClassDeclarationNode extends DeclarationNode {

    private DeclarationNode contentRoot;

    public ClassDeclarationNode(IdGenerator id, LexicalUnit name, DeclarationNode contentRoot) {
        super(id, name, new Name(name, new Type(name)));
        this.contentRoot = contentRoot;
    }

    public DeclarationNode getContentRoot() {
        return contentRoot;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
