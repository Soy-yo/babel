package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.Objects;

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

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        ClassDeclarationNode node = (ClassDeclarationNode) other;
        return Objects.equals(name, node.name);
    }

}
