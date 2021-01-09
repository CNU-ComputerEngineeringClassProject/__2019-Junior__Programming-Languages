package lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Scanner {
	
    // return tokens as an Iterator
    public static Iterator<Token> scan(String st)  {  // File������ �ƴ� String������ �Է��� �ޱ� ������ �Ű������� File���� String���� �����Ѵ�.
        ScanContext context = new ScanContext(st); //st�� ���ڰ����� �Ͽ� ScanContext��ü�� �����ϰ�  ScanContext context������ �����Ѵ�.
        return new TokenIterator(context); 
    }

    // return tokens as a Stream 
    public static Stream<Token> stream(String st)  {  // File������ �ƴ� String������ �Է��� �ޱ� ������ �Ű������� File���� String���� �����Ѵ�.
        Iterator<Token> tokens = scan(st); // st�� ���ڰ����� �ϰ� scan�Լ��� ȣ���ϰ� ��ȯ�� ���� Iterator<Token> tokens�� �����Ѵ�.
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(tokens, Spliterator.ORDERED), false);
    }
}