package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class ArrayAccessExpressionNode extends ExpressionNode {

    private ExpressionNode array;
    private ExpressionNode index;

    public ArrayAccessExpressionNode(IdGenerator id, LexicalUnit lexeme, ExpressionNode array,
                                     ExpressionNode index) {
        super(id, lexeme);
        this.array = array;
        this.index = index;
    }

    public ArrayAccessExpressionNode(IdGenerator id, ExpressionNode array,
                                     ExpressionNode index) {
        this(id, null, array, index);
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
