package syntactical.ast.visitors;

import syntactical.ast.*;

import java.util.Collections;

public class ASTPrinter implements Visitor {

    private ProgramNode program;
    private int depth = 0;

    public ASTPrinter(ProgramNode program) {
        this.program = program;
    }

    public void print() {
        program.accept(this);
    }

    private <T> void printLine(T value) {
        // System.out.println("--".repeat(depth) + value); // Java 11
        System.out.println(String.join("", Collections.nCopies(depth, "--")) + value);
    }

    @Override
    public void visit(ProgramNode node) {
        printLine("IMPORTED FILES");
        depth++;
        node.importedFiles().forEach(this::printLine);
        depth--;
        System.out.println();
        printLine("GLOBAL SCOPE");
        depth++;
        if (node.root() != null) {
            node.root().accept(this);
        }
    }

    @Override
    public void visit(VarDeclarationNode node) {
        printLine(node);
        if (node.getNext() != null) {
            node.getNext().accept(this);
        } else {
            depth--;
        }
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        printLine(node);
        depth++;
        if (node.getCode() != null) {
            node.getCode().accept(this);
        } else {
            printLine("NO CODE");
        }
        depth--;
        if (node.getNext() != null) {
            node.getNext().accept(this);
        } else {
            depth--;
        }
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        visit((FunctionDeclarationNode) node);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        printLine(node);
        depth++;
        if (node.getContentRoot() != null) {
            node.getContentRoot().accept(this);
        } else {
            printLine("NO CODE");
        }
        depth--;
        if (node.getNext() != null) {
            node.getNext().accept(this);
        } else {
            depth--;
        }
    }

    @Override
    public void visit(BlockStatementNode node) {
        printLine("block");
    }

}
