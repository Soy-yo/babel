package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class ErrorExpressionNode extends ExpressionNode {

    public ErrorExpressionNode(IdGenerator id) {
        super(id, new LexicalUnit("error"), Type.WILDCARD);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
