package lexer;


public enum TokenType {
	INT,
	ID, 
	TRUE, FALSE, NOT,
	PLUS, MINUS, TIMES, DIV,   //special chractor
	LT, GT, EQ, APOSTROPHE,    //special chractor
	L_PAREN, R_PAREN,QUESTION, //special chractor
	DEFINE, LAMBDA, COND, QUOTE,
	CAR, CDR, CONS,
	ATOM_Q, NULL_Q, EQ_Q; 

	static TokenType fromSpecialCharactor(char ch) { //special chractor에대해서 따로 토큰타입을 리턴하는 함수를 만든다. 이는 매개변수로 char형 ch를 받는다.
		//정규표현식에 나타난 token을 기준으로 나타남.
		switch ( ch ) { //switch문을 이용하여 인수로 받은 ch를 case에 따라 알맞은 토큰타입을 반환한다.
		case '+': //ch가 '+'기호인경우 
			return PLUS; //토큰타입으로 PLUS를 리턴한다.
			//나머지 Special Charactor에 대해 토큰을 반환하도록 작성
		case '-': //ch가 '-'기호인경우 
			return MINUS; //토큰타입으로 MINUS를 리턴한다.
		case '*': //ch가 '*'기호인경우 
			return TIMES;//토큰타입으로 TIMES를 리턴한다.
		case '/': //ch가 '/'기호인경우 
			return DIV;//토큰타입으로 DIV를 리턴한다.
		case '(': //ch가 '('기호인경우 
			return L_PAREN;//토큰타입으로 L_PAREN를 리턴한다.
		case ')': //ch가 ')'기호인경우 
			return R_PAREN;//토큰타입으로 R_PAREN를 리턴한다.
		case '<': //ch가 '<'기호인경우 
			return LT;//토큰타입으로 LT를 리턴한다.
		case '=': //ch가 '='기호인경우 
			return EQ;//토큰타입으로 EQ를 리턴한다.
		case '>': //ch가 '>'기호인경우 
			return GT;//토큰타입으로 GT를 리턴한다.
		case '\'': //ch가 '\''기호인경우 (이기호는 '(apostrophe) 이다.) 
			return APOSTROPHE;//토큰타입으로 APOSTROPHE를 리턴한다.
		case '?': //ch가 '?'기호인경우
			return QUESTION; //토큰타입으로 QUESTION를 리턴한다.
		default: //이외의 문자가 들어왔을 경우
			throw new IllegalArgumentException("unregistered char: " + ch); //잘못된 인자로 예외처리를 한다.
		}
	}
}
