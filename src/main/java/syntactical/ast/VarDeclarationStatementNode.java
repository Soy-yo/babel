package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.Objects;

public class VarDeclarationStatementNode extends StatementNode {

    private VarDeclarationNode declaration;

    VarDeclarationStatementNode(VarDeclarationNode declaration) {
        super(null, null);
        this.id = declaration.id;
        Objects.requireNonNull(declaration,
                "VarDeclarationStatements must be created from non null VarDeclarations");
        this.declaration = declaration;
    }

    public VarDeclarationNode asDeclaration() {
        return declaration;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return super.toString() + " - " + declaration.toString();
    }

}
