package lexical;

import error.LexicalException;

public class Operations {

    private final LexicalAnalyser analyser;

    public Operations(LexicalAnalyser analyser) {
        this.analyser = analyser;
    }

    public LexicalUnit integerUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit realUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit ifUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit elseUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit switchUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit forUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit whileUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit inUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit returnUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit trueUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit falseUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit nothingUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit nullUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit constUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit importUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit classUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit constructorUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit thisUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit underscoreUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit identifierUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opOpeningParenthesesUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opClosingParenthesesUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opOpeningSquareUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opClosingSquareUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opOpeningCurlyUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opClosingCurlyUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opEllipsisUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opPointUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opCommaUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opPlusAssignUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opPlusUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opMinusAssignUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opMinusUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opMultAssignUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opMultUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opDivAssignUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opDivUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opModAssignUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opModUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opLessEqualUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opLowerThanUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opGreaterEqualUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opGreaterThanUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opIdentityUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opEqualsUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opNotEqualsUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opAssignmentUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opNotUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opAndUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit opOrUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public LexicalUnit eofUnit() {
        return new LexicalUnit(analyser.row(), analyser.column());
    }

    public void error() {
        throw new LexicalException(analyser.row(), analyser.column(), analyser.lexeme());
    }

}
