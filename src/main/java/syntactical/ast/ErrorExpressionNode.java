package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class ErrorExpressionNode extends ExpressionNode {

    public ErrorExpressionNode(IdGenerator id, LexicalUnit lexeme) {
        super(id, lexeme);
    }

    public ErrorExpressionNode(IdGenerator id) {
        this(id, null);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
