package syntactical.ast.visitors;

import syntactical.ast.*;

import java.util.Collections;

public class ASTPrinter implements Visitor {

    private final ASTNode root;
    private int depth = 0;

    public ASTPrinter(ASTNode root) {
        this.root = root;
    }

    public void print() {
        root.accept(this);
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
        depth--;
    }

    @Override
    public void visit(VarDeclarationNode node) {
        printLine(node);
        next(node);
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        printLine(node);
        if (node.getCode() != null) {
            node.getCode().accept(this);
        } else {
            printLine("<no code>");
        }
        next(node);
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
            printLine("<no code>");
        }
        depth--;
        next(node);
    }

    @Override
    public void visit(BlockStatementNode node) {
        depth++;
        if (node.root() == null) {
            printLine("<empty block>");
        } else {
            node.root().accept(this);
        }
        depth--;
        next(node);
    }

    @Override
    public void visit(VarDeclarationStatementNode node) {
        printLine(node);
        next(node);
    }

    @Override
    public void visit(AssignmentStatementNode node) {
        // TODO
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        printLine(node);
        next(node);
    }

    @Override
    public void visit(IfElseStatementNode node) {
        printLine(node);
        // TODO ???
        //node.getCondition().accept(this);
        node.getIfBlock().accept(this);
        if (node.getElsePart() != null) {
            printLine("<else>");
            depth++;
            node.getElsePart().accept(this);
            depth--;
        }
        next(node);
    }

    private void next(QueueableNode<?> node) {
        if (node.hasNext()) {
            node.getNext().accept(this);
        }
    }

}
