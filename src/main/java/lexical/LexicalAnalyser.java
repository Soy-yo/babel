package lexical;

public class LexicalAnalyser implements java_cup.runtime.Scanner {
    private final int YY_BUFFER_SIZE = 512;
    private final int YY_F = -1;
    private final int YY_NO_STATE = -1;
    private final int YY_NOT_ACCEPT = 0;
    private final int YY_START = 1;
    private final int YY_END = 2;
    private final int YY_NO_ANCHOR = 4;
    private final int YY_BOL = 65536;
    private final int YY_EOF = 65537;

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

    private java.io.BufferedReader yy_reader;
    private int yy_buffer_index;
    private int yy_buffer_read;
    private int yy_buffer_start;
    private int yy_buffer_end;
    private char yy_buffer[];
    private int yyline;
    private boolean yy_at_bol;
    private int yy_lexical_state;

    public LexicalAnalyser(java.io.Reader reader) {
        this();
        if (null == reader) {
            throw (new Error("Error: Bad input stream initializer."));
        }
        yy_reader = new java.io.BufferedReader(reader);
    }

    public LexicalAnalyser(java.io.InputStream instream) {
        this();
        if (null == instream) {
            throw (new Error("Error: Bad input stream initializer."));
        }
        yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
    }

    private LexicalAnalyser() {
        yy_buffer = new char[YY_BUFFER_SIZE];
        yy_buffer_read = 0;
        yy_buffer_index = 0;
        yy_buffer_start = 0;
        yy_buffer_end = 0;
        yyline = 0;
        yy_at_bol = true;
        yy_lexical_state = YYINITIAL;

        ops = new Operations(this);
    }

    private boolean yy_eof_done = false;
    private final int YYINITIAL = 0;
    private final int yy_state_dtrans[] = {
            0
    };

    private void yybegin(int state) {
        yy_lexical_state = state;
    }

    private int yy_advance()
            throws java.io.IOException {
        int next_read;
        int i;
        int j;

        if (yy_buffer_index < yy_buffer_read) {
            return yy_buffer[yy_buffer_index++];
        }

        if (0 != yy_buffer_start) {
            i = yy_buffer_start;
            j = 0;
            while (i < yy_buffer_read) {
                yy_buffer[j] = yy_buffer[i];
                ++i;
                ++j;
            }
            yy_buffer_end = yy_buffer_end - yy_buffer_start;
            yy_buffer_start = 0;
            yy_buffer_read = j;
            yy_buffer_index = j;
            next_read = yy_reader.read(yy_buffer,
                    yy_buffer_read,
                    yy_buffer.length - yy_buffer_read);
            if (-1 == next_read) {
                return YY_EOF;
            }
            yy_buffer_read = yy_buffer_read + next_read;
        }

        while (yy_buffer_index >= yy_buffer_read) {
            if (yy_buffer_index >= yy_buffer.length) {
                yy_buffer = yy_double(yy_buffer);
            }
            next_read = yy_reader.read(yy_buffer,
                    yy_buffer_read,
                    yy_buffer.length - yy_buffer_read);
            if (-1 == next_read) {
                return YY_EOF;
            }
            yy_buffer_read = yy_buffer_read + next_read;
        }
        return yy_buffer[yy_buffer_index++];
    }

    private void yy_move_end() {
        if (yy_buffer_end > yy_buffer_start &&
                '\n' == yy_buffer[yy_buffer_end - 1])
            yy_buffer_end--;
        if (yy_buffer_end > yy_buffer_start &&
                '\r' == yy_buffer[yy_buffer_end - 1])
            yy_buffer_end--;
    }

    private boolean yy_last_was_cr = false;

