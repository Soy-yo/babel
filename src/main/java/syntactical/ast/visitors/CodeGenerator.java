package syntactical.ast.visitors;

import syntactical.Defaults;
import syntactical.ast.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CodeGenerator implements Visitor {

    private static final int OFFSET = 5;

    // Main node
    private final ASTNode root;

    // Utility structures
    private final SymbolTable symbolTable;
    private final Directions directions;

    // Actual code generation
    private final List<Instruction> code;
    private final BufferedWriter writer;

    // Dynamic control variables
    private int currentSP;
    private int currentPC;
    private int currentEP;
    private int maxEP;
    private int localMaxEP;
    private Type currentClass;
    private boolean inGlobalScope;

    // Label generator
    private final NewLabel newLabel;
    private final Map<String, List<Integer>> missingLabels;
    private final Map<String, Integer> labels;

    // TODO update EP
    // TODO check if it should be ... + OFFSET - 1 or just ... + OFFSET

    public CodeGenerator(ASTNode root, String file, SymbolTable symbolTable, Directions directions) throws IOException {
        this.root = root;
        this.symbolTable = symbolTable;
        this.directions = directions;
        this.code = new ArrayList<>();
        this.writer = new BufferedWriter(new FileWriter(file));
        this.currentSP = 0;
        this.currentPC = 0;
        this.currentEP = 0;
        this.maxEP = 0;
        this.currentClass = null;
        this.inGlobalScope = true;
        this.newLabel = new NewLabel();
        this.missingLabels = new HashMap<>();
        this.labels = new HashMap<>();
    }

    public void start() throws IOException {
        root.accept(this);
        writeCode();
        writer.close();
    }

    @Override
    public void visit(ProgramNode node) {
        int declarationsSize = directions.getFunctionSize(SymbolTable.GLOBAL_SCOPE_ID);
        String epLabel = newLabel.getLabel();
        issueLabeled("ent", epLabel, 0, "" + (declarationsSize + OFFSET));
        // Activate "global scope function" so all variables can be acceded with same formula
        currentSP = declarationsSize + OFFSET - 1;
        if (node.root() != null) {
            for (DeclarationNode n : node.root()) {
                // Visit global variables first
                if (n instanceof VarDeclarationNode) {
                    n.accept(this);
                }
            }
            Function main = symbolTable.getMainFunction();
            String end = newLabel.getLabel();
            // Call main
            issue("mst", "0");
            issueLabeled("cup", newLabel.getLabel(main), 1, "0");
            // Jump to stop instruction
            issueLabeled("ujp", end, 0);
            for (DeclarationNode n : node.root()) {
                // Visit classes (just methods)
                if (n instanceof ClassDeclarationNode) {
                    n.accept(this);
                }
            }
            for (DeclarationNode n : node.root()) {
                // Finally visit functions
                if (n instanceof FunctionDeclarationNode) {
                    n.accept(this);
                }
            }
            issueLabel(end);
        }
        issueLabel(epLabel, maxEP);
        issue("stp");
    }

    @Override
    public void visit(VarDeclarationNode node) {
        // TODO think it's okay
        ExpressionNode initialValue = node.getInitialValue();
        if (initialValue == null) {
            issue("ldc", "-1");
        } else {
            if (Defaults.FORM.equals(node.getType())) {
                // Allocate memory for the fields
                List<Directions.FieldData> fields = directions.getFormFields(node.getId());
                if (fields == null) {
                    alloc(1);
                } else {
                    alloc(fields.size());
                }
            }
            initialValue.accept(this);
        }
        int offset = directions.getVariableDir(node.getId());
        // Store expression result in the corresponding direction
        issue("str", "0", "" + (offset + OFFSET));
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        Function f = symbolTable.getFunctionById(node.getId());
        issueLabel(newLabel.getLabel(f));
        // Size of parameters and local variables
        int size = directions.getFunctionSize(node.getId());
        // Make room for local variables
        String epLabel = newLabel.getLabel();
        currentEP = 0;
        localMaxEP = 0;
        issueLabeled("ent", epLabel, 0, "" + (size + OFFSET));
        // Reset current SP
        int previousSP = currentSP;
        // TODO check -1
        currentSP = size + OFFSET - 1;
        boolean g = inGlobalScope;
        inGlobalScope = false;
        node.getCode().accept(this);
        inGlobalScope = g;
        currentSP = previousSP;
        // TODO check if this is the return we want
        // Extra return in case the function doesn't have one
        issue("retf");
        issueLabel(epLabel, localMaxEP);
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        // TODO check this
        Function f = symbolTable.getFunctionById(node.getId());
        Type type = node.getType();
        issueLabel(newLabel.getLabel(f));
        int size = directions.getFunctionSize(node.getId());
        String epLabel = newLabel.getLabel();
        currentEP = 0;
        localMaxEP = 0;
        issueLabeled("ent", epLabel, 0, "" + (size + OFFSET));
        // Reset current SP
        int previousSP = currentSP;
        // TODO check -1
        currentSP = size + OFFSET - 1;
        List<Directions.FieldData> fields = directions.getClassFields(type);
        // Allocate memory for all fields in the class
        alloc(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            Directions.FieldData d = fields.get(i);
            if (d.initialValue == null) {
                continue;
            }
            // Remember variable direction
            issue("dpl");
            if (i > 0) {
                // Add offset for every field
                issue("ldc", "" + i);
                issue("add");
            }
            d.initialValue.accept(this);
            // Store expression result in its corresponding direction
            issue("sto");
        }
        // Save "this" in the first parameter
        issue("str", "0", "" + OFFSET);
        boolean g = inGlobalScope;
        inGlobalScope = false;
        node.getCode().accept(this);
        // Save this as return value and ignore any other possible return
        issue("lod", "0", "" + OFFSET);
        issue("str", "0", "0");
        // Return
        issue("retf");
        issueLabel(epLabel, localMaxEP);
        inGlobalScope = g;
        currentSP = previousSP;
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        // TODO think it's okay
        currentClass = node.getType();
        if (node.getContentRoot() != null) {
            for (DeclarationNode n : node.getContentRoot()) {
                if (!(n instanceof VarDeclarationNode)) {
                    n.accept(this);
                }
            }
        }
        currentClass = null;
    }

    @Override
    public void visit(BlockStatementNode node) {
        // TODO think it's okay
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
        // TODO think it's okay
        Designator.AccessMethod method = node.getAccessMethod();
        ExpressionNode target = node.getTarget();
        switch (method) {
            case NONE:
                variableAccess((VariableExpressionNode) target);
                break;
            case FIELD:
                pointAccess(target, node.getAccessExpression());
                break;
            case ARRAY:
                arrayAccess(target, node.getAccessExpression());
                break;
        }
        node.getValue().accept(this);
        issue("sto");
    }

    @Override
    public void visit(FunctionCallStatementNode node) {
        // TODO think it's okay
        node.asExpression().accept(this);
        // Ignore return value
        issue("str", "0", "0");
    }

    @Override
    public void visit(ReturnStatementNode node) {
        // TODO think it's okay
        // Take mp to stack
        issue("lda", "0", "0");
        // Take return value to stack
        node.getReturnExpression().accept(this);
        // Store return value in mp
        issue("sto");
        // Return
        issue("retf");
    }

    @Override
    public void visit(IfElseStatementNode node) {
        // TODO think it's okay
        String ifFalseLabel = newLabel.getLabel();
        if (node.getElsePart() != null) {
            String ifEndLabel = newLabel.getLabel();
            // Write code for condition
            node.getCondition().accept(this);
            // If condition is false jump to #ifFalseLabel
            issueLabeled("fjp", ifFalseLabel, 0);
            // Write if block
            node.getIfBlock().accept(this);
            // Jump to #ifEndLabel
            issueLabeled("ujp", ifEndLabel, 0);
            // Issue label so if knows were to jump when condition is false
            issueLabel(ifFalseLabel);
            // Write else block
            node.getElsePart().accept(this);
            // Issue label so if knows were to jump when if block ends
            issueLabel(ifEndLabel);
        } else {
            node.getCondition().accept(this);
            issueLabeled("fjp", ifFalseLabel, 0);
            node.getIfBlock().accept(this);
            issueLabel(ifFalseLabel);
        }
    }

    @Override
    public void visit(SwitchStatementNode node) {
        // TODO think it's okay
        TreeMap<ConstantExpressionNode, StatementNode> map = new TreeMap<>(node.getCases());
        String defaultLabel;
        String endLabel = newLabel.getLabel();
        if (node.hasDefault()) {
            defaultLabel = newLabel.getLabel(node.getDef());
        } else {
            defaultLabel = endLabel;
        }
        // switch value should be a primitive
        // we place it on top of the stack
        node.getSwitchExpression().accept(this);
        // duplicate to compare to first
        issue("dpl");
        // put first in stack to compare
        issue("ldc", String.valueOf(map.firstKey().getValue()));
        issue("geq");
        // if false skip switch
        issueLabeled("fjp", defaultLabel, 0);
        // duplicate to compare to last
        issue("dpl");
        // put last in stack to compare
        issue("ldc", String.valueOf(map.lastKey().getValue()));
        issue("leq");
        // if false skip switch
        issueLabeled("fjp", defaultLabel, 0);
        // we place the first value on top of the stack for indexing
        issue("ldc", String.valueOf(map.firstKey().getValue()));
        // we subtract them so now we index starting on the first possible value
        issue("sub");
        String jumpTable = newLabel.getLabel();
        // we jump to the end of the jumpTable + the value of the expression
        issueLabeled("ixj", jumpTable, 0);
        // We create a list of labels, all of them point to the default one and it has exactly
        // the size we need
        String[] labels = new String[map.lastKey().getValue() - map.firstKey().getValue() + 1];
        Arrays.fill(labels, defaultLabel);
        for (Map.Entry<ConstantExpressionNode, StatementNode> entry : node.getCases().entrySet()) {
            String label = newLabel.getLabel();
            // we replace this element's label for the actual one
            labels[entry.getKey().getValue() - map.firstKey().getValue()] = label;
            // create the label
            issueLabel(label);
            // Think it should work
            entry.getValue().accept(this);
            issueLabeled("ujp", endLabel, 0);
        }
        if(node.hasDefault()) {
          issueLabel(defaultLabel);
          node.getDef().accept(this);
          issueLabeled("ujp", endLabel, 0);
        }
        // so we know were the jump table starts
        issueLabel(jumpTable);
        for (String label : labels) {
            // create jump table
            issueLabeled("ujp", label, 0);
        }
        issueLabel(endLabel);
    }

    @Override
    public void visit(WhileStatementNode node) {
        // TODO think it's okay
        String whileLabel = String.valueOf(currentPC);
        String whileEndLabel = newLabel.getLabel();
        // Write code for condition
        node.getCondition().accept(this);
        // If condition is false jump to #whileEndLabel
        issueLabeled("fjp", whileEndLabel, 0);
        // Write while block code
        node.getBlock().accept(this);
        // Jump back to first instruction of condition
        issue("ujp", whileLabel);
        // Issue label so while knows were to jump when condition is false
        issueLabel(whileEndLabel);
    }

    @Override
    public void visit(ForStatementNode node) {
        // TODO think it's okay
        VarDeclarationNode variable = node.getVariable();
        ExpressionNode iterable = node.getIterable();
        Variable v = symbolTable.getVariableById(variable.getId());
        int offset = directions.getVariableDir(v.id);
        String forEndLabel = newLabel.getLabel();
        boolean toFor = false;
        if (iterable instanceof FunctionCallExpressionNode) {
            FunctionCallExpressionNode iterableFunction = (FunctionCallExpressionNode) iterable;
            Function f = symbolTable.getFunctionById(iterableFunction.getId());
            ExpressionNode function = iterableFunction.getFunction();
            List<ExpressionNode> arguments = iterableFunction.getArguments();
            if (f.id == Defaults.Int.TO_ID || f.id == Defaults.Real.TO_ID || f.id == Defaults.Char.TO_ID) {
                // Primitive ... Primitive
                ExpressionNode left = ((PointExpressionNode) function).getHost();
                ExpressionNode right = arguments.get(0);
                right.accept(this);
                left.accept(this);
                int forStart = currentPC;
                // Save it into the loop variable
                issue("str", "0", "" + (offset + OFFSET));
                // Duplicate right for future comparisons
                issue("dpl");
                // Retrieve left again
                issue("lod", "0", "" + (offset + OFFSET));
                // variable <= right
                issue("geq");
                // Jump outside the loop if condition is false
                issueLabeled("fjp", forEndLabel, 0);
                // And again in case the variable gets modified inside the loop
                issue("lod", "0", "" + (offset + OFFSET));
                node.getBlock().accept(this);
                // Next iteration
                issue("inc", "1");
                // Jump to loop start
                issue("ujp", "" + forStart);
                toFor = true;
            }
        }
        if (!toFor) {
            // It's a for loop over an array
            iterable.accept(this);
            // Load index
            issue("ldc", "0");
            int forStart = currentPC;
            // Save it
            issue("str", "0", "0");
            // Duplicate array for future accesses
            issue("dpl");
            // Get array.size
            issue("ind");
            // Retrieve i again
            issue("lod", "0", "0");
            // index < size
            issue("grt");
            // Jump outside the loop if condition is false
            issueLabeled("fjp", forEndLabel, 0);
            // We will need array pointer again
            issue("dpl");
            // And index
            issue("lod", "0", "0");
            // index++ as we want to ignore size's location
            issue("inc", "1");
            // Don't lose it
            issue("dpl");
            // Save it incremented
            issue("str", "0", "0");
            // array pointer + index (+1)
            issue("add");
            // Get array[index]
            issue("ind");
            // Store in into the loop variable
            issue("str", "0", "" + (offset + OFFSET));
            // And get index again for the next iteration
            issue("lod", "0", "0");
            node.getBlock().accept(this);
            // Jump to loop start
            issue("ujp", "" + forStart);
        }
        issueLabel(forEndLabel);
        // Remove duplicated right or array
        issue("str", "0", "0");
    }

    @Override
    public void visit(PointExpressionNode node) {
        // TODO think it's okay
        // Starts with SP and ends with SP + 1 with the value of host.field on top of the stack
        pointAccess(node.getHost(), node.getField());
        // Finally get the field
        issue("ind");
    }

    private void pointAccess(ExpressionNode host, ExpressionNode field) {
        // Starts with SP and ends with SP + 1 with host.field direction on top of the stack
        Variable fieldVar = symbolTable.getVariableById(field.getId());
        Type type = host.getType();
        host.accept(this);
        if (Defaults.FORM.equals(type)) {
            // Must be a variable or a point expression
            Variable hostVar;
            if (host instanceof VariableExpressionNode) {
                hostVar = symbolTable.getVariableById(host.getId());
            } else {
                hostVar = symbolTable.getVariableById(((PointExpressionNode) host).getField().getId());
            }
            Directions.FieldData data = directions.getFormField(hostVar.id, fieldVar);
            if (data.index > 0) {
                // Set the direction to the pointer to the Form + the offset of the field
                issue("inc", "" + data.index);
            }
        } else if (!Defaults.ARRAY.equals(type)) {
            // If it is an array teh field is the size, stored in the first position
            Directions.FieldData data = directions.getClassField(type, fieldVar);
            if (data.index > 0) {
                // Set the direction to the pointer to the object + the offset of the field
                issue("inc", "" + data.index);
            }
        }
    }

    @Override
    public void visit(ArrayAccessExpressionNode node) {
        // TODO think it's okay
        // Starts with SP and ends with SP + 1: contents of array[index] (if no error)
        arrayAccess(node.getArray(), node.getIndex());
        // Get whatever it is in that direction
        issue("ind");
    }

    private void arrayAccess(ExpressionNode array, ExpressionNode index) {
        String error = newLabel.getLabel();
        String noError = newLabel.getLabel();
        // Write the code to access the array pointer
        array.accept(this);
        // Write the code to to access the array index
        index.accept(this);
        // We can't use chk because we don't know the size statically, so we have to fake it
        // Duplicate the index as we need to access twice
        issue("dpl");
        // This should retrieve the size of the array
        issue("ldd", "0");
        // index < array.size ?
        issue("les");
        // If false jump to #error
        issueLabeled("fjp", error, 0);
        // Duplicate again to compare with 0
        issue("dpl");
        issue("ldc", "0");
        // index >= 0 ?
        issue("geq");
        // If index < 0 jump to #error
        issueLabeled("fjp", error, 0);
        // If we are here everything is okay
        // Array direction + index
        issue("add");
        issue("inc", "1");
        // There has been no errors so jump after the error check
        issueLabeled("ujp", noError, 0);
        issueLabel(error);
        // Dummy error check -> if STORE[SP] not in (0, -1) then error
        issue("chk", "0", "-1");
        issueLabel(noError);
    }

    @Override
    public void visit(FunctionCallExpressionNode node) {
        // TODO special cases as well for Form == Form, Primitive ... Primitive
        Function f = symbolTable.getFunctionById(node.getId());
        ExpressionNode function = node.getFunction();
        List<ExpressionNode> args = node.getArguments();
        boolean generated = false;
        if (f.id == Defaults.IDENTITY_ID) {
            args.get(0).accept(this);
            args.get(1).accept(this);
            issue("equ");
            generated = true;
        } else if (function instanceof PointExpressionNode) {
            // May be an operator
            if (args.isEmpty()) {
                // Unary
                String code = unaryOperator(f.id);
                if (code != null) {
                    ((PointExpressionNode) function).getHost().accept(this);
                    issue(code);
                    generated = true;
                }
            } else if (args.size() == 1) {
                // Binary
                String code = binaryOperator(f.id);
                if (code != null) {
                    ((PointExpressionNode) function).getHost().accept(this);
                    args.get(0).accept(this);
                    issue(code);
                    generated = true;
                } else if (f.id == Defaults.Int.MOD_ID) {
                    // r = a % b = a - b * (a//b)
                    ((PointExpressionNode) function).getHost().accept(this);
                    // Duplicate a as we will ned it in the future
                    issue("dpl");
                    args.get(0).accept(this);
                    // We will need b as well
                    issue("dpl");
                    // Save b
                    issue("str", "0", "0");
                    // c = a//b
                    issue("div");
                    // Restore b
                    issue("lod", "0", "0");
                    // b * c
                    issue("mul");
                    // a - b * c
                    issue("sub");
                    generated = true;
                }
            }
        }
        if (!generated) {
            // Create activation frame
            issue("mst", inGlobalScope ? "0" : "1");
            // Now SP should be pointing to the first parameter direction
            int argsSize = args.size();
            if (f.isMethod) {
                argsSize++;
                if (function instanceof PointExpressionNode) {
                    // Send "this"
                    ExpressionNode receiver = ((PointExpressionNode) function).getHost();
                    receiver.accept(this);
                } else {
                    // The function is a method but it's not being called with point expression
                    // Then "this" is implicit "this": load it
                    issue("lod", "0", "" + OFFSET);
                }
            }
            for (ExpressionNode e : args) {
                // Evaluate the argument
                e.accept(this);
                // TODO probably there's no need to save it anywhere
                // Store it in its position
                //issue("str", "0", String.valueOf(i));
            }
            // TODO function should just be f
            // Get function
            //Function thisFunction = symbolTable.getFunctionById(node.getFunction().getId());
            // Save space for parameters, save return address and go to function
            issueLabeled("cup", newLabel.getLabel(f), 1, "" + argsSize);
            // TODO I think it is the function the one which have to do this
            //issue("ssp", String.valueOf(staticData));
            //issue("sep", String.valueOf(maxDepth));
        }
    }

    @Override
    public void visit(VariableExpressionNode node) {
        // TODO think it's okay,
        // Starts with SP and ends with SP + 1 with variable contents on top of the stack
        variableAccess(node);
        // Get whatever it is in that direction
        issue("ind");
    }

    private void variableAccess(VariableExpressionNode node) {
        // TODO make sure else if branch is okay
        // Starts with SP and ends with SP + 1: contents of variable
        Variable v = symbolTable.getVariableById(node.getId());
        if (directions.existsVariable(v.id)) {
            // Variable is global/local
            int offset = directions.getVariableDir(v.id);
            String base = inGlobalScope ? "0" : v.isGlobal ? "1" : "0";
            issue("lda", base, "" + (offset + OFFSET));
        } else if (currentClass != null) {
            // Variable is class field
            // Load "this" (is the first parameter of the method)
            issue("lod", "0", "" + OFFSET);
            Directions.FieldData data = directions.getClassField(currentClass, v);
            if (data.index > 0) {
                // Set the direction to the pointer to this + the offset of the field
                issue("inc", "" + data.index);
            }
        } else {
            throw new IllegalStateException("Unknown variable " + v);
        }
    }

    @Override
    public void visit(ConstantExpressionNode node) {
        // TODO think it's okay
        // Starts with SP and ends with SP + 1: constant
        if (node.getType().equals(Defaults.BOOL)) {
            issue("ldc", "" + (node.getValue() == 0b1));
        } else {
            issue("ldc", "" + node.getValue());
        }
    }

    @Override
    public void visit(ListConstructorExpressionNode node) {
        // TODO think it's okay
        // Starts with SP and ends with SP + 1: direction of the array
        // new instruction is stupid and needs to know where to put the pointer
        List<ExpressionNode> elements = node.getElements();
        // Allocate memory for all the elements (+1 for the size)
        alloc(elements.size() + 1);
        // TODO any better way to do this?
        // And we will use it multiple times
        issue("dpl");
        // Load again the size
        issue("ldc", "" + elements.size());
        // And store it in the first direction of the array
        issue("sto");
        // We have the direction of the array on the top of the stack
        int i = 1;
        for (ExpressionNode n : elements) {
            // Duplicate direction so we don't lose it for the next iteration
            issue("dpl");
            // Get next direction
            issue("inc", "" + i);
            n.accept(this);
            // Save the result of the expression
            issue("sto");
            i++;
        }
    }

    @Override
    public void visit(AnonymousObjectConstructorExpressionNode node) {
        // TODO think it's okay
        int i = 0;
        for (DeclarationNode n : node.getFields()) {
            ExpressionNode initialValue = ((VarDeclarationNode) n).getInitialValue();
            // Duplicate the pointer to the Form
            issue("dpl");
            if (i > 0) {
                // Add the offset of this field
                issue("inc", "" + i);
            }
            if (Defaults.FORM.equals(n.getType())) {
                // Allocate memory for the fields
                List<Directions.FieldData> fields = directions.getFormFields(n.getId());
                alloc(fields.size());
            }
            initialValue.accept(this);
            // Store the initial value in the correct position
            issue("sto");
            i++;
        }
    }

    @Override
    public void visit(ConstructorCallExpressionNode node) {
        // Starts with SP and NP and ends with SP + 1 with direction of NP - size of object allocated
        Function f = symbolTable.getFunctionById(node.getId());
        List<ExpressionNode> arguments = node.getArguments();
        if (f.id == Defaults.Array.CONSTRUCTOR_ID) {
            ExpressionNode size = arguments.get(0);
            // This must return the size on top of the stack
            size.accept(this);
            // Duplicate size to use it later
            issue("dpl");
            // Save the size in MP to use it later
            issue("str", "0", "0");
            // Load MP direction so result of new is saved there
            issue("lda", "0", "0");
            // Reload the size
            issue("lod", "0", "0");
            // +1 for the extra element
            issue("inc", "1");
            // Create room for size elements in the heap and put the pointer in MP
            issue("new");
            // TODO any better way so we don't have to use it this direction?
            // Save size into temporary direction: 1 as directions 0-4 are unused (but we might be using 0)
            issue("sro", "1");
            // Reload result from MP
            issue("lod", "0", "0");
            // And reload size
            issue("ldo", "1");
            // Save the size in the first position of the array
            issue("sto");
            // And finally reload result of the expression from MP
            issue("lod", "0", "0");
            // TODO if parameter is primitive fill it with zeroes
        } else {
            // Create activation frame
            issue("mst", inGlobalScope ? "0" : "1");
            // Now SP should be pointing to the first parameter direction
            int argsSize = arguments.size() + 1;
            // Put null in "this" parameter for the moment
            issue("ldc", "-1");
            for (ExpressionNode e : arguments) {
                // Evaluate the argument
                e.accept(this);
            }
            // Save space for parameters, save return address and go to function
            issueLabeled("cup", newLabel.getLabel(f), 1, "" + argsSize);
        }
    }

    @Override
    public void visit(ErrorDeclarationNode node) {
        // If we are generating code we can't find these kinds of nodes
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

    private void alloc(int size) {
        // Starts with SP and NP and ends with SP + 1 and NP - size with pointer to new NP in STORE[SP]
        // Load MP direction so result of new is saved there
        issue("lda", "0", "0");
        // Load the number of elements
        issue("ldc", "" + size);
        // Create room for size elements in the heap and put the pointer in MP
        issue("new");
        // Reload result from MP
        issue("lod", "0", "0");
    }

    private String binaryOperator(int operator) {
        switch (operator) {
            case Defaults.Int.PLUS_ID:
            case Defaults.Real.PLUS_ID:
                return "add";
            case Defaults.Int.MINUS_ID:
            case Defaults.Real.MINUS_ID:
                return "sub";
            case Defaults.Int.MULT_ID:
            case Defaults.Real.MULT_ID:
                return "mul";
            case Defaults.Int.DIV_ID:
            case Defaults.Real.DIV_ID:
                return "div";
            case Defaults.Int.EQUALS_ID:
            case Defaults.Real.EQUALS_ID:
            case Defaults.Bool.EQUALS_ID:
            case Defaults.Char.EQUALS_ID:
                return "equ";
            case Defaults.Int.LE_ID:
            case Defaults.Real.LE_ID:
            case Defaults.Char.LE_ID:
                return "leq";
            case Defaults.Int.GE_ID:
            case Defaults.Real.GE_ID:
            case Defaults.Char.GE_ID:
                return "geq";
            case Defaults.Int.LT_ID:
            case Defaults.Real.LT_ID:
            case Defaults.Char.LT_ID:
                return "les";
            case Defaults.Int.GT_ID:
            case Defaults.Real.GT_ID:
            case Defaults.Char.GT_ID:
                return "grt";
            case Defaults.Bool.AND_ID:
                return "and";
            case Defaults.Bool.OR_ID:
                return "or";
        }
        return null;
    }

    private String unaryOperator(int operator) {
        switch (operator) {
            case Defaults.Int.UNARY_PLUS_ID:
            case Defaults.Real.UNARY_PLUS_ID:
                // Do nothing
                return "";
            case Defaults.Int.UNARY_MINUS_ID:
            case Defaults.Real.UNARY_MINUS_ID:
                return "neg";
            case Defaults.Bool.NOT_ID:
                return "not";
        }
        return null;
    }

    private void issue(String instruction, String... parameters) {
        code.add(new Instruction(instruction, Arrays.asList(parameters)));
        updateSP(InstructionsMap.get(instruction));
        currentPC++;
    }

    private void issueLabeled(String instruction, String label, int index, String... parameters) {
        // Make room for the last parameter
        if (index == parameters.length) {
            parameters = Arrays.copyOf(parameters, parameters.length + 1);
            parameters[parameters.length - 1] = null;
        } else if (index > parameters.length) {
            throw new IllegalArgumentException("Cannot label a parameter that doesn't exist");
        }
        // If the label has already been issued just replace it
        if (labels.containsKey(label)) {
            String value = "" + labels.get(label);
            code.add(new Instruction(instruction, Arrays.asList(parameters), index).withLabelValue(value));
            currentPC++;
            return;
        }
        // Otherwise add the instruction and the missing label
        code.add(new Instruction(instruction, Arrays.asList(parameters), index));
        List<Integer> values = missingLabels.get(label);
        if (values == null) {
            missingLabels.put(label, new ArrayList<>(Collections.singletonList(currentPC)));
        } else {
            values.add(currentPC);
        }
        updateSP(InstructionsMap.get(instruction));
        currentPC++;
    }

    private void issueLabel(String label) {
        List<Integer> missing = missingLabels.get(label);
        if (missing != null) {
            for (Integer i : missing) {
                code.get(i).withLabelValue("" + currentPC);
            }
            missingLabels.remove(label);
        }
        labels.put(label, currentPC);
    }

    private void issueLabel(String label, int value) {
        List<Integer> missing = missingLabels.get(label);
        if (missing != null) {
            for (Integer i : missing) {
                code.get(i).withLabelValue("" + value);
            }
            missingLabels.remove(label);
        }
        labels.put(label, value);
    }

    private void updateSP(int n) {
        // TODO update EP if needed
        currentSP += n;
        currentEP += n;
        maxEP = currentSP > maxEP ? currentSP : maxEP;
        localMaxEP = currentEP > localMaxEP ? currentEP : localMaxEP;
    }

    private void writeCode() throws IOException {
        for (Instruction i : code) {
            writer.write(i.toString() + "\n");
        }
    }

    private static class Instruction {

        private final String instruction;
        private final List<String> parameters;
        private final int labelIndex;

        private Instruction(String instruction, List<String> parameters, int labelIndex) {
            this.instruction = instruction;
            this.parameters = new ArrayList<>(parameters);
            this.labelIndex = labelIndex;
        }

        private Instruction(String instruction, List<String> parameters) {
            this(instruction, parameters, -1);
        }

        Instruction withLabelValue(String labelValue) {
            parameters.set(labelIndex, labelValue);
            return this;
        }

        @Override
        public String toString() {
            return instruction + (parameters.isEmpty() ? "" : " " + String.join(" ", parameters)) + ";";
        }

    }

}
