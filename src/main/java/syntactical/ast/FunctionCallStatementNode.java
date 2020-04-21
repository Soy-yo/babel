package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FunctionCallStatementNode extends StatementNode {

    private ExpressionNode function;
    private List<ExpressionNode> arguments;

    public FunctionCallStatementNode(ExpressionNode function, Collection<ExpressionNode> arguments) {
        this.function = function;
        this.arguments = new ArrayList<>(arguments);
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
