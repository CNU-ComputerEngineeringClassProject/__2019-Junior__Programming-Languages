package lexer;

import java.io.File;
import java.io.FileNotFoundException;


class ScanContext {
	private final CharStream input;
	private StringBuilder builder;
	
	ScanContext(String st)  { // File������ �ƴ� String������ �Է��� �ޱ� ������ �Ű������� File���� String���� �����Ѵ�.
		this.input = CharStream.from(st); //st�� ���ڰ����� �Ͽ� CharStream class�� from�Լ��� ȣ���ϰ� ��ȯ�Ȱ��� �� class�� input�� �����Ѵ�.
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
