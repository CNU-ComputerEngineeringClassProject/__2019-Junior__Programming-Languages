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

	public CuteParser(String st) { // File������ �ƴ� String������ �Է��� �ޱ� ������ �Ű������� File���� String���� �����Ѵ�.
			tokens = Scanner.scan(st); //���ڸ� st�� �Ͽ� Scanner class�� scan�Լ��� ȣ���ϰ� ��ȯ�Ȱ��� tokens�� �����Ѵ�.
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
			// +,-,/,*,=,>,< ��ȣ�ϰ��
			BinaryOpNode BinNode = new BinaryOpNode(); //BinaryOpNode ��ü BinNode�� �����Ѵ�.
			BinNode.setValue(tType); //BinNode���� tType�� ���ڷ� �Ͽ� value���� �����Ѵ�. 
			return BinNode; // BinNode�� �����Ѵ�.
			
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
			FunctionNode finNode = new FunctionNode(); //FunctionNode ��ü finNode�� �����Ѵ�.
			finNode.setValue(tType); //finNode���� tType�� ���ڷ� �Ͽ� value���� �����Ѵ�.
			return finNode; //finNode�� �����Ѵ�.	
			
		case ID:                 
			return new IdNode(tLexeme);   
		case INT:                 
			if (tLexeme == null)       
				System.out.println("???");       
			return new IntNode(tLexeme); 
			//���� ������ BooleanNode Case  
		case FALSE:   
			return BooleanNode.FALSE_NODE;      
		case TRUE:  
			return BooleanNode.TRUE_NODE;  
			//���� ������ L_PAREN, R_PAREN  Case  
		case L_PAREN:
			return parseExprList();    
		case R_PAREN:   
			return END_OF_LIST ;         
			//���� �߰��� APOSTROPHE, QUOTE  
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
