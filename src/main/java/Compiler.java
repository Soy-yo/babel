import java_cup.runtime.Symbol;
import lexical.LexicalAnalyser;
import lexical.LexicalUnit;
import semantical.*;
import syntactical.SyntacticalAnalyser;
import syntactical.ast.DeclarationNode;
import syntactical.ast.IdGenerator;
import syntactical.ast.ProgramNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;

public class Compiler {


    private final String input;
    private final String workingDir;
    private final Set<File> importedFiles;
    private final Map<DeclarationNode, String> firstNodeOfFiles;
    private int errors;
    private final IdGenerator generator;

    public Compiler(String input) {
        this.input = input;
        this.workingDir = Paths.get(input).getParent().toString();
        this.importedFiles = new HashSet<>(Collections.singleton(new File(input)));
        this.firstNodeOfFiles = new IdentityHashMap<>();
        this.errors = 0;
        this.generator = new IdGenerator();
    }

    public boolean getResult() {
        return errors == 0;
    }

    public boolean compile() throws Exception {
        System.out.println("[INFO] Compiling " + input);
        LexicalAnalyser la = new LexicalAnalyser(new InputStreamReader(new FileInputStream(input)));
        SyntacticalAnalyser sa = new SyntacticalAnalyser(la, generator);
        Symbol result = sa.parse();
        errors = sa.errors();
        if (result != null) {
            ProgramNode program = (ProgramNode) result.value;
            return compile(program);
        }
        return false;
    }

    private boolean compile(ProgramNode program) throws IOException {
        mergeFiles(program);
        if (errors > 30) {
            throw new IllegalStateException("Compiler found too many errors and won't continue");
        }
        SymbolTableCreator creator = new SymbolTableCreator(program, firstNodeOfFiles, generator);
        SymbolTable symbolTable = creator.create();
        errors += creator.errors();
        new syntactical.ast.visitors.ASTPrinter(program).print();
        if (symbolTable.getMainFunction() == null) {
            System.err.println("[ERROR] main function not declared");
            errors++;
        }
        // If there were any error don't try to generate code
        if (errors > 0) {
            System.err.println("[ERROR] Found " + errors + " error(s) during compilation");
            return false;
        }
        Directions directions = new MemoryAssigner(program, symbolTable).start();
        String output = input.replace(".bbl", "");
        CodeGenerator generator = new CodeGenerator(program, output, symbolTable, directions);
        generator.start();
        return true;
    }

    private void mergeFiles(ProgramNode program) {
        firstNodeOfFiles.put(program.root(), input);
        boolean first = true;
        DeclarationNode insertionPoint = null;
        for (LexicalUnit lu : program.importedFiles()) {
            String file = lu.lexeme();
            if ("".equals(file)) {
                // There were errors importing this file
                continue;
            }
            try {
                File fileObj = new File(workingDir + "/" + file + ".bbl");
                // Don't add same file multiple times
                if (importedFiles.contains(fileObj)) {
                    continue;
                }
                System.out.println("[INFO] Compiling " + file);
                LexicalAnalyser la = new LexicalAnalyser(new InputStreamReader(new FileInputStream(fileObj)));
                SyntacticalAnalyser sa = new SyntacticalAnalyser(la, generator);
                Symbol parsedLib = sa.parse();
                errors += sa.errors();
                if (parsedLib == null) {
                    throw new NullPointerException("couldn't parse file " + file);
                }
                importedFiles.add(fileObj);
                ProgramNode library = (ProgramNode) parsedLib.value;
                firstNodeOfFiles.put(library.root(), fileObj.toString());
                // Recursively merging files
                mergeFiles(library);
                DeclarationNode libraryRoot = library.root();
                if (first) {
                    DeclarationNode oldRoot = program.setRoot(libraryRoot);
                    insertionPoint = getLast(libraryRoot).linkedTo(oldRoot);
                    first = false;
                } else {
                    DeclarationNode next = insertionPoint.getNext();
                    insertionPoint.unlinked().linkedTo(libraryRoot);
                    insertionPoint = getLast(libraryRoot).linkedTo(next);
                }
                System.out.println("[INFO] Ended compiling " + file);
            } catch (Exception e) {
                System.err.println("[ERROR] imported file " + file + " contains errors: " + e.getMessage());
                errors++;
            }
        }
    }

    private static <T> T getLast(Iterable<T> it) {
        T result = null;
        for (T x : it) {
            result = x;
        }
        return result;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("[ERROR] main file was not specified");
            return;
        }
        String input = args[0];
        if (!input.endsWith(".bbl")) {
            input = input + ".bbl";
        }
        try {
            Compiler compiler = new Compiler(input);
            if (compiler.compile()) {
                System.out.println("[INFO] Compile process ended successfully");
            } else {
                System.out.println("[INFO] Compile process ended with errors");
            }
        } catch (Exception e) {
            if (e.getMessage() == null) {
                e.printStackTrace();
            } else {
                System.err.println("[ERROR] " + e.getMessage());
            }
        }
    }

}
