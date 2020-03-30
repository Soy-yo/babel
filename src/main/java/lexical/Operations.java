package lexical;

import error.LexicalException;

import static syntactical.ClassConstants.*;

public class Operations {

    private final LexicalAnalyser analyser;

    public Operations(LexicalAnalyser analyser) {
        this.analyser = analyser;
    }

    private LexicalUnit createUnit(int lexicalClass) {
        return new LexicalUnit(analyser.row(), analyser.column(), lexicalClass, analyser.lexeme());
    }

    public LexicalUnit integerUnit() {
        return createUnit(INT);
    }

    public LexicalUnit realUnit() {
        return createUnit(REAL);
    }

    public LexicalUnit ifUnit() {
        return createUnit(IF);
    }

    public LexicalUnit elseUnit() {
        return createUnit(ELSE);
    }

    public LexicalUnit switchUnit() {
        return createUnit(SWITCH);
    }

    public LexicalUnit forUnit() {
        return createUnit(FOR);
    }

    public LexicalUnit whileUnit() {
        return createUnit(WHILE);
    }

    public LexicalUnit inUnit() {
        return createUnit(IN);
    }

    public LexicalUnit returnUnit() {
        return createUnit(RETURN);
    }

    public LexicalUnit trueUnit() {
        return createUnit(TRUE);
    }

    public LexicalUnit falseUnit() {
        return createUnit(FALSE);
    }

    public LexicalUnit nothingUnit() {
        return createUnit(NOTHING);
    }

    public LexicalUnit nullUnit() {
        return createUnit(NULL);
    }

    public LexicalUnit constUnit() {
        return createUnit(CONST);
    }

    public LexicalUnit importUnit() {
        return createUnit(IMPORT);
    }

    public LexicalUnit classUnit() {
        return createUnit(CLASS);
    }

    public LexicalUnit constructorUnit() {
        return createUnit(CONSTRUCTOR);
    }

    public LexicalUnit thisUnit() {
        return createUnit(THIS);
    }

    public LexicalUnit underscoreUnit() {
        return createUnit(UNDERSCORE);
    }

    public LexicalUnit identifierUnit() {
        return createUnit(IDENTIFIER);
    }

    public LexicalUnit characterUnit() {
        return createUnit(CHAR);
    }

    public LexicalUnit stringUnit() {
        return createUnit(STRING);
    }

    public LexicalUnit opSemicolonUnit() {
        return createUnit(SEMICOLON);
    }

    public LexicalUnit opOpeningParenthesesUnit() {
        return createUnit(PARENTH_L);
    }

    public LexicalUnit opClosingParenthesesUnit() {
        return createUnit(PARENTH_R);
    }

    public LexicalUnit opOpeningSquareUnit() {
        return createUnit(SQUARE_L);
    }

    public LexicalUnit opClosingSquareUnit() {
        return createUnit(SQUARE_R);
    }

    public LexicalUnit opOpeningCurlyUnit() {
        return createUnit(CURLY_L);
    }

    public LexicalUnit opClosingCurlyUnit() {
        return createUnit(CURLY_R);
    }

    public LexicalUnit opArrowUnit() {
        return createUnit(ARROW);
    }

    public LexicalUnit opEllipsisUnit() {
        return createUnit(ELLIPSIS);
    }

    public LexicalUnit opPointUnit() {
        return createUnit(POINT);
    }

    public LexicalUnit opCommaUnit() {
        return createUnit(COMMA);
    }

    public LexicalUnit opPlusAssignUnit() {
        return createUnit(PLUS_ASSIGN);
    }

    public LexicalUnit opPlusUnit() {
        return createUnit(PLUS);
    }

    public LexicalUnit opMinusAssignUnit() {
        return createUnit(MINUS_ASSIGN);
    }

    public LexicalUnit opMinusUnit() {
        return createUnit(MINUS);
    }

    public LexicalUnit opMultAssignUnit() {
        return createUnit(MULT_ASSIGN);
    }

    public LexicalUnit opMultUnit() {
        return createUnit(MULT);
    }

    public LexicalUnit opDivAssignUnit() {
        return createUnit(DIV_ASSIGN);
    }

    public LexicalUnit opDivUnit() {
        return createUnit(DIV);
    }

    public LexicalUnit opModAssignUnit() {
        return createUnit(MOD_ASSIGN);
    }

    public LexicalUnit opModUnit() {
        return createUnit(MOD);
    }

    public LexicalUnit opLessEqualUnit() {
        return createUnit(LE);
    }

    public LexicalUnit opLowerThanUnit() {
        return createUnit(LT);
    }

    public LexicalUnit opGreaterEqualUnit() {
        return createUnit(GE);
    }

    public LexicalUnit opGreaterThanUnit() {
        return createUnit(GT);
    }

    public LexicalUnit opIdentityUnit() {
        return createUnit(IDENTITY);
    }

    public LexicalUnit opEqualsUnit() {
        return createUnit(EQUALS);
    }

    public LexicalUnit opNotEqualsUnit() {
        return createUnit(NEQ);
    }

    public LexicalUnit opAssignmentUnit() {
        return createUnit(ASSIGNMENT);
    }

    public LexicalUnit opNotUnit() {
        return createUnit(NOT);
    }

    public LexicalUnit opAndUnit() {
        return createUnit(AND);
    }

    public LexicalUnit opOrUnit() {
        return createUnit(OR);
    }

    public LexicalUnit eofUnit() {
        return createUnit(EOF);
    }

    public void error() {
        throw new LexicalException(analyser.row(), analyser.column(), analyser.lexeme());
    }

}
