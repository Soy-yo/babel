package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionDeclarationNode extends DeclarationNode {

    private List<VarDeclarationNode> parameters;
    private BlockStatementNode code;

    public FunctionDeclarationNode(
            IdGenerator id,
            LexicalUnit lexeme,
            Name name,
            Collection<Name> parameterNames,
            BlockStatementNode code) {
        super(id, lexeme, name);
        this.parameters = parameterNames.stream()
                .map(n -> new VarDeclarationNode(id, n))
                .collect(Collectors.toList());
        this.code = code;
    }

    public FunctionDeclarationNode(
            IdGenerator id,
            Name name,
            Collection<Name> parameterNames,
            BlockStatementNode code) {
        this(id, name.getIdentifierLexicalUnit(), name, parameterNames, code);
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
