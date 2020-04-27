package lexical;

import java.util.function.Supplier;

%%

%cup
%line
%class LexicalAnalyser
%unicode
%public

%{
    private Operations ops;
    private int column = 0;

    public String lexeme() {
        return yytext();
    }

    public int row() {
        return yyline + 1;
    }

    public int column() {
        return column;
    }

    private java_cup.runtime.Symbol onRead(Supplier<java_cup.runtime.Symbol> f) {
        updateColumn();
        return f.get();
    }
    
    private void throwError() {
        updateColumn();
        ops.error();
    }

    private void updateColumn() {
        String[] lines = lexeme().split("\n");
        if (lines.length > 1) {
            String finalLine = lines[lines.length - 1];
            column = finalLine.length();
        } else if (lines.length == 1) {
            column += lines[0].length();
        } else {
            column = 0;
        }
    }
%}

%eofval{
    return ops.eofUnit();
%eofval}

%init{
    ops = new Operations(this);
%init}

ignore = [ \t\r\b\n]
comment = (//[^\n]*)|(/\*[^(\*/)]*\*/)

decimalNumber = ([1-9][0-9]*)|0
binaryNumber = 0b[01]+
hexNumber = 0x[0-9a-fA-F]+
octalNumber = 0[0-7]+
integer = {decimalNumber}|{binaryNumber}|{hexNumber}|{octalNumber}
real = (([1-9][0-9]*)|0)?\.[0-9]+

if = if
else = else
switch = switch
for = for
while = while
in = in
return = return
true = true
false = false
nothing = nothing
null = null
const = const
import = import
class = class
constructor = constructor
this = this
underscore = _
varIdentifier = [a-z_][0-9a-zA-Z_]*
globalConst = [A-Z][0-9A-Z_]*
typeName = [A-Z][0-9a-zA-Z_]*

escapeChar = \\[tbnrf0\\]
character = '([\x20-\x26\x28-\x5b\x5d-\xff]|{escapeChar}|\\')'
string = \"([\x20\x21\x23-\x5b\x5d-\xff]|{escapeChar}|\\\")*\"

opSemicolon = ;
opOpeningParentheses = \(
opClosingParentheses = \)
opOpeningSquare = \[
opClosingSquare = \]
opOpeningCurly = \{
opClosingCurly = \}
opArrow = ->
opEllipsis = \.\.\.
opPoint = \.
opComma = ,
opPlusAssign = \+=
opPlus = \+
opMinusAssign = -=
opMinus = -
opMultAssign = \*=
opMult = \*
opDivAssign = /=
opDiv = /
opModAssign = \%=
opMod = \%
opLessEqual = <=
opLowerThan = <
opGreaterEqual = >=
opGreaterThan = >
opIdentity = ===
opEquals = ==
opNotEquals = !=
opAssignment = \=
opNot = !
opAnd = &&
opOr = \|\|

%%

{ignore}                  {updateColumn();}
{comment}                 {updateColumn();}
{integer}                 {return onRead(() -> ops.integerUnit());}
{real}                    {return onRead(() -> ops.realUnit());}
{if}                      {return onRead(() -> ops.ifUnit());}
{else}                    {return onRead(() -> ops.elseUnit());}
{switch}                  {return onRead(() -> ops.switchUnit());}
{for}                     {return onRead(() -> ops.forUnit());}
{while}                   {return onRead(() -> ops.whileUnit());}
{in}                      {return onRead(() -> ops.inUnit());}
{return}                  {return onRead(() -> ops.returnUnit());}
{true}                    {return onRead(() -> ops.trueUnit());}
{false}                   {return onRead(() -> ops.falseUnit());}
{nothing}                 {return onRead(() -> ops.nothingUnit());}
{null}                    {return onRead(() -> ops.nullUnit());}
{const}                   {return onRead(() -> ops.constUnit());}
{import}                  {return onRead(() -> ops.importUnit());}
{class}                   {return onRead(() -> ops.classUnit());}
{constructor}             {return onRead(() -> ops.constructorUnit());}
{this}                    {return onRead(() -> ops.thisUnit());}
{underscore}              {return onRead(() -> ops.underscoreUnit());}
{varIdentifier}           {return onRead(() -> ops.varIdentifierUnit());}
{globalConst}             {return onRead(() -> ops.globalConstUnit());}
{typeName}                {return onRead(() -> ops.typeNameUnit());}
{character}               {return onRead(() -> ops.characterUnit());}
{string}                  {return onRead(() -> ops.stringUnit());}
{opSemicolon}             {return onRead(() -> ops.opSemicolonUnit());}
{opOpeningParentheses}    {return onRead(() -> ops.opOpeningParenthesesUnit());}
{opClosingParentheses}    {return onRead(() -> ops.opClosingParenthesesUnit());}
{opOpeningSquare}         {return onRead(() -> ops.opOpeningSquareUnit());}
{opClosingSquare}         {return onRead(() -> ops.opClosingSquareUnit());}
{opOpeningCurly}          {return onRead(() -> ops.opOpeningCurlyUnit());}
{opClosingCurly}          {return onRead(() -> ops.opClosingCurlyUnit());}
{opArrow}                 {return onRead(() -> ops.opArrowUnit());}
{opEllipsis}              {return onRead(() -> ops.opEllipsisUnit());}
{opPoint}                 {return onRead(() -> ops.opPointUnit());}
{opComma}                 {return onRead(() -> ops.opCommaUnit());}
{opPlusAssign}            {return onRead(() -> ops.opPlusAssignUnit());}
{opPlus}                  {return onRead(() -> ops.opPlusUnit());}
{opMinusAssign}           {return onRead(() -> ops.opMinusAssignUnit());}
{opMinus}                 {return onRead(() -> ops.opMinusUnit());}
{opMultAssign}            {return onRead(() -> ops.opMultAssignUnit());}
{opMult}                  {return onRead(() -> ops.opMultUnit());}
{opDivAssign}             {return onRead(() -> ops.opDivAssignUnit());}
{opDiv}                   {return onRead(() -> ops.opDivUnit());}
{opModAssign}             {return onRead(() -> ops.opModAssignUnit());}
{opMod}                   {return onRead(() -> ops.opModUnit());}
{opLessEqual}             {return onRead(() -> ops.opLessEqualUnit());}
{opLowerThan}             {return onRead(() -> ops.opLowerThanUnit());}
{opGreaterEqual}          {return onRead(() -> ops.opGreaterEqualUnit());}
{opGreaterThan}           {return onRead(() -> ops.opGreaterThanUnit());}
{opIdentity}              {return onRead(() -> ops.opIdentityUnit());}
{opEquals}                {return onRead(() -> ops.opEqualsUnit());}
{opNotEquals}             {return onRead(() -> ops.opNotEqualsUnit());}
{opAssignment}            {return onRead(() -> ops.opAssignmentUnit());}
{opNot}                   {return onRead(() -> ops.opNotUnit());}
{opAnd}                   {return onRead(() -> ops.opAndUnit());}
{opOr}                    {return onRead(() -> ops.opOrUnit());}
[^]                       {throwError();}