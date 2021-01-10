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
import lexer.TokenType;

public class CuteInterpreter {


	   private static Map<String, Node> fromDefine = new HashMap<String, Node>(); 
	   private static Map<String, Node> temp_table = new HashMap<String, Node>();//lambda �Լ��� ����� �� parameter ���� �ӽ÷� ���ε��Ǵµ�, �̶� �ӽ� ���ε��� ó���ϱ� ���� map�� �߰��ߴ�.

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
		      if (list.car() instanceof FunctionNode) {//list�� car�� FunctionNode�� ���,

		         if (((FunctionNode) list.car()).funcType == FunctionNode.FunctionType.LAMBDA) {//lambda�� ���, �� lambda �Լ��� ���� ���� ���
		            return runFunction((FunctionNode) list.car(), list);//runFunction�� ���� lambda�� �����Ű�µ�, �̶� stripList�� ���� �ʰ� list ��ü�� �Ѱ��ش�.
		         }
		         if (!list.cdr().equals(ListNode.EMPTYLIST)) {//�ڿ� ���ڰ� �ִ� ��쿡, list.cdr�� �� ListNode�� �ƴ� ��� runFunction�� ȣ���Ѵ�.
		            return runFunction((FunctionNode) list.car(), (ListNode) stripList(list.cdr()));
		         }
		      }
		      if (list.car() instanceof ListNode) {//list.car()�� ListNode�� ���
		         if (((ListNode) list.car()).car() instanceof FunctionNode) {//list.car().car()�� FunctionNode�̰� functionType�� lambda�� ���,
		            if (((FunctionNode) ((ListNode) list.car()).car()).funcType == FunctionNode.FunctionType.LAMBDA) {//�� ���� lambda �Լ� ���� �Բ� actual parameter���� �Է��� ����,
		               return runFunction((FunctionNode) ((ListNode) list.car()).car(), list);//actual parameter���� ó���ϱ� ���� list ��ü�� operand�� �ش�.
		            }
		         }
		      }
		      if (list.car() instanceof BinaryOpNode) {
		         return runBinary(list);
		      }
		      if (list.car() instanceof IdNode) { //�Լ��� �����Ͽ� idNode�� ������ �Ŀ� ������ �Լ��� ����� �� idNode�� actual parameter�� �Է��Ͽ� ȣ���Ѵ�.
		         //list.car�� IdNode�̰�, ���̺� ��Ī�Ǵ� �Լ��� �ִ� ���,
		         if (lookupTable(list.car().toString()) != null) {
		            return runList(ListNode.cons(lookupTable(list.car().toString()), list.cdr())); //��Ī�Ǵ� �Լ��� actual parameter�� ���� ListNode�� ��ȯ �Ŀ� runList�� ȣ���� ó���Ѵ�. 
		         }
		      }
		      return list;
		   }


		   private Node runFunction(FunctionNode operator, ListNode operand) {     

		      switch (operator.funcType) {  //operator.funcType�� ���� �ٸ��� �۵��ǵ��� switch���� ����Ѵ�. 

		      case CAR: // operator.funcType�� CAR�ΰ��
		         
		         Node returnNode = null; // ��ȯ �� ��带 ���� ����

		         if(operand.car() instanceof IdNode) { // ListNode operand�� ù ��° ��尡 IdNode�� ���
		            //operator CAR�� ��� ��ȯ�ؾ� �ϴ� ���� ù ��° ����, IdNode�� ���� ���� ������ ���, ��ȯ�ϴ� ���� �� ���̾�� �Ѵ�. 
		            // �̶�, ���� �Է��� ������� �����Ƿ�, ���� ���� ���� �ܼ� ID���� ���, null�� ��ȯ�ϰ� �ȴ�. car�� ����Ʈ �������̹Ƿ� ù��° ��尡 IdNode�� ��� �� IdNode�� �ݵ�� ListNode ���� ��� �־�� �Ѵ�.
		            
		            //lookupTable�� ù���� ������ idString ���� ���� ��ȯ��带 �ǿ�����, CAR�� �����ڷ� �Ͽ� runFunction �޼ҵ带 ȣ���� �� ���� �����Ѵ�.
		            return runFunction(operator, (ListNode)lookupTable(((IdNode)operand.car()).toString())); 
		         }
		         if(operand.car() instanceof QuoteNode) { //operand�� ������ ���� ����Ʈ�� ���ϸ�, operand�� ù��° ��尡 quote�� ��� �� ' �Է��� ���
		            //CAR�� ����Ʈ �����ڷ�, runQuote�� ��ȯ ���� ListNode�̾�� �Ѵ�.
		            returnNode = ((ListNode)runQuote(operand)).car(); //runQuote �޼ҵ带 ���� ����Ʈ �� QuoteNode ���� ���, ListNode�� �ް� �� ù ��° ��带 returnNode�� �����Ѵ�.
		            
		            if(!(returnNode instanceof IntNode) && !(returnNode instanceof BooleanNode)) { //��ȯ�� ��尡 IntNode �Ǵ�  BooleanNode�� �ƴ� ���,
		               //IntNode�� BooleanNode��  ������ �ʴ� ���� �����Ƿ� ��� ����Ͽ� QuoteNode�� ������ �ʴ´�.
		               QuoteNode quote = new QuoteNode(returnNode); //QuoteNode�� ���� ��, ����Ʈ�� ��� ��ȯ�Ѵ�.
		               returnNode = ListNode.cons(quote, ListNode.EMPTYLIST);// ListNode.cons�� ����Ͽ� QuoteNode�� ListNode.EMPTYLIST�� �̾� returnNode�� �Ѵ�.
		            }
		         }
		         if(operand.car() instanceof FunctionNode) { // �ǿ������� ù ��° ��尡 FunctionNode�� ���, �� �� ���� �Է��� �����Ƿ� FunctionNode�� ����Ʈ �������̾�� �Ѵ�.
		            return runFunction(operator, (ListNode)runExpr(operand)); // �ǿ����� ����Ʈ�� ���� runExpr�� ȣ���Ͽ� �޾ƿ� ���� �ٽ� �ǿ����ڷ� �ϰ�, CAR�� operator�� �Ͽ� runFunction�� ȣ���� ��ȯ�Ѵ�.
		         }
		         return returnNode; //returnNode�� ��ȯ�Ѵ�.
		         
		      case CDR: // operator.funcType�� CDR�ΰ��   

		         if(operand.car() instanceof IdNode) { //�ǿ������� ù ��° ��尡 IdNode�� ���, �� ���� ( cdr a ) �� ���� ���ڰ� �ϳ��� ���� ����.
		            //CDR�� QuoteNode�� ���� ��� ����Ʈ�� ���� �������̹Ƿ�, QuoteNode�� ���� ���� ����Ʈ ( cdr ( a 1 ) )�� ���� �Է¿� ���ؼ��� ������� �ʴ´�.
		            return runFunction(operator, (ListNode)lookupTable(((IdNode)operand.car()).toString()));
		         }
		         if(operand.car() instanceof QuoteNode) { // ����Ʈ�� ù��° ��尡 QuoteNode�� ���,
		            returnNode =((ListNode)runQuote(operand)).cdr(); //CDR�� ����Ʈ �������̹Ƿ� runQuote�� ���� �޾ƿ� QuoteNode ���γ��� ListNode���� �Ѵ�. ListNode�� cdr�� returnNode�� �����Ѵ�.
		            QuoteNode quote = new QuoteNode(((ListNode)runQuote(operand)).cdr()); // CDR�� ����Ʈ�� ��ȯ�ϹǷ� QuoteNode�� ���, ListNode.cons�� ���� QuoteNode�� ListNode�� ��� ��ȯ�Ѵ�.
		            return ListNode.cons(quote, ListNode.EMPTYLIST);
		         }
		         if(operand.car() instanceof FunctionNode) { //����Ʈ�� ù ��° ��尡 FunctionNode�� ���, �� �� ���� �Է��� �����Ƿ� FunctionNode�� ����Ʈ �������̾�� �Ѵ�.
		            return runFunction(operator, (ListNode)runExpr(operand));// �ǿ����� ����Ʈ�� ���� runExpr�� ȣ���Ͽ� �޾ƿ� ���� �ٽ� �ǿ����ڷ� �ϰ�, CDR�� operator�� �Ͽ� runFunction�� ȣ���� ��ȯ�Ѵ�.
		         }

		      case CONS: // operator.funcType�� CONS�ΰ��
		      
		         Node lncons1; //�Ѱ��� ����(head)�� �����������Ͽ� Node lncons1������ �����Ͽ���.
		         Node licarFirst = operand.car(); //operand.car()�� ������ licarFirst�� �����Ѵ�. 

		         if(licarFirst instanceof IdNode) { //licarFirst�� IdNode��ü�� ���licarFirst�� HashMap�� id������ ����Ǿ��ִ� ���ǵ� �����ϼ��� �ְ� ����Ǿ��������� id���ϼ����ִ�. if���� �����Ͽ� �̸� �ľ��Ѵ�.
		            licarFirst =  lookupTable(((IdNode)licarFirst).toString()); // ((IdNode)licarFirst).toString()�� ���ڰ����� �Ͽ� lookupTable�� ȣ���ϰ� ��ġ�Ǵ� ��尪�� ��ȯ�޾� licarFirst�� �����Ѵ�.
		            if(licarFirst == null) licarFirst = operand.car(); //���� licarFirst�� null�̶�� �̴� ���ǵ� ������ �ƴ϶�� �ǹ��̴�. �׷��Ƿ� �ٽ� operand.car()�� ������ licarFirst�� �����Ѵ�.
		         }

		         if(licarFirst instanceof ListNode) { //�Ѱ��� ���ҿ����ϴ� licarFirst�� ListNode��ü���
		         
		            if(((ListNode)licarFirst).car() instanceof QuoteNode) { // licarFirst.car()�� QuoteNode��ü���
		               lncons1 = runQuote((ListNode)licarFirst); // licarFirst�� ListNode�� ����ȯ�� ���� ���ڰ����� �Ͽ� runQuote�Լ��� ȣ���Ѵ�. �׸��� �� ��ȯ���� lncons1�� �����Ѵ�.
		            }else { //�ƴ϶��
		               lncons1 = runExpr(licarFirst); //licarFirst�� ���ڰ����� �Ͽ� runExpr�Լ��� ȣ���ϰ� ��ȯ���� lncons1�� �����Ѵ�.
		            }
		            
		         }else { //�ƴѰ�� licarFirst�� ListNode ��ü�� �ƴѰ�� �ϳ��� ���Ҷ�� �ǹ��̴�.
		            lncons1 = licarFirst; //licarFirst�� lncons1�� �����Ѵ�.
		         }

		         Node lncons2; //�Ѱ��� ����Ʈ(tail)�� �����������Ͽ� Node lncons2������ �����Ͽ���.
		         Node licarSecond = (operand.cdr()).car(); // (operand.cdr()).car()�� ������ licarSecond�� �����Ѵ�.

		         if(licarSecond instanceof IdNode) { // licarSecond�� IdNode��ü�� ���licarSecond�� HashMap�� id������ ����Ǿ��ִ� ���ǵ� �����ϼ��� �ְ� ����Ǿ��������� id���ϼ����ִ�. if���� �����Ͽ� �̸� �ľ��Ѵ�.
		            licarSecond = lookupTable(((IdNode)licarSecond).toString()); // ((IdNode)licarSecond).toString()�� ���ڰ����� �Ͽ� lookupTable�� ȣ���ϰ� ��ġ�Ǵ� ��尪�� ��ȯ�޾� licarSecond�� �����Ѵ�.
		            if(licarSecond == null) licarSecond = (operand.cdr()).car(); //���� licarSecond�� null�̶�� �̴� ���ǵ� ������ �ƴ϶�� �ǹ��̴�. �׷��Ƿ� �ٽ�(operand.cdr()).car()�� ������ licarSecond�� �����Ѵ�.
		         }

		         if(((ListNode)licarSecond).car() instanceof QuoteNode) { //// licarSecond.car()�� QuoteNode��ü���
		            lncons2 = runQuote((ListNode)licarSecond); //�Ѱ��� ����Ʈ(tail)�� �����������Ͽ�licarSecond�� ListNode�� ����ȯ�� ���� ���ڰ����� �Ͽ� runQuote�Լ��� ȣ���Ѵ�.�̸�  lncons2�� �����Ѵ�.
		         }else { //�ƴ϶��
		            lncons2 = runExpr(licarSecond); //licarSecond�� ���ڰ����� �Ͽ� runExpr�Լ��� ȣ���ϰ� ��ȯ���� lncons2�� �����Ѵ�.
		         }

		         ListNode result =  ListNode.cons(lncons1, (ListNode)lncons2); //lncons1�� lncons2�� �ٿ��� ���ο� ����Ʈ ��带 ��������Ͽ� ListNode.cons�� ����Ͽ�  ���(car)�� lncons1�� �ϰ�, ����(cdr)�� lncons2�� ListNode�� ����ȯ �Ͽ� �������. �̸� ListNode��ü result�� �����Ͽ���.
		      
		         return ListNode.cons(new QuoteNode(result), ListNode.EMPTYLIST); //result�� ���ڰ����� �Ͽ� ���ο� QuoteNode�� �����ϰ� �̸� ���(car)�� �ϰ� ListNode.EMPTYLIST�� ���� (cdr)�� ListNode�� �����ϰ� �̸� ��ȯ�Ѵ�.

		      case COND: // operator.funcType�� COND�ΰ��

		         Node only_one; // cond�� �ǿ����� ����Ʈ�� �ϳ��ۿ� ���� operand��ü�� �ǿ����� ù��° ����Ʈ�� ���� �ƴѰ�츦 �����Ͽ� �����ϱ����� �ʿ��� Node only_one�� �����Ѵ�.

		         if(operand.car() instanceof IdNode) { //operand.car()�� IdNode ��ü�� ���  operand.car()�� HashMap�� ��尪���� BooleanNode�� ��ġ�Ǿ��ִ� id������ ����Ǿ��ִ� ���ǵ� ���� �� ���̴�. 
		            only_one = lookupTable(((IdNode)(operand.car())).toString()); //((IdNode)(operand.car())).toString()�� ���ڷ� �Ͽ� lookupTable�� ȣ���ϰ� ��ȯ���� only_one�� �����Ѵ�.
		         }else { //�ƴ� ���
		            only_one = runExpr(operand.car()); //runExpr(operand.car())�� ��ȯ���� only_one�� �����Ѵ�.
		         }

		         if( only_one instanceof BooleanNode) { //only_one�� BooleanNode��ü���

		            if( only_one == BooleanNode.TRUE_NODE) { // only_one���� BooleanNode.TRUE_NODE���

		               Node one_cdr = operand.cdr().car(); //��ȯ�ؾ��ϴ� ���� �˾Ƴ������ؼ� operand.cdr().car()���� ������ Node one_cdr�� �����Ѵ�.

		               if( one_cdr instanceof IdNode) { //one_cdr�� IdNode ��ü�� ���  one_cdr�� HashMap�� ����Ǿ��ִ� ���ǵ� �������� �ƴ��� Ȯ���ؾ��Ѵ�. if���� �����Ͽ� �̸� �ľ��Ѵ�.
		                  
		                  one_cdr = lookupTable(((IdNode)one_cdr).toString()); // ((IdNode)one_cdr).toString()�� ���ڰ����� �Ͽ� lookupTable�� ȣ���ϰ� ��ġ�Ǵ� ��尪�� ��ȯ�޾� one_cdr�� �����Ѵ�.
		                  if(one_cdr == null) one_cdr = operand.cdr().car(); //���� one_cdr�� null�̶�� �̴� ���ǵ� ������ �ƴ϶�� �ǹ��̴�. �׷��Ƿ� �ٽ�(operand.cdr()).car()�� ������ one_cdr�� �����Ѵ�.

		               }

		               if(one_cdr instanceof FunctionNode) { //one_cdr�� FunctionNode��ü�ΰ�� ������ ����Ʈ ǥ�ð� �ȵǾ������Ƿ� �̸� id������ �����Ͽ� FunctionNode�� ��ȯ�Ѵ�.
		                  return one_cdr; //one_cdr�� ��ȯ�Ѵ�.
		               }else{ //�ƴ϶�� 
		                  return runExpr(one_cdr); // one_cdr�� ���ڷ� �Ͽ� runExpr�Լ��� ȣ���ϰ� ��ȯ���� ��ȯ�Ѵ�.
		               }

		            }else {
		               return BooleanNode.FALSE_NODE; //�̴� �ǿ����� ����Ʈ�� �ϳ��ۿ� ���� ����̱� ������ ���ǹ��� ���ΰ��� ���� ��� BooleanNode.FALSE_NODE�� �����Ѵ�.
		            }
		            
		         }else { //only_one�� BooleanNode��ü�� �ƴҰ�� �̴� ����Ʈ����� ����̴�.

		            ListNode ifN ; //����Ʈ��� ���� ù��° ����Ʈ��带 �����������Ͽ� ListNode��ü ifN�� �����Ѵ�.

		            if(operand.car() instanceof IdNode) { //operand.car()�� IdNode ��ü�� ���   HashMap���� ListNode�� ��尪�� ��ġ�Ǵ� ���ǵ� ������� �ǹ��̴�.
		               ifN = (ListNode) lookupTable(((IdNode)(operand.car())).toString()); //((IdNode)(operand.car())).toString()�� ���ڰ����� �Ͽ� lookupTable�� ȣ���ϰ� ��ġ�Ǵ� ��尪�� ��ȯ�޾� ifN�� �����Ѵ�.
		            }else {
		               ifN = (ListNode)operand.car(); // operand.car()�� ����ϰ� �̸� ListNode�� ����ȯ�� �ѵڿ� ifN�� �����Ѵ�.
		            }

		            Node T_F = ifN.car(); //ifN.car()���� ������ Node T_F�� �����Ѵ�.
		            Node returnValue = ifN.cdr(); //ifN.cdr()���� ������ Node returnValue�� �����Ѵ�.

		            if(T_F instanceof IdNode) { //T_F�� IdNode�� ��� HashMap�� ��尪���� BooleanNode�� ��ġ�Ǿ��ִ� id������ ����Ǿ��ִ� ���ǵ� ���� �� ���̴�.
		               T_F = lookupTable(((IdNode)ifN.car()).toString());    //((IdNode)ifN.car()).toString()�� ���ڰ����� �Ͽ� lookupTable�� ȣ���ϰ� ��ġ�Ǵ� ��尪�� ��ȯ�޾� T_F�� �����Ѵ�.      
		            }

		            if(runExpr(T_F) == BooleanNode.TRUE_NODE) { //���ǽ��� �������� �ľ��ϱ����Ͽ� T_F�� ���ڰ����� �Ͽ� runExpr()�� ȣ���ϰ� ��ȯ���� ���� BooleanNode.TRUE_NODE��� ���ǽ��� ���̶�� �ǹ��̹Ƿ� if������ �ڵ带 �����Ѵ�.

		               if(((ListNode)returnValue).car() instanceof IdNode) {//((ListNode)returnValue).car()�� IdNode��ü�� ���  HashMap�� ����Ǿ��ִ� ���ǵ� �������� �ƴ��� Ȯ���ؾ��Ѵ�. if���� �����Ͽ� �̸� �ľ��Ѵ�.
		                  Node result_return = lookupTable(((IdNode)(((ListNode)returnValue).car())).toString());   
		                  // ((IdNode)(((ListNode)returnValue).car())).toString()�� ���ڰ����� �Ͽ� lookupTable�� ȣ���ϰ� ��ġ�Ǵ� ��尪�� ��ȯ�޾� Node result_return�� �����Ѵ�.
		                  if(result_return == null) result_return = ((ListNode)returnValue).car(); //
		                  // result_return�� null�̶�� �̴� ���ǵ� ������ �ƴ϶�� �ǹ��̴�. �׷��Ƿ� ((ListNode)returnValue).car()�� ������ result_return�� �����Ѵ�.

		                  return runExpr(result_return); //runExpr(result_return)�� ȣ���ϰ� ��ȯ�Ȱ��� ��ȯ�Ѵ�.

		               }else { //�ƴѰ��
		                  return runExpr(((ListNode)returnValue).car()); // ((ListNode)returnValue).car()�� ���ڷ� �Ͽ� runExpr�Լ��� ȣ���ϰ� ��ȯ���� ��ȯ�Ѵ�.
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
		         
		         Node NotN = operand.car(); // operand.car()�� ������ Node NotN ������ �����Ͽ� �����Ѵ�.

		         if(NotN instanceof IdNode) { //���� NotN��  IdNode��ü��� NotN�� HashMap�� ��尪���� BooleanNode�� ��ġ�Ǿ��ִ� id������ ����Ǿ��ִ� ���ǵ� ���� �� ���̴�.
		            NotN = lookupTable(((IdNode)NotN).toString());// ((IdNode)NtoN).toString()�� ���ڰ����� �Ͽ� lookupTable�� ȣ���ϰ� ��ġ�Ǵ� ��尪�� ��ȯ�ް� NotN�� �����Ѵ�.   
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

		      case NULL_Q: // operator.funcType�� NULL_Q�ΰ��
		         
		         Node carNull = operand.car(); // operand.car()���� ������ �̸� Node carNull�� �����Ѵ�.

		         if(carNull instanceof IdNode) { //carNull�� IdNode��ü��� carNull�� HashMap�� id������ ����Ǿ��ִ� ���ǵ� �����ϼ��� �ְ� ����Ǿ��������� id���ϼ����ִ�. if���� �����Ͽ� �̸� �ľ��Ѵ�.
		            Node result_null = lookupTable(((IdNode)carNull).toString()); // ((IdNode)carNull).toString()�� ���ڰ����� �Ͽ� lookupTable�� ȣ���ϰ� ��ġ�Ǵ� ��尪�� ��ȯ�޾� Node result_null�� �����Ѵ�.
		            if(result_null != null) operand = (ListNode)result_null; //   ���� result_null�� null�� �ƴ϶�� �̴� ���ǵ� ������� �ǹ��̴�. �׷��Ƿ� operand�� result_null�� ListNode�� ����ȯ���� �����Ѵ�.
		         }
		         
		         else if(carNull instanceof FunctionNode) {//carNull�� FunctionNode ��ü�� ���, 
		            operand = (ListNode) runExpr(operand);//operand�� ���� runExpr�� ȣ���Ͽ� ��� ��带 operand�� �Ѵ� .
		         }
		         
		         ListNode lnNullQ = (ListNode)runQuote(operand); // operand�� ���ڰ��� runQuote�Լ��� ȣ���Ͽ� Node������ ����Ʈ ��带 ��ȯ�޴´�. �̸� ListNode�� ����ȯ�� �ϰ� lnNullQ ListNode��ü�� �����Ͽ� ���⿡ �����Ѵ�.

		         return (runExpr(lnNullQ) == ListNode.EMPTYLIST ) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE; 
		         // lnNullQ�� ���ڰ����� �Ͽ� runExpr�Լ��� ȣ���ϰ� ���� ����� ListNode.EMPTYLIST���  BooleanNode.TRUE_NODE�� ��ȯ�ϰ�, �ƴ϶��  BooleanNode.FALSE_NODE�� ��ȯ�Ѵ�.

		      case EQ_Q: // operator.funcType�� EQ_Q�ΰ��
		         
		         Node node_1=null; // ���� ù ��° ��带 ���� ���� ����
		         Node node_2=null; // ���� �� ��° ��带 ���� ���� ����
		         //IdNode�� ���
		         if(operand.car() instanceof IdNode) { // ù ��° ��尡 IdNode�� ���, 
		            node_1 = lookupTable(((IdNode)operand.car()).toString()); // lookupTable �޼ҵ带 ���� ù ��° ������ idString�� ���� ��ȯ ��带 �޾� �� ���� node_1�� �Ѵ�. 
		            if(node_1 == null) node_1 = operand.car(); // ��ȯ ��尡 null�̸� �ɹ� ���̺� ù ��° ������ idString�� ���� ���� ���� ������, IdNode �״��  node_1�� �Ѵ�.

		            if(node_1 instanceof ListNode) {//lookupTable�� ���� �ҷ��� ���� ListNode�� ���, lookupTable���� null�� ������ ��� node_1�� IdNode�̹Ƿ� else���� �ش��Ѵ�.
		               if(((ListNode)node_1).car() instanceof QuoteNode ) node_1 = runQuote((ListNode)node_1); //ListNode �ȿ� QuoteNode�� �ִ� ���, runQuote �޼ҵ带 ���� ���� ��带 node_1�� �Ѵ�.
		               //���� �����ϴ� IdNode�� ���, IntNode, BooleanNodeó�� ��� ���� �����ų� QuoteNode�� ���� ListNode�� ���� ������ ���� ������ �����ϹǷ� QuoteNode�� ���� ���� ListNode�� ���ؼ��� ������� �ʴ´�.
		               
		               
		               //operand.cdr�� ListNode�� ù ��° ��带 ������ ������ ��带 ����Ʈ�� ��� ��ȯ�ϹǷ�, operand.cdr().car()�� �� ��° ���� �Ѵ�.
		               //�� �� ���� �Է��� ������� �ʱ� ���� ���� ���� 3�� �̻� ���� ��츦 ó������ �ʾҴ�.
		               if(!(operand.cdr().car() instanceof ListNode)) { // ù ��° ��尡 ListNode�ȿ�  QuoteNode��  �� ' �Է� �� ��, �� ��° ��尡 ListNode�� �ƴ� ���,

		                  if(operand.cdr().car() instanceof IdNode) {// �� ��° ��尡 IdNode�� ���,
		                     node_2 = lookupTable(((IdNode)operand.cdr().car()).toString()); //lookupTable�� ���� �� ��° ��忡 ����� ���� �ҷ��´�.
		                     if(node_2 == null) node_2 = operand.cdr().car(); //�ɹ����̺� �� ��° ��忡 ���� ����� ���� ���� ���, IdNode�� ���� ��� node_2�� �Ѵ�.
		                     
		                     if(node_2 instanceof ListNode) {// lookupTable�� ���� �ҷ��� ���� ListNode�� ���, 
		                        if(((ListNode)node_2).car() instanceof QuoteNode ) node_2 = runQuote((ListNode)node_2);
		                        //������ ���, ������ ���� ������ �����ϹǷ� QuoteNode�� ���� ���� ListNode�� ������ �ش��Ѵ�.         
		                     }
		                  }else node_2 = operand.cdr().car();// ù��° ��尡 ' �Է��̰�, �ι�° ��尡 IdNode�� ListNode�� �ƴ� ���, IntNode�� BooleanNode, �Ǵ� �ǿ����� ���� �����ڸ� �Էµ� ����̴�. 
		               }
		               else {// �� ��° ��尡 ListNode�� ���, 
		                  // �� ���, ������ �ؾ� �ϴ� ����Ʈ�� ������ ���� �ʴ� QuoteNode�� ���� ����Ʈ�� ���� ó���Ѵ�.
		                  // QuoteNode�� ���� ���, ���� ��� node_2�� runQuote�� ���� QuoteNode ���� ��带 node_2�� �Ѵ�.
		                  if(((ListNode)((ListNode)operand.cdr()).car()).car() instanceof QuoteNode) node_2 = runQuote((ListNode)operand.cdr().car());
		                  //QuoteNode�� ���� ���� ���, �� ��° ��忡 ���� runExpr�� ȣ���Ͽ� node_2�� �Ѵ�.
		                  else node_2 = runExpr(operand.cdr().car());
		               }
		            }else {//lookupTable�� ���� �ҷ��� ���� ListNode�� �ƴ� ���, �� IntNode�� BooleanNode ó�� ��� ���� ���,
		               
		               if(operand.cdr().car() instanceof IdNode) { //�� ��° ��尡 IdNode�� ���,
		                  node_2 = lookupTable(((IdNode)operand.cdr().car()).toString());// lookupTable�� ���� �� ���� ��忡 ���� ����� ���� �ҷ��´�.
		                  if(node_2 == null) node_2 = operand.cdr().car();// ����� ���� ���� ���, IdNode �״�� node_2�� �����Ѵ�.
		                  
		                  if(node_2 instanceof ListNode) {//lookupTable���� �ҷ��� ���� ListNode�� ���,
		                     node_2 = runQuote((ListNode)node_2); //������ ���� ������ �� ������ ���� ���·� ����ǹǷ�, ����� ���� ����Ʈ�� ���, �׻� QuoteNode�� �پ��ִ�.
		                  }
		               }else if(operand.cdr().car() instanceof ListNode) { //�� ��° ��尡 ListNode�� ���,
		                  //������ �ؾ� �ϴ� ����Ʈ�� ������ ���� �ʴ� QuoteNode�� ���� ����Ʈ�� ���� ó���Ѵ�.
		                  //QuoteNode�� ���� ���, runQuote �޼ҵ带 ���� QuoteNode ���� ��带 node_2�� �Ѵ�.
		                  if(((ListNode)((ListNode)operand.cdr()).car()).car() instanceof QuoteNode) node_2 = runQuote((ListNode)operand.cdr().car());
		                  else {//QuoteNode�� ���� ���� ���, ������ ������ �� ��� ���� �޾ƿ;� �ϹǷ� �� ��° ��忡 ���� runExpr�� ȣ���Ͽ� node_2�� �Ѵ�.
		                     node_2 = runExpr(operand.cdr().car());
		                  }
		               }
		               //���� �Է��� ������� �����Ƿ� ����Ʈ�� ��� ���� ���� �ǿ����ڰ� �ִ� FunctionNode�� BinaryOpNode�� ��츦 ó������ �ʾҴ�.
		               else node_2 = operand.cdr().car(); //�ι�° ��尡 IdNode, ListNode�� �ƴ� ���, �� IntNode�� BooleanNode�� ���,�Ǵ� �ǿ����� ���� �����ڸ� �Էµ� ����̴�.
		            }            
		         //ù ��° ��尡 ListNode�� ���,
		         }else if(operand.car() instanceof ListNode) {

		            //QuoteNode�� ���� ListNode�� ���, runQuote�� ���� QuoteNode ���� ��带 node_1�� �Ѵ�.
		            if(((ListNode)operand.car()).car() instanceof QuoteNode) node_1 = runQuote((ListNode)operand.car());
		            else node_1 = runExpr(operand.car()); // QuoteNode�� ���� ���� ���, runExpr�� ȣ���Ͽ� ������ �ϰ� �� ��� ���� node_1�� �Ѵ�.

		            if(node_1 instanceof IdNode) { //QuoteNode ���� ��� �Ǵ� runExpr�� ��ȯ ��尡 IdNode�� ���,
		               //EQ_Q�� operator�� �ϰ�, ���� operand�� ù ��° ��� ��� ���Ӱ� ���� node_1�� operand�� ù ��° ���� �� ��, runFunction�� ȣ���Ͽ� ��ȯ ���� �����Ѵ�. 
		               return runFunction(operator, ListNode.cons(node_1, operand.cdr()));
		            }
		            
		            if(!(operand.cdr().car() instanceof ListNode)) {//operand�� �� ��° ��尡 ListNode�� �ƴ� ���,
		               if(operand.cdr().car() instanceof IdNode) {//operand�� �� ��° ��尡 IdNode�� ���
		                  node_2 = lookupTable(((IdNode)operand.cdr().car()).toString()); //lookupTable �޼ҵ带 ���� �� ��° ��忡 ����� ���� �ҷ��´�.
		                  if(node_2 == null) node_2 = operand.cdr().car();// ����� ���� ���� ��� IdNode �״�� node_2�� �Ѵ�.
		                  
		                  if(node_2 instanceof ListNode) {//lookupTable�� ��ȯ ����� ListNode�� ���, �տ���ó�� QuoteNode�� ���� ���� ����Ʈ�� ���������� ó������ �ʴ´�.
		                     node_2 = runQuote((ListNode)node_2); //runQuote�޼ҵ带 ���� ���γ�带 �޾� node_2�� �Ѵ�.
		                  }
		               }else node_2 = operand.cdr().car(); //IdNode, ListNode�� �ƴ� ���, �ٷ� node_2�� �����Ѵ�.
		            }
		            else {//�� ��° ��尡 ListNode�� ���,
		               //QuoteNode�� ���� ListNode�� ���, runQuote�� ���� QuoteNode ���� ��带 node_2�� �Ѵ�.
		               if(((ListNode)((ListNode)operand.cdr()).car()).car() instanceof QuoteNode) node_2 = runQuote((ListNode)operand.cdr().car());
		               // QuoteNode�� ���� ���� ���, runExpr�� ȣ���Ͽ� ������ �ϰ� �� ��� ���� node_2�� �Ѵ�.
		               else node_2 = runExpr(operand.cdr().car());
		            }
		         }
		         else {//ù ��° ��尡 IntNode �Ǵ� BooleanNode�� ���, (���� �Է��� ������� �����Ƿ� ����Ʈ�� ��� ���� ���� FunctionNode�� BinaryOpNode�� ������� �ʴ´�.) 
		            node_1 = operand.car();// ù ��° ��带 �ٷ� node_1�� �����Ѵ�
		            if(!(operand.cdr().car() instanceof ListNode)) { // �� ��° ��尡 ListNode�� �ƴ� ���,
		               if(operand.cdr().car() instanceof IdNode) { // �� ��° ��尡 IdNode�� ���,
		                  node_2 = lookupTable(((IdNode)operand.cdr().car()).toString()); //lookupTable�� ���� �� ��° ��忡 ����� ���� �ҷ��´�.
		                  if(node_2 == null) node_2 = operand.cdr().car();// ����� ���� ���� ��� IdNode �״�� node_2�� �����Ѵ�
		                  if(node_2 instanceof ListNode) node_2 = runQuote((ListNode)operand.cdr().car()); //lookupTable���� �ҷ��� ���� ListNode�� ���, QuoteNode�� ���� ������ runQuote�� ���� ���γ�带 node_2�� �Ѵ�.
		               }else node_2 = operand.cdr().car(); //�� ��° ��尡 IntNode �Ǵ� BooleanNode�� ���,
		            }else {//�� ��° ��尡 ListNode�� ���, 
		               //QuoteNode�� ���� ListNode�� ���, runQuote�� ���� QuoteNode ���� ��带 node_2�� �Ѵ�.
		               if(((ListNode)((ListNode)operand.cdr()).car()).car() instanceof QuoteNode) node_2 = runQuote((ListNode)operand.cdr().car());
		               // QuoteNode�� ���� ���� ���, runExpr�� ȣ���Ͽ� ������ �ϰ� �� ��� ���� node_2�� �Ѵ�.
		               else node_2 = runExpr(operand.cdr().car());
		            }
		         }
		         
		         if(node_1.toString().equals(node_2.toString())) return BooleanNode.TRUE_NODE; //������ TRUE_NODE�� ��ȯ�Ѵ�.
		         else return BooleanNode.FALSE_NODE; //�ٸ� ����� ���� ��� FALSE_NODE�� �����Ѵ�.
		   
		      case ATOM_Q: // operator.funcType�� ATOM_Q�ΰ�� 

		         //atom?�� ����Ʈ�� �ƴ� ���鿡 ���� True���� ��ȯ�ϴ� ������ �����Ѵ�.
		         if(operand.car() instanceof QuoteNode) { //ù ��° ��尡 QuoteNode�� ���, 
		            Node atomNode = runQuote((ListNode)operand);//�ǿ����ڿ� ���� runQuote�� ȣ���Ͽ� QuoteNode ���� ��带 �޾ƿ�  atomNode�� �����Ѵ�.
		            if(atomNode instanceof ListNode) {//atomNode�� ListNode�� ���, EMPTY�� ��츦 ������ �������� false���� ���� BooleanNode�� ��ȯ�Ѵ�.
		               if(((ListNode)atomNode).equals(ListNode.EMPTYLIST)) //EMPTY�� �ƴ� ����Ʈ�� ���� ��� true���� ���� BooleanNode�� ��ȯ�Ѵ�.
		                  return BooleanNode.TRUE_NODE;
		               return BooleanNode.FALSE_NODE; //QuoteNode ���� ��尡 ListNode �϶�, EMPTY�� �ƴ� ��� FALSE_NODE�� �����Ѵ�.
		            }else {//ListNode�� �ƴ� ��� true���� ���� BooleanNode�� ��ȯ�Ѵ�.
		               return BooleanNode.TRUE_NODE;
		            }
		         }
		         break;

		      case DEFINE:  // operator.funcType�� DEFINE�ΰ��
		         
		         //form 2 
		         //lambda ��ȣ ���� �Լ� define�� �����ϱ� ���� �߰��� �κ��̴�. ���� 13-2�� ������ form-2�� �ش��ϴ� �Է��� ó���Ѵ�.
		         if(operand.car() instanceof ListNode) {//operand.car�� ListNode�� ���, �� ListNode �ȿ��� �Լ��� �̸��� formal parameter�� �ִ�.
		            if(((ListNode)operand.car()).car() instanceof IdNode) {//operand.car().car()�� �Լ��� �̸��̴�.
		               operator.setValue(TokenType.LAMBDA);//�̸� ���� 13-2�� ������ form-1 �������� �ٲٱ� ���� operator�� lambda�� �ٲٰ� �Ʒ��� ó���Ѵ�.
		               //lambda �����ڿ� formal parameter, �� ���� block�� body�� ���� lambda �Լ� ������ �ǹ��ϴ� ListNode�� ��ȯ�Ѵ�. �̸� �Լ� �̸��� �Բ� ���̺� �����Ѵ�.
		               insertTable(((ListNode)operand.car()).car().toString(), ListNode.cons(operator, ListNode.cons(((ListNode)operand.car()).cdr(), ListNode.cons((ListNode)operand.cdr().car(), ListNode.EMPTYLIST))));
		               break;
		            }
		         }
		         if(operand.cdr().car() instanceof FunctionNode) { //operand.cdr().car()�� FunctionNode��ü��� �̴� runFunction�� ���� ����� �ѵڿ� HashMap�� ���� �����ؾ��Ѵ�.

		            Node funtion_result = runExpr(operand.cdr()); // runExpr(operand.cdr())�� �Ͽ� ���� ����Ǿ���ϴ� Node function_result�� �����Ѵ�.
		            insertTable( ((IdNode)operand.car()).toString() , funtion_result); //insertTable�� ���ڰ����� ((IdNode)operand.car()).toString()�� funtion_result�� �־� ���� id�� value�� ���� ��ġ�ǰ� HashMap�� �����Ѵ�.

		         }else if(operand.cdr().car() instanceof IdNode) { //operand.cdr().car()�� IdNode��ü��� operand.cdr().car()��  HashMap�� id������ ����Ǿ��ִ� ���ǵ� �����ϼ��� �ְ� ����Ǿ��������� id���ϼ����ִ�. else if���� �����Ͽ� �̸� �ľ��Ѵ�.

		            Node id_node = lookupTable(((IdNode)(operand.cdr().car())).toString()); //((IdNode)(operand.cdr().car())).toString()�� ���ڰ����� �Ͽ� lookupTable�� ȣ���Ͽ� ��ȯ���� Node id_node�� �����Ѵ�.

		            if (id_node == null) { //id_node�� null�� ��� operand.cdr().car()�� HashMap�� ����� ���ǵ� ���� id���� �ƴϰ� �Ϲ� ���ǵ������� id���̶�� �ǹ��̴�. �׷��Ƿ� ������ operand.cdr().car()�� ���� �����Ѵ�.
		               insertTable( ((IdNode)operand.car()).toString() , runExpr(operand.cdr().car()) ); //insertTable�� ���ڰ����� ((IdNode)operand.car()).toString()�� runExpr(operand.cdr().car())�� �־� ���� id�� value�� ���� ��ġ�ǰ� HashMap�� �����Ѵ�.
		            }else { //�ƴҰ�� operand.cdr().car()��  HashMap�� ����� ���ǵ� ���� id���̰� �׿� ��ġ�Ǵ� ��尡 id_node�� ��ȯ�Ǿ��ٴ� �ǹ��̴�. 
		               insertTable( ((IdNode)operand.car()).toString() ,id_node) ; //�׷��Ƿ� insertTable�� ���ڰ����� ((IdNode)operand.car()).toString()�� id_node�� �־� ���� id�� value�� ���� ��ġ�ǰ� HashMap�� �����Ѵ�.
		            }

		            //form 1
		         }else if(operand.cdr().car() instanceof ListNode){ // define ���꿡���� operand�� car�� IdNode, �� �ڿ��� IdNode�� ������ ���� ���µ�, �� ���� ListNode�� ���
		            ListNode lss = (ListNode)operand.cdr().car();//IdNode�� ������ ���̴�.
		            if(lss.car() instanceof FunctionNode) {//lss�� car�� lambda�� ���, lss�� lambda �Լ� ���� �κ��� ListNode�̸� ���� 13-2�� ������ form-1 �ش��ϴ� �Է��� ó���Ѵ�. 
		               if(((FunctionNode)lss.car()).funcType == FunctionNode.FunctionType.LAMBDA) {
		                  insertTable(operand.car().toString(), lss); //ListNode ���� �����ڰ� LAMBDA�� ��� lss�� ��Ī��ų �Լ� �̸��� �Բ� ���̺� �����Ѵ�
		               }
		            }else {//FunctionNode�� �ƴ� ���, ���� �����ؾ��ϴ� ����Ʈ�̰ų� �������Ʈ �̹Ƿ� runExpr�� ȣ���Ͽ� idNode�� toString�� ��Ī��Ų��.
		               insertTable(((IdNode)operand.car()).toString() , runExpr(operand.cdr().car()) );
		            }
		         }else {//���� �� ��찡 �ƴҰ�� 
		            insertTable( ((IdNode)operand.car()).toString() , runExpr(operand.cdr().car()) );  //insertTable�� ���ڰ����� ((IdNode)operand.car()).toString()�� runExpr(operand.cdr().car())�� �־� ���� id�� value�� ���� ��ġ�ǰ� HashMap�� �����Ѵ�.
		         }
		         break;
		      case LAMBDA://operator.funcType�� LAMBDA�ΰ��

		         ListNode parameter = null; //formal parameter ����Ʈ�� �����Ѵ�.
		         ListNode body = null;//body ����Ʈ�� �����Ѵ�

		         if (operand.car() instanceof FunctionNode) { //operand�� car�� functionNode�� ���� �Ű����� �Է� ���� �Լ� ���� �ִ� ����, �� ���� ������ �������� �ʰ� list�� ��°�� ��ȯ�Ѵ�.
		            if (((FunctionNode) operand.car()).funcType == FunctionNode.FunctionType.LAMBDA) {
		               return operand; // �Ű������� ���� list�� ��ȯ�Ѵ�.
		            }
		         }
		         if (operand.car() instanceof ListNode) { //operand�� car�� LIstNode�� ���, car�� �Լ� �����̰� cdr���� �Լ��� actual parameter�� �־� ������ �����Ѵ�.

		            if (operand.cdr().equals(ListNode.EMPTYLIST)) {//operand�� cdr�� �� ListNode�� ���, �� ���� ��ȣ �ȿ� �Լ� ���� �ְ� actual parameter�� ����  ���� list�� ��°�� ��ȯ�Ѵ�. 
		               return operand;//list�� ��°�� ��ȯ�Ѵ�.
		            }

		            if (((ListNode) operand.car()).cdr().car() instanceof ListNode) { // operand�� car�� �Լ� ���� ListNode�̰�, operand�� car�� cdr�� lambda �����ڸ� ������ ������ ������ List�̴�. 
		               //operand.car().cdr().car()�� formal parameter�� �ǹ��Ѵ�. ��  ��, operand.car().cdr().cdr()�� block�� body�� �̷������
		               parameter = (ListNode) ((ListNode) operand.car()).cdr().car();
		            }
		            //block�� body�� �̷���� ListNode�� executeBlock �޼ҵ忡 �Ű������� �־� block�� �����Ű�� body�� ��ȯ�޴´�.
		            body = executeBlock(((ListNode) operand.car()).cdr().cdr());
		         }

		         //formal parameter�� operand.cdr �� actual parameter�� ���ε��Ѵ�.
		         parameterBinding(parameter, operand.cdr());
		         ListNode readedBody = readBody(body); // readBody�� ȣ���Ͽ� body�� actual parameter�� ���Խ��� ��ȯ�޴´�.
		         
		         Node endNode = runList(readedBody);//actual parameter�� ���� �Ϸ��� body�� runList�� ���� �����Ų �� ��� ��带 endNode�� �����Ѵ�.
		         
		         deleteTable(parameter);//deleteTalbe �޼ҵ带 ���� �ӽ� ���̺� ����� formal parameter�� ���� actual parameter�� ���ε��� �����Ѵ�.
		         return endNode;//endNode�� �����Ѵ�.

		      default: // ���� ������ �ƴѰ��
		         break; // break�� �Ͽ� switch���� ���´�.
		      }
		      return null; // null�� �����Ѵ�.
		   }

		   private void insertTable(String id, Node value) { // insertTable�޼ҵ�� �Ű������� ���� id�� value�� HashMap�� ���� �����ϴ� �Լ��̴�.
		      fromDefine.put(id, value); //fromDefine.put�� ����Ͽ� HashMap�� id�� value�� �����Ͽ� ��ġ��Ų��.

		   }

		   private Node lookupTable (String id) { // lookupTable�޼ҵ�� �Ű������� id ���� �޾� �̸� HashMap���� ��ġ���� �˸��� ��带 ��ȯ�Ѵ�.
		      return fromDefine.get(id); // fromDefine.get�� id�� ���ڰ����� �Ͽ� ȣ���ѵ� HashMap���� ��ġ���� �˸��� ��带 ��ȯ�ϸ� �̸� ��ȯ�Ѵ�.
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

		      BinaryOpNode operator = (BinaryOpNode) list.car(); //�����ڸ� operator�� �����Ѵ�
		      Integer result; //���� ������� �����ϴ� ����

		      ListNode ln = list.cdr(); //�ǿ����ڸ� ln�� �����Ѵ�.
		      IntNode first_operand; //�� �����ڸ� 2�� ������, �� �� ù��° �� �����ڸ� �����ϴ� ������.
		      IntNode second_operand;//�ι�° �ǿ����ڸ� �����ϴ� ����

		      if(ln.car()  instanceof IdNode) { //�ǿ������� ù ��° ��尡 IdNode�� ���, 
		         Node licar = ln.car();// �� �������� ù��° ��带 licar�� ������ ��, 
		         first_operand = (IntNode)lookupTable(((IdNode)licar).toString()); //lookupTable �޼ҵ带 ���� ���� licar�� ����� ���� �ҷ��� first_operand�� �����Ѵ�. 
		      }else {// �ǿ������� ù��° ��尡 IdNode�� �ƴ� ���
		         //�� �� �����Է��� ���ٰ� �����Ͽ����Ƿ�, ù��° ��忡 ���� runExpr�� ȣ������ �� ��ȯ ���� IntNode���� �Ѵ�.
		         first_operand = (IntNode)runExpr(ln.car()); //�ǿ������� ù��° ��忡 ���� runExpr�� ȣ���Ͽ� ��ȯ���� first_operand�� �����Ѵ�.          
		      }

		      if((ln.cdr()).car()  instanceof IdNode) { //�� �������� �ι�° ��尡 IdNode�� ���,
		         second_operand = (IntNode)lookupTable(((IdNode)(ln.cdr()).car()).toString()); // ù��° ���� ���������� lookupTable���� �ι�° ��忡 ����� ���� �ҷ��� second_operand�� �����Ѵ�.
		      }else {         
		         //�� �� �����Է��� ���ٰ� �����Ͽ����Ƿ�, �ι�° ��忡 ���� runExpr�� ȣ������ �� ��ȯ ���� IntNode���� �Ѵ�.
		         second_operand = (IntNode)runExpr((ln.cdr()).car()); //�ǿ������� �� ��° ��忡 ���� runExpr�� ȣ���Ͽ� ��ȯ���� second_operand�� �����Ѵ�.
		      }

		      
		      switch (operator.binType) {  
		      case PLUS: 
		         result = first_operand.getValue() + second_operand.getValue();  
		         return new IntNode(result.toString()); 
		      case MINUS: 
		         result = first_operand.getValue() - second_operand.getValue(); 
		         return new IntNode(result.toString()); 

		      case TIMES: 
		         result = first_operand.getValue() * second_operand.getValue(); 
		         return new IntNode(result.toString()); 
		      case DIV: 
		         result = first_operand.getValue() / second_operand.getValue(); 
		         return new IntNode(result.toString()); 

		      case LT: 
		         return (first_operand.getValue() < second_operand.getValue() ) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE;

		      case GT: 
		         return (first_operand.getValue() > second_operand.getValue() ) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE;

		      case EQ: 
		         return (first_operand.equals(second_operand)) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE; 

		      default: 
		         break;
		      }
		      return null; 
		   } 

		   private Node runQuote(ListNode node) {  
		      return ((QuoteNode) node.car()).nodeInside(); 
		   }
		   // lambda �Լ��� ���, formal parameter�� actual parameter�� �ӽ÷� ���ε� ���״ٰ� �Լ� ������ ������ �����ϵ��� �Ѵ�.
		   // deleteTable�� �ӽ� ���ε��� �����ϴ� �޼ҵ��̴�.
		   private void deleteTable(ListNode id) { //parameter���� ��� �ִ� ListNode�� �Ű������� �Է� �޴´�.
		      if(id.equals(ListNode.EMPTYLIST)) return;//parameter�� ��� Ž���Ͽ� �� ����Ʈ�� ������ �����Ͽ� �����Ѵ�.
		      
		      temp_table.remove(id.car().toString());//parameter�� toString() ���� ���ε��� ��带 �ӽ� ���ε� ���̺� temp_table���� �����Ѵ�.
		      //��͸� ����Ͽ� parameter����Ʈ�� ��� Ž���Ѵ�.
		      deleteTable(id.cdr());
		   }
		   //formal parameter�� actual parameter�� ���ε��ϴ� �޼ҵ��̴�.
		   private void parameterBinding(ListNode id, ListNode actual) {// formal parameter ����Ʈ�� actual parameter ����Ʈ�� �Ű������� �Է¹޴´�.
		      if(id.equals(ListNode.EMPTYLIST)|| actual.equals(ListNode.EMPTYLIST)) return; //formal parameter�� actual parameter�� ������ ���ƾ� �ϸ�, ������ ���� �ʴ��� �� ���� ���� ���ε����� �ʴ´�. parameter�� ��� Ž���Ͽ� �� ����Ʈ�� ������ �����Ͽ� �����Ѵ�. 
		      
		      temp_table.put(id.car().toString(), actual.car());//temp_table�̶�� �ӽ� parameter ���ε� ���̺� �ϳ��� formal parameter toString ���� �ϳ��� actual parameter�� ���� �����Ѵ�.
		       
		      id = id.cdr(); //ù ��° ��忡 ���� ���ε��� ������ ������, ù ��° ��带 ������ ListNode�� id�� �Ѵ�. 
		      actual = actual.cdr();//ù ��° ��忡 ���� ���ε��� ����, ù ��° ��带 ������ ListNode�� actual�� �Ѵ�. 
		      
		      //��͸� ����Ͽ� formal parameter�� actual parameter�� ��� Ž���Ѵ�.
		      parameterBinding(id, actual);
		   }
		   //parameterBinding �޼ҵ带 ���� formal parameter�� ���� actual parameter���� temp_table�� �����ߴ�.
		   //������ actual parameter�� body�� ���Խ�Ű�� ���� �޼ҵ��̴�.
		   private ListNode readBody(ListNode body) {//ListNode body�� �Ű������� �Է¹޴´�.
		      if(body.equals(ListNode.EMPTYLIST)) return ListNode.EMPTYLIST; //body�� �� ListNode�� ��� �� ListNode�� �����Ͽ� �����Ѵ�.
		      
		      Node readed; //ListNode body�� ù��° ��带 Ȯ���Ͽ� temp_table�� ������ �� ������ �ٲٰ�, temp_table�� ���� ��� ù ��° ��带 �״�� ����Ѵ�. �̶� readed�� �� ������ ������ ���� ù ��° ��尪�� �����Ѵ�.
		      
		      if(temp_table.get(body.car().toString())!=null) {//ù��° ��忡 ���� actual parameter�� temp_table�� �ִ� ���,         
		         readed = temp_table.get(body.car().toString());//readed�� temp_table���� ã�� ���̴�.
		         
		      }else {//temp_table�� ���� ���, �� ������ ���� ���� ���
		         if(body.car() instanceof ListNode) {//body�� ù��° ��尡 ListNode�� ���,
		            readed = readBody((ListNode)body.car());//�� ListNode ���ο����� actual parameter�� ���ԵǾ�� �ϱ� ������ body.car�� ���Ͽ� readBody�� ȣ���Ѵ�.
		         }else {//ListNode�� �ƴ� ���
		            readed = body.car();//body.car�� �״�� �����Ѵ�.
		         }
		      }
		      return ListNode.cons(readed, readBody(body.cdr()));//readed�� ù��° ���� �ϰ�, body�� ������ ��忡 ���Ͽ� readBody�� ȣ���� �� ��ȯ���� tail�� �Ͽ� ��ȯ�Ѵ�.
		      
		   }
		   
		   //lambda �Լ� ���ο� define ���� ���� �ִ� ���, �� ��ø ������ ����ϱ� ���� �߰��� �޼ҵ��̴�.
		   // lambda �Լ��� ������ ��, lambda ������, formal parameter��, block�� (���� ���� ���� ���� �ִ�.), body�� ���� �����ϴµ�
		   // executeBlock�� formal parameter ���� ������ ListNode�� ���� �� ListNode�� �Ű������� �޾�, block�� �����Ű��, body�� ��ȯ�Ѵ�. 
		   private ListNode executeBlock(ListNode blocks) {
		      if(blocks.cdr().equals(ListNode.EMPTYLIST)) {//blocks�� cdr�� �� ListNode�� ���, blocks�� car�� body�̴�.
		         if(blocks.car() instanceof ListNode) {//body�� ListNode�̾�� �Ѵ�.
		            return (ListNode)blocks.car();// block�� ���� body�� �ִ� ����, body�� ��ȯ�ϰ� �����Ѵ�.
		         }else {//body�� ListNode�� �ƴ� ���, ListNode.cons�� �̿��� ListNode�� ���� �� ��ȯ�Ѵ�.
		            return ListNode.cons(blocks.car(), ListNode.EMPTYLIST);
		         }
		      }
		      
		      if(blocks.car() instanceof ListNode) runList((ListNode)blocks.car()); // blocks�� cdr�� �� ����Ʈ��尡 �ƴ� ���, blocks�� car�� ������Ѿ��� block���� runList�� ȣ���Ѵ�.
		      return executeBlock(blocks.cdr()); //block�� �����Ų ��, blocks�� cdr�� ���ؼ��� �� ������ �ݺ��ϵ��� ȣ���Ѵ�.
		   }

		}