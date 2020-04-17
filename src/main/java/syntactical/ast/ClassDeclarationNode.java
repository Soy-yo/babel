package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class ClassDeclarationNode extends DeclarationNode {

    private DeclarationNode contentRoot;

    public ClassDeclarationNode(String name, DeclarationNode contentRoot) {
        super(new Name(name, new Type(name)));
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
