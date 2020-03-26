package lexical;

import error.LexicalException;

public class Operations {

    private final LexicalAnalyser analyser;

    public Operations(LexicalAnalyser analyser) {
        this.analyser = analyser;
    }

    private LexicalUnit createUnit(int lexicalClass) {
        // TODO how to find file :/ (remove?)
        return new LexicalUnit("TODO", analyser.row(), analyser.column(), lexicalClass, analyser.lexeme());
    }

    public LexicalUnit integerUnit() {
        return createUnit(0);
    }

    public LexicalUnit realUnit() {
        return createUnit(0);
    }

    public LexicalUnit ifUnit() {
        return createUnit(0);
    }

    public LexicalUnit elseUnit() {
        return createUnit(0);
    }

    public LexicalUnit switchUnit() {
        return createUnit(0);
    }

    public LexicalUnit forUnit() {
        return createUnit(0);
    }

    public LexicalUnit whileUnit() {
        return createUnit(0);
    }

    public LexicalUnit inUnit() {
        return createUnit(0);
    }

    public LexicalUnit returnUnit() {
        return createUnit(0);
    }

    public LexicalUnit trueUnit() {
        return createUnit(0);
    }

    public LexicalUnit falseUnit() {
        return createUnit(0);
    }

    public LexicalUnit nothingUnit() {
        return createUnit(0);
    }

    public LexicalUnit nullUnit() {
        return createUnit(0);
    }

    public LexicalUnit constUnit() {
        return createUnit(0);
    }

    public LexicalUnit importUnit() {
        return createUnit(0);
    }

    public LexicalUnit classUnit() {
        return createUnit(0);
    }

    public LexicalUnit constructorUnit() {
        return createUnit(0);
    }

    public LexicalUnit thisUnit() {
        return createUnit(0);
    }

    public LexicalUnit underscoreUnit() {
        return createUnit(0);
    }

    public LexicalUnit identifierUnit() {
        return createUnit(0);
    }

    public LexicalUnit characterUnit() {
        return createUnit(0);
    }

    public LexicalUnit stringUnit() {
        return createUnit(0);
    }

    public LexicalUnit opSemicolonUnit() {
        return createUnit(0);
    }

    public LexicalUnit opOpeningParenthesesUnit() {
        return createUnit(0);
    }

    public LexicalUnit opClosingParenthesesUnit() {
        return createUnit(0);
    }

    public LexicalUnit opOpeningSquareUnit() {
        return createUnit(0);
    }

    public LexicalUnit opClosingSquareUnit() {
        return createUnit(0);
    }

    public LexicalUnit opOpeningCurlyUnit() {
        return createUnit(0);
    }

    public LexicalUnit opClosingCurlyUnit() {
        return createUnit(0);
    }

    public LexicalUnit opArrowUnit() {
        return createUnit(0);
    }

    public LexicalUnit opEllipsisUnit() {
        return createUnit(0);
    }

    public LexicalUnit opPointUnit() {
        return createUnit(0);
    }

    public LexicalUnit opCommaUnit() {
        return createUnit(0);
    }

    public LexicalUnit opPlusAssignUnit() {
        return createUnit(0);
    }

    public LexicalUnit opPlusUnit() {
        return createUnit(0);
    }

    public LexicalUnit opMinusAssignUnit() {
        return createUnit(0);
    }

    public LexicalUnit opMinusUnit() {
        return createUnit(0);
    }

    public LexicalUnit opMultAssignUnit() {
        return createUnit(0);
    }

    public LexicalUnit opMultUnit() {
        return createUnit(0);
    }

    public LexicalUnit opDivAssignUnit() {
        return createUnit(0);
    }

    public LexicalUnit opDivUnit() {
        return createUnit(0);
    }

    public LexicalUnit opModAssignUnit() {
        return createUnit(0);
    }

    public LexicalUnit opModUnit() {
        return createUnit(0);
    }

    public LexicalUnit opLessEqualUnit() {
        return createUnit(0);
    }

    public LexicalUnit opLowerThanUnit() {
        return createUnit(0);
    }

    public LexicalUnit opGreaterEqualUnit() {
        return createUnit(0);
    }

    public LexicalUnit opGreaterThanUnit() {
        return createUnit(0);
    }

    public LexicalUnit opIdentityUnit() {
        return createUnit(0);
    }

    public LexicalUnit opEqualsUnit() {
        return createUnit(0);
    }

    public LexicalUnit opNotEqualsUnit() {
        return createUnit(0);
    }

    public LexicalUnit opAssignmentUnit() {
        return createUnit(0);
    }

    public LexicalUnit opNotUnit() {
        return createUnit(0);
    }

    public LexicalUnit opAndUnit() {
        return createUnit(0);
    }

    public LexicalUnit opOrUnit() {
        return createUnit(0);
    }

    public LexicalUnit eofUnit() {
        return createUnit(0);
    }

    public void error() {
        throw new LexicalException("TODO", analyser.row(), analyser.column(), analyser.lexeme());
    }

}
