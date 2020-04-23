package syntactical.ast.visitors;

import syntactical.ast.*;

import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.stream.Collectors;

public class ASTPrinter implements Visitor {

    private static final String CHILD_POINTER = "├";
    private static final String LAST_CHILD_POINTER = "└";
    private static final int SPACES = 2;

    private final ASTNode root;
    private final StringBuilder prefix;

    // TODO display Types as trees too (Array -> parameter: String) ???

    public ASTPrinter(ASTNode root) {
        this.root = root;
        this.prefix = new StringBuilder();
    }

    public void print() {
        root.accept(this);
    }

    @Override
    public void visit(ProgramNode node) {
        println("program");
        indent();
        println("imports");
        indent();
        printAll(node.importedFiles());
        outdent();
        lastChild();
        println("global scope");
        indent(true);
        if (node.root() != null) {
            node.root().accept(this);
        }
        outdent();
    }

    @Override
    public void visit(VarDeclarationNode node) {
        if (!node.hasNext()) {
            lastChild();
        }
        println("var declaration");
        indent(!node.hasNext());
        println("identifier: " + node.getIdentifier());
        println("type: " + node.getType());
        lastChild();
        if (node.getInitialValue() != null) {
            println("initial value");
            indent(true);
            node.getInitialValue().accept(this);
            outdent();
        } else {
            println("initial value: none");
        }
        outdent();
        next(node);
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        if (!node.hasNext()) {
            lastChild();
        }
        println("function declaration");
        indent(!node.hasNext());
        println("function name: " + node.getIdentifier());
        println("return type: " + node.getType());
        println("parameters");
        indent();
        printAll(node.getParameters().stream().map(VarDeclarationNode::getType).collect(Collectors.toList()));
        outdent();
        lastChild();
        node.getCode().accept(this);
        outdent();
        next(node);
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        visit((FunctionDeclarationNode) node);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        if (!node.hasNext()) {
            lastChild();
        }
        println("class declaration");
        indent(!node.hasNext());
        println("class name: " + node.getIdentifier());
        println("contents");
        indent(true);
        node.getContentRoot().accept(this);
        outdent();
        outdent();
        next(node);
    }

    @Override
    public void visit(BlockStatementNode node) {
        if (!node.hasNext()) {
            lastChild();
        }
        println("block statement");
        indent(!node.hasNext());
        if (node.root() != null) {
            node.root().accept(this);
        }
        outdent();
        next(node);
    }

    @Override
    public void visit(VarDeclarationStatementNode node) {
        if (!node.hasNext()) {
            lastChild();
        }
        println("var declaration statement");
        indent(!node.hasNext());
        node.asDeclaration().accept(this);
        outdent();
        next(node);
    }

    @Override
    public void visit(AssignmentStatementNode node) {
        if (!node.hasNext()) {
            lastChild();
        }
        println("assignment statement");
        indent(!node.hasNext());
        // TODO
        outdent();
        next(node);
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        if (!node.hasNext()) {
            lastChild();
        }
        println("function call statement");
        indent(!node.hasNext());
        // TODO
        outdent();
        next(node);
    }

    @Override
    public void visit(IfElseStatementNode node) {
        boolean hasElse = node.getElsePart() != null;
        if (!node.hasNext()) {
            lastChild();
        }
        println("if-else statement");
        indent(!node.hasNext());
        println("condition");
        indent();
        //node.getCondition().accept(this);
        outdent();
        if (!hasElse) {
            lastChild();
        }
        println("then part");
        indent(!hasElse);
        node.getIfBlock().accept(this);
        outdent();
        if (hasElse) {
            lastChild();
            println("else part");
            indent(true);
            node.getElsePart().accept(this);
            outdent();
        }
        outdent();
        next(node);
    }

    @Override
    public void visit(SwitchStatementNode node) {
        int numCases = node.getNumCases();
        if (!node.hasNext()) {
            lastChild();
        }
        println("switch statement");
        indent(!node.hasNext());
        println("switching");
        indent();
        node.getVariable().accept(this);
        outdent();
        if (numCases == 0) {
            lastChild();
        }
        Iterator iter = node.getCases().iterator();
        Iterator iter2 = node.getBlocks().iterator();
        for(int i = 0; i < numCases; i++) {
            println("case " + i);
            indent();
            iter.next(); //TODO: no sé qué hacer aquí
            println("block " + i);
            iter2.next();
            outdent();
        }
        outdent();
        next(node);
    }

    private void println(String value) {
        System.out.println(prefix + value);
    }

    private <E> void printAll(Iterable<E> elements) {
        Iterator<E> it = elements.iterator();
        while (it.hasNext()) {
            E e = it.next();
            if (!it.hasNext()) {
                lastChild();
            }
            println(e.toString());
        }
    }

    private void indent() {
        indent(false);
    }

    private void indent(boolean isLast) {
        if (prefix.length() > 2) {
            prefix.replace(prefix.length() - 2, prefix.length() - 1, isLast ? " " : "|");
        }
        prefix.append(String.join("", Collections.nCopies(SPACES, " ")))
                .append(CHILD_POINTER)
                .append(" ");
    }

    private void outdent() {
        prefix.delete(prefix.length() - (SPACES + 2), prefix.length())
                .replace(prefix.length() - 2, prefix.length() - 1, CHILD_POINTER);
    }

    private void lastChild() {
        prefix.replace(prefix.length() - 2, prefix.length() - 1, LAST_CHILD_POINTER);
    }

    private void next(QueueableNode<?> node) {
        if (node.hasNext()) {
            node.getNext().accept(this);
        }
    }

}
