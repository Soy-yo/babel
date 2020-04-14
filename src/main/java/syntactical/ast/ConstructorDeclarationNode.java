package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.Collection;

public class ConstructorDeclarationNode extends FunctionDeclarationNode {

    private static final String NAME = "constructor";
    private static final Type UNKNOWN_TYPE = new Type("?");

    public ConstructorDeclarationNode(Collection<Name> parameterNames, BlockStatementNode code) {
        super(new Name(NAME, UNKNOWN_TYPE), parameterNames, code);
    }

    public void setType(ClassDeclarationNode classNode) {
        name = new Name(NAME, classNode.getType());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
