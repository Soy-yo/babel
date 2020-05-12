package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class ErrorExpressionNode extends ExpressionNode {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
