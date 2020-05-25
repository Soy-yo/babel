package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FunctionCallExpressionNode extends ExpressionNode {

    private final ExpressionNode function;
    private final List<ExpressionNode> arguments;

    public FunctionCallExpressionNode(IdGenerator id, LexicalUnit lexeme, ExpressionNode function, Collection<ExpressionNode> arguments) {
        super(id, lexeme);
        this.function = function;
        this.arguments = new ArrayList<>(arguments);
    }

    public FunctionCallExpressionNode(IdGenerator id, ExpressionNode function, Collection<ExpressionNode> arguments) {
        this(id, null, function, arguments);
    }

    public static FunctionCallExpressionNode operator(
            IdGenerator id,
            LexicalUnit lexeme,
            ExpressionNode left,
            String operator,
            ExpressionNode right) {
        return new FunctionCallExpressionNode(id, lexeme, new PointExpressionNode(id, lexeme, left,
            operator),
                new ArrayList<>(Collections.singletonList(right))
        );
    }

    public static FunctionCallExpressionNode operator(
        IdGenerator id,
        ExpressionNode left,
        String operator,
        ExpressionNode right) {
        return operator(id, null, left, operator, right);
    }

    public static FunctionCallExpressionNode operator(IdGenerator id, LexicalUnit lexeme, String operator, ExpressionNode target) {
        return new FunctionCallExpressionNode(id, lexeme, new PointExpressionNode(id, lexeme,
            target,
            operator), new ArrayList<>());
    }

    public static FunctionCallExpressionNode operator(IdGenerator id, String operator,
                                                      ExpressionNode target) {
        return operator(id, (LexicalUnit) null, operator, target);
    }

    public ExpressionNode getFunction() {
        return function;
    }

    public List<ExpressionNode> getArguments() {
        return arguments;
    }

    public FunctionCallStatementNode asStatement() {
        return new FunctionCallStatementNode(this);
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
