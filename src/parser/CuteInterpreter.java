package parser;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import ast_node.BinaryOpNode;
import ast_node.BooleanNode;
import ast_node.FunctionNode;
import ast_node.IdNode;
import ast_node.IntNode;
import ast_node.ListNode;
import ast_node.Node;
import ast_node.NodePrinter;
import ast_node.QuoteNode;
public class CuteInterpreter {


	private static Map<String, Node> fromDefine = new HashMap<String,Node>();


	public static void main(String[] args) {    

		Scanner scanner = new Scanner(System.in); //�ܼ�â���� �Է��� �ޱ����ؼ� Scanner ��ü�� �����Ѵ�.

		while(true) { // ���ѹݺ����� �����Ͽ� ����ؼ� �Է��� �ް� �׿� �´� ����� �Ҽ��ֵ��� �Ͽ���.

			System.out.print("> "); //Interpreter�� ������Ű�� > prompt�� ����.
			String input_s = scanner.nextLine(); //�Է¹��� ���ڿ��� input_s�� �����Ѵ�.
			System.out.print("... "); //��� ������ �� ���� ǥ�� �� ����Ͽ� �������� ���δ�.

			CuteParser cuteParser = new CuteParser(input_s);  // ���ڰ��� input_s�� �Ͽ� CuteParser�� �����ϰ� CuteParser cuteParser������ �����Ѵ�. 
			CuteInterpreter interpreter = new CuteInterpreter();         
			Node parseTree = cuteParser.parseExpr(); 
			Node resultNode = interpreter.runExpr(parseTree);
			NodePrinter nodePrinter = new NodePrinter(resultNode);       
			nodePrinter.prettyPrint();

		}
	} 


	private void errorLog(String err) {  
		System.out.println(err); 

	}

	public Node runExpr(Node rootExpr) {
		if (rootExpr == null)        
			return null;        
		if (rootExpr instanceof IdNode)       
			return rootExpr;       
		else if (rootExpr instanceof IntNode)     
			return rootExpr;       
		else if (rootExpr instanceof BooleanNode) 
			return rootExpr;        
		else if (rootExpr instanceof ListNode)  
			return runList((ListNode) rootExpr); 
		else         
			errorLog("run Expr error");     
		return null;     
	} 

	private Node runList(ListNode list) { 
		if (list.equals(ListNode.EMPTYLIST))
			return list;     
		if (list.car() instanceof FunctionNode) { 
			return runFunction((FunctionNode) list.car(), (ListNode) stripList(list.cdr()));       
		}
		if (list.car() instanceof BinaryOpNode) {   
			return runBinary(list);    

		}
		return list; 
	} 


