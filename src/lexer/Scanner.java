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
    public static Iterator<Token> scan(String st)  {  // File구조가 아닌 String형으로 입력을 받기 때문에 매개변수를 File에서 String으로 변경한다.
        ScanContext context = new ScanContext(st); //st를 인자값으로 하여 ScanContext객체를 생성하고  ScanContext context변수에 저장한다.
        return new TokenIterator(context); 
    }

    // return tokens as a Stream 
    public static Stream<Token> stream(String st)  {  // File구조가 아닌 String형으로 입력을 받기 때문에 매개변수를 File에서 String으로 변경한다.
        Iterator<Token> tokens = scan(st); // st를 입자값으로 하고 scan함수를 호출하고 반환된 값을 Iterator<Token> tokens에 저장한다.
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(tokens, Spliterator.ORDERED), false);
    }
}