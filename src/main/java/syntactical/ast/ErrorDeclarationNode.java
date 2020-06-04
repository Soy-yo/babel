package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class ErrorDeclarationNode extends DeclarationNode {

    public ErrorDeclarationNode(IdGenerator id) {
        super(id, new LexicalUnit("error"), new Name(new LexicalUnit("error"), Type.WILDCARD));
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
