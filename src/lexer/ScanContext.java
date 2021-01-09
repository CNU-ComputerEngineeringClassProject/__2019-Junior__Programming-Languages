package lexer;

import java.io.File;
import java.io.FileNotFoundException;


class ScanContext {
	private final CharStream input;
	private StringBuilder builder;
	
	ScanContext(String st)  { // File구조가 아닌 String형으로 입력을 받기 때문에 매개변수를 File에서 String으로 변경한다.
		this.input = CharStream.from(st); //st를 인자값으로 하여 CharStream class의 from함수를 호출하고 반환된값을 이 class의 input에 저장한다.
		this.builder = new StringBuilder();
	}
	
	CharStream getCharStream() {
		return input;
	}
	
	String getLexime() {
		String str = builder.toString();
		builder.setLength(0);
		return str;
	}
	
	void append(char ch) {
		builder.append(ch);
	}
}
