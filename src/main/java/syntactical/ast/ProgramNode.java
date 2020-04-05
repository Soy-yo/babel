package syntactical.ast;

import syntactical.ast.visitors.Visitable;
import syntactical.ast.visitors.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProgramNode implements ASTNode, Visitable {

    private DeclarationNode root;
    private List<String> importedFiles;

    public ProgramNode(DeclarationNode root, Collection<String> importedFiles) {
        this.root = root;
        this.importedFiles = new ArrayList<>(importedFiles);
    }

    public DeclarationNode root() {
        return root;
    }

    public Iterable<String> importedFiles() {
        return importedFiles;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
