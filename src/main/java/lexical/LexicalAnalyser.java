package lexical;
import error.*;
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
		/* 59 */ YY_NOT_ACCEPT,
		/* 60 */ YY_NO_ANCHOR,
		/* 61 */ YY_NO_ANCHOR,
		/* 62 */ YY_NO_ANCHOR,
		/* 63 */ YY_NO_ANCHOR,
		/* 64 */ YY_NO_ANCHOR,
		/* 65 */ YY_NOT_ACCEPT,
		/* 66 */ YY_NO_ANCHOR,
		/* 67 */ YY_NO_ANCHOR,
		/* 68 */ YY_NO_ANCHOR,
		/* 69 */ YY_NOT_ACCEPT,
		/* 70 */ YY_NO_ANCHOR,
		/* 71 */ YY_NO_ANCHOR,
		/* 72 */ YY_NO_ANCHOR,
		/* 73 */ YY_NOT_ACCEPT,
		/* 74 */ YY_NO_ANCHOR,
		/* 75 */ YY_NO_ANCHOR,
		/* 76 */ YY_NO_ANCHOR,
		/* 77 */ YY_NOT_ACCEPT,
		/* 78 */ YY_NO_ANCHOR,
		/* 79 */ YY_NOT_ACCEPT,
		/* 80 */ YY_NO_ANCHOR,
		/* 81 */ YY_NOT_ACCEPT,
		/* 82 */ YY_NO_ANCHOR,
		/* 83 */ YY_NOT_ACCEPT,
		/* 84 */ YY_NO_ANCHOR,
		/* 85 */ YY_NOT_ACCEPT,
		/* 86 */ YY_NO_ANCHOR,
		/* 87 */ YY_NOT_ACCEPT,
		/* 88 */ YY_NO_ANCHOR,
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
		/* 130 */ YY_NO_ANCHOR
	};
	private int yy_cmap[] = unpackFromString(1,65538,
"6:8,4:2,1,6:2,4,6:18,37,54,40,38:2,52,55,36,42,3,5,50,49,47,15,2,9,11,14:6," +
"7:2,38,41,53,51,48,38:2,13:6,34:20,43,39,44,38,33,38,29,10,23,8,18,17,30,24" +
",16,34:2,19,31,27,25,32,34,26,20,22,28,35,21,12,34:2,45,56,46,38:130,6:6528" +
"0,0:2")[0];

	private int yy_rmap[] = unpackFromString(1,131,
"0,1:2,2,1,3,1,4,5,6,5,1:6,7,8,1,9,10,11,12,13,14,1:2,15,5:2,1:5,16,1:6,5,1:" +
"2,5:7,17,5:5,18,5,19,20,21,1,22,23,24,25,26,27,22,28,15,29,26,30,31,32,33,3" +
"4,35,36,23,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,5" +
"8,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,8" +
"3")[0];

	private int yy_nxt[][] = unpackFromString(84,57,
"1,2,3,4,2,5,6,7,8,62,8,7,8:2,7,9,63,94,108,8,127,120,109,121,8:2,128,110,8:" +
"5,10,8,60,61,2,6:2,66,11,12,13,14,15,16,17,18,19,20,21,22,23,24,70,74,-1:59" +
",25,-1:2,59,-1:45,26,-1:56,27,-1:13,7,-1:55,8:8,-1,8:20,-1:29,28,-1:6,77,-1" +
":89,32,-1:2,33,-1:56,34,-1:56,35,-1:56,36,-1:56,37,-1:56,38,-1:56,39,-1:7,2" +
"5:55,-1:8,28,-1:99,45,-1:12,8:8,-1,8:10,130,8:9,-1:22,59,-1:2,59,87,59:36,-" +
"1,59:14,-1:2,79:2,-1,79,-1,79:29,-1,79:2,81,79:17,-1:9,67,65,67,69,-1,67,73" +
",-1:48,8:8,-1,8,29,8:9,30,8:3,122,8:4,-1:30,71,-1,71,-1:47,83:2,-1,83,-1,83" +
":32,85,31,83:16,-1:9,67,-1,67,-1:2,67,-1:49,8:8,-1,8:10,43,8:9,-1:28,75:5,-" +
"1,75:2,-1:2,75:2,-1:4,75,-1:5,75,-1:82,40,-1:8,8:8,-1,8:2,46,8:17,-1:77,41," +
"-1:7,8:8,-1,8:4,47,8:15,-1:36,42,-1:48,8:8,-1,8:2,48,8:17,-1:57,44,-1:27,8:" +
"8,-1,8:3,49,8:16,-1:30,79:2,-1:6,79,-1:4,79,-1:3,79:2,-1:8,79,-1:2,79,-1:24" +
",8:8,-1,8:2,50,8:17,-1:28,8:8,-1,8:2,51,8:17,-1:30,83:2,-1:6,83,-1:4,83,-1:" +
"3,83:2,-1:11,83:2,-1:23,8:8,-1,8:4,52,8:15,-1:23,64,-1:61,8:8,-1,8:6,53,8:1" +
"3,-1:28,8:8,-1,8:6,54,8:13,-1:28,8:8,-1,8:8,55,8:11,-1:28,8:8,-1,8:11,56,8:" +
"8,-1:28,8:8,-1,8:14,57,8:5,-1:28,8:8,-1,8:10,58,8:9,-1:28,8:8,-1,8:9,68,8:3" +
",111,8:6,-1:28,8:8,-1,8:4,72,8:15,-1:28,8:8,-1,76,8:19,-1:28,8:8,-1,8:12,78" +
",8:7,-1:28,8:8,-1,8:3,80,8:16,-1:28,8:8,-1,8:4,82,8:15,-1:28,8:8,-1,8:3,84," +
"8:16,-1:28,8:8,-1,8:4,86,8:15,-1:28,8:8,-1,8:4,88,8:15,-1:28,8:8,-1,8:10,89" +
",8:9,-1:28,8:8,-1,8:7,90,8:12,-1:28,8:8,-1,8:10,91,8:9,-1:28,8:8,-1,8:11,92" +
",8:8,-1:28,8:8,-1,8:9,93,8:10,-1:28,8:8,-1,8:3,95,8:16,-1:28,8:8,-1,8:8,96," +
"8,97,8:9,-1:28,8:8,-1,8:9,129,8:2,98,8:7,-1:28,8:8,-1,8:3,99,8:16,-1:28,8:8" +
",-1,100,8:19,-1:28,8:8,-1,8:13,101,8:6,-1:28,8:8,-1,8:11,102,8:8,-1:28,8:8," +
"-1,8:9,103,8:10,-1:28,8:8,-1,8:6,104,8:13,-1:28,8:8,-1,8:12,105,8:7,-1:28,8" +
":8,-1,106,8:19,-1:28,8:8,-1,8:6,107,8:13,-1:28,8:8,-1,8:8,112,8:11,-1:28,8:" +
"8,-1,8:3,113,8:5,114,8:10,-1:28,8:8,-1,8:16,115,8:3,-1:28,8:8,-1,116,8:19,-" +
"1:28,8:8,-1,8:6,117,8:13,-1:28,8:8,-1,8:8,118,8:11,-1:28,8:8,-1,8:7,119,8:1" +
"2,-1:28,8:8,-1,8:5,123,8:14,-1:28,8:8,-1,8:2,124,8:17,-1:28,8:8,-1,8:6,125," +
"8:13,-1:28,8:8,-1,8:12,126,8:7,-1:21");

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
						{return onRead(() -> ops.identifierUnit());}
					case -9:
						break;
					case 9:
						{return onRead(() -> ops.opPointUnit());}
					case -10:
						break;
					case 10:
						{return onRead(() -> ops.underscoreUnit());}
					case -11:
						break;
					case 11:
						{return onRead(() -> ops.opSemicolonUnit());}
					case -12:
						break;
					case 12:
						{return onRead(() -> ops.opOpeningParenthesesUnit());}
					case -13:
						break;
					case 13:
						{return onRead(() -> ops.opOpeningSquareUnit());}
					case -14:
						break;
					case 14:
						{return onRead(() -> ops.opClosingSquareUnit());}
					case -15:
						break;
					case 15:
						{return onRead(() -> ops.opOpeningCurlyUnit());}
					case -16:
						break;
					case 16:
						{return onRead(() -> ops.opClosingCurlyUnit());}
					case -17:
						break;
					case 17:
						{return onRead(() -> ops.opMinusUnit());}
					case -18:
						break;
					case 18:
						{return onRead(() -> ops.opGreaterThanUnit());}
					case -19:
						break;
					case 19:
						{return onRead(() -> ops.opCommaUnit());}
					case -20:
						break;
					case 20:
						{return onRead(() -> ops.opPlusUnit());}
					case -21:
						break;
					case 21:
						{return onRead(() -> ops.opAssignmentUnit());}
					case -22:
						break;
					case 22:
						{return onRead(() -> ops.opModUnit());}
					case -23:
						break;
					case 23:
						{return onRead(() -> ops.opLowerThanUnit());}
					case -24:
						break;
					case 24:
						{return onRead(() -> ops.opNotUnit());}
					case -25:
						break;
					case 25:
						{updateColumn();}
					case -26:
						break;
					case 26:
						{return onRead(() -> ops.opDivAssignUnit());}
					case -27:
						break;
					case 27:
						{return onRead(() -> ops.opMultAssignUnit());}
					case -28:
						break;
					case 28:
						{return onRead(() -> ops.realUnit());}
					case -29:
						break;
					case 29:
						{return onRead(() -> ops.ifUnit());}
					case -30:
						break;
					case 30:
						{return onRead(() -> ops.inUnit());}
					case -31:
						break;
					case 31:
						{return onRead(() -> ops.stringUnit());}
					case -32:
						break;
					case 32:
						{return onRead(() -> ops.opArrowUnit());}
					case -33:
						break;
					case 33:
						{return onRead(() -> ops.opMinusAssignUnit());}
					case -34:
						break;
					case 34:
						{return onRead(() -> ops.opGreaterEqualUnit());}
					case -35:
						break;
					case 35:
						{return onRead(() -> ops.opPlusAssignUnit());}
					case -36:
						break;
					case 36:
						{return onRead(() -> ops.opEqualsUnit());}
					case -37:
						break;
					case 37:
						{return onRead(() -> ops.opModAssignUnit());}
					case -38:
						break;
					case 38:
						{return onRead(() -> ops.opLessEqualUnit());}
					case -39:
						break;
					case 39:
						{return onRead(() -> ops.opNotEqualsUnit());}
					case -40:
						break;
					case 40:
						{return onRead(() -> ops.opAndUnit());}
					case -41:
						break;
					case 41:
						{return onRead(() -> ops.opOrUnit());}
					case -42:
						break;
					case 42:
						{return onRead(() -> ops.opEllipsisUnit());}
					case -43:
						break;
					case 43:
						{return onRead(() -> ops.forUnit());}
					case -44:
						break;
					case 44:
						{return onRead(() -> ops.characterUnit());}
					case -45:
						break;
					case 45:
						{return onRead(() -> ops.opIdentityUnit());}
					case -46:
						break;
					case 46:
						{return onRead(() -> ops.elseUnit());}
					case -47:
						break;
					case 47:
						{return onRead(() -> ops.thisUnit());}
					case -48:
						break;
					case 48:
						{return onRead(() -> ops.trueUnit());}
					case -49:
						break;
					case 49:
						{return onRead(() -> ops.nullUnit());}
					case -50:
						break;
					case 50:
						{return onRead(() -> ops.falseUnit());}
					case -51:
						break;
					case 51:
						{return onRead(() -> ops.whileUnit());}
					case -52:
						break;
					case 52:
						{return onRead(() -> ops.classUnit());}
					case -53:
						break;
					case 53:
						{return onRead(() -> ops.constUnit());}
					case -54:
						break;
					case 54:
						{return onRead(() -> ops.importUnit());}
					case -55:
						break;
					case 55:
						{return onRead(() -> ops.switchUnit());}
					case -56:
						break;
					case 56:
						{return onRead(() -> ops.returnUnit());}
					case -57:
						break;
					case 57:
						{return onRead(() -> ops.nothingUnit());}
					case -58:
						break;
					case 58:
						{return onRead(() -> ops.constructorUnit());}
					case -59:
						break;
					case 60:
						{updateColumn();}
					case -60:
						break;
					case 61:
						{throwError();}
					case -61:
						break;
					case 62:
						{return onRead(() -> ops.integerUnit());}
					case -62:
						break;
					case 63:
						{return onRead(() -> ops.identifierUnit());}
					case -63:
						break;
					case 64:
						{updateColumn();}
					case -64:
						break;
					case 66:
						{throwError();}
					case -65:
						break;
					case 67:
						{return onRead(() -> ops.integerUnit());}
					case -66:
						break;
					case 68:
						{return onRead(() -> ops.identifierUnit());}
					case -67:
						break;
					case 70:
						{throwError();}
					case -68:
						break;
					case 71:
						{return onRead(() -> ops.integerUnit());}
					case -69:
						break;
					case 72:
						{return onRead(() -> ops.identifierUnit());}
					case -70:
						break;
					case 74:
						{throwError();}
					case -71:
						break;
					case 75:
						{return onRead(() -> ops.integerUnit());}
					case -72:
						break;
					case 76:
						{return onRead(() -> ops.identifierUnit());}
					case -73:
						break;
					case 78:
						{return onRead(() -> ops.identifierUnit());}
					case -74:
						break;
					case 80:
						{return onRead(() -> ops.identifierUnit());}
					case -75:
						break;
					case 82:
						{return onRead(() -> ops.identifierUnit());}
					case -76:
						break;
					case 84:
						{return onRead(() -> ops.identifierUnit());}
					case -77:
						break;
					case 86:
						{return onRead(() -> ops.identifierUnit());}
					case -78:
						break;
					case 88:
						{return onRead(() -> ops.identifierUnit());}
					case -79:
						break;
					case 89:
						{return onRead(() -> ops.identifierUnit());}
					case -80:
						break;
					case 90:
						{return onRead(() -> ops.identifierUnit());}
					case -81:
						break;
					case 91:
						{return onRead(() -> ops.identifierUnit());}
					case -82:
						break;
					case 92:
						{return onRead(() -> ops.identifierUnit());}
					case -83:
						break;
					case 93:
						{return onRead(() -> ops.identifierUnit());}
					case -84:
						break;
					case 94:
						{return onRead(() -> ops.identifierUnit());}
					case -85:
						break;
					case 95:
						{return onRead(() -> ops.identifierUnit());}
					case -86:
						break;
					case 96:
						{return onRead(() -> ops.identifierUnit());}
					case -87:
						break;
					case 97:
						{return onRead(() -> ops.identifierUnit());}
					case -88:
						break;
					case 98:
						{return onRead(() -> ops.identifierUnit());}
					case -89:
						break;
					case 99:
						{return onRead(() -> ops.identifierUnit());}
					case -90:
						break;
					case 100:
						{return onRead(() -> ops.identifierUnit());}
					case -91:
						break;
					case 101:
						{return onRead(() -> ops.identifierUnit());}
					case -92:
						break;
					case 102:
						{return onRead(() -> ops.identifierUnit());}
					case -93:
						break;
					case 103:
						{return onRead(() -> ops.identifierUnit());}
					case -94:
						break;
					case 104:
						{return onRead(() -> ops.identifierUnit());}
					case -95:
						break;
					case 105:
						{return onRead(() -> ops.identifierUnit());}
					case -96:
						break;
					case 106:
						{return onRead(() -> ops.identifierUnit());}
					case -97:
						break;
					case 107:
						{return onRead(() -> ops.identifierUnit());}
					case -98:
						break;
					case 108:
						{return onRead(() -> ops.identifierUnit());}
					case -99:
						break;
					case 109:
						{return onRead(() -> ops.identifierUnit());}
					case -100:
						break;
					case 110:
						{return onRead(() -> ops.identifierUnit());}
					case -101:
						break;
					case 111:
						{return onRead(() -> ops.identifierUnit());}
					case -102:
						break;
					case 112:
						{return onRead(() -> ops.identifierUnit());}
					case -103:
						break;
					case 113:
						{return onRead(() -> ops.identifierUnit());}
					case -104:
						break;
					case 114:
						{return onRead(() -> ops.identifierUnit());}
					case -105:
						break;
					case 115:
						{return onRead(() -> ops.identifierUnit());}
					case -106:
						break;
					case 116:
						{return onRead(() -> ops.identifierUnit());}
					case -107:
						break;
					case 117:
						{return onRead(() -> ops.identifierUnit());}
					case -108:
						break;
					case 118:
						{return onRead(() -> ops.identifierUnit());}
					case -109:
						break;
					case 119:
						{return onRead(() -> ops.identifierUnit());}
					case -110:
						break;
					case 120:
						{return onRead(() -> ops.identifierUnit());}
					case -111:
						break;
					case 121:
						{return onRead(() -> ops.identifierUnit());}
					case -112:
						break;
					case 122:
						{return onRead(() -> ops.identifierUnit());}
					case -113:
						break;
					case 123:
						{return onRead(() -> ops.identifierUnit());}
					case -114:
						break;
					case 124:
						{return onRead(() -> ops.identifierUnit());}
					case -115:
						break;
					case 125:
						{return onRead(() -> ops.identifierUnit());}
					case -116:
						break;
					case 126:
						{return onRead(() -> ops.identifierUnit());}
					case -117:
						break;
					case 127:
						{return onRead(() -> ops.identifierUnit());}
					case -118:
						break;
					case 128:
						{return onRead(() -> ops.identifierUnit());}
					case -119:
						break;
					case 129:
						{return onRead(() -> ops.identifierUnit());}
					case -120:
						break;
					case 130:
						{return onRead(() -> ops.identifierUnit());}
					case -121:
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
