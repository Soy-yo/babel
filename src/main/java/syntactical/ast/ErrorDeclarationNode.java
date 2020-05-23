package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class ErrorDeclarationNode extends DeclarationNode {

    public ErrorDeclarationNode(IdGenerator id, LexicalUnit lexeme) {
        super(id, lexeme, null);
    }

    public ErrorDeclarationNode(IdGenerator id) {
        this(id, null);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
