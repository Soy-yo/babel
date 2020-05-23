package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConstructorCallExpressionNode extends ExpressionNode {

    private List<ExpressionNode> arguments;

    public ConstructorCallExpressionNode(IdGenerator id, LexicalUnit lexeme, Type constructor, Collection<ExpressionNode> arguments) {
        super(id, lexeme, constructor);
        this.arguments = new ArrayList<>(arguments);
    }

    public ConstructorCallExpressionNode(IdGenerator id, Type constructor, Collection<ExpressionNode> arguments) {
        this(id, null, constructor, arguments);
    }

    public List<ExpressionNode> getArguments() {
        return arguments;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {type:" + type
                + ", arguments:" + arguments
                + "}";
    }

}
