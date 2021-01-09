package lexer;


class Char {
	private final char value;
	private final CharacterType type;

	enum CharacterType {
		LETTER, DIGIT, SPECIAL_CHAR, WS, END_OF_STREAM,
	}
	
	static Char of(char ch) {
		return new Char(ch, getType(ch));
	}
	
	static Char end() {
		return new Char(Character.MIN_VALUE, CharacterType.END_OF_STREAM);
	}
	
	private Char(char ch, CharacterType type) {
		this.value = ch;
		this.type = type;
	}
	
	char value() {
		return this.value;
	}
	
	CharacterType type() {
		return this.type;
	}
	
	private static CharacterType getType(char ch) {
		
		int code = (int)ch;
		
		if (Character.isAlphabetic(ch) || ch == '?') { //(letter가 되는 조건식:) ch가 영문자이거나 ch가 '?'일경우 if문을 실행한다. Character.isAlphabetic는 ch가 영문자일경우 true를 반환한다.
			return CharacterType.LETTER; //이문자는 letter이란 의미로 Charater형 type으로 Letter를 반환한다.
		}
		
		if ( Character.isDigit(ch) ) {
			return CharacterType.DIGIT;
		}
		
		switch ( ch ) {
			case '-': case '+': case '*': case '/':
			case '(': case ')':
			case '<': case '=': case '>':
			case '#': case '\'': 
				return CharacterType.SPECIAL_CHAR;
		}
		
		if ( Character.isWhitespace(ch) ) {
			return CharacterType.WS;
		}
	
	
		throw new IllegalArgumentException("input=" + ch);
	}
}
