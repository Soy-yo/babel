import java_cup.runtime.Symbol;
import lexical.LexicalAnalyser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) throws IOException {
        String input = "src/main/resources/examples/helloWorld/helloWorld.bbl";
        LexicalAnalyser la = new LexicalAnalyser(new InputStreamReader(new FileInputStream(input)));
        for (int i = 0; i < 30; i++) {
            System.out.println(la.row() + ":" + la.column());
            Symbol symbol = la.next_token();
            System.out.println("\t" + symbol);
        }
    }

}
