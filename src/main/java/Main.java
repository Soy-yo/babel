import java_cup.runtime.Symbol;
import lexical.LexicalAnalyser;
import syntactical.SyntacticalAnalyser;
import syntactical.ast.ProgramNode;
import syntactical.ast.visitors.ASTPrinter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws Exception {
        List<Path> files = Files.walk(Paths.get("src/main/resources/examples"))
            .filter(Files::isRegularFile)
            .collect(Collectors.toList());
        for(Path p : files) {
            lexicalTest(p.toString());
            syntacticalTest(p.toString());
        }
    }

    private static void lexicalTest(String input) throws IOException {
        System.out.println("Lexical test");
        LexicalAnalyser la = new LexicalAnalyser(new InputStreamReader(new FileInputStream(input)));
        Symbol symbol;
        do {
            System.out.println(la.row() + ":" + la.column());
            symbol = la.next_token();
            System.out.println("\t" + symbol);
        } while (!symbol.value.equals(""));
        System.out.println("Lexical parsing finished successfully");
    }

    private static void syntacticalTest(String input) throws Exception {
        System.out.println("Syntactical test");
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
