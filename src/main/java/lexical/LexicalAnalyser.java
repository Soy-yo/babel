package lexical;
import java.util.function.Supplier;


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
	private java.io.BufferedReader yy_reader;
	private int yy_buffer_index;
	private int yy_buffer_read;
	private int yy_buffer_start;
	private int yy_buffer_end;
	private char yy_buffer[];
	private int yyline;
	private boolean yy_at_bol;
	private int yy_lexical_state;

	public LexicalAnalyser (java.io.Reader reader) {
		this ();
		if (null == reader) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(reader);
	}

	public LexicalAnalyser (java.io.InputStream instream) {
		this ();
		if (null == instream) {
			throw (new Error("Error: Bad input stream initializer."));
		}
		yy_reader = new java.io.BufferedReader(new java.io.InputStreamReader(instream));
	}

	private LexicalAnalyser () {
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
	private void yybegin (int state) {
		yy_lexical_state = state;
	}
	private int yy_advance ()
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
	private void yy_move_end () {
		if (yy_buffer_end > yy_buffer_start &&
		    '\n' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
		if (yy_buffer_end > yy_buffer_start &&
		    '\r' == yy_buffer[yy_buffer_end-1])
			yy_buffer_end--;
	}
	private boolean yy_last_was_cr=false;
	private void yy_mark_start () {
		int i;
		for (i = yy_buffer_start; i < yy_buffer_index; ++i) {
			if ('\n' == yy_buffer[i] && !yy_last_was_cr) {
				++yyline;
			}
			if ('\r' == yy_buffer[i]) {
				++yyline;
				yy_last_was_cr=true;
			} else yy_last_was_cr=false;
		}
		yy_buffer_start = yy_buffer_index;
	}
	private void yy_mark_end () {
		yy_buffer_end = yy_buffer_index;
	}
	private void yy_to_mark () {
		yy_buffer_index = yy_buffer_end;
		yy_at_bol = (yy_buffer_end > yy_buffer_start) &&
		            ('\r' == yy_buffer[yy_buffer_end-1] ||
		             '\n' == yy_buffer[yy_buffer_end-1] ||
		             2028/*LS*/ == yy_buffer[yy_buffer_end-1] ||
		             2029/*PS*/ == yy_buffer[yy_buffer_end-1]);
	}
	private java.lang.String yytext () {
		return (new java.lang.String(yy_buffer,
			yy_buffer_start,
			yy_buffer_end - yy_buffer_start));
	}
	private int yylength () {
		return yy_buffer_end - yy_buffer_start;
	}
	private char[] yy_double (char buf[]) {
		int i;
		char newbuf[];
		newbuf = new char[2*buf.length];
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
	private void yy_error (int code,boolean fatal) {
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
		for (int i= 0; i < size1; i++) {
			for (int j= 0; j < size2; j++) {
				if (sequenceLength != 0) {
					res[i][j] = sequenceInteger;
					sequenceLength--;
					continue;
				}
				commaIndex = st.indexOf(',');
				workString = (commaIndex==-1) ? st :
					st.substring(0, commaIndex);
				st = st.substring(commaIndex+1);
				colonIndex = workString.indexOf(':');
				if (colonIndex == -1) {
					res[i][j]=Integer.parseInt(workString);
					continue;
				}
				lengthString =
					workString.substring(colonIndex+1);
				sequenceLength=Integer.parseInt(lengthString);
				workString=workString.substring(0,colonIndex);
				sequenceInteger=Integer.parseInt(workString);
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
		/* 55 */ YY_NO_ANCHOR,
		/* 56 */ YY_NO_ANCHOR,
		/* 57 */ YY_NO_ANCHOR,
		/* 58 */ YY_NO_ANCHOR,
		/* 59 */ YY_NO_ANCHOR,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NOT_ACCEPT,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NO_ANCHOR,
		/* 66 */ YY_NOT_ACCEPT,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NO_ANCHOR,
		/* 70 */ YY_NOT_ACCEPT,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NO_ANCHOR,
		/* 74 */ YY_NOT_ACCEPT,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NO_ANCHOR,
		/* 78 */ YY_NOT_ACCEPT,
		/* 79 */ YY_NO_ANCHOR,
		/* 80 */ YY_NOT_ACCEPT,
		/* 81 */ YY_NO_ANCHOR,
		/* 82 */ YY_NOT_ACCEPT,
		/* 83 */ YY_NO_ANCHOR,
		/* 84 */ YY_NOT_ACCEPT,
		/* 85 */ YY_NO_ANCHOR,
		/* 86 */ YY_NOT_ACCEPT,
		/* 87 */ YY_NO_ANCHOR,
		/* 88 */ YY_NOT_ACCEPT,
		/* 89 */ YY_NO_ANCHOR,
		/* 90 */ YY_NO_ANCHOR,
		/* 91 */ YY_NO_ANCHOR,
		/* 92 */ YY_NO_ANCHOR,
		/* 93 */ YY_NO_ANCHOR,
		/* 94 */ YY_NO_ANCHOR,
		/* 95 */ YY_NO_ANCHOR,
		/* 96 */ YY_NO_ANCHOR,
		/* 97 */ YY_NO_ANCHOR,
		/* 98 */ YY_NO_ANCHOR,
		/* 99 */ YY_NO_ANCHOR,
		/* 100 */ YY_NO_ANCHOR,
		/* 101 */ YY_NO_ANCHOR,
		/* 102 */ YY_NO_ANCHOR,
		/* 103 */ YY_NO_ANCHOR,
		/* 104 */ YY_NO_ANCHOR,
		/* 105 */ YY_NO_ANCHOR,
		/* 106 */ YY_NO_ANCHOR,
		/* 107 */ YY_NO_ANCHOR,
		/* 108 */ YY_NO_ANCHOR,
		/* 109 */ YY_NO_ANCHOR,
		/* 110 */ YY_NO_ANCHOR,
		/* 111 */ YY_NO_ANCHOR,
		/* 112 */ YY_NO_ANCHOR,
		/* 113 */ YY_NO_ANCHOR,
		/* 114 */ YY_NO_ANCHOR,
		/* 115 */ YY_NO_ANCHOR,
		/* 116 */ YY_NO_ANCHOR,
		/* 117 */ YY_NO_ANCHOR,
		/* 118 */ YY_NO_ANCHOR,
		/* 119 */ YY_NO_ANCHOR,
		/* 120 */ YY_NO_ANCHOR,
		/* 121 */ YY_NO_ANCHOR,
		/* 122 */ YY_NO_ANCHOR,
		/* 123 */ YY_NO_ANCHOR,
		/* 124 */ YY_NO_ANCHOR,
		/* 125 */ YY_NO_ANCHOR,
		/* 126 */ YY_NO_ANCHOR,
		/* 127 */ YY_NO_ANCHOR,
		/* 128 */ YY_NO_ANCHOR,
		/* 129 */ YY_NO_ANCHOR,
		/* 130 */ YY_NO_ANCHOR,
		/* 131 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,65538,
"6:8,4:2,1,6:2,4,6:18,37,54,40,38:2,52,55,36,42,3,5,50,49,47,14,2,8,10,13:6," +
"7:2,38,41,53,51,48,38:2,12:6,35:20,43,39,44,38,32,38,28,9,22,33,17,16,29,23" +
",15,34:2,18,30,26,24,31,34,25,19,21,27,34,20,11,34:2,45,56,46,38:130,6:6528" +
"0,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,132,
"0,1:2,2,1,3,1,4,5,6,7,5,1:6,8,9,1,10,11,12,13,14,15,1:2,16,17,5:2,1:5,18,1:" +
"6,5,1:2,5:7,19,5:5,20,21,22,23,1,17,24,25,26,27,28,27,29,30,31,30,32,33,34," +
"35,36,37,38,24,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58," +
"59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83," +
"84,85")[0];

	private int yy_nxt[][] = unpackFromString(86,57,
"1,2,3,4,2,5,6,7,63,8,7,8,9,7,10,64,95,109,8,128,121,110,122,8:2,129,111,8:5" +
",11,8:2,9,62,2,6:2,67,12,13,14,15,16,17,18,19,20,21,22,23,24,25,71,75,-1:59" +
",26,-1:2,61,-1:45,27,-1:56,28,-1:12,7:2,-1,7,-1:2,7,66,-1:49,8:7,-1,8:21,-1" +
":28,9:2,29,9,29,9:2,-1,29:17,9,29:2,9,-1:28,30:2,-1,30,-1:2,30,78,-1:90,34," +
"-1:2,35,-1:56,36,-1:56,37,-1:56,38,-1:56,39,-1:56,40,-1:56,41,-1:7,26:55,-1" +
":7,29:7,-1,29:21,-1:28,30:2,-1,30,-1:2,30,-1:94,47,-1:12,8:7,-1,8:10,131,8:" +
"10,-1:22,61,-1:2,61,88,61:36,-1,61:14,-1:2,80:2,-1,80,-1,80:29,-1,80:2,82,8" +
"0:17,-1:8,68,70,68,74,-1,68,66,-1:49,8:7,-1,8,31,8:9,32,8:3,123,8:5,-1:23,8" +
"4:2,-1,84,-1,84:32,86,33,84:16,-1:8,68,-1,68,-1:2,68,-1:50,8:7,-1,8:10,45,8" +
":10,-1:29,72,-1,72,-1:101,42,-1:8,8:7,-1,8:2,48,8:18,-1:28,76:4,-1,76:2,-1:" +
"2,76:2,-1:4,76,-1:5,76,-1:4,76,-1:79,43,-1:7,8:7,-1,8:4,49,8:16,-1:35,44,-1" +
":49,8:7,-1,8:2,50,8:18,-1:57,46,-1:27,8:7,-1,8:3,51,8:17,-1:29,80:2,-1:6,80" +
",-1:4,80,-1:3,80:2,-1:9,80,-1:2,80,-1:24,8:7,-1,8:2,52,8:18,-1:28,8:7,-1,8:" +
"2,53,8:18,-1:29,84:2,-1:6,84,-1:4,84,-1:3,84:2,-1:12,84:2,-1:23,8:7,-1,8:4," +
"54,8:16,-1:23,65,-1:61,8:7,-1,8:6,55,8:14,-1:28,8:7,-1,8:6,56,8:14,-1:28,8:" +
"7,-1,8:8,57,8:12,-1:28,8:7,-1,8:11,58,8:9,-1:28,8:7,-1,8:14,59,8:6,-1:28,8:" +
"7,-1,8:10,60,8:10,-1:28,8:7,-1,8:9,69,8:3,112,8:7,-1:28,8:7,-1,8:4,73,8:16," +
"-1:28,8:7,-1,77,8:20,-1:28,8:7,-1,8:12,79,8:8,-1:28,8:7,-1,8:3,81,8:17,-1:2" +
"8,8:7,-1,8:4,83,8:16,-1:28,8:7,-1,8:3,85,8:17,-1:28,8:7,-1,8:4,87,8:16,-1:2" +
"8,8:7,-1,8:4,89,8:16,-1:28,8:7,-1,8:10,90,8:10,-1:28,8:7,-1,8:7,91,8:13,-1:" +
"28,8:7,-1,8:10,92,8:10,-1:28,8:7,-1,8:11,93,8:9,-1:28,8:7,-1,8:9,94,8:11,-1" +
":28,8:7,-1,8:3,96,8:17,-1:28,8:7,-1,8:8,97,8,98,8:10,-1:28,8:7,-1,8:9,130,8" +
":2,99,8:8,-1:28,8:7,-1,8:3,100,8:17,-1:28,8:7,-1,101,8:20,-1:28,8:7,-1,8:13" +
",102,8:7,-1:28,8:7,-1,8:11,103,8:9,-1:28,8:7,-1,8:9,104,8:11,-1:28,8:7,-1,8" +
":6,105,8:14,-1:28,8:7,-1,8:12,106,8:8,-1:28,8:7,-1,107,8:20,-1:28,8:7,-1,8:" +
"6,108,8:14,-1:28,8:7,-1,8:8,113,8:12,-1:28,8:7,-1,8:3,114,8:5,115,8:11,-1:2" +
"8,8:7,-1,8:16,116,8:4,-1:28,8:7,-1,117,8:20,-1:28,8:7,-1,8:6,118,8:14,-1:28" +
",8:7,-1,8:8,119,8:12,-1:28,8:7,-1,8:7,120,8:13,-1:28,8:7,-1,8:5,124,8:15,-1" +
":28,8:7,-1,8:2,125,8:18,-1:28,8:7,-1,8:6,126,8:14,-1:28,8:7,-1,8:12,127,8:8" +
",-1:21");

	public java_cup.runtime.Symbol next_token ()
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
			}
			else {
				if (YY_NO_STATE == yy_last_accept_state) {
					throw (new Error("Lexical Error: Unmatched Input."));
				}
				else {
					yy_anchor = yy_acpt[yy_last_accept_state];
					if (0 != (YY_END & yy_anchor)) {
						yy_move_end();
					}
					yy_to_mark();
					switch (yy_last_accept_state) {
					case 1:
						
					case -2:
						break;
					case 2:
						{updateColumn();}
					case -3:
						break;
					case 3:
						{return onRead(() -> ops.opDivUnit());}
					case -4:
						break;
					case 4:
						{return onRead(() -> ops.opClosingParenthesesUnit());}
					case -5:
						break;
					case 5:
						{return onRead(() -> ops.opMultUnit());}
					case -6:
						break;
					case 6:
						{throwError();}
					case -7:
						break;
					case 7:
						{return onRead(() -> ops.integerUnit());}
					case -8:
						break;
					case 8:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -9:
						break;
					case 9:
						{return onRead(() -> ops.globalConstUnit());}
					case -10:
						break;
					case 10:
						{return onRead(() -> ops.opPointUnit());}
					case -11:
						break;
					case 11:
						{return onRead(() -> ops.underscoreUnit());}
					case -12:
						break;
					case 12:
						{return onRead(() -> ops.opSemicolonUnit());}
					case -13:
						break;
					case 13:
						{return onRead(() -> ops.opOpeningParenthesesUnit());}
					case -14:
						break;
					case 14:
						{return onRead(() -> ops.opOpeningSquareUnit());}
					case -15:
						break;
					case 15:
						{return onRead(() -> ops.opClosingSquareUnit());}
					case -16:
						break;
					case 16:
						{return onRead(() -> ops.opOpeningCurlyUnit());}
					case -17:
						break;
					case 17:
						{return onRead(() -> ops.opClosingCurlyUnit());}
					case -18:
						break;
					case 18:
						{return onRead(() -> ops.opMinusUnit());}
					case -19:
						break;
					case 19:
						{return onRead(() -> ops.opGreaterThanUnit());}
					case -20:
						break;
					case 20:
						{return onRead(() -> ops.opCommaUnit());}
					case -21:
						break;
					case 21:
						{return onRead(() -> ops.opPlusUnit());}
					case -22:
						break;
					case 22:
						{return onRead(() -> ops.opAssignmentUnit());}
					case -23:
						break;
					case 23:
						{return onRead(() -> ops.opModUnit());}
					case -24:
						break;
					case 24:
						{return onRead(() -> ops.opLowerThanUnit());}
					case -25:
						break;
					case 25:
						{return onRead(() -> ops.opNotUnit());}
					case -26:
						break;
					case 26:
						{updateColumn();}
					case -27:
						break;
					case 27:
						{return onRead(() -> ops.opDivAssignUnit());}
					case -28:
						break;
					case 28:
						{return onRead(() -> ops.opMultAssignUnit());}
					case -29:
						break;
					case 29:
						{return onRead(() -> ops.typeNameUnit());}
					case -30:
						break;
					case 30:
						{return onRead(() -> ops.realUnit());}
					case -31:
						break;
					case 31:
						{return onRead(() -> ops.ifUnit());}
					case -32:
						break;
					case 32:
						{return onRead(() -> ops.inUnit());}
					case -33:
						break;
					case 33:
						{return onRead(() -> ops.stringUnit());}
					case -34:
						break;
					case 34:
						{return onRead(() -> ops.opArrowUnit());}
					case -35:
						break;
					case 35:
						{return onRead(() -> ops.opMinusAssignUnit());}
					case -36:
						break;
					case 36:
						{return onRead(() -> ops.opGreaterEqualUnit());}
					case -37:
						break;
					case 37:
						{return onRead(() -> ops.opPlusAssignUnit());}
					case -38:
						break;
					case 38:
						{return onRead(() -> ops.opEqualsUnit());}
					case -39:
						break;
					case 39:
						{return onRead(() -> ops.opModAssignUnit());}
					case -40:
						break;
					case 40:
						{return onRead(() -> ops.opLessEqualUnit());}
					case -41:
						break;
					case 41:
						{return onRead(() -> ops.opNotEqualsUnit());}
					case -42:
						break;
					case 42:
						{return onRead(() -> ops.opAndUnit());}
					case -43:
						break;
					case 43:
						{return onRead(() -> ops.opOrUnit());}
					case -44:
						break;
					case 44:
						{return onRead(() -> ops.opEllipsisUnit());}
					case -45:
						break;
					case 45:
						{return onRead(() -> ops.forUnit());}
					case -46:
						break;
					case 46:
						{return onRead(() -> ops.characterUnit());}
					case -47:
						break;
					case 47:
						{return onRead(() -> ops.opIdentityUnit());}
					case -48:
						break;
					case 48:
						{return onRead(() -> ops.elseUnit());}
					case -49:
						break;
					case 49:
						{return onRead(() -> ops.thisUnit());}
					case -50:
						break;
					case 50:
						{return onRead(() -> ops.trueUnit());}
					case -51:
						break;
					case 51:
						{return onRead(() -> ops.nullUnit());}
					case -52:
						break;
					case 52:
						{return onRead(() -> ops.falseUnit());}
					case -53:
						break;
					case 53:
						{return onRead(() -> ops.whileUnit());}
					case -54:
						break;
					case 54:
						{return onRead(() -> ops.classUnit());}
					case -55:
						break;
					case 55:
						{return onRead(() -> ops.constUnit());}
					case -56:
						break;
					case 56:
						{return onRead(() -> ops.importUnit());}
					case -57:
						break;
					case 57:
						{return onRead(() -> ops.switchUnit());}
					case -58:
						break;
					case 58:
						{return onRead(() -> ops.returnUnit());}
					case -59:
						break;
					case 59:
						{return onRead(() -> ops.nothingUnit());}
					case -60:
						break;
					case 60:
						{return onRead(() -> ops.constructorUnit());}
					case -61:
						break;
					case 62:
						{throwError();}
					case -62:
						break;
					case 63:
						{return onRead(() -> ops.integerUnit());}
					case -63:
						break;
					case 64:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -64:
						break;
					case 65:
						{updateColumn();}
					case -65:
						break;
					case 67:
						{throwError();}
					case -66:
						break;
					case 68:
						{return onRead(() -> ops.integerUnit());}
					case -67:
						break;
					case 69:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -68:
						break;
					case 71:
						{throwError();}
					case -69:
						break;
					case 72:
						{return onRead(() -> ops.integerUnit());}
					case -70:
						break;
					case 73:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -71:
						break;
					case 75:
						{throwError();}
					case -72:
						break;
					case 76:
						{return onRead(() -> ops.integerUnit());}
					case -73:
						break;
					case 77:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -74:
						break;
					case 79:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -75:
						break;
					case 81:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -76:
						break;
					case 83:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -77:
						break;
					case 85:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -78:
						break;
					case 87:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -79:
						break;
					case 89:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -80:
						break;
					case 90:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -81:
						break;
					case 91:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -82:
						break;
					case 92:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -83:
						break;
					case 93:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -84:
						break;
					case 94:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -85:
						break;
					case 95:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -86:
						break;
					case 96:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -87:
						break;
					case 97:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -88:
						break;
					case 98:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -89:
						break;
					case 99:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -90:
						break;
					case 100:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -91:
						break;
					case 101:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -92:
						break;
					case 102:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -93:
						break;
					case 103:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -94:
						break;
					case 104:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -95:
						break;
					case 105:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -96:
						break;
					case 106:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -97:
						break;
					case 107:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -98:
						break;
					case 108:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -99:
						break;
					case 109:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -100:
						break;
					case 110:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -101:
						break;
					case 111:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -102:
						break;
					case 112:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -103:
						break;
					case 113:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -104:
						break;
					case 114:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -105:
						break;
					case 115:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -106:
						break;
					case 116:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -107:
						break;
					case 117:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -108:
						break;
					case 118:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -109:
						break;
					case 119:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -110:
						break;
					case 120:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -111:
						break;
					case 121:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -112:
						break;
					case 122:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -113:
						break;
					case 123:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -114:
						break;
					case 124:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -115:
						break;
					case 125:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -116:
						break;
					case 126:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -117:
						break;
					case 127:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -118:
						break;
					case 128:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -119:
						break;
					case 129:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -120:
						break;
					case 130:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -121:
						break;
					case 131:
						{return onRead(() -> ops.varIdentifierUnit());}
					case -122:
						break;
					default:
						yy_error(YY_E_INTERNAL,false);
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
