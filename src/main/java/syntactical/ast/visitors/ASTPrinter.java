package syntactical.ast.visitors;

import lexical.LexicalUnit;
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
    private static final String LAST_CHILD_POINTER = "└─";
    private static final int SPACES = 1;

    private final ASTNode root;
    private final StringBuilder prefix;
    private boolean listing;
    private PrintWriter stdOut;

    public ASTPrinter(ASTNode root) {
        this.root = root;
        this.prefix = new StringBuilder();
        this.listing = false;
    }

    public void print() {
        try {
            stdOut = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8.name()));
            root.accept(this);
            stdOut.flush();
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
        printAll(node.importedFiles().stream()
                .map(LexicalUnit::lexeme)
                .collect(Collectors.toList()));
        outdent();
        lastChild();
        println("global scope");
        indent(true);
        if (node.root() != null) {
            visitAll(node.root());
        }
        outdent();
    }

    @Override
    public void visit(VarDeclarationNode node) {
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
            lastChild();
            node.getInitialValue().accept(this);
            outdent();
        } else {
            println("initial value: none");
        }
        outdent();
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
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
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        visit((FunctionDeclarationNode) node);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        println("class declaration");
        indent(!node.hasNext());
        println("class name: " + node.getIdentifier());
        lastChild();
        if (node.getContentRoot() != null) {
            println("contents");
            indent(true);
            visitAll(node.getContentRoot());
            outdent();
        } else {
            println("contents: none");
        }
        outdent();
    }

    @Override
    public void visit(BlockStatementNode node) {
        println("block statement");
        indent(!node.hasNext());
        if (node.root() != null) {
            visitAll(node.root());
        }
        outdent();
    }

    @Override
    public void visit(VarDeclarationStatementNode node) {
        println("var declaration statement");
        indent(!node.hasNext());
        lastChild();
        node.asDeclaration().accept(this);
        outdent();
    }

    @Override
    public void visit(AssignmentStatementNode node) {
        println("assignment statement");
        indent(!node.hasNext());
        println("for object");
        indent();
        lastChild();
        node.getTarget().accept(this);
        outdent();
        switch (node.getAccessMethod()) {
            case FIELD: {
                println("at field");
                indent();
                lastChild();
                node.getAccessExpression().accept(this);
                outdent();
            }
            break;
            case ARRAY: {
                println("at index");
                indent();
                lastChild();
                node.getAccessExpression().accept(this);
                outdent();
            }
            break;
        }
        lastChild();
        println("value");
        indent(true);
        lastChild();
        node.getValue().accept(this);
        outdent();
        outdent();
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        println("function call statement");
        indent(!node.hasNext());
        lastChild();
        node.asExpression().accept(this);
        outdent();
    }

    @Override
    public void visit(ReturnStatementNode node) {
        println("return statement");
        indent(!node.hasNext());
        lastChild();
        node.getReturnExpression().accept(this);
        outdent();
    }

    @Override
    public void visit(IfElseStatementNode node) {
        boolean hasElse = node.getElsePart() != null;
        println("if-else statement");
        indent(!node.hasNext());
        println("condition");
        indent();
        lastChild();
        node.getCondition().accept(this);
        outdent();
        if (!hasElse) {
            lastChild();
        }
        println("then part");
        indent(!hasElse);
        lastChild();
        node.getIfBlock().accept(this);
        outdent();
        if (hasElse) {
            lastChild();
            println("else part");
            indent(true);
            lastChild();
            node.getElsePart().accept(this);
            outdent();
        }
        outdent();
    }

    @Override
    public void visit(SwitchStatementNode node) {
        int numCases = node.getNumCases();
        println("switch statement");
        indent(!node.hasNext());
        println("switch expression");
        indent();
        lastChild();
        node.getSwitchExpression().accept(this);
        outdent();
        lastChild();
        println("cases");
        indent(true);
        int i = 0;
        for (Map.Entry<ConstantExpressionNode, StatementNode> entry : node.getCases().entrySet()) {
            if (i == numCases - 1) {
                lastChild();
            }
            println("case [" + i + "]");
            indent(i == numCases - 1);
            println("key");
            indent();
            lastChild();
            entry.getKey().accept(this);
            outdent();
            lastChild();
            println("value");
            indent(true);
            lastChild();
            entry.getValue().accept(this);
            outdent();
            outdent();
            i++;
        }
        outdent();
        outdent();
    }

    @Override
    public void visit(WhileStatementNode node) {
        println("while statement");
        indent(!node.hasNext());
        println("condition");
        indent();
        lastChild();
        node.getCondition().accept(this);
        outdent();
        lastChild();
        node.getBlock().accept(this);
        outdent();
    }

    @Override
    public void visit(ForStatementNode node) {
        println("for statement");
        indent(!node.hasNext());
        println("variable");
        indent();
        lastChild();
        node.getVariable().accept(this);
        outdent();
        println("iterable");
        indent();
        lastChild();
        node.getIterable().accept(this);
        outdent();
        lastChild();
        node.getBlock().accept(this);
        outdent();
    }

    @Override
    public void visit(PointExpressionNode node) {
        println("point operator expression");
        indent(!listing);
        println("type: " + node.getType());
        println("left hand side");
        indent();
        lastChild();
        node.getHost().accept(this);
        outdent();
        lastChild();
        println("right hand side");
        indent(true);
        lastChild();
        node.getField().accept(this);
        outdent();
        outdent();
    }

    @Override
    public void visit(ArrayAccessExpressionNode node) {
        println("array access operator expression");
        indent(!listing);
        println("type: " + node.getType());
        println("array");
        indent();
        lastChild();
        node.getArray().accept(this);
        outdent();
        lastChild();
        println("index");
        indent(true);
        lastChild();
        node.getIndex().accept(this);
        outdent();
        outdent();
    }

    @Override
    public void visit(FunctionCallExpressionNode node) {
        println("function call expression");
        indent(!listing);
        println("type: " + node.getType());
        println("function");
        indent();
        lastChild();
        node.getFunction().accept(this);
        outdent();
        lastChild();
        if (!node.getArguments().isEmpty()) {
            println("arguments");
            indent(true);
            visitAll(node.getArguments());
            outdent();
        } else {
            println("arguments: none");
        }
        outdent();
    }

    @Override
    public void visit(VariableExpressionNode node) {
        println("variable expression: " + node.get() + " - " + node.getType());
    }

    @Override
    public void visit(ConstantExpressionNode node) {
        println("constant expression");
        indent(!listing);
        printAll("type: " + node.getType(),
                "value: " + node.getRepresentation(),
                "hex: 0x" + String.format("%1$8s", node.getHex()).replace(' ', '0')
        );
        outdent();
    }

    @Override
    public void visit(ListConstructorExpressionNode node) {
        println("list constructor expression");
        indent(!listing);
        println("type: " + node.getType());
        List<ExpressionNode> elements = node.getElements();
        if (elements.isEmpty()) {
            lastChild();
            println("size: " + elements.size());
        } else {
            println("size: " + elements.size());
            lastChild();
            println("elements");
            indent(true);
            visitAll(elements);
            outdent();
        }
        outdent();
    }

    @Override
    public void visit(AnonymousObjectConstructorExpressionNode node) {
        println("anonymous object constructor expression");
        indent(!listing);
        if (node.isEmpty()) {
            lastChild();
        }
        println("type: " + node.getType());
        if (!node.isEmpty()) {
            lastChild();
            println("fields");
            indent(true);
            visitAll(node.getFields());
            outdent();
        }
        outdent();
    }

    @Override
    public void visit(ConstructorCallExpressionNode node) {
        println("constructor call expression");
        indent(!listing);
        println("type: " + node.getType());
        lastChild();
        if (!node.getArguments().isEmpty()) {
            println("arguments");
            indent(true);
            visitAll(node.getArguments());
            outdent();
        } else {
            println("arguments: none");
        }
        outdent();
    }

    @Override
    public void visit(ErrorDeclarationNode node) {
        println("declaration error");
    }

    @Override
    public void visit(ErrorStatementNode node) {
        println("statement error");
    }

    @Override
    public void visit(ErrorExpressionNode node) {
        println("expression error");
    }

    private void println(String value) {
        stdOut.println(prefix + value);
    }

    private void visitAll(Iterable<? extends ASTNode> nodes) {
        Iterator<? extends ASTNode> it = nodes.iterator();
        while (it.hasNext()) {
            listing = true;
            ASTNode child = it.next();
            if (!it.hasNext()) {
                listing = false;
                lastChild();
            }
            child.accept(this);
        }
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
        // Force indent(false)
        if (listing) {
            isLast = false;
            listing = false;
        }
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

}
