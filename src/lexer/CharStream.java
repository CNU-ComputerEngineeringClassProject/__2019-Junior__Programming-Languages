package lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

class CharStream {
	private final Reader reader;
	private Character cache;
	
	static CharStream from(String str) { // File구조가 아닌 String형으로 입력을 받기 때문에 매개변수를 File에서 String으로 변경한다.
		return new CharStream(new StringReader(str)); //생성자 CharStream을 보면 매개변수로 Reader를 받는다. 
		//그래서 str를 인자값으로 하여 StringReader 객체를 생성한다. 그리고 이를 인자값으로 하여 CharStream객체를 호출하고 이를 리턴한다.
	}
	
	CharStream(Reader reader) {
		this.reader = reader;
		this.cache = null;
	}
	
	Char nextChar() {
		if ( cache != null ) {
			char ch = cache;
			cache = null;
			
			return Char.of(ch);
		}
		else {
			try {
				int ch = reader.read();
				if ( ch == -1 ) {
					return Char.end();
				}
				else {
					return Char.of((char)ch);
				}
			}
			catch ( IOException e ) {
				throw new ScannerException("" + e);
			}
		}
	}
	
	void pushBack(char ch) {
		cache = ch;
	}
}
