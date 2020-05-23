package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class ErrorStatementNode extends StatementNode {

    public ErrorStatementNode(IdGenerator id, LexicalUnit lexeme) {
        super(id, lexeme);
    }

    public ErrorStatementNode(IdGenerator id) {
        this(id, null);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
