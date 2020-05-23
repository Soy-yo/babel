package syntactical.ast;

import syntactical.ast.visitors.Visitor;

public class ArrayAccessExpressionNode extends ExpressionNode {

    private ExpressionNode array;
    private ExpressionNode index;

    public ArrayAccessExpressionNode(IdGenerator id, ExpressionNode array, ExpressionNode index) {
        super(id);
        this.array = array;
        this.index = index;
    }

    public ExpressionNode getArray() {
        return array;
    }

    public ExpressionNode getIndex() {
        return index;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
