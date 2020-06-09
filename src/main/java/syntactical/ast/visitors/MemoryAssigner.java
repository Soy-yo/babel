package syntactical.ast.visitors;

import syntactical.Defaults;
import syntactical.ast.*;

public class MemoryAssigner implements Visitor {

    private final ProgramNode root;
    private final SymbolTable symbolTable;
    private final Directions directions;
    private int relativeDir;
    private Integer currentClassId;
    private Integer currentForm;

    public MemoryAssigner(ProgramNode root, SymbolTable symbolTable) {
        this.root = root;
        this.symbolTable = symbolTable;
        this.directions = new Directions();
        this.relativeDir = 0;
        this.currentClassId = null;
        this.currentForm = null;
    }

    public Directions start() {
        root.accept(this);
        return directions;
    }

    @Override
    public void visit(ProgramNode node) {
        if (node.root() != null) {
            for (DeclarationNode n : node.root()) {
                n.accept(this);
            }
        }
    }

    @Override
    public void visit(VarDeclarationNode node) {
        directions.registerVariable(node.getId(), relativeDir);
        relativeDir++;
        if (Defaults.FORM.equals(node.getType())) {
            currentForm = node.getId();
            // Add also Form fields
            node.getInitialValue().accept(this);
            currentForm = null;
        }
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        int previousDir = relativeDir;
        relativeDir = 0;
        // Add all parameters first
        if (currentClassId != null) {
            // Add "this" (might overwrite previous "this" for this class, but with the same value)
            directions.registerVariable(currentClassId, relativeDir);
            relativeDir++;
        }
        for (VarDeclarationNode n : node.getParameters()) {
            directions.registerVariable(n.getId(), relativeDir);
            relativeDir++;
        }
        node.getCode().accept(this);
        relativeDir = previousDir;
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        visit(((FunctionDeclarationNode) node));
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        if (node.getContentRoot() != null) {
            currentClassId = node.getId();
            Type type = node.getType();
            for (DeclarationNode n : node.getContentRoot()) {
                // Register all fields and just visit methods
                if (n instanceof VarDeclarationNode) {
                    VarDeclarationNode field = (VarDeclarationNode) n;
                    directions.registerClassField(type, symbolTable.getVariableById(field.getId()),
                            field.getInitialValue());
                } else {
                    n.accept(this);
                }
            }
            currentClassId = null;
        }
    }

    @Override
    public void visit(BlockStatementNode node) {
        if (node.root() != null) {
            for (StatementNode n : node.root()) {
                n.accept(this);
            }
        }
    }

    @Override
    public void visit(VarDeclarationStatementNode node) {
        node.asDeclaration().accept(this);
    }

    @Override
    public void visit(AssignmentStatementNode node) {
        // Nothing to assign
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        // Nothing to assign
    }

    @Override
    public void visit(ReturnStatementNode node) {
        // Nothing to assign
    }

    @Override
    public void visit(IfElseStatementNode node) {
        node.getIfBlock().accept(this);
        if (node.getElsePart() != null) {
            node.getElsePart().accept(this);
        }
    }

    @Override
    public void visit(SwitchStatementNode node) {
        if (node.getCases() != null) {
            for (StatementNode n : node.getCases().values()) {
                n.accept(this);
            }
        }
        if (node.hasDefault()) {
            node.getDef().accept(this);
        }
    }

    @Override
    public void visit(WhileStatementNode node) {
        node.getBlock().accept(this);
    }

    @Override
    public void visit(ForStatementNode node) {
        node.getVariable().accept(this);
        node.getBlock().accept(this);
    }

    @Override
    public void visit(PointExpressionNode node) {
        // Nothing to assign
    }

    @Override
    public void visit(ArrayAccessExpressionNode node) {
        // Nothing to assign
    }

    @Override
    public void visit(FunctionCallExpressionNode node) {
        // Nothing to assign
    }

    @Override
    public void visit(VariableExpressionNode node) {
        // Nothing to assign
    }

    @Override
    public void visit(ConstantExpressionNode node) {
        // Nothing to assign
    }

    @Override
    public void visit(ListConstructorExpressionNode node) {
        // Nothing to assign
    }

    @Override
    public void visit(AnonymousObjectConstructorExpressionNode node) {
        if (node.getFields() != null) {
            for (DeclarationNode n : node.getFields()) {
                VarDeclarationNode var = (VarDeclarationNode) n;
                Variable v = symbolTable.getVariableById(var.getId());
                directions.registerFormField(currentForm, v, var.getInitialValue());
                if (Defaults.FORM.equals(v.type)) {
                    // Visit also recursive Form constructor
                    Integer previousForm = currentForm;
                    currentForm = var.getId();
                    var.getInitialValue().accept(this);
                    currentForm = previousForm;
                }
            }
        }
    }

    @Override
    public void visit(ConstructorCallExpressionNode node) {
        // Nothing to assign
    }

    @Override
    public void visit(ErrorDeclarationNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(ErrorStatementNode node) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visit(ErrorExpressionNode node) {
        throw new UnsupportedOperationException();
    }

}
