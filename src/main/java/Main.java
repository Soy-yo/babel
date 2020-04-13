import java_cup.runtime.Symbol;
import lexical.LexicalAnalyser;
import syntactical.SyntacticalAnalyser;
import syntactical.ast.ProgramNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws Exception {
        String input = "src/main/resources/examples/helloWorld/helloWorld.bbl";
        lexicalTest(input);
        syntacticalTest(input);
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
            System.out.println(program);
        }
        System.out.println("Syntactical parsing finished successfully");
    }

}
