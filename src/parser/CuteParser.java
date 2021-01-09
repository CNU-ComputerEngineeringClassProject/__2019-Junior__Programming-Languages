package parser;

import java.util.Iterator;

import ast_node.FunctionNode;
import ast_node.BinaryOpNode;
import ast_node.BooleanNode;
import ast_node.IdNode;
import ast_node.IntNode;
import ast_node.ListNode;
import ast_node.Node;
import ast_node.QuoteNode;
import lexer.Scanner;
import lexer.Token;
import lexer.TokenType;

public class CuteParser {
	private Iterator<Token> tokens;
	private static Node END_OF_LIST = new Node(){}; 

	public CuteParser(String st) { // File구조가 아닌 String형으로 입력을 받기 때문에 매개변수를 File에서 String으로 변경한다.
			tokens = Scanner.scan(st); //인자를 st로 하여 Scanner class의 scan함수를 호출하고 반환된값을 tokens에 저장한다.
	}
	private Token getNextToken() {
		if (!tokens.hasNext())
			return null;
		return tokens.next();

	}
	public Node parseExpr() {
		Token t = getNextToken();

		if (t == null) {
			System.out.println("No more token");
			return null;

		}
		TokenType tType = t.type();
		String tLexeme = t.lexme();
		switch (tType) {
		
		case DIV : //  /
		case EQ : // =
		case MINUS : // -
		case GT : // >
		case PLUS : // +
		case TIMES : // *
		case LT :  // <
			// +,-,/,*,=,>,< 기호일경우
			BinaryOpNode BinNode = new BinaryOpNode(); //BinaryOpNode 객체 BinNode를 생성한다.
			BinNode.setValue(tType); //BinNode에서 tType을 인자로 하여 value값을 설정한다. 
			return BinNode; // BinNode를 리턴한다.
			
		case ATOM_Q:
		case CAR:
		case CDR:
		case COND:
		case CONS:
		case DEFINE:
		case EQ_Q:
		case LAMBDA:
		case NOT:
		case NULL_Q:
			FunctionNode finNode = new FunctionNode(); //FunctionNode 객체 finNode를 생성한다.
			finNode.setValue(tType); //finNode에서 tType을 인자로 하여 value값을 설정한다.
			return finNode; //finNode를 리턴한다.	
			
		case ID:                 
			return new IdNode(tLexeme);   
		case INT:                 
			if (tLexeme == null)       
				System.out.println("???");       
			return new IntNode(tLexeme); 
			//새로 구현된 BooleanNode Case  
		case FALSE:   
			return BooleanNode.FALSE_NODE;      
		case TRUE:  
			return BooleanNode.TRUE_NODE;  
			//새로 구현된 L_PAREN, R_PAREN  Case  
		case L_PAREN:
			return parseExprList();    
		case R_PAREN:   
			return END_OF_LIST ;         
			//새로 추가된 APOSTROPHE, QUOTE  
		case APOSTROPHE:     
			QuoteNode quoteNode = new QuoteNode(parseExpr());
			ListNode listNode = ListNode.cons(quoteNode, ListNode.EMPTYLIST  );
			return listNode;  
		case QUOTE:  
			return new QuoteNode(parseExpr());   
		default:   
			System.out.println("Parsing Error!");    
			return null;
		}
	}

	private ListNode parseExprList() {  
		Node head = parseExpr();   
		if (head == null)   
			return null; 
		if (head == END_OF_LIST) 
			// if next token is RPAREN 
			return ListNode.EMPTYLIST;    
		ListNode tail = parseExprList();      
		
		if (tail == null)
			return null; 
		return ListNode.cons(head, tail);         
	}
} 
