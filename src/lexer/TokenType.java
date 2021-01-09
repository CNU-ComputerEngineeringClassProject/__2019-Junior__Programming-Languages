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

	static TokenType fromSpecialCharactor(char ch) { //special chractor�����ؼ� ���� ��ūŸ���� �����ϴ� �Լ��� �����. �̴� �Ű������� char�� ch�� �޴´�.
		//����ǥ���Ŀ� ��Ÿ�� token�� �������� ��Ÿ��.
		switch ( ch ) { //switch���� �̿��Ͽ� �μ��� ���� ch�� case�� ���� �˸��� ��ūŸ���� ��ȯ�Ѵ�.
		case '+': //ch�� '+'��ȣ�ΰ�� 
			return PLUS; //��ūŸ������ PLUS�� �����Ѵ�.
			//������ Special Charactor�� ���� ��ū�� ��ȯ�ϵ��� �ۼ�
		case '-': //ch�� '-'��ȣ�ΰ�� 
			return MINUS; //��ūŸ������ MINUS�� �����Ѵ�.
		case '*': //ch�� '*'��ȣ�ΰ�� 
			return TIMES;//��ūŸ������ TIMES�� �����Ѵ�.
		case '/': //ch�� '/'��ȣ�ΰ�� 
			return DIV;//��ūŸ������ DIV�� �����Ѵ�.
		case '(': //ch�� '('��ȣ�ΰ�� 
			return L_PAREN;//��ūŸ������ L_PAREN�� �����Ѵ�.
		case ')': //ch�� ')'��ȣ�ΰ�� 
			return R_PAREN;//��ūŸ������ R_PAREN�� �����Ѵ�.
		case '<': //ch�� '<'��ȣ�ΰ�� 
			return LT;//��ūŸ������ LT�� �����Ѵ�.
		case '=': //ch�� '='��ȣ�ΰ�� 
			return EQ;//��ūŸ������ EQ�� �����Ѵ�.
		case '>': //ch�� '>'��ȣ�ΰ�� 
			return GT;//��ūŸ������ GT�� �����Ѵ�.
		case '\'': //ch�� '\''��ȣ�ΰ�� (�̱�ȣ�� '(apostrophe) �̴�.) 
			return APOSTROPHE;//��ūŸ������ APOSTROPHE�� �����Ѵ�.
		case '?': //ch�� '?'��ȣ�ΰ��
			return QUESTION; //��ūŸ������ QUESTION�� �����Ѵ�.
		default: //�̿��� ���ڰ� ������ ���
			throw new IllegalArgumentException("unregistered char: " + ch); //�߸��� ���ڷ� ����ó���� �Ѵ�.
		}
	}
}
