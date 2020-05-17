package syntactical.ast;

import syntactical.ast.visitors.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProgramNode extends ASTNode {

    private DeclarationNode root;
    private List<String> importedFiles;

    public ProgramNode(DeclarationNode root, Collection<String> importedFiles) {
        this.root = root;
        this.importedFiles = new ArrayList<>(importedFiles);
    }

    public DeclarationNode root() {
        return root;
    }

    public DeclarationNode setRoot(DeclarationNode root) {
        DeclarationNode old = this.root;
        this.root = root;
        return old;
    }

    public Iterable<String> importedFiles() {
        return importedFiles;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
