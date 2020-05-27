package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class VariableExpressionNode extends ExpressionNode {

    public VariableExpressionNode(IdGenerator id, LexicalUnit variable) {
        super(id, variable);
    }

    public String get() {
        return lexeme.lexeme();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {variable:" + get()
                + "}";
    }

}
