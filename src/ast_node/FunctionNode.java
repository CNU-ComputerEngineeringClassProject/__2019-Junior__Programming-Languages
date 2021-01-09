package ast_node;

import java.util.HashMap;
import java.util.Map;

import lexer.TokenType;

public class FunctionNode  implements ValueNode{
	//binaryOpNodeŬ������ ���� �����ؼ� �ۼ�

	public enum FunctionType { //FunctionType�� ���� Ÿ��(enum)�� �����Ѵ�.   
		//Ư���� �ǹ̸� ������ keyword�� ��쿡 tonkenType�� ��ȯ�ϱ����Ͽ� �ش� keywordtokenType�̸�����Ͽ� ���������� �����Ͽ���.
		DEFINE { TokenType tokenType() {return TokenType.DEFINE;} }, 
		LAMBDA { TokenType tokenType() {return TokenType.LAMBDA;} }, 
		COND { TokenType tokenType() {return TokenType.COND;} }, 
		NOT { TokenType tokenType() {return TokenType.NOT;} },  
		CDR { TokenType tokenType() {return TokenType.CDR;} }, 
		CAR { TokenType tokenType() {return TokenType.CAR;} }, 
		CONS { TokenType tokenType() {return TokenType.CONS;} }, 
		EQ_Q { TokenType tokenType() {return TokenType.EQ_Q;} }, 
		NULL_Q { TokenType tokenType() {return TokenType.NULL_Q;} }, 
		ATOM_Q { TokenType tokenType() {return TokenType.ATOM_Q;} }; 
		

		private static Map<TokenType, FunctionType> fromTokenType = new HashMap<TokenType,
				FunctionType>(); //TokenType, FunctionType�� �����ϴ� HashMap fromTokenType�� �����Ѵ�.

		static {
			for (FunctionType fType : FunctionType.values()){  // for���� �̿��Ͽ� FunctionType.values()�� ���� �ϳ��� FunctionType fType�� �����Ѵ�.
				fromTokenType.put(fType.tokenType(), fType); //fromTokenType�� fType�� �´� tokenType�� fType�� �����Ѵ�.
			}
		}

		static FunctionType getFunctionType(TokenType tType){ // tType�� ���ڷ� �Ͽ� FunctionType�� ��ȯ�ϴ� getFunctionType�Լ��̴�.
			return fromTokenType.get(tType); //tType�� ���� FunctionType�� �޾ƿ� �̸� ��ȯ�Ѵ�.
		}

		abstract TokenType tokenType(); //�߻�޼ҵ带 �����Ѵ�.
	}
	

	public FunctionType funcType;

	
	@Override
	public String toString(){
		//���� ä���
		return funcType.name();  //value.name()�� �����Ͽ� ��ȯ�Ȱ��� ��ȯ�Ѵ�.
	}
	public void setValue(TokenType tType) {
		//���� ä���
		FunctionType fType = FunctionType.getFunctionType(tType); //FunctionType fType ������ ����� tType�� �´� FunctionType�� ������ �����Ѵ�.
		funcType = fType; //value�� tType�� �´� FunctionType�� �����Ѵ�.
	}
}
