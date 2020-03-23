java -cp lib/jlex.jar JLex.Main src/main/java/lexical/LexicalAnalyser.l
cmd /c move /y src\main\java\lexical\LexicalAnalyser.l.java src\main\java\lexical\LexicalAnalyser.java
cd src\main\java\syntactical
java -cp ../../../../lib/cup.jar java_cup.Main -parser SyntacticalAnalyser -symbols ClassConstants -nopositions SyntacticalAnalyser.cup