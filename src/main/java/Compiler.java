import java_cup.runtime.Symbol;
import lexical.LexicalAnalyser;
import syntactical.SyntacticalAnalyser;
import syntactical.ast.DeclarationNode;
import syntactical.ast.ProgramNode;
import syntactical.ast.visitors.ASTPrinter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Compiler {

    private final String input;
    private final String workingDir;
    private final Set<File> importedFiles;
    private boolean result;

    public Compiler(String input) {
        this.input = input;
        this.workingDir = Paths.get(input).getParent().toString();
        this.importedFiles = new HashSet<>(Collections.singleton(new File(input)));
        this.result = false;
    }

    public boolean getResult() {
        return result;
    }

    public boolean compile() throws Exception {
        LexicalAnalyser la = new LexicalAnalyser(new InputStreamReader(new FileInputStream(input)));
        SyntacticalAnalyser sa = new SyntacticalAnalyser(la);
        Symbol result = sa.parse();
        if (result != null) {
            ProgramNode program = (ProgramNode) result.value;
            return compile(program);
        }
        return false;
    }

    private boolean compile(ProgramNode program) {
        // TODO stop at some point if result is already false
        mergeFiles(program);
        new ASTPrinter(program).print();
        return result;
    }

    private void mergeFiles(ProgramNode program) {
        boolean first = true;
        DeclarationNode insertionPoint = null;
        for (String file : program.importedFiles()) {
            // TODO if there were errors in input some may be empty strings ""
            // don't generate code in such cases
            try {
                File fileObj = new File(workingDir + "/" + file + ".bbl");
                // Don't add same file multiple times
                if (importedFiles.contains(fileObj)) {
                    continue;
                }
                LexicalAnalyser la = new LexicalAnalyser(new InputStreamReader(new FileInputStream(fileObj)));
                SyntacticalAnalyser sa = new SyntacticalAnalyser(la);
                Symbol parsedLib = sa.parse();
                if (parsedLib == null) {
                    throw new NullPointerException("couldn't parse file " + file);
                }
                importedFiles.add(fileObj);
                ProgramNode library = (ProgramNode) parsedLib.value;
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
            } catch (Exception e) {
                System.err.println("[ERROR] imported file " + file + " contains errors: " + e.getMessage());
                result = false;
            }
        }
    }

    // TODO efficiency ?
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
                System.out.println("File " + input + " compiled successfully");
            } else {
                // TODO message
            }
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
        }
    }

}
