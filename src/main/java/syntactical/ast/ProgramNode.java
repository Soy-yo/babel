package syntactical.ast;

import lexical.LexicalUnit;
import syntactical.ast.visitors.Visitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProgramNode extends ASTNode {

    private DeclarationNode root;
    private List<LexicalUnit> importedFiles;

    public ProgramNode(IdGenerator id, DeclarationNode root, Collection<LexicalUnit> importedFiles) {
        super(id, null);
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

    public List<LexicalUnit> importedFiles() {
        return Collections.unmodifiableList(importedFiles);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
