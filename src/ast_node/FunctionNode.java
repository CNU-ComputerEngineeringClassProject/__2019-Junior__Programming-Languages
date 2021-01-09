package ast_node;

import java.util.HashMap;
import java.util.Map;

import lexer.TokenType;

public class FunctionNode  implements ValueNode{
	//binaryOpNode클래스를 보고 참고해서 작성

	public enum FunctionType { //FunctionType은 열거 타입(enum)을 생성한다.   
		//특별한 의미를 가지는 keyword일 경우에 tonkenType을 반환하기위하여 해당 keywordtokenType이름사용하여 열거형값을 지정하였다.
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
				FunctionType>(); //TokenType, FunctionType을 저장하는 HashMap fromTokenType을 생성한다.

		static {
			for (FunctionType fType : FunctionType.values()){  // for문을 이용하여 FunctionType.values()의 값을 하나씩 FunctionType fType에 저장한다.
				fromTokenType.put(fType.tokenType(), fType); //fromTokenType에 fType에 맞는 tokenType과 fType을 저장한다.
			}
		}

		static FunctionType getFunctionType(TokenType tType){ // tType를 인자로 하여 FunctionType을 반환하는 getFunctionType함수이다.
			return fromTokenType.get(tType); //tType에 만든 FunctionType을 받아와 이를 반환한다.
		}

		abstract TokenType tokenType(); //추상메소드를 생성한다.
	}
	

	public FunctionType funcType;

	
	@Override
	public String toString(){
		//내용 채우기
		return funcType.name();  //value.name()을 실행하여 반환된값을 반환한다.
	}
	public void setValue(TokenType tType) {
		//내용 채우기
		FunctionType fType = FunctionType.getFunctionType(tType); //FunctionType fType 변수를 만들고 tType에 맞는 FunctionType을 가져와 저장한다.
		funcType = fType; //value에 tType에 맞는 FunctionType를 저장한다.
	}
}