    private void yy_mark_start() {
        int i;
        for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
            if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
                ++yyline;
            }
            if ('\r' == yy_buffer[i]) {
                ++yyline;
                yy_last_was_cr = true;
            } else yy_last_was_cr = false;
        }
        yy_buffer_start = yy_buffer_index;
    }

    private void yy_mark_end() {
        yy_buffer_end = yy_buffer_index;
    }

    private void yy_to_mark() {
        yy_buffer_index = yy_buffer_end;
        yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
                ('\r' == yy_buffer[yy_buffer_end - 1] ||
                        '\n' == yy_buffer[yy_buffer_end - 1] ||
                        2028/*LS*/ == yy_buffer[yy_buffer_end - 1] ||
                        2029/*PS*/ == yy_buffer[yy_buffer_end - 1]);
    }

    private java.lang.String yytext() {
        return (new java.lang.String(yy_buffer,
                yy_buffer_start,
                yy_buffer_end - yy_buffer_start));
    }

    private int yylength() {
        return yy_buffer_end - yy_buffer_start;
    }

    private char[] yy_double(char buf[]) {
        int i;
        char newbuf[];
        newbuf = new char[2 * buf.length];
        for (i = 0; i < buf.length; ++i) {
            newbuf[i] = buf[i];
        }
        return newbuf;
    }

    private final int YY_E_INTERNAL = 0;
    private final int YY_E_MATCH = 1;
    private java.lang.String yy_error_string[] = {
            "Error: Internal error.\n",
            "Error: Unmatched input.\n"
    };

    private void yy_error(int code, boolean fatal) {
        java.lang.System.out.print(yy_error_string[code]);
        java.lang.System.out.flush();
        if (fatal) {
            throw new Error("Fatal Error.\n");
        }
    }

    private int[][] unpackFromString(int size1, int size2, String st) {
        int colonIndex = -1;
        String lengthString;
        int sequenceLength = 0;
        int sequenceInteger = 0;

        int commaIndex;
        String workString;

        int res[][] = new int[size1][size2];
        for (int i = 0; i < size1; i++) {
            for (int j = 0; j < size2; j++) {
                if (sequenceLength != 0) {
                    res[i][j] = sequenceInteger;
                    sequenceLength--;
                    continue;
                }
                commaIndex = st.indexOf(',');
                workString = (commaIndex == -1) ? st :
                        st.substring(0, commaIndex);
                st = st.substring(commaIndex + 1);
                colonIndex = workString.indexOf(':');
                if (colonIndex == -1) {
                    res[i][j] = Integer.parseInt(workString);
                    continue;
                }
                lengthString =
                        workString.substring(colonIndex + 1);
                sequenceLength = Integer.parseInt(lengthString);
                workString = workString.substring(0, colonIndex);
                sequenceInteger = Integer.parseInt(workString);
                res[i][j] = sequenceInteger;
                sequenceLength--;
            }
        }
        return res;
    }

    private int yy_acpt[] = {
            /* 0 */ YY_NOT_ACCEPT,
            /* 1 */ YY_NO_ANCHOR,
            /* 2 */ YY_NO_ANCHOR,
            /* 3 */ YY_NO_ANCHOR,
            /* 4 */ YY_NO_ANCHOR,
            /* 5 */ YY_NO_ANCHOR,
            /* 6 */ YY_NO_ANCHOR,
            /* 7 */ YY_NO_ANCHOR,
            /* 8 */ YY_NO_ANCHOR,
            /* 9 */ YY_NO_ANCHOR,
            /* 10 */ YY_NO_ANCHOR,
            /* 11 */ YY_NO_ANCHOR,
            /* 12 */ YY_NO_ANCHOR,
            /* 13 */ YY_NO_ANCHOR,
            /* 14 */ YY_NO_ANCHOR,
            /* 15 */ YY_NO_ANCHOR,
            /* 16 */ YY_NO_ANCHOR,
            /* 17 */ YY_NO_ANCHOR,
            /* 18 */ YY_NO_ANCHOR,
            /* 19 */ YY_NO_ANCHOR,
            /* 20 */ YY_NO_ANCHOR,
            /* 21 */ YY_NO_ANCHOR,
            /* 22 */ YY_NO_ANCHOR,
            /* 23 */ YY_NO_ANCHOR,
            /* 24 */ YY_NO_ANCHOR,
            /* 25 */ YY_NO_ANCHOR,
            /* 26 */ YY_NO_ANCHOR,
            /* 27 */ YY_NO_ANCHOR,
            /* 28 */ YY_NO_ANCHOR,
            /* 29 */ YY_NO_ANCHOR,
            /* 30 */ YY_NO_ANCHOR,
            /* 31 */ YY_NO_ANCHOR,
            /* 32 */ YY_NO_ANCHOR,
            /* 33 */ YY_NO_ANCHOR,
            /* 34 */ YY_NO_ANCHOR,
            /* 35 */ YY_NO_ANCHOR,
            /* 36 */ YY_NO_ANCHOR,
            /* 37 */ YY_NO_ANCHOR,
            /* 38 */ YY_NO_ANCHOR,
            /* 39 */ YY_NO_ANCHOR,
            /* 40 */ YY_NO_ANCHOR,
            /* 41 */ YY_NO_ANCHOR,
            /* 42 */ YY_NO_ANCHOR,
            /* 43 */ YY_NO_ANCHOR,
            /* 44 */ YY_NO_ANCHOR,
            /* 45 */ YY_NO_ANCHOR,
            /* 46 */ YY_NO_ANCHOR,
            /* 47 */ YY_NO_ANCHOR,
            /* 48 */ YY_NO_ANCHOR,
            /* 49 */ YY_NO_ANCHOR,
            /* 50 */ YY_NO_ANCHOR,
            /* 51 */ YY_NO_ANCHOR,
            /* 52 */ YY_NO_ANCHOR,
            /* 53 */ YY_NO_ANCHOR,
            /* 54 */ YY_NO_ANCHOR,
            /* 55 */ YY_NOT_ACCEPT,
            /* 56 */ YY_NO_ANCHOR,
            /* 57 */ YY_NO_ANCHOR,
            /* 58 */ YY_NO_ANCHOR,
            /* 59 */ YY_NO_ANCHOR,
            /* 60 */ YY_NO_ANCHOR,
            /* 61 */ YY_NOT_ACCEPT,
            /* 62 */ YY_NO_ANCHOR,
            /* 63 */ YY_NO_ANCHOR,
            /* 64 */ YY_NOT_ACCEPT,
            /* 65 */ YY_NO_ANCHOR,
            /* 66 */ YY_NO_ANCHOR,
            /* 67 */ YY_NOT_ACCEPT,
            /* 68 */ YY_NO_ANCHOR,
            /* 69 */ YY_NO_ANCHOR,
            /* 70 */ YY_NOT_ACCEPT,
            /* 71 */ YY_NO_ANCHOR,
            /* 72 */ YY_NOT_ACCEPT,
            /* 73 */ YY_NO_ANCHOR,
            /* 74 */ YY_NOT_ACCEPT,
            /* 75 */ YY_NO_ANCHOR,
            /* 76 */ YY_NOT_ACCEPT,
            /* 77 */ YY_NO_ANCHOR,
            /* 78 */ YY_NOT_ACCEPT,
            /* 79 */ YY_NO_ANCHOR,
            /* 80 */ YY_NOT_ACCEPT,
            /* 81 */ YY_NOT_ACCEPT,
            /* 82 */ YY_NOT_ACCEPT,
            /* 83 */ YY_NOT_ACCEPT,
            /* 84 */ YY_NOT_ACCEPT,
            /* 85 */ YY_NOT_ACCEPT,
            /* 86 */ YY_NOT_ACCEPT,
            /* 87 */ YY_NOT_ACCEPT,
            /* 88 */ YY_NOT_ACCEPT,
            /* 89 */ YY_NOT_ACCEPT,
            /* 90 */ YY_NOT_ACCEPT,
            /* 91 */ YY_NOT_ACCEPT,
            /* 92 */ YY_NOT_ACCEPT,
            /* 93 */ YY_NOT_ACCEPT,
            /* 94 */ YY_NOT_ACCEPT,
            /* 95 */ YY_NOT_ACCEPT,
            /* 96 */ YY_NOT_ACCEPT,
            /* 97 */ YY_NOT_ACCEPT,
            /* 98 */ YY_NOT_ACCEPT,
            /* 99 */ YY_NOT_ACCEPT,
            /* 100 */ YY_NOT_ACCEPT,
            /* 101 */ YY_NOT_ACCEPT,
            /* 102 */ YY_NOT_ACCEPT,
            /* 103 */ YY_NOT_ACCEPT,
            /* 104 */ YY_NOT_ACCEPT,
            /* 105 */ YY_NOT_ACCEPT,
            /* 106 */ YY_NOT_ACCEPT,
            /* 107 */ YY_NOT_ACCEPT,
            /* 108 */ YY_NOT_ACCEPT,
            /* 109 */ YY_NOT_ACCEPT,
            /* 110 */ YY_NOT_ACCEPT,
            /* 111 */ YY_NOT_ACCEPT,
            /* 112 */ YY_NOT_ACCEPT,
            /* 113 */ YY_NOT_ACCEPT,
            /* 114 */ YY_NOT_ACCEPT,
            /* 115 */ YY_NOT_ACCEPT,
            /* 116 */ YY_NOT_ACCEPT,
            /* 117 */ YY_NOT_ACCEPT,
            /* 118 */ YY_NOT_ACCEPT,
            /* 119 */ YY_NOT_ACCEPT
    };
    private int yy_cmap[] = unpackFromString(1, 65538,
            "6:8,4:2,1,6:2,4,6:19,46,6:3,43,47,6,34,3,5,40,39,42,15,2,9,11,14:6,7:2,6:2," +
                    "44,41,45,6:2,13:6,6:20,35,6,36,6,33,6,29,10,23,8,18,17,30,24,16,6:2,19,31,2" +
                    "7,25,32,6,26,20,22,28,4,21,12,6:2,37,48,38,6:65410,0:2")[0];

    private int yy_rmap[] = unpackFromString(1, 120,
            "0,1:2,2,1,3,1,4,5,6,1:7,7,8,9,10,11,12,13,14,1:2,15,1:3,16,1:17,17,1:5,18,1" +
                    "9,20,21,22,1,23,24,25,26,27,23,15,28,26,29,30,31,32,33,34,35,36,37,38,39,40" +
                    ",41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65" +
                    ",66,67,68,69,70,71,72,73,74,75,76,77,78")[0];

    private int yy_nxt[][] = unpackFromString(79, 49,
            "1,2,3,4,2,5,6,7,6,58,6,7,6:2,7,8,57,62,65,6,56,9,68,71,6:2,73,75,6:5,10,11," +
                    "12,13,14,15,16,17,18,19,20,21,22,23,77,79,-1:51,24,-1:2,55,-1:35,25,-1:48,2" +
                    "6,-1:15,7,-1:48,27,-1:6,70,-1:54,59,-1:2,114,-1:65,30,-1:48,31,-1:48,32,-1:" +
                    "48,33,-1:48,34,-1:48,35,-1:48,36,-1:9,24:47,-1:8,27,-1:81,41,-1:33,106,-1:2" +
                    "3,55,-1:2,55,85,55:28,-1,55:14,-1:21,80,-1:44,28,-1:9,29,-1:3,72,-1:26,63,6" +
                    "1,63,64,-1,63,67,-1:54,59,-1:36,66,-1,66,-1:62,74,-1:3,76,-1:28,63,-1,63,-1" +
                    ":2,63,-1:41,69:5,-1,69:2,-1:2,69:2,-1:4,69,-1:5,69,-1:38,78,-1:53,118,-1,81" +
                    ",-1:37,39,-1:52,82,-1:5,83,-1:55,86,-1:34,84,-1:56,40,-1:47,115,-1:2,112,-1" +
                    ":39,113,-1:76,37,-1:21,87,-1:76,38,-1:16,119,-1:60,90,-1:49,91,-1:46,116,-1" +
                    ":43,92,-1:28,60,-1:71,95,-1:41,42,-1:49,98,-1:49,43,-1:46,44,-1:50,99,-1:56" +
                    ",117,-1:44,101,-1:43,45,-1:55,102,-1:40,46,-1:53,103,-1:43,47,-1:50,48,-1:5" +
                    "0,49,-1:42,105,-1:54,50,-1:50,51,-1:51,52,-1:48,107,-1:49,108,-1:50,53,-1:4" +
                    "1,109,-1:47,110,-1:51,111,-1:49,54,-1:41,94,-1:49,96,-1:44,88,-1:54,93,-1:4" +
                    "6,100,-1:54,104,-1:38,89,-1:54,97,-1:26");

    public java_cup.runtime.Symbol next_token()
            throws java.io.IOException {
        int yy_lookahead;
        int yy_anchor = YY_NO_ANCHOR;
        int yy_state = yy_state_dtrans[yy_lexical_state];
        int yy_next_state = YY_NO_STATE;
        int yy_last_accept_state = YY_NO_STATE;
        boolean yy_initial = true;
        int yy_this_accept;

        yy_mark_start();
        yy_this_accept = yy_acpt[yy_state];
        if (YY_NOT_ACCEPT != yy_this_accept) {
            yy_last_accept_state = yy_state;
            yy_mark_end();
        }
        while (true) {
            if (yy_initial && yy_at_bol) yy_lookahead = YY_BOL;
            else yy_lookahead = yy_advance();
            yy_next_state = YY_F;
            yy_next_state = yy_nxt[yy_rmap[yy_state]][yy_cmap[yy_lookahead]];
            if (YY_EOF == yy_lookahead && true == yy_initial) {

                return ops.eofUnit();
            }
            if (YY_F != yy_next_state) {
                yy_state = yy_next_state;
                yy_initial = false;
                yy_this_accept = yy_acpt[yy_state];
                if (YY_NOT_ACCEPT != yy_this_accept) {
                    yy_last_accept_state = yy_state;
                    yy_mark_end();
                }
            } else {
                if (YY_NO_STATE == yy_last_accept_state) {
                    throw (new Error("Lexical Error: Unmatched Input."));
                } else {
                    yy_anchor = yy_acpt[yy_last_accept_state];
                    if (0 != (YY_END & yy_anchor)) {
                        yy_move_end();
                    }
                    yy_to_mark();
                    switch (yy_last_accept_state) {
                        case 1:

                        case -2:
                            break;
                        case 2: {
                            updateColumn();
                        }
                        case -3:
                            break;
                        case 3: {
                            return ops.opDivUnit();
                        }
                        case -4:
                            break;
                        case 4: {
                            return ops.opClosingParenthesesUnit();
                        }
                        case -5:
                            break;
                        case 5: {
                            return ops.opMultUnit();
                        }
                        case -6:
                            break;
                        case 6: {
                            ops.error();
                        }
                        case -7:
                            break;
                        case 7: {
                            return ops.integerUnit();
                        }
                        case -8:
                            break;
                        case 8: {
                            return ops.opPointUnit();
                        }
                        case -9:
                            break;
                        case 9: {
                            return ops.identifierUnit();
                        }
                        case -10:
                            break;
                        case 10: {
                            return ops.underscoreUnit();
                        }
                        case -11:
                            break;
                        case 11: {
                            return ops.opOpeningParenthesesUnit();
                        }
                        case -12:
                            break;
                        case 12: {
                            return ops.opOpeningSquareUnit();
                        }
                        case -13:
                            break;
                        case 13: {
                            return ops.opClosingSquareUnit();
                        }
                        case -14:
                            break;
                        case 14: {
                            return ops.opOpeningCurlyUnit();
                        }
                        case -15:
                            break;
                        case 15: {
                            return ops.opClosingCurlyUnit();
                        }
                        case -16:
                            break;
                        case 16: {
                            return ops.opCommaUnit();
                        }
                        case -17:
                            break;
                        case 17: {
                            return ops.opPlusUnit();
                        }
                        case -18:
                            break;
                        case 18: {
                            return ops.opAssignmentUnit();
                        }
                        case -19:
                            break;
                        case 19: {
                            return ops.opMinusUnit();
                        }
                        case -20:
                            break;
                        case 20: {
                            return ops.opModUnit();
                        }
                        case -21:
                            break;
                        case 21: {
                            return ops.opLowerThanUnit();
                        }
                        case -22:
                            break;
                        case 22: {
                            return ops.opGreaterThanUnit();
                        }
                        case -23:
                            break;
                        case 23: {
                            return ops.opNotUnit();
                        }
                        case -24:
                            break;
                        case 24: {
                            updateColumn();
                        }
                        case -25:
                            break;
                        case 25: {
                            return ops.opDivAssignUnit();
                        }
                        case -26:
                            break;
                        case 26: {
                            return ops.opMultAssignUnit();
                        }
                        case -27:
                            break;
                        case 27: {
                            return ops.realUnit();
                        }
                        case -28:
                            break;
                        case 28: {
                            return ops.ifUnit();
                        }
                        case -29:
                            break;
                        case 29: {
                            return ops.inUnit();
                        }
                        case -30:
                            break;
                        case 30: {
                            return ops.opPlusAssignUnit();
                        }
                        case -31:
                            break;
                        case 31: {
                            return ops.opEqualsUnit();
                        }
                        case -32:
                            break;
                        case 32: {
                            return ops.opMinusAssignUnit();
                        }
                        case -33:
                            break;
                        case 33: {
                            return ops.opModAssignUnit();
                        }
                        case -34:
                            break;
                        case 34: {
                            return ops.opLessEqualUnit();
                        }
                        case -35:
                            break;
                        case 35: {
                            return ops.opGreaterEqualUnit();
                        }
                        case -36:
                            break;
                        case 36: {
                            return ops.opNotEqualsUnit();
                        }
                        case -37:
                            break;
                        case 37: {
                            return ops.opAndUnit();
                        }
                        case -38:
                            break;
                        case 38: {
                            return ops.opOrUnit();
                        }
                        case -39:
                            break;
                        case 39: {
                            return ops.opEllipsisUnit();
                        }
                        case -40:
                            break;
                        case 40: {
                            return ops.forUnit();
                        }
                        case -41:
                            break;
                        case 41: {
                            return ops.opIdentityUnit();
                        }
                        case -42:
                            break;
                        case 42: {
                            return ops.elseUnit();
                        }
                        case -43:
                            break;
                        case 43: {
                            return ops.thisUnit();
                        }
                        case -44:
                            break;
                        case 44: {
                            return ops.trueUnit();
                        }
                        case -45:
                            break;
                        case 45: {
                            return ops.nullUnit();
                        }
                        case -46:
                            break;
                        case 46: {
                            return ops.falseUnit();
                        }
                        case -47:
                            break;
                        case 47: {
                            return ops.whileUnit();
                        }
                        case -48:
                            break;
                        case 48: {
                            return ops.classUnit();
                        }
                        case -49:
                            break;
                        case 49: {
                            return ops.constUnit();
                        }
                        case -50:
                            break;
                        case 50: {
                            return ops.importUnit();
                        }
                        case -51:
                            break;
                        case 51: {
                            return ops.switchUnit();
                        }
                        case -52:
                            break;
                        case 52: {
                            return ops.returnUnit();
                        }
                        case -53:
                            break;
                        case 53: {
                            return ops.nothingUnit();
                        }
                        case -54:
                            break;
                        case 54: {
                            return ops.constructorUnit();
                        }
                        case -55:
                            break;
                        case 56: {
                            updateColumn();
                        }
                        case -56:
                            break;
                        case 57: {
                            ops.error();
                        }
                        case -57:
                            break;
                        case 58: {
                            return ops.integerUnit();
                        }
                        case -58:
                            break;
                        case 59: {
                            return ops.identifierUnit();
                        }
                        case -59:
                            break;
                        case 60: {
                            updateColumn();
                        }
                        case -60:
                            break;
                        case 62: {
                            ops.error();
                        }
                        case -61:
                            break;
                        case 63: {
                            return ops.integerUnit();
                        }
                        case -62:
                            break;
                        case 65: {
                            ops.error();
                        }
                        case -63:
                            break;
                        case 66: {
                            return ops.integerUnit();
                        }
                        case -64:
                            break;
                        case 68: {
                            ops.error();
                        }
                        case -65:
                            break;
                        case 69: {
                            return ops.integerUnit();
                        }
                        case -66:
                            break;
                        case 71: {
                            ops.error();
                        }
                        case -67:
                            break;
                        case 73: {
                            ops.error();
                        }
                        case -68:
                            break;
                        case 75: {
                            ops.error();
                        }
                        case -69:
                            break;
                        case 77: {
                            ops.error();
                        }
                        case -70:
                            break;
                        case 79: {
                            ops.error();
                        }
                        case -71:
                            break;
                        default:
                            yy_error(YY_E_INTERNAL, false);
                        case -1:
                    }
                    yy_initial = true;
                    yy_state = yy_state_dtrans[yy_lexical_state];
                    yy_next_state = YY_NO_STATE;
                    yy_last_accept_state = YY_NO_STATE;
                    yy_mark_start();
                    yy_this_accept = yy_acpt[yy_state];
                    if (YY_NOT_ACCEPT != yy_this_accept) {
                        yy_last_accept_state = yy_state;
                        yy_mark_end();
                    }
                }
            }
        }
    }
}
