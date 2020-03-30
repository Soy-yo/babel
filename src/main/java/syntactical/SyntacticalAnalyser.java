
//----------------------------------------------------
// The following code was generated by CUP v0.11b beta 20140220
// Mon Mar 30 20:04:13 CEST 2020
//----------------------------------------------------

package syntactical;

import lexical.LexicalAnalyser;

/**
 * CUP v0.11b beta 20140220 generated parser.
 *
 * @version Mon Mar 30 20:04:13 CEST 2020
 */
public class SyntacticalAnalyser extends java_cup.runtime.lr_parser {

    /**
     * Default constructor.
     */
    public SyntacticalAnalyser() {
        super();
    }

    /**
     * Constructor which sets the default scanner.
     */
    public SyntacticalAnalyser(java_cup.runtime.Scanner s) {
        super(s);
    }

    /**
     * Constructor which sets the default scanner.
     */
    public SyntacticalAnalyser(java_cup.runtime.Scanner s, java_cup.runtime.SymbolFactory sf) {
        super(s, sf);
    }

    /**
     * Production table.
     */
    protected static final short _production_table[][] =
            unpackFromStrings(new String[]{
                    "\000\002\000\002\002\002\000\002\002\004"});

    /**
     * Access to production table.
     */
    public short[][] production_table() {
        return _production_table;
    }

    /**
     * Parse-action table.
     */
    protected static final short[][] _action_table =
            unpackFromStrings(new String[]{
                    "\000\003\000\004\002\001\001\002\000\004\002\005\001" +
                            "\002\000\004\002\000\001\002"});

    /**
     * Access to parse-action table.
     */
    public short[][] action_table() {
        return _action_table;
    }

    /**
     * <code>reduce_goto</code> table.
     */
    protected static final short[][] _reduce_table =
            unpackFromStrings(new String[]{
                    "\000\003\000\004\002\003\001\001\000\002\001\001\000" +
                            "\002\001\001"});

    /**
     * Access to <code>reduce_goto</code> table.
     */
    public short[][] reduce_table() {
        return _reduce_table;
    }

    /**
     * Instance of action encapsulation class.
     */
    protected CUP$SyntacticalAnalyser$actions action_obj;

    /**
     * Action encapsulation object initializer.
     */
    protected void init_actions() {
        action_obj = new CUP$SyntacticalAnalyser$actions(this);
    }

    /**
     * Invoke a user supplied parse action.
     */
    public java_cup.runtime.Symbol do_action(
            int act_num,
            java_cup.runtime.lr_parser parser,
            java.util.Stack stack,
            int top)
            throws java.lang.Exception {
        /* call code in generated class */
        return action_obj.CUP$SyntacticalAnalyser$do_action(act_num, parser, stack, top);
    }

    /**
     * Indicates start state.
     */
    public int start_state() {
        return 0;
    }

    /**
     * Indicates start production.
     */
    public int start_production() {
        return 1;
    }

    /**
     * <code>EOF</code> Symbol index.
     */
    public int EOF_sym() {
        return 0;
    }

    /**
     * <code>error</code> Symbol index.
     */
    public int error_sym() {
        return 1;
    }


    /**
     * User initialization code.
     */
    public void user_init() throws java.lang.Exception {

        LexicalAnalyser analyser = (LexicalAnalyser) getScanner();

    }

    /**
     * Scan to get the next Symbol.
     */
    public java_cup.runtime.Symbol scan()
            throws java.lang.Exception {
        return getScanner().next_token();
    }

    /* TODO ?? */
}

/**
 * Cup generated class to encapsulate user supplied action code.
 */
class CUP$SyntacticalAnalyser$actions {

    /* TODO tree root declaration */
    private final SyntacticalAnalyser parser;

    /**
     * Constructor
     */
    CUP$SyntacticalAnalyser$actions(SyntacticalAnalyser parser) {
        this.parser = parser;
    }

    /**
     * Method 0 with the actual generated action code for actions 0 to 300.
     */
    public final java_cup.runtime.Symbol CUP$SyntacticalAnalyser$do_action_part00000000(
            int CUP$SyntacticalAnalyser$act_num,
            java_cup.runtime.lr_parser CUP$SyntacticalAnalyser$parser,
            java.util.Stack CUP$SyntacticalAnalyser$stack,
            int CUP$SyntacticalAnalyser$top)
            throws java.lang.Exception {
        /* Symbol object for return from actions */
        java_cup.runtime.Symbol CUP$SyntacticalAnalyser$result;

        /* select the action based on the action number */
        switch (CUP$SyntacticalAnalyser$act_num) {
            /*. . . . . . . . . . . . . . . . . . . .*/
            case 0: // Program ::=
            {
                Object RESULT = null;

                CUP$SyntacticalAnalyser$result = parser.getSymbolFactory().newSymbol("Program", 0, RESULT);
            }
            return CUP$SyntacticalAnalyser$result;

            /*. . . . . . . . . . . . . . . . . . . .*/
            case 1: // $START ::= Program EOF
            {
                Object RESULT = null;
                Object start_val = (Object) ((java_cup.runtime.Symbol) CUP$SyntacticalAnalyser$stack.elementAt(CUP$SyntacticalAnalyser$top - 1)).value;
                RESULT = start_val;
                CUP$SyntacticalAnalyser$result = parser.getSymbolFactory().newSymbol("$START", 0, RESULT);
            }
            /* ACCEPT */
            CUP$SyntacticalAnalyser$parser.done_parsing();
            return CUP$SyntacticalAnalyser$result;

            /* . . . . . .*/
            default:
                throw new Exception(
                        "Invalid action number " + CUP$SyntacticalAnalyser$act_num + "found in internal parse table");

        }
    } /* end of method */

    /**
     * Method splitting the generated action code into several parts.
     */
    public final java_cup.runtime.Symbol CUP$SyntacticalAnalyser$do_action(
            int CUP$SyntacticalAnalyser$act_num,
            java_cup.runtime.lr_parser CUP$SyntacticalAnalyser$parser,
            java.util.Stack CUP$SyntacticalAnalyser$stack,
            int CUP$SyntacticalAnalyser$top)
            throws java.lang.Exception {
        return CUP$SyntacticalAnalyser$do_action_part00000000(
                CUP$SyntacticalAnalyser$act_num,
                CUP$SyntacticalAnalyser$parser,
                CUP$SyntacticalAnalyser$stack,
                CUP$SyntacticalAnalyser$top);
    }
}