	private Node runFunction(FunctionNode operator, ListNode operand) {     

		switch (operator.funcType) {  //operator.funcType�� ���� �ٸ��� �۵��ǵ��� switch���� ����Ѵ�. 

		case CAR: // operator.funcType�� CAR�ΰ��

			if(operand.car()  instanceof IdNode) {
				Node licar = operand.car();
				return runExpr(ListNode.cons(operator, (ListNode) lookuptable(((IdNode)licar).toString()))); 
			}else {

				if(operand.car() instanceof FunctionNode) {
					operand = (ListNode)runExpr(operand);
				}

				if(operand.car() instanceof QuoteNode) {
					ListNode lncar = (ListNode)runQuote(operand); //operand�� ���ڰ��� runQuote�Լ��� ȣ���Ͽ� Node������ ����Ʈ ��带 ��ȯ�޴´�. �̸� ListNode�� ����ȯ�� �ϰ� lncar ListNode��ü�� �����Ͽ� ���⿡ �����Ѵ�.
					if(lncar.car() instanceof ListNode) { //lncar.car()�� ListNode��ü���
						QuoteNode quoteNode = new QuoteNode(lncar.car()); //lncar.car()�� ���ڷ��ϴ� QuoteNode�� �����Ѵ�. �׸��� �̸� QuoteNode ��ü quoteNode�� �����Ѵ�.
						return ListNode.cons(quoteNode, ListNode.EMPTYLIST  ); //�׸��� ���(car)�� quoteNode�̰�, ����(cdr)�� ListNode.EMPTYLIST�� �ϴ� ����Ʈ ��带 ���� �����ϰ� �̸� ��ȯ�Ѵ�.
					}else { //�ƴ϶�� (lncar.car()�� ListNode��ü�� �ƴ϶��)
						if(lncar.car() instanceof IntNode || lncar.car() instanceof BooleanNode) { //lncar.car()�� IntNode �ų� BooleanNode�� ��ü��� 
							return lncar.car(); //lncar.car()�� �����Ѵ�.
						}else {
							QuoteNode quoteNode = new QuoteNode(lncar.car()); 
							return ListNode.cons(quoteNode, ListNode.EMPTYLIST);
							//return new QuoteNode(lncar.car()); // �̿��� ��� lncar.car()�� ���ڰ����� �Ͽ� QuoteNode�� �����ϰ� �̸� �����Ѵ�.
						}
					}
				}
			}

			return null;

		case CDR: // operator.funcType�� CDR�ΰ��   

			if(operand.car() instanceof FunctionNode) {
				operand = (ListNode)runExpr(operand);
			}
			if(operand.car()  instanceof IdNode) {
				Node licar = operand.car();
				return runExpr(ListNode.cons(operator, (ListNode) lookuptable(((IdNode)licar).toString()))); 
			}else {			
				ListNode lncdr = (ListNode)runQuote(operand); //operand�� ���ڰ��� runQuote�Լ��� ȣ���Ͽ� Node������ ����Ʈ ��带 ��ȯ�޴´�. �̸� ListNode�� ����ȯ�� �ϰ� lncdr ListNode��ü�� �����Ͽ� ���⿡ �����Ѵ�.
				QuoteNode quoteNode = new QuoteNode(lncdr.cdr()); //lncar.cdr()�� ���ڷ��ϴ� QuoteNode�� �����Ѵ�. �׸��� �̸� QuoteNode ��ü quoteNode�� �����Ѵ�.
				return ListNode.cons(quoteNode, ListNode.EMPTYLIST  ); //�׸��� ���(car)�� quoteNode�̰�, ����(cdr)�� ListNode.EMPTYLIST�� �ϴ� ����Ʈ ��带 ���� �����ϰ� �̸� ��ȯ�Ѵ�.
			}

		case CONS: // operator.funcType�� CONS�ΰ��
			Node lncons1; //�Ѱ��� ����(head)�� �����������Ͽ� Node lncons1������ �����Ͽ���.
			Node licarFirst = operand.car();
			Node licarSecond = (operand.cdr()).car();

			if(operand.car()  instanceof IdNode) {
				licarFirst =  lookuptable(((IdNode)operand.car()).toString()); 
				if(licarFirst == null) licarFirst = operand.car();
			}

			if(licarFirst instanceof ListNode) { //�Ѱ��� ������ operand.car()�� ListNode��ü���
				lncons1 = runQuote((ListNode)licarFirst); //operand.car()�� ListNode�� ����ȯ�� ���� ���ڰ����� �Ͽ� runQuote�Լ��� ȣ���Ѵ�. �׸��� �� ��ȯ���� lncons1�� �����Ѵ�.
			}else { //�ƴѰ�� operand.car()�� ListNode ��ü�� �ƴѰ�� 
				lncons1 = licarFirst; //operand.car()�� lncons1�� �����Ѵ�.
			}

			if(licarSecond instanceof IdNode) {
				licarSecond = lookuptable(((IdNode)licarSecond).toString()); 
				if(licarSecond == null) licarSecond = (operand.cdr()).car();
			}

			Node lncons2 = runQuote((ListNode)licarSecond); //�Ѱ��� ����Ʈ(tail)�� �����������Ͽ� (operand.cdr()).car()�� ListNode�� ����ȯ�� ���� ���ڰ����� �Ͽ� runQuote�Լ��� ȣ���Ѵ�.�̸� Node lncons2�� �����Ѵ�.
			ListNode result =  ListNode.cons(lncons1, (ListNode)lncons2); //lncons1�� lncons2�� �ٿ��� ���ο� ����Ʈ ��带 ��������Ͽ� ListNode.cons�� ����Ͽ�  ���(car)�� lncons1�� �ϰ�, ����(cdr)�� lncons2�� ListNode�� ����ȯ �Ͽ� �������. �̸� ListNode��ü result�� �����Ͽ���.
			return ListNode.cons(new QuoteNode(result), ListNode.EMPTYLIST); //result�� ���ڰ����� �Ͽ� ���ο� QuoteNode�� �����ϰ� �̸� ���(car)�� �ϰ� ListNode.EMPTYLIST�� ���� (cdr)�� ListNode�� �����ϰ� �̸� ��ȯ�Ѵ�.

		case COND: // operator.funcType�� COND�ΰ��

			ListNode ifN ;

			Node only_one;
			if(operand.car() instanceof IdNode) {
				only_one = lookuptable(((IdNode)(operand.car())).toString()); 
			}else {
				only_one = operand.car();
			}

			if( runExpr(only_one) instanceof BooleanNode) {

				if( runExpr(only_one) == BooleanNode.TRUE_NODE) { 
					Node one_cdr = operand.cdr().car();

					if( one_cdr instanceof IdNode) {
						one_cdr = lookuptable(((IdNode)one_cdr).toString());	
						if(one_cdr == null) one_cdr = operand.cdr().car();
					}

					if(one_cdr instanceof FunctionNode) {
						return one_cdr;
					}else{
						return runExpr(one_cdr); //ifNn�� ù��° ����� ���� ��ȯ�ؾ��ϹǷ� ifNn.car()�� ���ڷ� �Ͽ� runExpr�Լ��� ȣ���ϰ� ��ȯ���� ��ȯ�Ѵ�.
					}

				}else return BooleanNode.FALSE_NODE;
			}else {

				if(operand.car() instanceof IdNode) {
					ifN = (ListNode) lookuptable(((IdNode)(operand.car())).toString()); 
				}else {
					ifN = (ListNode)operand.car(); // ����Ʈ��� ���� ù��° ����Ʈ��带 �����������Ͽ�  operand.car()�� ����ϰ� �̸� ListNode�� ����ȯ�� �ѵڿ� ListNode ��ü ifN�� �����Ѵ�.
				}

				Node T_F = ifN.car();
				Node returnValue = ifN.cdr();

				if(T_F instanceof IdNode) {
					T_F = lookuptable(((IdNode)ifN.car()).toString());			
				}

				if(runExpr(T_F) == BooleanNode.TRUE_NODE) { //���ǽ��� �������� �ľ��ϱ����Ͽ� ifN.car()�� ���ڰ����� �Ͽ� runExpr()�� ȣ���ϰ� ��ȯ���� ���� BooleanNode.TRUE_NODE��� ���ǽ��� ���̶�� �ǹ��̹Ƿ� if������ �ڵ带 �����Ѵ�.
					//				ListNode ifNn = ifN.cdr(); //���ǽ��� ���̹Ƿ� ifN.cdr()�� ù��° ���� ��ȯ�ؾ��Ѵ�. ���� listNode ��ü ifNn�� ��ȯ���� �����Ѵ�.
					if(((ListNode)returnValue).car() instanceof IdNode) {
						Node result_return = lookuptable(((IdNode)(((ListNode)returnValue).car())).toString());	

						if(result_return == null) result_return = ((ListNode)returnValue).car();

						return runExpr(result_return);
					}else {
						return runExpr(((ListNode)returnValue).car()); //ifNn�� ù��° ����� ���� ��ȯ�ؾ��ϹǷ� ifNn.car()�� ���ڷ� �Ͽ� runExpr�Լ��� ȣ���ϰ� ��ȯ���� ��ȯ�Ѵ�.
					}

				}else if( operand.cdr() != ListNode.EMPTYLIST) { //���ǽ��� �����̰� operand.cdr()�� �� ����Ʈ ��尡 �ƴҰ�쿡�� ���� ���ǽ��� Ȯ���ؾ��ϹǷ� else if���� �����Ѵ�.

					ListNode newlistNode = ListNode.cons(operator, ListNode.cons(operand.cdr(), ListNode.EMPTYLIST)); 
					//���� ����Ʈ ��尡 �����, ���� ���� ���� ���� �˼� ���⶧���� �������� �Ǵܵ� ����Ʈ ��带 �����ϰ� ���� ����Ʈ ����� �տ� cond�� �ٿ��� ���ο� listNode�� �����Ͽ���.
					//���� ���� ����Ʈ ���鸸�� ����Ͽ� ����Ʈ ��带 �����. ListNode.cons�� ����ؼ� ���(car)�� operand.cdr()�� �ϰ�, ����(cdr)�� ListNode.EMPTYLIST�� �Ͽ� �������. 
					//�׸��� ListNode.cons����Ͽ� ���� ������ ����Ʈ ��带 ����(cdr)�� �ϴ� ���ο� ����Ʈ ��带 �����. �̶� ���(car)�� cond�� ���� ���̱⶧���� operator�� �ִ´�. 
					//�׸��� ������� ���ο� listNode�� ListNode ��ü newlistNode�� �����Ѵ�.
					return runExpr(newlistNode); //���� ���� ���ǽ��� �Ǵ��ؾ��ϱ⶧���� newlistNode�� ���ڰ����� �Ͽ� runExpr�Լ��� ȣ���Ͽ� ��ȯ���� ��ȯ�Ѵ�.
				}

				return BooleanNode.FALSE_NODE; //���ǹ��� ���ΰ��� ���� ��� BooleanNode.FALSE_NODE�� �����Ѵ�.
			}
		case NOT: // operator.funcType�� NOT�ΰ��

			Node NotN = operand.car();

			if(NotN instanceof IdNode) {
				NotN = runExpr(lookuptable(((IdNode)NotN).toString()));			
			}

			if(NotN instanceof BooleanNode) { //operand.car()�� BooleanNode ��ü�� ���
				return (NotN == BooleanNode.TRUE_NODE) ? BooleanNode.FALSE_NODE : BooleanNode.TRUE_NODE ;
				//operand.car()�� BooleanNode.TRUE_NODE��� �ݴ�(not)�� BooleanNode.FALSE_NODE�� ��ȯ�Ѵ�. �׸���  BooleanNode.FALSE_NODE��� �ݴ�(not)�� BooleanNode.TRUE_NODE�� ��ȯ�Ѵ�.
			}
			else {//operand.car()�� BooleanNode��ü�� �ƴҰ��
				BooleanNode before_Result = (BooleanNode) runExpr(operand); // operand�� ���ڷ� �Ͽ� runExpr�Լ��� ȣ���ϸ� BooleanNode�� Node������ ��ȯ�ɰ��̴�.
				//�׷��� BooleanNode�� ����ȯ�� �����صڿ�  BooleanNode ��ü before_Result�� �����Ͽ� �����Ѵ�.
				return (before_Result == BooleanNode.TRUE_NODE) ? BooleanNode.FALSE_NODE : BooleanNode.TRUE_NODE ;
				//before_Result�� BooleanNode.TRUE_NODE��� �ݴ�(not)�� BooleanNode.FALSE_NODE�� ��ȯ�Ѵ�. �׸���  BooleanNode.FALSE_NODE��� �ݴ�(not)�� BooleanNode.TRUE_NODE�� ��ȯ�Ѵ�.
			}

		case EQ_Q: // operator.funcType�� EQ_Q�ΰ��

			Node first_operand = operand.car(); // ��ü �񱳸� ���� ù��° ��带 ������ �����ϱ����� first_operand Node��ü�� �����Ѵ�.
			Node second_operand = (operand.cdr()).car(); // ��ü �񱳸� ���� ù��° ��带 ������ �����ϱ����� second_operand Node��ü�� �����Ѵ�.

			boolean first_Q = false;
			boolean second_Q = false;

			if(first_operand instanceof IdNode) {
				first_operand = lookuptable(((IdNode)first_operand).toString());		
				if(first_operand == null) first_operand = operand.car();
			}
			if(second_operand instanceof IdNode) {
				second_operand = lookuptable(((IdNode)second_operand).toString());		
				if(second_operand == null) second_operand = (operand.cdr()).car();
			}


			if(first_operand instanceof ListNode) {
				first_operand = runExpr(first_operand);
				if(first_operand instanceof ListNode) {	
					Node f = ((ListNode)first_operand).car();
					if( f instanceof QuoteNode) {
						if(runQuote((ListNode)first_operand) instanceof ListNode) {
							first_operand = runQuote((ListNode)first_operand);//��ü �񱳸� ���� ù��° ��带 �����������ؼ� operand.car()�� ListNode�� ����ȯ�ϰ� �̸� ���ڰ����� �ϴ� runQuote�Լ��� ȣ���Ͽ� Node�� ��ȯ�޴´�. �̸� first_operand�� �����Ѵ�.				
						}else {
							first_Q = true;
							first_operand = runQuote((ListNode)first_operand);
						}
					}
				}
			}

			if( second_operand instanceof ListNode) {
				second_operand = runExpr(second_operand);
				if( second_operand instanceof ListNode) {
					Node s = ((ListNode)second_operand).car();
					if( s instanceof QuoteNode) {
						if(runQuote((ListNode)second_operand) instanceof ListNode) {
							second_operand = runQuote((ListNode)second_operand); //��ü �񱳸� ���� �ι�° ��带 �����������ؼ�  (operand.cdr()).car()�� ListNode�� ����ȯ�ϰ� �̸� ���ڰ����� �ϴ� runQuote�Լ��� ȣ���Ͽ� Node�� ��ȯ�޴´�. �̸� second_operand Node��ü�� �����Ѵ�.
						}else {
							second_Q = true;
							second_operand = runQuote((ListNode)second_operand);
						}
					}
				}
			}

			if(first_Q && second_Q) return first_operand.toString().equals(second_operand.toString())? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE; 
			else if(first_Q || second_Q) {
				if(first_operand instanceof IntNode && second_operand instanceof IntNode) {
					return  BooleanNode.TRUE_NODE;
				}else if(first_operand instanceof BooleanNode && second_operand instanceof BooleanNode) {
					return  BooleanNode.TRUE_NODE;
				}
				return  BooleanNode.FALSE_NODE;
			}



			if(first_operand instanceof FunctionNode && second_operand instanceof FunctionNode) {
				return first_operand.toString().equals(second_operand.toString())? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE; 
			}


			return (first_operand.equals(second_operand) ) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE; 
			//second_operand���ڰ������Ͽ� first_operand�� equals�޼ҵ带 ����Ͽ� ���� ��ü�� �����ϴ��� Ȯ���Ѵ�. ���� ��ü�� �����Ѵٸ� BooleanNode.TRUE_NODE�� ��ȯ�ϰ�, �ƴ϶��  BooleanNode.FALSE_NODE�� ��ȯ�Ѵ�.  

		case NULL_Q: // operator.funcType�� NULL_Q�ΰ��

			Node carNull = operand.car();

			if(carNull instanceof IdNode) {
				Node result_null = lookuptable(((IdNode)carNull).toString());
				if(result_null != null) operand = (ListNode)result_null;			
			}

			ListNode lnNullQ = (ListNode)runQuote(operand); // operand�� ���ڰ��� runQuote�Լ��� ȣ���Ͽ� Node������ ����Ʈ ��带 ��ȯ�޴´�. �̸� ListNode�� ����ȯ�� �ϰ� lnNullQ ListNode��ü�� �����Ͽ� ���⿡ �����Ѵ�.
			return (runExpr(lnNullQ) == ListNode.EMPTYLIST ) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE; 
			// lnNullQ�� ���ڰ����� �Ͽ� runExpr�Լ��� ȣ���ϰ� ���� ����� ListNode.EMPTYLIST���  BooleanNode.TRUE_NODE�� ��ȯ�ϰ�, �ƴ϶��  BooleanNode.FALSE_NODE�� ��ȯ�Ѵ�.

		case ATOM_Q: // operator.funcType�� ATOM_Q�ΰ�� //9

			Node Atomcar = operand.car();

			if( Atomcar instanceof IdNode) {

				Node result_atom = lookuptable(((IdNode)Atomcar).toString());	
				if(result_atom != null) operand = (ListNode)result_atom;	

			}
			Node nAtomQ = runQuote(operand); // operand�� ���ڰ����� �Ͽ� runQuote�Լ��� ȣ���Ͽ� Node�� ��ȯ�޴´�. �̸� nAtomQ Node��ü�� �����Ͽ� ���⿡ �����Ѵ�.
			if(nAtomQ instanceof ListNode) { //nAtomQ�� ListNode ��ü��� �󸮽�Ʈ����ϰ�� true�� ��ȯ�ؾ��ϱ� ������ if�� ���� �ڵ��� �����Ͽ� �̸� �Ǻ��Ѵ�.
				return (runExpr(nAtomQ) == ListNode.EMPTYLIST ) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE;	
				//runExpr(nAtomQ)�� �Ͽ� ���� ����� ListNode.EMPTYLIST���  null list�� atom ���� ��޵Ǿ�� �ϱ⶧���� BooleanNode.TRUE_NODE�� �����Ѵ�. �ƴ϶�� BooleanNode.FALSE_NODE�� �����Ѵ�.  
			}else{//nAtomQ�� ListNode��ü�� �ƴ϶�� �̴� ���Ҷ�� �ǹ��̴�.
				return BooleanNode.TRUE_NODE; //�׷��Ƿ� BooleanNode.TRUE_NODE�� �����Ѵ�.
			}

		case DEFINE: //4

			if(operand.cdr().car() instanceof FunctionNode) {
				insertTable( ((IdNode)operand.car()).toString() , operand.cdr().car());

			}else {

				if(operand.cdr().car() instanceof IdNode) {
					Node id_node = lookuptable(((IdNode)(operand.cdr().car())).toString());
					if (id_node == null) {
						insertTable( ((IdNode)operand.car()).toString() , runExpr(operand.cdr().car()) );
					}else {
						insertTable( ((IdNode)operand.car()).toString() ,id_node) ;
					}
					
				}else {
					insertTable( ((IdNode)operand.car()).toString() , runExpr(operand.cdr()) );
				}
			}
			return null;

		default: //���� ������ �ƴѰ��
			break;    //break�� �Ͽ� switch���� ���´�.
		}
		return null; //null�� �����Ѵ�. 
	} 

