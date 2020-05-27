package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class ErrorExpressionNode extends ExpressionNode {

    public ErrorExpressionNode(IdGenerator id) {
        super(id);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
