package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class VariableExpressionNode extends ExpressionNode {

    private String variable;

    public VariableExpressionNode(IdGenerator id, LexicalUnit lexeme, String variable) {
        super(id, lexeme);
        this.variable = variable;
    }

    public VariableExpressionNode(IdGenerator id, String variable) {
        this(id, null, variable);
    }

    public String get() {
        return variable;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {variable:" + variable
                + "}";
    }

}
