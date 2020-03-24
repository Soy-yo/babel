package lexical;

import error.*;

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
        updateColumn();
        return column;
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

ignore = [\s\t\v\r\b\n]
comment = (//[^\n]*)|(/\*([^(\*/)]|{ignore})*\*/)

decimalNumber = ([1-9]\d*)|0
binaryNumber = 0b[01]+
hexNumber = 0x[0-9a-fA-F]+
octalNumber = 0[0-7]+
integer = {decimalNumber}|{binaryNumber}|{hexNumber}|{octalNumber}
real = {decimalNumber}?\.\d+

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
identifier = \w+

opOpeningParentheses = \(
opClosingParentheses = \)
opOpeningSquare = \[
opClosingSquare = \]
opOpeningCurly = \{
opClosingCurly = \}
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
{integer}                 {return ops.integerUnit();}
{real}                    {return ops.realUnit();}
{if}                      {return ops.ifUnit();}
{else}                    {return ops.elseUnit();}
{switch}                  {return ops.switchUnit();}
{for}                     {return ops.forUnit();}
{while}                   {return ops.whileUnit();}
{in}                      {return ops.inUnit();}
{return}                  {return ops.returnUnit();}
{true}                    {return ops.trueUnit();}
{false}                   {return ops.falseUnit();}
{nothing}                 {return ops.nothingUnit();}
{null}                    {return ops.nullUnit();}
{const}                   {return ops.constUnit();}
{import}                  {return ops.importUnit();}
{class}                   {return ops.classUnit();}
{constructor}             {return ops.constructorUnit();}
{this}                    {return ops.thisUnit();}
{underscore}              {return ops.underscoreUnit();}
{identifier}              {return ops.identifierUnit();}
{opOpeningParentheses}    {return ops.opOpeningParenthesesUnit();}
{opClosingParentheses}    {return ops.opClosingParenthesesUnit();}
{opOpeningSquare}         {return ops.opOpeningSquareUnit();}
{opClosingSquare}         {return ops.opClosingSquareUnit();}
{opOpeningCurly}          {return ops.opOpeningCurlyUnit();}
{opClosingCurly}          {return ops.opClosingCurlyUnit();}
{opEllipsis}              {return ops.opEllipsisUnit();}
{opPoint}                 {return ops.opPointUnit();}
{opComma}                 {return ops.opCommaUnit();}
{opPlusAssign}            {return ops.opPlusAssignUnit();}
{opPlus}                  {return ops.opPlusUnit();}
{opMinusAssign}           {return ops.opMinusAssignUnit();}
{opMinus}                 {return ops.opMinusUnit();}
{opMultAssign}            {return ops.opMultAssignUnit();}
{opMult}                  {return ops.opMultUnit();}
{opDivAssign}             {return ops.opDivAssignUnit();}
{opDiv}                   {return ops.opDivUnit();}
{opModAssign}             {return ops.opModAssignUnit();}
{opMod}                   {return ops.opModUnit();}
{opLessEqual}             {return ops.opLessEqualUnit();}
{opLowerThan}             {return ops.opLowerThanUnit();}
{opGreaterEqual}          {return ops.opGreaterEqualUnit();}
{opGreaterThan}           {return ops.opGreaterThanUnit();}
{opIdentity}              {return ops.opIdentityUnit();}
{opEquals}                {return ops.opEqualsUnit();}
{opNotEquals}             {return ops.opNotEqualsUnit();}
{opAssignment}            {return ops.opAssignmentUnit();}
{opNot}                   {return ops.opNotUnit();}
{opAnd}                   {return ops.opAndUnit();}
{opOr}                    {return ops.opOrUnit();}
[^]                       {ops.error();}