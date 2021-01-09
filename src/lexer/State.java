package lexer;

import static lexer.TransitionOutput.GOTO_ACCEPT_ID;
import static lexer.TransitionOutput.GOTO_ACCEPT_INT;
import static lexer.TransitionOutput.GOTO_EOS;
import static lexer.TransitionOutput.GOTO_FAILED;
import static lexer.TransitionOutput.GOTO_MATCHED;
import static lexer.TransitionOutput.GOTO_SHARP;
import static lexer.TransitionOutput.GOTO_SIGN;
import static lexer.TransitionOutput.GOTO_START;
import static lexer.TokenType.FALSE;
import static lexer.TokenType.INT;
import static lexer.TokenType.MINUS;
import static lexer.TokenType.PLUS;
import static lexer.TokenType.TRUE;


enum State {
	START {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			
			switch ( ch.type() ) {
			case LETTER:
				context.append(v);
				return GOTO_ACCEPT_ID;
			case DIGIT:
				context.append(v);
				return GOTO_ACCEPT_INT;
			case SPECIAL_CHAR: //special charactor가 들어온 경우 
				if (v=='+' || v=='-' ) { //v가 부호(+,-)인 경우에는 
					context.append(v);  //스트링버퍼에 문자를 저장한다. 토큰 문자열을 문자 순서대로 버퍼에 저장할예정.
					return GOTO_SIGN; //현재 이 토큰의 첫문자로 부호가 나왔다는 의미로 GOTO_SIGN상태로 반환한다. 
				}
				else if (v=='#') {   //boolean인 경우에는 토큰의 첫문자로 '#'부호가 들어온다 그러므로 v가 '#'인 경우에는
					context.append(v);  //스트링버퍼에 문자를 저장한다. 토큰 문자열을 문자 순서대로 버퍼에 저장할예정.
					return GOTO_SHARP; //현재 이 토큰의 첫문자로 부호가 나왔다는 의미로 GOTO_SIGN상태로 반환한다.
				}
				else { //그외에는 type을 알아내서 알맞은 상태로 반환
					
					context.append(v);  //스트링버퍼에 문자를 저장한다. 토큰 문자열을 문자 순서대로 버퍼에 저장할예정.
					return GOTO_MATCHED( TokenType.fromSpecialCharactor(v), context.getLexime()); 
					//다른 기호들은 입력시 기호하나만 들어오는 것들이다. 그러므로 알맞은 상태로 반환한다.
					//TokenType class의 fromSpecialCharactor함수에서 v를 인자값으로 하여 알맞은 타입으로 반환받은 값과, 버퍼에 저장되어있는 값(기호). 이 두 값을 인자로 하여  GOTO_MATCHED함수를 호출하여 이 특수문자에대한 알맞은 상태와 특수문자를 토큰으로 생성하고 다음상태로 MATCHED상태를 저장한다. 

				}
			case WS:
				return GOTO_START;
			case END_OF_STREAM:
				return GOTO_EOS;
			default:
				throw new AssertionError();
			}
		}
	},
	ACCEPT_ID {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch ( ch.type() ) {
			case LETTER:
			case DIGIT:
				context.append(v);
				return GOTO_ACCEPT_ID;
			case SPECIAL_CHAR:
				return GOTO_FAILED;
			case WS:
			case END_OF_STREAM:
				return GOTO_MATCHED(Token.ofName(context.getLexime()));
			default:
				throw new AssertionError();
			}
		}
	},
	ACCEPT_INT {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			switch ( ch.type() ) {
			case LETTER:
				return GOTO_FAILED;
			case DIGIT:
				context.append(ch.value());
				return GOTO_ACCEPT_INT;
			case SPECIAL_CHAR:
				return GOTO_FAILED;
			case WS:
				return GOTO_MATCHED(INT, context.getLexime());
			case END_OF_STREAM:
				return GOTO_FAILED;
			default:
				throw new AssertionError();
			}
		}
	},
	SHARP {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			switch ( ch.type() ) {
			case LETTER:
				switch ( v ) {
				case 'T':
					context.append(v);
					return GOTO_MATCHED(TRUE, context.getLexime());
				case 'F':
					context.append(v);
					return GOTO_MATCHED(FALSE, context.getLexime());
				default:
					return GOTO_FAILED;
				}
			default:
				return GOTO_FAILED;
			}
		}
	},
	SIGN {
		@Override
		public TransitionOutput transit(ScanContext context) {
			Char ch = context.getCharStream().nextChar();
			char v = ch.value();
			
			switch ( ch.type() ) {
			case LETTER:
				return GOTO_FAILED;
			case DIGIT:
				context.append(v);
				return GOTO_ACCEPT_INT;
			case SPECIAL_CHAR:
				return GOTO_FAILED;
			case WS:
				String lexme = context.getLexime();
				switch ( lexme ) {
				case "+":
					return GOTO_MATCHED(PLUS, lexme);
				case "-":
					return GOTO_MATCHED(MINUS, lexme);
				default:
					throw new AssertionError();
				}
			case END_OF_STREAM:
				return GOTO_FAILED;
			default:
				throw new AssertionError();
			}
		}
	},
	MATCHED {
		@Override
		public TransitionOutput transit(ScanContext context) {
			throw new IllegalStateException("at final state");
		}
	},
	FAILED{
		@Override
		public TransitionOutput transit(ScanContext context) {
			throw new IllegalStateException("at final state");
		}
	},
	EOS {
		@Override
		public TransitionOutput transit(ScanContext context) {
			return GOTO_EOS;
		}
	};

	abstract TransitionOutput transit(ScanContext context);
}
