package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProgramNode extends ASTNode {

    private DeclarationNode root;
    private List<String> importedFiles;

    public ProgramNode(IdGenerator id, LexicalUnit lexeme, DeclarationNode root, Collection<String> importedFiles) {
        super(id, lexeme);
        this.root = root;
        this.importedFiles = new ArrayList<>(importedFiles);
    }

    public ProgramNode(IdGenerator id, DeclarationNode root, Collection<String> importedFiles) {
        this(id, null, root, importedFiles);
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
