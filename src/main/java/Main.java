import error.SyntacticalException;
import java_cup.runtime.Symbol;
import lexical.LexicalAnalyser;
import lexical.LexicalUnit;
import syntactical.SyntacticalAnalyser;
import syntactical.ast.ProgramNode;
import syntactical.ast.visitors.ASTPrinter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        List<Path> files = args.length == 0 ?
                Files.walk(Paths.get("src/main/resources/examples"))
                        .filter(Files::isRegularFile)
                        .collect(Collectors.toList()) :
                Arrays.stream(args)
                        .map(f -> new File(f).toPath())
                        .filter(Files::isRegularFile)
                        .collect(Collectors.toList());
        for (Path p : files) {
            try {
                lexicalTest(p.toString());
            } catch (IOException e) {
                System.out.println("[ERROR] failed reading file " + p);
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("[ERROR] fatal error occurred");
                System.out.println(e.getMessage());
            }
            try {
                syntacticalTest(p.toString());
            } catch (IOException e) {
                System.out.println("[ERROR] failed reading file " + p);
                System.out.println(e.getMessage());
            } catch (SyntacticalException e) {
                System.out.println("[ERROR] fatal syntactical error occurred");
                System.out.println(e.getMessage());
            } catch (Exception e) {
                System.out.println("[ERROR] fatal error occurred");
                System.out.println(e.getMessage());
            }
            System.out.println("Press enter to continue...");
            System.in.read();
        }
    }

    private static void lexicalTest(String input) throws IOException {
        System.out.println("Lexical test: " + input);
        LexicalAnalyser la = new LexicalAnalyser(new InputStreamReader(new FileInputStream(input)));
        Symbol symbol;
        do {
            System.out.println(la.row() + ":" + la.column());
            symbol = la.next_token();
            System.out.println("\t" + symbol);
        } while (!"".equals(((LexicalUnit) symbol).lexeme()));
        System.out.println("Lexical parsing finished successfully");
    }

    private static void syntacticalTest(String input) throws Exception {
        System.out.println("Syntactical test: " + input);
        LexicalAnalyser la = new LexicalAnalyser(new InputStreamReader(new FileInputStream(input)));
        SyntacticalAnalyser sa = new SyntacticalAnalyser(la);
        Symbol result = sa.parse();
        if (result != null) {
            ProgramNode program = (ProgramNode) result.value;
            new ASTPrinter(program).print();
        }
        System.out.println("Syntactical parsing finished successfully");
    }

}
