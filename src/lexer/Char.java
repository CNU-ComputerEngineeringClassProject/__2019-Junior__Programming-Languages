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
		
		if (Character.isAlphabetic(ch) || ch == '?') { //(letter�� �Ǵ� ���ǽ�:) ch�� �������̰ų� ch�� '?'�ϰ�� if���� �����Ѵ�. Character.isAlphabetic�� ch�� �������ϰ�� true�� ��ȯ�Ѵ�.
			return CharacterType.LETTER; //�̹��ڴ� letter�̶� �ǹ̷� Charater�� type���� Letter�� ��ȯ�Ѵ�.
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
