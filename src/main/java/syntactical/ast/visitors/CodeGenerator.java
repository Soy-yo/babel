package syntactical.ast.visitors;

import syntactical.Defaults;
import syntactical.ast.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CodeGenerator implements Visitor {

    private final ASTNode root;
    private final String file;
    private final SymbolTable symbolTable;
    // Variable id -> dir
    private final Map<Integer, Integer> variableDirs;
    // Type -> [(field, initial value expression)]
    private final Map<Type, List<VarInitPair>> classFields;
    // Variable id -> [field]
    private final Map<Integer, List<Variable>> formFields;
    private boolean classFieldsGenerated;
    private int currentSP;
    private int globalSP;
    private int currentPC;
    private Type currentClass;
    private List<Instruction> code;
    private BufferedWriter writer;
    // Label generator
    private final NewLabel newLabel;
    private final Map<String, List<Integer>> missingLabels;
    private final Map<String, Integer> labels;

    // TODO probably when using Variable.depth we should say currentDepth - Variable.depth

    public CodeGenerator(ASTNode root, String file, SymbolTable symbolTable) throws IOException {
        this.root = root;
        this.file = file;
        this.symbolTable = symbolTable;
        this.variableDirs = new HashMap<>();
        this.classFields = new HashMap<>();
        this.formFields = new HashMap<>();
        this.classFieldsGenerated = false;
        // TODO theoretically STORE[0] = main program MP
        this.currentSP = 0;
        this.globalSP = 0;
        this.currentPC = 0;
        this.currentClass = null;
        this.code = new ArrayList<>();
        this.newLabel = new NewLabel();
        this.missingLabels = new HashMap<>();
        this.labels = new HashMap<>();
        this.writer = new BufferedWriter(new FileWriter(file));
    }

    public void start() throws IOException {
        root.accept(this);
        writeCode();
        writer.close();
    }

    @Override
    public void visit(ProgramNode node) {
        // TODO think it's okay
        if (node.root() != null) {
            for (DeclarationNode n : node.root()) {
                // Visit classes (just fields)
                if (n instanceof ClassDeclarationNode) {
                    n.accept(this);
                }
            }
            classFieldsGenerated = true;
            for (DeclarationNode n : node.root()) {
                // Visit global variables first
                if (n instanceof VarDeclarationNode) {
                    n.accept(this);
                }
            }
            // Call main
            Function main = symbolTable.getMainFunction();
            issueLabeled("ujp", newLabel.getLabel(main), 0);
            for (DeclarationNode n : node.root()) {
                // Visit classes (now just methods)
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
        }
        issue("stp");
    }

    @Override
    public void visit(VarDeclarationNode node) {
        // TODO think it's okay
        Type type = node.getType();
        ExpressionNode initialValue = node.getInitialValue();
        if (initialValue == null) {
            issue("ldc", "0");
        } else {
            initialValue.accept(this);
            if (Defaults.FORM.equals(type)) {
                // Fix the map
                List<Variable> fields = formFields.remove(initialValue.getId());
                formFields.put(node.getId(), fields);
            }
        }
        variableDirs.put(node.getId(), currentSP - 1);
    }

    @Override
    public void visit(FunctionDeclarationNode node) {
        Function f = symbolTable.getFunctionById(node.getId());
        issueLabel(newLabel.getLabel(f));
        // TODO make room for parameters (and "this" for methods)
        node.getCode().accept(this);
    }

    @Override
    public void visit(ConstructorDeclarationNode node) {
        visit((FunctionDeclarationNode) node);
    }

    @Override
    public void visit(ClassDeclarationNode node) {
        // TODO think it's okay
        currentClass = node.getType();
        if (!classFieldsGenerated) {
            List<VarInitPair> fields = new ArrayList<>();
            classFields.put(node.getType(), fields);
            if (node.getContentRoot() != null) {
                for (DeclarationNode n : node.getContentRoot()) {
                    if (n instanceof VarDeclarationNode) {
                        // Just remember that this class has this variable declaration
                        Variable v = symbolTable.getVariableById(n.getId());
                        fields.add(new VarInitPair(v, ((VarDeclarationNode) n).getInitialValue()));
                    }
                }
            }
        } else {
            if (node.getContentRoot() != null) {
                for (DeclarationNode n : node.getContentRoot()) {
                    if (!(n instanceof VarDeclarationNode)) {
                        n.accept(this);
                    }
                }
            }
        }
        currentClass = null;
    }

    @Override
    public void visit(BlockStatementNode node) {
        if (node.root() != null) {
            // TODO add MP, etc ?
            //int dir = currentSP;
            //currentSP = 0;
            for (StatementNode n : node.root()) {
                n.accept(this);
            }
            //currentSP = dir;
            // TODO somehow remove or ignore memory used
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
                target.accept(this);
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
        node.asExpression().accept(this);
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
            issue("fjp", ifFalseLabel);
            // Write if block
            node.getIfBlock().accept(this);
            // Jump to #ifEndLabel
            issue("ujp", ifEndLabel);
            // Issue label so if knows were to jump when condition is false
            issueLabel(ifFalseLabel);
            // Write else block
            node.getElsePart().accept(this);
            // Issue label so if knows were to jump when if block ends
            issueLabel(ifEndLabel);
        } else {
            node.getCondition().accept(this);
            issue("fjp", ifFalseLabel);
            node.getIfBlock().accept(this);
            issueLabel(ifFalseLabel);
        }
    }

    @Override
    public void visit(SwitchStatementNode node) {
        // TODO think it's okay
        TreeMap<ConstantExpressionNode, StatementNode> map = new TreeMap<>(node.getCases());
        String endLabel = newLabel.getLabel();
        // switch value should be a primitive
        // we place it on top of the stack
        node.getSwitchExpression().accept(this);
        // duplicate to compare to first
        issue("dpl");
        // put first in stack to compare
        issue("ldc", String.valueOf(map.firstKey().getValue()));
        issue("geq");
        // if false skip switch
        issueLabeled("fjp", endLabel, 0);
        // duplicate to compare to last
        issue("dpl");
        // put last in stack to compare
        issue("ldc", String.valueOf(map.lastKey().getValue()));
        issue("leq");
        // if false skip switch
        issueLabeled("fjp", endLabel, 0);
        // we place the first value on top of the stack for indexing
        issue("ldc", String.valueOf(map.firstKey().getValue()));
        // we subtract them so now we index starting on the first possible value
        issue("sub");
        String defaultLabel;
        String jumpTable = newLabel.getLabel();
        // we jump to the end of the jumpTable + the value of the expression
        issueLabeled("ixj", jumpTable, 0);
        if (node.hasDefault()) {
            defaultLabel = newLabel.getLabel(node.getDef());
        } else {
            defaultLabel = endLabel;
        }
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
            // TODO: this could be a block of code, don't know if it'd work
            // Think it should work
            entry.getValue().accept(this);
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
        issue("fjp", whileEndLabel);
        // Write while block code
        node.getBlock().accept(this);
        // Jump back to first instruction of condition
        issue("ujp", whileLabel);
        // Issue label so while knows were to jump when condition is false
        issueLabel(whileEndLabel);
    }

    @Override
    public void visit(ForStatementNode node) {
        VarDeclarationNode variable = node.getVariable();
        ExpressionNode iterable = node.getIterable();
        BlockStatementNode block = node.getBlock();
        Variable v = symbolTable.getVariableById(variable.getId());
        // TODO we are now in a new block add MP, etc ?
        issue("ldc", "0");
        // Duplicate value so we can remember it
        int forStart = currentPC;
        issue("dpl");
        // First variable of block
        variableDirs.put(variable.getId(), 0);
        if (iterable instanceof FunctionCallExpressionNode) {
            FunctionCallExpressionNode iterableFunction = (FunctionCallExpressionNode) iterable;
            Function f = symbolTable.getFunctionById(iterableFunction.getId());
            ExpressionNode function = iterableFunction.getFunction();
            List<ExpressionNode> arguments = iterableFunction.getArguments();
            if (f.id == Defaults.Int.TO_ID || f.id == Defaults.Real.TO_ID || f.id == Defaults.Char.TO_ID) {
                // Primitive ... Primitive
                // TODO i + left and compare to right
                // TODO if left > right go down somehow
                // String forEndLabel = newLabel.getLabel();
                // issue("fjp", forEndLabel);
                ExpressionNode left = ((PointExpressionNode) function).getHost();
                ExpressionNode right = arguments.get(0);
                left.accept(this);
                // variable = i + left
                issue("add");
                issue("dpl");
                issue("str", "" + v.depth, "0");
                right.accept(this);
                // variable > right
                issue("grt");
                // TODO jump if true (*)
                // issue("fjp", forEndLabel);
            }
        } else {
            // TODO access to iterable[i]
        }
        if (block.root() != null) {
            for (StatementNode n : block.root()) {
                n.accept(this);
            }
        }
        // Now the top of the stack should be i value so i++
        issue("inc", "1");
        // Return to the beginning of the loop
        issue("ujp", "" + forStart);
        // TODO (*) jump here
        // issueLabel(forEndLabel);
        // Remove duplicated loop index (random store because I can't find any pop instruction)
        issue("str", "" + v.depth, "0");
        // TODO close block etc
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
            List<Variable> fields = formFields.get(hostVar.id);
            int offset = fields.indexOf(fieldVar);
            if (offset > 0) {
                // Set the direction to the pointer to the Form + the offset of the field
                issue("inc", "" + offset);
            }
        } else {
            List<Variable> fields = classFields.get(type).stream()
                    .map(p -> p.variable)
                    .collect(Collectors.toList());
            int offset = fields.indexOf(fieldVar);
            if (offset > 0) {
                // Set the direction to the pointer to the object + the offset of the field
                issue("inc", "" + offset);
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
        // TODO update SP
        // TODO special cases as well for Form == Form, Array == Array, Primitive ... Primitive and maybe Int % Int
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
                }
            }
        }
        if (!generated) {
            int i = 1;
            for(ExpressionNode e : node.getArguments()) {
                // Evaluate the argument
                e.accept(this);
                // Store it in its position
                issue("str", "0", String.valueOf(i));
            }
            // TODO assing actual values
            // TODO I actually don't know what this one is
            int mstValue = 0;
            // Size of all the parameters
            int paramSize = node.getArguments().size();
            // Create the links
            issue("mst", String.valueOf(mstValue));
            // Get function
            Function thisFunction = symbolTable.getFunctionById(node.getFunction().getId());
            // Save space for parameters, save return address and go to function
            issueLabeled("cup", newLabel.getLabel(thisFunction), 1, String.valueOf(paramSize));
            // TODO Change for actual value
            int staticData = 0;
            issue("ssp", String.valueOf(staticData));
            // TODO Change for actual value
            int maxDepth = 0;
            issue("sep", String.valueOf(maxDepth));
        }
    }

    @Override
    public void visit(VariableExpressionNode node) {
        // TODO probably okay, but be careful with the else if branch
        // Starts with SP and ends with SP + 1: contents of variable
        Variable v = symbolTable.getVariableById(node.getId());
        if (variableDirs.containsKey(v.id)) {
            // Variable is global/local
            int dir = variableDirs.get(v.id);
            issue("lod", "" + v.depth, "" + dir);
        } else if (currentClass != null) {
            // Variable is class field
            int thisId = symbolTable.classId(currentClass);
            int thisDir = variableDirs.get(thisId);
            // TODO find this depth
            // Load this
            issue("lod", "0", "" + thisDir);
            List<Variable> fields = classFields.get(currentClass).stream()
                    .map(p -> p.variable)
                    .collect(Collectors.toList());
            int offset = fields.indexOf(v);
            if (offset > 0) {
                // Set the direction to the pointer to the Form + the offset of the field
                issue("inc", "" + offset);
            }
            // Get the field
            issue("ind");
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
        List<Variable> fields = new ArrayList<>();
        if (node.getFields() == null) {
            formFields.put(node.getId(), fields);
            alloc(1);
            return;
        }
        for (DeclarationNode n : node.getFields()) {
            Variable v = symbolTable.getVariableById(n.getId());
            fields.add(v);
        }
        formFields.put(node.getId(), fields);
        // Allocate memory for the fields
        alloc(fields.size());
        int i = 0;
        for (DeclarationNode n : node.getFields()) {
            ExpressionNode initialValue = ((VarDeclarationNode) n).getInitialValue();
            // Duplicate the pointer to the Form
            issue("dpl");
            if (i > 0) {
                // Add the offset of this field
                issue("inc", "" + i);
            }
            initialValue.accept(this);
            if (Defaults.FORM.equals(n.getType())) {
                // Fix the map
                List<Variable> f = formFields.remove(initialValue.getId());
                formFields.put(n.getId(), f);
            }
            // Store the initial value in the correct position
            issue("sto");
            i++;
        }
    }

    @Override
    public void visit(ConstructorCallExpressionNode node) {
        // TODO when reloading the size of the array, maybe do it as if it was a normal variable (relative to MP etc)
        //      so we don't have to hack it too much
        // Starts with SP and NP and ends with SP + 1 with direction of NP - size of object allocated
        Function f = symbolTable.getFunctionById(node.getId());
        Type type = f.returnType;
        // TODO test if it works (if it does we have to do it also inside functions)
        Type previousClass = currentClass;
        // Safe where "this" is
        variableDirs.put(symbolTable.classId(type), currentSP);
        List<ExpressionNode> arguments = node.getArguments();
        if (f.id == Defaults.Array.CONSTRUCTOR_ID) {
            ExpressionNode size = arguments.get(0);
            // This must return the size on top of the stack
            size.accept(this);
            // Notify that we are now inside the class
            currentClass = type;
            // "Hack" to make new store the result on top of the stack
            issue("ldc", "" + globalSP);
            // Duplicate it so the direction can be overwritten
            issue("dpl");
            // Reload the size
            issue("ldo", "" + (globalSP - 3));
            // +1 for the extra element
            issue("inc", "" + 1);
            // Create room for size elements in the heap and put the pointer on top of the stack
            // (with "ldc globalSP" hack)
            issue("new");
            // At this point we have (size, pointer) on top of the stack
            // Duplicate the pointer so we don't lose it
            issue("dpl");
            // Reload the size again
            issue("ldo", "" + (globalSP - 2));
            // Save the size in the first position of the array
            issue("sto");
            // TODO if parameter is primitive fill it with zeroes
            // Copy pointer over size so we don't generate garbage
            issue("sli");
            currentClass = previousClass;
            return;
        }
        // Notify that we are now inside the class
        currentClass = type;
        List<VarInitPair> fields = classFields.get(type);
        // Allocate memory for all fields in the class
        alloc(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            VarInitPair p = fields.get(i);
            if (p.initialValue == null) {
                continue;
            }
            // Remember variable direction
            issue("dpl");
            if (i > 0) {
                // Add offset for every field
                issue("ldc", "" + i);
                issue("add");
            }
            p.initialValue.accept(this);
            // Store expression result in its corresponding direction
            issue("sto");
        }
        currentClass = previousClass;
        // TODO call constructor as if it was just another function (with "this" parameter)
        //      (probably, we don't need to retrieve return value as we already have it on top of the stack)
        // TODO currentClass = type again after computing parameters
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
        // "Hack" to make new store the result on top of the stack
        issue("ldc", "" + globalSP);
        // Duplicate so we can use this position to save the result of new
        issue("dpl");
        // Load the number of elements
        issue("ldc", "" + size);
        // Create room for size elements in the heap and put the pointer on top of the stack
        // (with "ldc globalSP" hack)
        issue("new");

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
            // NEQ is translated as x._equals(y)._not()
            //case "!=":
            //    return "neq";
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

    private String convertType(Type type) {
        if (Defaults.INT.realEquals(type)) {
            return "i";
        } else if (Defaults.BOOL.realEquals(type)) {
            return "b";
        } else if (Defaults.REAL.realEquals(type)) {
            return "r";
        } else if (Defaults.CHAR.realEquals(type)) {
            return "c";
        }
        return "a";
    }

    private void issue(String instruction, String... parameters) {
        code.add(new Instruction(instruction, Arrays.asList(parameters)));
        updateSP(InstructionsMap.get(instruction));
        currentPC++;
    }

    private void issueLabeled(String instruction, String label, int index, String... parameters) {
        // TODO: I'm using currentPC as indexing for code list, if that's not right change it for
        //  actual indexing
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
        // TODO: I don't know if currentPC applies here, if not change for actual direction of the
        //  label
        List<Integer> missing = missingLabels.get(label);
        if (missing != null) {
            for (Integer i : missing) {
                code.get(i).withLabelValue("" + currentPC);
            }
            missingLabels.remove(label);
        }
        labels.put(label, currentPC);
    }

    private void updateSP(int n) {
        // TODO update EP if needed
        currentSP += n;
        globalSP += n;
    }

    private void writeCode() throws IOException {
        for (Instruction i : code) {
            writer.write(i.toString() + "\n");
        }
    }

    private static class VarInitPair {
        final Variable variable;
        final ExpressionNode initialValue;

        private VarInitPair(Variable variable, ExpressionNode initialValue) {
            this.variable = variable;
            this.initialValue = initialValue;
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
