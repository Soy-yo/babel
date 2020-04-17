package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionDeclarationNode extends DeclarationNode {

    private List<VarDeclarationNode> parameters;
    private BlockStatementNode code;

    public FunctionDeclarationNode(
            Name name,
            Collection<Name> parameterNames,
            BlockStatementNode code
    ) {
        super(name);
        this.parameters = parameterNames.stream()
                .map(VarDeclarationNode::new)
                .collect(Collectors.toList());
        this.code = code;
    }

    public List<VarDeclarationNode> getParameters() {
        return parameters;
    }

    public BlockStatementNode getCode() {
        return code;
    }

    private Type[] parameterTypes() {
        return parameters.stream().map(VarDeclarationNode::getType).toArray(Type[]::new);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString()
                + " {parameters:" + Arrays.stream(parameterTypes()).map(Type::toString)
                .collect(Collectors.joining(",", "[", "]"))
                + "}";
    }

}
