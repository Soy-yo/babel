package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class ErrorDeclarationNode extends DeclarationNode {

    public ErrorDeclarationNode() {
        super(null);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
