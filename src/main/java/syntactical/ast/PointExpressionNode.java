package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

public class PointExpressionNode extends ExpressionNode {

    private final ExpressionNode host;
    private final VariableExpressionNode field;

    public PointExpressionNode(IdGenerator id, LexicalUnit lexeme, ExpressionNode host, VariableExpressionNode field) {
        super(id, lexeme);
        this.host = host;
        this.field = field;
    }

    public PointExpressionNode(IdGenerator id, LexicalUnit lexeme, ExpressionNode host, String field) {
        this(id, lexeme, host, new VariableExpressionNode(id, lexeme, field));
    }

    public ExpressionNode getHost() {
        return this.host;
    }

    public VariableExpressionNode getField() {
        return this.field;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {host:" + host
                + ", field:" + field
                + "}";
    }

}