	private void insertTable(String id, Node value) {
		fromDefine.put(id, value);

	}

	private Node lookuptable (String id) {
		return fromDefine.get(id);
	}

	private Node stripList(ListNode node) {    
		if (node.car() instanceof ListNode && node.cdr() == ListNode.EMPTYLIST) {  
			Node listNode = node.car();        
			return listNode;       
		} else {
			return node;    
		}
	} 

	private Node runBinary(ListNode list) {  

		BinaryOpNode operator = (BinaryOpNode) list.car(); 
		Integer result; 

		ListNode ln = list.cdr(); 
		IntNode first_operand;
		IntNode second_operand;

		if(ln.car()  instanceof IdNode) {
			Node licar = ln.car();
			first_operand = (IntNode)lookuptable(((IdNode)licar).toString()); 
		}else {
			first_operand = (IntNode)runExpr(ln.car()); 			
		}

		if((ln.cdr()).car()  instanceof IdNode) {
			second_operand = (IntNode)lookuptable(((IdNode)(ln.cdr()).car()).toString());
		}else {			
			second_operand = (IntNode)runExpr((ln.cdr()).car()); 
		}



		//������������ �ʿ��� ���� �� �Լ� �۾� ����      
		switch (operator.binType) {  //operator.binType�� ���� �ٸ��� �۵��ǵ��� switch���� ����Ѵ�.

		case PLUS: //operator.binType�� PLUS�� ���
			result = first_operand.getValue() + second_operand.getValue(); // first_operand.getValue()�� second_operand.getValue()�� �Ͽ� ������ IntNode�� ����Ǿ��ִ� ������ ����(value��)�� �����´�. �׸���  �� ���� ���ϰ� result�� �����Ѵ�.
			return new IntNode(result.toString()); // Integer���� result�� String���� ����������ȯ�� �ϰ� �̸� value�� �ϴ� ���ο� IntNode ��ü�� �����ϰ� ��ȯ�Ѵ�.

		case MINUS: // operator.binType�� MINUS�� ���
			result = first_operand.getValue() - second_operand.getValue(); // first_operand.getValue()�� second_operand.getValue()�� �Ͽ� ������ IntNode�� ����Ǿ��ִ� ������ ����(value��)�� �����´�. �׸��� �� ���� ���� result�� �����Ѵ�.
			return new IntNode(result.toString()); // Integer���� result�� String���� ����������ȯ�� �ϰ� �̸� value�� �ϴ� ���ο� IntNode ��ü�� �����ϰ� ��ȯ�Ѵ�.

		case TIMES: // operator.binType�� TIMES�� ���
			result = first_operand.getValue() * second_operand.getValue(); // first_operand.getValue()�� second_operand.getValue()�� �Ͽ� ������ IntNode�� ����Ǿ��ִ� ������ ����(value��)�� �����´�. �׸��� �� ���� ���ϰ� result�� �����Ѵ�.
			return new IntNode(result.toString()); // Integer���� result�� String���� ����������ȯ�� �ϰ� �̸� value�� �ϴ� ���ο� IntNode ��ü�� �����ϰ� ��ȯ�Ѵ�.

		case DIV: // operator.binType�� DIV�� ���
			result = first_operand.getValue() / second_operand.getValue(); // first_operand.getValue()�� second_operand.getValue()�� �Ͽ� ������ IntNode�� ����Ǿ��ִ� ������ ����(value��)�� �����´�. �׸��� �� ���� ������ result�� �����Ѵ�.
			return new IntNode(result.toString()); // Integer���� result�� String���� ����������ȯ�� �ϰ� �̸� value�� �ϴ� ���ο� IntNode ��ü�� �����ϰ� ��ȯ�Ѵ�.

		case LT: // operator.binType�� LT�� ��� ; <
			return (first_operand.getValue() < second_operand.getValue() ) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE;

		case GT: // operator.binType�� GT�� ��� ; >
			return (first_operand.getValue() > second_operand.getValue() ) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE;

		case EQ: //operator.binType�� EQ�ΰ��
			return (first_operand.equals(second_operand)) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE; 



		default: //���� ������ �ƴѰ��
			break;    //break�� �Ͽ� switch���� ���´�.
		}
		return null; //null�� �����Ѵ�.

	} 

	private Node runQuote(ListNode node) {  
		return ((QuoteNode) node.car()).nodeInside(); 
	}
}