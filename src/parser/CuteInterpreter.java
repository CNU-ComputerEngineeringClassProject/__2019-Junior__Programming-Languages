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

		Scanner scanner = new Scanner(System.in); //콘솔창에서 입력을 받기위해서 Scanner 객체를 생성한다.

		while(true) { // 무한반복문을 생성하여 계속해서 입력을 받고 그에 맞는 출력을 할수있도록 하였다.

			System.out.print("> "); //Interpreter를 구동시키면 > prompt를 띄운다.
			String input_s = scanner.nextLine(); //입력받은 문자열을 input_s에 저장한다.
			System.out.print("... "); //출력 전에는 … 등의 표시 후 출력하여 가독성을 높인다.

			CuteParser cuteParser = new CuteParser(input_s);  // 인자값을 input_s로 하여 CuteParser를 생성하고 CuteParser cuteParser변수에 저장한다. 
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

		switch (operator.funcType) {  //operator.funcType에 따라 다르게 작동되도록 switch문을 사용한다. 

		case CAR: // operator.funcType이 CAR인경우

			if(operand.car()  instanceof IdNode) {
				Node licar = operand.car();
				return runExpr(ListNode.cons(operator, (ListNode) lookuptable(((IdNode)licar).toString()))); 
			}else {

				if(operand.car() instanceof FunctionNode) {
					operand = (ListNode)runExpr(operand);
				}

				if(operand.car() instanceof QuoteNode) {
					ListNode lncar = (ListNode)runQuote(operand); //operand가 인자값인 runQuote함수를 호출하여 Node형으로 리스트 노드를 반환받는다. 이를 ListNode로 형변환을 하고 lncar ListNode객체를 생성하여 여기에 저장한다.
					if(lncar.car() instanceof ListNode) { //lncar.car()이 ListNode객체라면
						QuoteNode quoteNode = new QuoteNode(lncar.car()); //lncar.car()를 인자로하는 QuoteNode를 생성한다. 그리고 이를 QuoteNode 객체 quoteNode에 저장한다.
						return ListNode.cons(quoteNode, ListNode.EMPTYLIST  ); //그리고 헤더(car)이 quoteNode이고, 테일(cdr)을 ListNode.EMPTYLIST로 하는 리스트 노드를 새로 생성하고 이를 반환한다.
					}else { //아니라면 (lncar.car()이 ListNode객체가 아니라면)
						if(lncar.car() instanceof IntNode || lncar.car() instanceof BooleanNode) { //lncar.car()이 IntNode 거나 BooleanNode의 객체라면 
							return lncar.car(); //lncar.car()를 리턴한다.
						}else {
							QuoteNode quoteNode = new QuoteNode(lncar.car()); 
							return ListNode.cons(quoteNode, ListNode.EMPTYLIST);
							//return new QuoteNode(lncar.car()); // 이외의 경우 lncar.car()를 인자값으로 하여 QuoteNode를 생성하고 이를 리턴한다.
						}
					}
				}
			}

			return null;

		case CDR: // operator.funcType이 CDR인경우   

			if(operand.car() instanceof FunctionNode) {
				operand = (ListNode)runExpr(operand);
			}
			if(operand.car()  instanceof IdNode) {
				Node licar = operand.car();
				return runExpr(ListNode.cons(operator, (ListNode) lookuptable(((IdNode)licar).toString()))); 
			}else {			
				ListNode lncdr = (ListNode)runQuote(operand); //operand가 인자값인 runQuote함수를 호출하여 Node형으로 리스트 노드를 반환받는다. 이를 ListNode로 형변환을 하고 lncdr ListNode객체를 생성하여 여기에 저장한다.
				QuoteNode quoteNode = new QuoteNode(lncdr.cdr()); //lncar.cdr()를 인자로하는 QuoteNode를 생성한다. 그리고 이를 QuoteNode 객체 quoteNode에 저장한다.
				return ListNode.cons(quoteNode, ListNode.EMPTYLIST  ); //그리고 헤더(car)이 quoteNode이고, 테일(cdr)을 ListNode.EMPTYLIST로 하는 리스트 노드를 새로 생성하고 이를 반환한다.
			}

		case CONS: // operator.funcType이 CONS인경우
			Node lncons1; //한개의 원소(head)를 가져오기위하여 Node lncons1변수를 생성하였다.
			Node licarFirst = operand.car();
			Node licarSecond = (operand.cdr()).car();

			if(operand.car()  instanceof IdNode) {
				licarFirst =  lookuptable(((IdNode)operand.car()).toString()); 
				if(licarFirst == null) licarFirst = operand.car();
			}

			if(licarFirst instanceof ListNode) { //한개의 원소인 operand.car()가 ListNode객체라면
				lncons1 = runQuote((ListNode)licarFirst); //operand.car()를 ListNode로 형변환한 값을 인자값으로 하여 runQuote함수를 호출한다. 그리고 이 반환값을 lncons1에 저장한다.
			}else { //아닌경우 operand.car()가 ListNode 객체가 아닌경우 
				lncons1 = licarFirst; //operand.car()를 lncons1에 저장한다.
			}

			if(licarSecond instanceof IdNode) {
				licarSecond = lookuptable(((IdNode)licarSecond).toString()); 
				if(licarSecond == null) licarSecond = (operand.cdr()).car();
			}

			Node lncons2 = runQuote((ListNode)licarSecond); //한개의 리스트(tail)을 가져오기위하여 (operand.cdr()).car()를 ListNode로 형변환한 값을 인자값으로 하여 runQuote함수를 호출한다.이를 Node lncons2에 저장한다.
			ListNode result =  ListNode.cons(lncons1, (ListNode)lncons2); //lncons1과 lncons2를 붙여서 새로운 리스트 노드를 만들기위하여 ListNode.cons를 사용하여  헤더(car)을 lncons1로 하고, 테일(cdr)을 lncons2를 ListNode로 형변환 하여 만들었다. 이를 ListNode객체 result에 저장하였다.
			return ListNode.cons(new QuoteNode(result), ListNode.EMPTYLIST); //result를 인자값으로 하여 새로운 QuoteNode를 생성하고 이를 헤더(car)로 하고 ListNode.EMPTYLIST가 테일 (cdr)인 ListNode를 생성하고 이를 반환한다.

		case COND: // operator.funcType이 COND인경우

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
						return runExpr(one_cdr); //ifNn의 첫번째 노드의 값을 반환해야하므로 ifNn.car()를 인자로 하여 runExpr함수를 호출하고 반환값을 반환한다.
					}

				}else return BooleanNode.FALSE_NODE;
			}else {

				if(operand.car() instanceof IdNode) {
					ifN = (ListNode) lookuptable(((IdNode)(operand.car())).toString()); 
				}else {
					ifN = (ListNode)operand.car(); // 리스트노드 안의 첫번째 리스트노드를 가져오기위하여  operand.car()를 사용하고 이를 ListNode로 형변환을 한뒤에 ListNode 객체 ifN에 저장한다.
				}

				Node T_F = ifN.car();
				Node returnValue = ifN.cdr();

				if(T_F instanceof IdNode) {
					T_F = lookuptable(((IdNode)ifN.car()).toString());			
				}

				if(runExpr(T_F) == BooleanNode.TRUE_NODE) { //조건식이 참인지를 파악하기위하여 ifN.car()를 인자값으로 하여 runExpr()를 호출하고 반환받은 값이 BooleanNode.TRUE_NODE라면 조건식이 참이라는 의미이므로 if문안의 코드를 실행한다.
					//				ListNode ifNn = ifN.cdr(); //조건식이 참이므로 ifN.cdr()의 첫번째 값을 반환해야한다. 먼저 listNode 객체 ifNn에 반환값을 저장한다.
					if(((ListNode)returnValue).car() instanceof IdNode) {
						Node result_return = lookuptable(((IdNode)(((ListNode)returnValue).car())).toString());	

						if(result_return == null) result_return = ((ListNode)returnValue).car();

						return runExpr(result_return);
					}else {
						return runExpr(((ListNode)returnValue).car()); //ifNn의 첫번째 노드의 값을 반환해야하므로 ifNn.car()를 인자로 하여 runExpr함수를 호출하고 반환값을 반환한다.
					}

				}else if( operand.cdr() != ListNode.EMPTYLIST) { //조건식이 거짓이고 operand.cdr()이 빈 리스트 노드가 아닐경우에는 다음 조건식을 확인해야하므로 else if문을 실행한다.

					ListNode newlistNode = ListNode.cons(operator, ListNode.cons(operand.cdr(), ListNode.EMPTYLIST)); 
					//남은 리스트 노드가 몇개인지, 또한 언제 참이 될지 알수 없기때문에 거짓으로 판단된 리스트 노드를 제외하고 남은 리스트 노드들과 앞에 cond를 붙여서 새로운 listNode를 생성하였다.
					//먼저 남은 리스트 노드들만을 사용하여 리스트 노드를 만든다. ListNode.cons를 사용해서 헤더(car)을 operand.cdr()로 하고, 테일(cdr)을 ListNode.EMPTYLIST로 하여 만들었다. 
					//그리고 ListNode.cons사용하여 새로 생성한 리스트 노드를 테일(cdr)로 하는 새로운 리스트 노드를 만든다. 이때 헤더(car)은 cond를 넣을 것이기때문에 operator를 넣는다. 
					//그리고 만들어진 새로운 listNode를 ListNode 객체 newlistNode에 저장한다.
					return runExpr(newlistNode); //이제 다음 조건식을 판단해야하기때문에 newlistNode를 인자값으로 하여 runExpr함수를 호출하여 반환값을 반환한다.
				}

				return BooleanNode.FALSE_NODE; //조건문이 참인것이 없는 경우 BooleanNode.FALSE_NODE를 리턴한다.
			}
		case NOT: // operator.funcType이 NOT인경우

			Node NotN = operand.car();

			if(NotN instanceof IdNode) {
				NotN = runExpr(lookuptable(((IdNode)NotN).toString()));			
			}

			if(NotN instanceof BooleanNode) { //operand.car()가 BooleanNode 객체인 경우
				return (NotN == BooleanNode.TRUE_NODE) ? BooleanNode.FALSE_NODE : BooleanNode.TRUE_NODE ;
				//operand.car()가 BooleanNode.TRUE_NODE라면 반대(not)인 BooleanNode.FALSE_NODE를 반환한다. 그리고  BooleanNode.FALSE_NODE라면 반대(not)인 BooleanNode.TRUE_NODE를 반환한다.
			}
			else {//operand.car()가 BooleanNode객체가 아닐경우
				BooleanNode before_Result = (BooleanNode) runExpr(operand); // operand를 인자로 하여 runExpr함수를 호출하면 BooleanNode가 Node형으로 반환될것이다.
				//그래서 BooleanNode로 형변환을 시켜준뒤에  BooleanNode 객체 before_Result를 생성하여 저장한다.
				return (before_Result == BooleanNode.TRUE_NODE) ? BooleanNode.FALSE_NODE : BooleanNode.TRUE_NODE ;
				//before_Result가 BooleanNode.TRUE_NODE라면 반대(not)인 BooleanNode.FALSE_NODE를 반환한다. 그리고  BooleanNode.FALSE_NODE라면 반대(not)인 BooleanNode.TRUE_NODE를 반환한다.
			}

		case EQ_Q: // operator.funcType이 EQ_Q인경우

			Node first_operand = operand.car(); // 객체 비교를 위한 첫번째 노드를 가져와 저장하기위한 first_operand Node객체를 생성한다.
			Node second_operand = (operand.cdr()).car(); // 객체 비교를 위한 첫번째 노드를 가져와 저장하기위한 second_operand Node객체를 생성한다.

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
							first_operand = runQuote((ListNode)first_operand);//객체 비교를 위한 첫번째 노드를 가져오기위해서 operand.car()를 ListNode로 형변환하고 이를 인자값으로 하는 runQuote함수를 호출하여 Node를 반환받는다. 이를 first_operand에 저장한다.				
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
							second_operand = runQuote((ListNode)second_operand); //객체 비교를 위한 두번째 노드를 가져오기위해서  (operand.cdr()).car()를 ListNode로 형변환하고 이를 인자값으로 하는 runQuote함수를 호출하여 Node를 반환받는다. 이를 second_operand Node객체에 저장한다.
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
			//second_operand인자값으로하여 first_operand의 equals메소드를 사용하여 같은 객체를 참조하는지 확인한다. 같은 객체를 참조한다면 BooleanNode.TRUE_NODE를 반환하고, 아니라면  BooleanNode.FALSE_NODE를 반환한다.  

		case NULL_Q: // operator.funcType이 NULL_Q인경우

			Node carNull = operand.car();

			if(carNull instanceof IdNode) {
				Node result_null = lookuptable(((IdNode)carNull).toString());
				if(result_null != null) operand = (ListNode)result_null;			
			}

			ListNode lnNullQ = (ListNode)runQuote(operand); // operand가 인자값인 runQuote함수를 호출하여 Node형으로 리스트 노드를 반환받는다. 이를 ListNode로 형변환을 하고 lnNullQ ListNode객체를 생성하여 여기에 저장한다.
			return (runExpr(lnNullQ) == ListNode.EMPTYLIST ) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE; 
			// lnNullQ를 인자값으로 하여 runExpr함수를 호출하고 받은 결과가 ListNode.EMPTYLIST라면  BooleanNode.TRUE_NODE를 반환하고, 아니라면  BooleanNode.FALSE_NODE를 반환한다.

		case ATOM_Q: // operator.funcType이 ATOM_Q인경우 //9

			Node Atomcar = operand.car();

			if( Atomcar instanceof IdNode) {

				Node result_atom = lookuptable(((IdNode)Atomcar).toString());	
				if(result_atom != null) operand = (ListNode)result_atom;	

			}
			Node nAtomQ = runQuote(operand); // operand를 인자값으로 하여 runQuote함수를 호출하여 Node를 반환받는다. 이를 nAtomQ Node객체를 생성하여 여기에 저장한다.
			if(nAtomQ instanceof ListNode) { //nAtomQ가 ListNode 객체라면 빈리스트노드일경우 true를 반환해야하기 때문에 if문 안의 코드의 실행하여 이를 판별한다.
				return (runExpr(nAtomQ) == ListNode.EMPTYLIST ) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE;	
				//runExpr(nAtomQ)을 하여 나온 결과가 ListNode.EMPTYLIST라면  null list은 atom 으로 취급되어야 하기때문에 BooleanNode.TRUE_NODE를 리턴한다. 아니라면 BooleanNode.FALSE_NODE를 리턴한다.  
			}else{//nAtomQ가 ListNode객체가 아니라면 이는 원소라는 의미이다.
				return BooleanNode.TRUE_NODE; //그러므로 BooleanNode.TRUE_NODE를 리턴한다.
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

		default: //위의 경우들이 아닌경우
			break;    //break를 하여 switch문을 나온다.
		}
		return null; //null을 리턴한다. 
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



		//구현과정에서 필요한 변수 및 함수 작업 가능      
		switch (operator.binType) {  //operator.binType에 따라 다르게 작동되도록 switch문을 사용한다.

		case PLUS: //operator.binType이 PLUS인 경우
			result = first_operand.getValue() + second_operand.getValue(); // first_operand.getValue()와 second_operand.getValue()을 하여 각각의 IntNode에 저장되어있던 정수형 숫자(value값)를 가져온다. 그리고  두 값을 더하고 result에 저장한다.
			return new IntNode(result.toString()); // Integer형인 result를 String으로 데이터형변환을 하고 이를 value로 하는 새로운 IntNode 객체를 생성하고 반환한다.

		case MINUS: // operator.binType이 MINUS인 경우
			result = first_operand.getValue() - second_operand.getValue(); // first_operand.getValue()와 second_operand.getValue()을 하여 각각의 IntNode에 저장되어있던 정수형 숫자(value값)를 가져온다. 그리고 두 값을 빼고 result에 저장한다.
			return new IntNode(result.toString()); // Integer형인 result를 String으로 데이터형변환을 하고 이를 value로 하는 새로운 IntNode 객체를 생성하고 반환한다.

		case TIMES: // operator.binType이 TIMES인 경우
			result = first_operand.getValue() * second_operand.getValue(); // first_operand.getValue()와 second_operand.getValue()을 하여 각각의 IntNode에 저장되어있던 정수형 숫자(value값)를 가져온다. 그리고 두 값을 곱하고 result에 저장한다.
			return new IntNode(result.toString()); // Integer형인 result를 String으로 데이터형변환을 하고 이를 value로 하는 새로운 IntNode 객체를 생성하고 반환한다.

		case DIV: // operator.binType이 DIV인 경우
			result = first_operand.getValue() / second_operand.getValue(); // first_operand.getValue()와 second_operand.getValue()을 하여 각각의 IntNode에 저장되어있던 정수형 숫자(value값)를 가져온다. 그리고 두 값을 나누고 result에 저장한다.
			return new IntNode(result.toString()); // Integer형인 result를 String으로 데이터형변환을 하고 이를 value로 하는 새로운 IntNode 객체를 생성하고 반환한다.

		case LT: // operator.binType이 LT인 경우 ; <
			return (first_operand.getValue() < second_operand.getValue() ) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE;

		case GT: // operator.binType이 GT인 경우 ; >
			return (first_operand.getValue() > second_operand.getValue() ) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE;

		case EQ: //operator.binType이 EQ인경우
			return (first_operand.equals(second_operand)) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE; 



		default: //위의 경우들이 아닌경우
			break;    //break를 하여 switch문을 나온다.
		}
		return null; //null을 리턴한다.

	} 

	private Node runQuote(ListNode node) {  
		return ((QuoteNode) node.car()).nodeInside(); 
	}
}