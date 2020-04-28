package syntactical.ast.visitors;

import syntactical.ast.*;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ASTPrinter implements Visitor {

    private static final String CHILD_POINTER = "├─";
    //private static final String CHILD_POINTER = "├";
    private static final String LAST_CHILD_POINTER = "└─";
    //private static final String LAST_CHILD_POINTER = "└";
    private static final int SPACES = 1;

    private final ASTNode root;
    private final StringBuilder prefix;
    private PrintWriter stdOut;

    // TODO display Types as trees too (Array -> parameter: String) ???

    public ASTPrinter(ASTNode root) {
        this.root = root;
        this.prefix = new StringBuilder();
    }

    public void print() {
        try {
            stdOut = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8.name()));
            root.accept(this);
            stdOut.close();
        } catch (UnsupportedEncodingException e) {
            System.err.println("AST print failed (unsupported charset)");
            System.err.println(e.getMessage());
        }
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
        if (node.isConst()) {
            println("constant");
        }
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
    public void visit(ClassDeclarationNode node) {
        if (!node.hasNext()) {
            lastChild();
        }
        println("class declaration");
        indent(!node.hasNext());
        println("class name: " + node.getIdentifier());
        lastChild();
        if (node.getContentRoot() != null) {
            println("contents");
            indent(true);
            node.getContentRoot().accept(this);
            outdent();
        } else {
            println("contents: none");
        }
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
    public void visit(ReturnStatementNode node) {
        if (!node.hasNext()) {
            lastChild();
        }
        println("return statement");
        indent(!node.hasNext());
        lastChild();
        node.getReturnExpression().accept(this);
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
        node.getVariable().accept(this);
        indent();
        outdent();
        if (numCases == 0) {
            lastChild();
        }

        Map<ExpressionNode, StatementNode> cases = node.getAll();
        int i = 0;
        for(Map.Entry<ExpressionNode, StatementNode> entry : cases.entrySet()) {
            if(i == cases.size() - 1) lastChild();
            entry.getKey().accept(this);
            if(i == cases.size() - 1) indent(true);
            else indent();
            lastChild();
            entry.getValue().accept(this);
            outdent();
            i++;
        }
        outdent();
        next(node);
    }

    @Override
    public void visit(WhileStatementNode node) {
        if (!node.hasNext()) {
            lastChild();
        }
        println("while ");
        node.getCondition().accept(this);
        if (!node.hasNext()) {
            indent(true);
        }
        else indent();
        lastChild();
        node.getBlock().accept(this);
        outdent();
        next(node);
    }

    @Override
    public void visit(ForStatementNode node) {
        if (!node.hasNext()) {
            lastChild();
        }
        println("for " + node.getVariable());
        node.getIterable().accept(this);
        if (!node.hasNext()) {
            indent(true);
        }
        else indent();
        lastChild();
        node.getBlock().accept(this);
        outdent();
        next(node);
    }

    @Override
    public void visit(OperatorsExpressionNode node) {
        lastChild();
        println("operators expression");

        indent(true);
        printAll("type: " + node.getType(),
                "operator: " + node.getOperator());
        node.getFirst().accept(this);
        lastChild();
        node.getLast().accept(this);
        outdent();
    }

    @Override
    public void visit(ConstantExpressionNode node) {
        lastChild();
        println("constant expression");
        indent(true);
        printAll("type: " + node.getType(),
                "value: " + node.getRepresentation(),
                "hex: 0x" + String.format("%1$8s", node.getHex()).replace(' ', '0')
        );
        outdent();
    }

    @Override
    public void visit(ListConstructorExpressionNode node) {
        lastChild();
        println("list constructor expression");
        indent(true);
        println("type: " + node.getType());
        List<ExpressionNode> elements = node.getElements();
        if (elements.isEmpty()) {
            lastChild();
        }
        println("size: " + elements.size());
        for (int i = 0; i < elements.size(); i++) {
            if (i == elements.size() - 1) {
                lastChild();
            }
            println("element [" + i + "]");
            indent(i == elements.size() - 1);
            lastChild();
            elements.get(i).accept(this);
            outdent();
        }
        outdent();
    }

    @Override
    public void visit(AnonymousObjectConstructorExpressionNode node) {
        lastChild();
        println("anonymous object constructor expression");
        indent(true);
        if (node.isEmpty()) {
            lastChild();
        }
        println("type: " + node.getType());
        if (!node.isEmpty()) {
            lastChild();
            println("fields");
            indent(true);
            VarDeclarationNode root = node.getFields();
            root.accept(this);
            outdent();
        }
        outdent();
    }

    private void println(String value) {
        stdOut.println(prefix + value);
    }

    private void printAll(Iterable<?> elements) {
        Iterator<?> it = elements.iterator();
        while (it.hasNext()) {
            Object e = it.next();
            if (!it.hasNext()) {
                lastChild();
            }
            println(e.toString());
        }
    }

    private void printAll(Object... elements) {
        for (int i = 0; i < elements.length; i++) {
            if (i == elements.length - 1) {
                lastChild();
            }
            println(elements[i].toString());
        }
    }

    private void indent() {
        indent(false);
    }

    private void indent(boolean isLast) {
        if (prefix.length() > 2) {
            prefix.replace(prefix.length() - (CHILD_POINTER.length() + 1),
                    prefix.length() - 1, isLast ? "  " : "│ ");
        }
        prefix.append(String.join("", Collections.nCopies(SPACES, " ")))
                .append(CHILD_POINTER)
                .append(" ");
    }

    private void outdent() {
        prefix.delete(prefix.length() - (SPACES + CHILD_POINTER.length() + 1), prefix.length())
                .replace(prefix.length() - (CHILD_POINTER.length() + 1), prefix.length() - 1, CHILD_POINTER);
    }

    private void lastChild() {
        prefix.replace(prefix.length() - (CHILD_POINTER.length() + 1),
                prefix.length() - 1, LAST_CHILD_POINTER);
    }

    private void next(QueueableNode<?> node) {
        if (node.hasNext()) {
            node.getNext().accept(this);
        }
    }

}
