package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FunctionCallExpressionNode extends ExpressionNode {

    private final ExpressionNode function;
    private final List<ExpressionNode> arguments;

    public FunctionCallExpressionNode(ExpressionNode function, Collection<ExpressionNode> arguments) {
        this.function = function;
        this.arguments = new ArrayList<>(arguments);
    }

    public static FunctionCallExpressionNode operator(ExpressionNode left, String operator, ExpressionNode right) {
        return new FunctionCallExpressionNode(
                new PointExpressionNode(left, operator),
                new ArrayList<>(Collections.singletonList(right))
        );
    }

    public static FunctionCallExpressionNode operator(String operator, ExpressionNode target) {
        return new FunctionCallExpressionNode(new PointExpressionNode(target, operator), new ArrayList<>());
    }

    public ExpressionNode getFunction() {
        return function;
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
                + " {function:" + function
                + ", arguments:" + arguments
                + "}";
    }

}
