package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.Collection;

public class ConstructorDeclarationNode extends FunctionDeclarationNode {

    private static final String NAME = "constructor";

    public ConstructorDeclarationNode(Collection<Name> parameterNames, BlockStatementNode code) {
        super(new Name(NAME, "?"), parameterNames, code);
    }

    public void setType(ClassDeclarationNode classNode) {
        name = new Name(NAME, classNode.typeName());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
