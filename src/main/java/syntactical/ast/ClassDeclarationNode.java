package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class ClassDeclarationNode extends DeclarationNode {

    private DeclarationNode contentRoot;
    private String id;

    public ClassDeclarationNode(String name, DeclarationNode contentRoot) {
        super(new Name(name, new Type(name)));
        this.contentRoot = contentRoot;
    }

    public DeclarationNode getContentRoot() {
        return contentRoot;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
