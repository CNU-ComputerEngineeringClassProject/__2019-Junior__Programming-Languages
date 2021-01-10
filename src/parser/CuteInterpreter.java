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
	   private static Map<String, Node> temp_table = new HashMap<String, Node>();//lambda 함수를 사용할 때 parameter 값은 임시로 바인딩되는데, 이때 임시 바인딩을 처리하기 위해 map을 추가했다.

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
		      if (list.car() instanceof FunctionNode) {//list의 car이 FunctionNode일 경우,

		         if (((FunctionNode) list.car()).funcType == FunctionNode.FunctionType.LAMBDA) {//lambda일 경우, 즉 lambda 함수의 명세만 들어온 경우
		            return runFunction((FunctionNode) list.car(), list);//runFunction을 통해 lambda를 실행시키는데, 이때 stripList을 하지 않고 list 전체를 넘겨준다.
		         }
		         if (!list.cdr().equals(ListNode.EMPTYLIST)) {//뒤에 인자가 있는 경우에, list.cdr이 빈 ListNode가 아닌 경우 runFunction을 호출한다.
		            return runFunction((FunctionNode) list.car(), (ListNode) stripList(list.cdr()));
		         }
		      }
		      if (list.car() instanceof ListNode) {//list.car()이 ListNode인 경우
		         if (((ListNode) list.car()).car() instanceof FunctionNode) {//list.car().car()이 FunctionNode이고 functionType이 lambda인 경우,
		            if (((FunctionNode) ((ListNode) list.car()).car()).funcType == FunctionNode.FunctionType.LAMBDA) {//이 경우는 lambda 함수 명세와 함께 actual parameter까지 입력한 경우로,
		               return runFunction((FunctionNode) ((ListNode) list.car()).car(), list);//actual parameter까지 처리하기 위해 list 전체를 operand로 준다.
		            }
		         }
		      }
		      if (list.car() instanceof BinaryOpNode) {
		         return runBinary(list);
		      }
		      if (list.car() instanceof IdNode) { //함수를 정의하여 idNode에 저장한 후에 정의한 함수를 사용할 때 idNode와 actual parameter를 입력하여 호출한다.
		         //list.car이 IdNode이고, 테이블에 매칭되는 함수가 있는 경우,
		         if (lookupTable(list.car().toString()) != null) {
		            return runList(ListNode.cons(lookupTable(list.car().toString()), list.cdr())); //매칭되는 함수와 actual parameter를 묶어 ListNode로 변환 후에 runList를 호출해 처리한다. 
		         }
		      }
		      return list;
		   }


		   private Node runFunction(FunctionNode operator, ListNode operand) {     

		      switch (operator.funcType) {  //operator.funcType에 따라 다르게 작동되도록 switch문을 사용한다. 

		      case CAR: // operator.funcType이 CAR인경우
		         
		         Node returnNode = null; // 반환 할 노드를 담을 변수

		         if(operand.car() instanceof IdNode) { // ListNode operand의 첫 번째 노드가 IdNode일 경우
		            //operator CAR의 경우 반환해야 하는 값은 첫 번째 노드로, IdNode가 값을 담은 변수일 경우, 반환하는 값은 그 값이어야 한다. 
		            // 이때, 오류 입력을 고려하지 않으므로, 값을 담지 않은 단순 ID값일 경우, null을 반환하게 된다. car은 리스트 연산자이므로 첫번째 노드가 IdNode일 경우 그 IdNode는 반드시 ListNode 값을 담고 있어야 한다.
		            
		            //lookupTable에 첫번재 인자의 idString 값에 대한 반환노드를 피연산자, CAR을 연산자로 하여 runFunction 메소드를 호출해 그 값을 리턴한다.
		            return runFunction(operator, (ListNode)lookupTable(((IdNode)operand.car()).toString())); 
		         }
		         if(operand.car() instanceof QuoteNode) { //operand는 연산자 뒤의 리스트를 뜻하며, operand의 첫번째 노드가 quote일 경우 즉 ' 입력일 경우
		            //CAR은 리스트 연산자로, runQuote의 반환 노드는 ListNode이어야 한다.
		            returnNode = ((ListNode)runQuote(operand)).car(); //runQuote 메소드를 통해 리스트 속 QuoteNode 내부 노드, ListNode를 받고 그 첫 번째 노드를 returnNode에 저장한다.
		            
		            if(!(returnNode instanceof IntNode) && !(returnNode instanceof BooleanNode)) { //반환할 노드가 IntNode 또는  BooleanNode가 아닐 경우,
		               //IntNode나 BooleanNode는  변하지 않는 값을 가지므로 상수 취급하여 QuoteNode를 붙이지 않는다.
		               QuoteNode quote = new QuoteNode(returnNode); //QuoteNode에 담은 후, 리스트에 담아 반환한다.
		               returnNode = ListNode.cons(quote, ListNode.EMPTYLIST);// ListNode.cons를 사용하여 QuoteNode와 ListNode.EMPTYLIST를 이어 returnNode로 한다.
		            }
		         }
		         if(operand.car() instanceof FunctionNode) { // 피연산자의 첫 번째 노드가 FunctionNode인 경우, 이 때 오류 입력은 없으므로 FunctionNode는 리스트 연산자이어야 한다.
		            return runFunction(operator, (ListNode)runExpr(operand)); // 피연산자 리스트에 대해 runExpr를 호출하여 받아온 값을 다시 피연산자로 하고, CAR을 operator로 하여 runFunction을 호출해 반환한다.
		         }
		         return returnNode; //returnNode를 반환한다.
		         
		      case CDR: // operator.funcType이 CDR인경우   

		         if(operand.car() instanceof IdNode) { //피연산자의 첫 번째 노드가 IdNode인 경우, 이 경우는 ( cdr a ) 와 같이 인자가 하나만 들어온 경우다.
		            //CDR은 QuoteNode가 붙은 상수 리스트에 대한 연산자이므로, QuoteNode가 붙지 않은 리스트 ( cdr ( a 1 ) )과 같은 입력에 대해서는 고려하지 않는다.
		            return runFunction(operator, (ListNode)lookupTable(((IdNode)operand.car()).toString()));
		         }
		         if(operand.car() instanceof QuoteNode) { // 리스트의 첫번째 노드가 QuoteNode일 경우,
		            returnNode =((ListNode)runQuote(operand)).cdr(); //CDR은 리스트 연산자이므로 runQuote를 통해 받아온 QuoteNode 내부노드는 ListNode여야 한다. ListNode의 cdr을 returnNode에 저장한다.
		            QuoteNode quote = new QuoteNode(((ListNode)runQuote(operand)).cdr()); // CDR은 리스트를 반환하므로 QuoteNode에 담아, ListNode.cons를 통해 QuoteNode를 ListNode에 담아 반환한다.
		            return ListNode.cons(quote, ListNode.EMPTYLIST);
		         }
		         if(operand.car() instanceof FunctionNode) { //리스트의 첫 번째 노드가 FunctionNode인 경우, 이 때 오류 입력은 없으므로 FunctionNode는 리스트 연산자이어야 한다.
		            return runFunction(operator, (ListNode)runExpr(operand));// 피연산자 리스트에 대해 runExpr를 호출하여 받아온 값을 다시 피연산자로 하고, CDR을 operator로 하여 runFunction을 호출해 반환한다.
		         }

		      case CONS: // operator.funcType이 CONS인경우
		      
		         Node lncons1; //한개의 원소(head)를 가져오기위하여 Node lncons1변수를 생성하였다.
		         Node licarFirst = operand.car(); //operand.car()을 가져와 licarFirst에 저장한다. 

		         if(licarFirst instanceof IdNode) { //licarFirst이 IdNode객체일 경우licarFirst이 HashMap에 id값으로 저장되어있는 정의된 변수일수도 있고 저장되어있지않은 id값일수도있다. if문을 실행하여 이를 파악한다.
		            licarFirst =  lookupTable(((IdNode)licarFirst).toString()); // ((IdNode)licarFirst).toString()을 인자값으로 하여 lookupTable을 호출하고 매치되는 노드값을 반환받아 licarFirst에 저장한다.
		            if(licarFirst == null) licarFirst = operand.car(); //만약 licarFirst가 null이라면 이는 정의된 변수가 아니라는 의미이다. 그러므로 다시 operand.car()을 가져와 licarFirst에 저장한다.
		         }

		         if(licarFirst instanceof ListNode) { //한개의 원소여야하는 licarFirst가 ListNode객체라면
		         
		            if(((ListNode)licarFirst).car() instanceof QuoteNode) { // licarFirst.car()이 QuoteNode객체라면
		               lncons1 = runQuote((ListNode)licarFirst); // licarFirst를 ListNode로 형변환한 값을 인자값으로 하여 runQuote함수를 호출한다. 그리고 이 반환값을 lncons1에 저장한다.
		            }else { //아니라면
		               lncons1 = runExpr(licarFirst); //licarFirst를 인자값으로 하여 runExpr함수를 호출하고 반환값을 lncons1에 저장한다.
		            }
		            
		         }else { //아닌경우 licarFirst가 ListNode 객체가 아닌경우 하나의 원소라는 의미이다.
		            lncons1 = licarFirst; //licarFirst를 lncons1에 저장한다.
		         }

		         Node lncons2; //한개의 리스트(tail)를 가져오기위하여 Node lncons2변수를 생성하였다.
		         Node licarSecond = (operand.cdr()).car(); // (operand.cdr()).car()을 가져와 licarSecond에 저장한다.

		         if(licarSecond instanceof IdNode) { // licarSecond이 IdNode객체일 경우licarSecond이 HashMap에 id값으로 저장되어있는 정의된 변수일수도 있고 저장되어있지않은 id값일수도있다. if문을 실행하여 이를 파악한다.
		            licarSecond = lookupTable(((IdNode)licarSecond).toString()); // ((IdNode)licarSecond).toString()을 인자값으로 하여 lookupTable을 호출하고 매치되는 노드값을 반환받아 licarSecond에 저장한다.
		            if(licarSecond == null) licarSecond = (operand.cdr()).car(); //만약 licarSecond가 null이라면 이는 정의된 변수가 아니라는 의미이다. 그러므로 다시(operand.cdr()).car()을 가져와 licarSecond에 저장한다.
		         }

		         if(((ListNode)licarSecond).car() instanceof QuoteNode) { //// licarSecond.car()이 QuoteNode객체라면
		            lncons2 = runQuote((ListNode)licarSecond); //한개의 리스트(tail)을 가져오기위하여licarSecond를 ListNode로 형변환한 값을 인자값으로 하여 runQuote함수를 호출한다.이를  lncons2에 저장한다.
		         }else { //아니라면
		            lncons2 = runExpr(licarSecond); //licarSecond를 인자값으로 하여 runExpr함수를 호출하고 반환값을 lncons2에 저장한다.
		         }

		         ListNode result =  ListNode.cons(lncons1, (ListNode)lncons2); //lncons1과 lncons2를 붙여서 새로운 리스트 노드를 만들기위하여 ListNode.cons를 사용하여  헤더(car)을 lncons1로 하고, 테일(cdr)을 lncons2를 ListNode로 형변환 하여 만들었다. 이를 ListNode객체 result에 저장하였다.
		      
		         return ListNode.cons(new QuoteNode(result), ListNode.EMPTYLIST); //result를 인자값으로 하여 새로운 QuoteNode를 생성하고 이를 헤더(car)로 하고 ListNode.EMPTYLIST가 테일 (cdr)인 ListNode를 생성하고 이를 반환한다.

		      case COND: // operator.funcType이 COND인경우

		         Node only_one; // cond의 피연산자 리스트가 하나밖에 없어 operand자체가 피연산자 첫번째 리스트인 경우와 아닌경우를 구분하여 구현하기위해 필요한 Node only_one을 생성한다.

		         if(operand.car() instanceof IdNode) { //operand.car()가 IdNode 객체인 경우  operand.car()이 HashMap에 노드값으로 BooleanNode가 매치되어있는 id값으로 저장되어있는 정의된 변수 일 것이다. 
		            only_one = lookupTable(((IdNode)(operand.car())).toString()); //((IdNode)(operand.car())).toString()를 인자로 하여 lookupTable을 호출하고 반환값을 only_one에 저장한다.
		         }else { //아닌 경우
		            only_one = runExpr(operand.car()); //runExpr(operand.car())의 반환값을 only_one에 저장한다.
		         }

		         if( only_one instanceof BooleanNode) { //only_one이 BooleanNode객체라면

		            if( only_one == BooleanNode.TRUE_NODE) { // only_one값이 BooleanNode.TRUE_NODE라면

		               Node one_cdr = operand.cdr().car(); //반환해야하는 값을 알아내기위해서 operand.cdr().car()값을 가져와 Node one_cdr에 저장한다.

		               if( one_cdr instanceof IdNode) { //one_cdr가 IdNode 객체인 경우  one_cdr이 HashMap에 저장되어있는 정의된 변수인지 아닌지 확인해야한다. if문을 실행하여 이를 파악한다.
		                  
		                  one_cdr = lookupTable(((IdNode)one_cdr).toString()); // ((IdNode)one_cdr).toString()을 인자값으로 하여 lookupTable을 호출하고 매치되는 노드값을 반환받아 one_cdr에 저장한다.
		                  if(one_cdr == null) one_cdr = operand.cdr().car(); //만약 one_cdr가 null이라면 이는 정의된 변수가 아니라는 의미이다. 그러므로 다시(operand.cdr()).car()을 가져와 one_cdr에 저장한다.

		               }

		               if(one_cdr instanceof FunctionNode) { //one_cdr이 FunctionNode객체인경우 그전에 리스트 표시가 안되어있으므로 이를 id값으로 생각하여 FunctionNode를 반환한다.
		                  return one_cdr; //one_cdr를 반환한다.
		               }else{ //아니라면 
		                  return runExpr(one_cdr); // one_cdr를 인자로 하여 runExpr함수를 호출하고 반환값을 반환한다.
		               }

		            }else {
		               return BooleanNode.FALSE_NODE; //이는 피연산자 리스트가 하나밖에 없는 경우이기 때문에 조건문이 참인것이 없는 경우 BooleanNode.FALSE_NODE를 리턴한다.
		            }
		            
		         }else { //only_one이 BooleanNode객체가 아닐경우 이는 리스트노드일 경우이다.

		            ListNode ifN ; //리스트노드 안의 첫번째 리스트노드를 가져오기위하여 ListNode객체 ifN를 생성한다.

		            if(operand.car() instanceof IdNode) { //operand.car()가 IdNode 객체인 경우   HashMap에서 ListNode로 노드값이 매치되는 정의된 변수라는 의미이다.
		               ifN = (ListNode) lookupTable(((IdNode)(operand.car())).toString()); //((IdNode)(operand.car())).toString()을 인자값으로 하여 lookupTable을 호출하고 매치되는 노드값을 반환받아 ifN에 저장한다.
		            }else {
		               ifN = (ListNode)operand.car(); // operand.car()를 사용하고 이를 ListNode로 형변환을 한뒤에 ifN에 저장한다.
		            }

		            Node T_F = ifN.car(); //ifN.car()값을 가져와 Node T_F에 저장한다.
		            Node returnValue = ifN.cdr(); //ifN.cdr()값을 가져와 Node returnValue에 저장한다.

		            if(T_F instanceof IdNode) { //T_F가 IdNode인 경우 HashMap에 노드값으로 BooleanNode가 매치되어있는 id값으로 저장되어있는 정의된 변수 일 것이다.
		               T_F = lookupTable(((IdNode)ifN.car()).toString());    //((IdNode)ifN.car()).toString()을 인자값으로 하여 lookupTable을 호출하고 매치되는 노드값을 반환받아 T_F에 저장한다.      
		            }

		            if(runExpr(T_F) == BooleanNode.TRUE_NODE) { //조건식이 참인지를 파악하기위하여 T_F를 인자값으로 하여 runExpr()를 호출하고 반환받은 값이 BooleanNode.TRUE_NODE라면 조건식이 참이라는 의미이므로 if문안의 코드를 실행한다.

		               if(((ListNode)returnValue).car() instanceof IdNode) {//((ListNode)returnValue).car()이 IdNode객체인 경우  HashMap에 저장되어있는 정의된 변수인지 아닌지 확인해야한다. if문을 실행하여 이를 파악한다.
		                  Node result_return = lookupTable(((IdNode)(((ListNode)returnValue).car())).toString());   
		                  // ((IdNode)(((ListNode)returnValue).car())).toString()을 인자값으로 하여 lookupTable을 호출하고 매치되는 노드값을 반환받아 Node result_return에 저장한다.
		                  if(result_return == null) result_return = ((ListNode)returnValue).car(); //
		                  // result_return가 null이라면 이는 정의된 변수가 아니라는 의미이다. 그러므로 ((ListNode)returnValue).car()을 가져와 result_return에 저장한다.

		                  return runExpr(result_return); //runExpr(result_return)를 호출하고 반환된값을 반환한다.

		               }else { //아닌경우
		                  return runExpr(((ListNode)returnValue).car()); // ((ListNode)returnValue).car()를 인자로 하여 runExpr함수를 호출하고 반환값을 반환한다.
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
		         
		         Node NotN = operand.car(); // operand.car()을 가져와 Node NotN 변수를 생성하여 저장한다.

		         if(NotN instanceof IdNode) { //만약 NotN이  IdNode객체라면 NotN이 HashMap에 노드값으로 BooleanNode가 매치되어있는 id값으로 저장되어있는 정의된 변수 일 것이다.
		            NotN = lookupTable(((IdNode)NotN).toString());// ((IdNode)NtoN).toString()을 인자값으로 하여 lookupTable을 호출하고 매치되는 노드값을 반환받고 NotN에 저장한다.   
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

		      case NULL_Q: // operator.funcType이 NULL_Q인경우
		         
		         Node carNull = operand.car(); // operand.car()값을 가져와 이를 Node carNull에 저장한다.

		         if(carNull instanceof IdNode) { //carNull이 IdNode객체라면 carNull이 HashMap에 id값으로 저장되어있는 정의된 변수일수도 있고 저장되어있지않은 id값일수도있다. if문을 실행하여 이를 파악한다.
		            Node result_null = lookupTable(((IdNode)carNull).toString()); // ((IdNode)carNull).toString()을 인자값으로 하여 lookupTable을 호출하고 매치되는 노드값을 반환받아 Node result_null에 저장한다.
		            if(result_null != null) operand = (ListNode)result_null; //   만약 result_null이 null이 아니라면 이는 정의된 변수라는 의미이다. 그러므로 operand에 result_null을 ListNode로 형변환시켜 저장한다.
		         }
		         
		         else if(carNull instanceof FunctionNode) {//carNull이 FunctionNode 객체일 경우, 
		            operand = (ListNode) runExpr(operand);//operand에 대해 runExpr을 호출하여 결과 노드를 operand로 한다 .
		         }
		         
		         ListNode lnNullQ = (ListNode)runQuote(operand); // operand가 인자값인 runQuote함수를 호출하여 Node형으로 리스트 노드를 반환받는다. 이를 ListNode로 형변환을 하고 lnNullQ ListNode객체를 생성하여 여기에 저장한다.

		         return (runExpr(lnNullQ) == ListNode.EMPTYLIST ) ? BooleanNode.TRUE_NODE :  BooleanNode.FALSE_NODE; 
		         // lnNullQ를 인자값으로 하여 runExpr함수를 호출하고 받은 결과가 ListNode.EMPTYLIST라면  BooleanNode.TRUE_NODE를 반환하고, 아니라면  BooleanNode.FALSE_NODE를 반환한다.

		      case EQ_Q: // operator.funcType이 EQ_Q인경우
		         
		         Node node_1=null; // 비교할 첫 번째 노드를 담을 변수 선언
		         Node node_2=null; // 비교할 두 번째 노드를 담을 변수 선언
		         //IdNode인 경우
		         if(operand.car() instanceof IdNode) { // 첫 번째 노드가 IdNode일 경우, 
		            node_1 = lookupTable(((IdNode)operand.car()).toString()); // lookupTable 메소드를 통해 첫 번째 인자의 idString에 대한 반환 노드를 받아 그 값을 node_1로 한다. 
		            if(node_1 == null) node_1 = operand.car(); // 반환 노드가 null이면 심벌 테이블에 첫 번째 인자의 idString에 대한 값이 없는 것으로, IdNode 그대로  node_1로 한다.

		            if(node_1 instanceof ListNode) {//lookupTable을 통해 불러온 값이 ListNode일 경우, lookupTable에서 null을 리턴한 경우 node_1은 IdNode이므로 else문에 해당한다.
		               if(((ListNode)node_1).car() instanceof QuoteNode ) node_1 = runQuote((ListNode)node_1); //ListNode 안에 QuoteNode가 있는 경우, runQuote 메소드를 통해 내부 노드를 node_1로 한다.
		               //값을 저장하는 IdNode의 경우, IntNode, BooleanNode처럼 상수 값을 가지거나 QuoteNode가 붙은 ListNode와 같이 연산이 끝난 값만을 저장하므로 QuoteNode가 붙지 않은 ListNode에 대해서는 고려하지 않는다.
		               
		               
		               //operand.cdr은 ListNode의 첫 번째 노드를 제외한 나머지 노드를 리스트에 담아 반환하므로, operand.cdr().car()을 두 번째 노드로 한다.
		               //이 때 오류 입력을 고려하지 않기 위해 인자 값이 3개 이상 들어온 경우를 처리하지 않았다.
		               if(!(operand.cdr().car() instanceof ListNode)) { // 첫 번째 노드가 ListNode안에  QuoteNode인  즉 ' 입력 일 때, 두 번째 노드가 ListNode가 아닌 경우,

		                  if(operand.cdr().car() instanceof IdNode) {// 두 번째 노드가 IdNode인 경우,
		                     node_2 = lookupTable(((IdNode)operand.cdr().car()).toString()); //lookupTable을 통해 두 번째 노드에 저장된 값을 불러온다.
		                     if(node_2 == null) node_2 = operand.cdr().car(); //심벌테이블에 두 번째 노드에 대해 저장된 값이 없는 경우, IdNode를 비교할 노드 node_2로 한다.
		                     
		                     if(node_2 instanceof ListNode) {// lookupTable을 통해 불러온 값이 ListNode일 경우, 
		                        if(((ListNode)node_2).car() instanceof QuoteNode ) node_2 = runQuote((ListNode)node_2);
		                        //변수의 경우, 연산이 끝난 값만을 저장하므로 QuoteNode가 붙지 않은 ListNode는 오류에 해당한다.         
		                     }
		                  }else node_2 = operand.cdr().car();// 첫번째 노드가 ' 입력이고, 두번째 노드가 IdNode와 ListNode가 아닌 경우, IntNode나 BooleanNode, 또는 피연산자 없이 연산자만 입력된 경우이다. 
		               }
		               else {// 두 번째 노드가 ListNode인 경우, 
		                  // 이 경우, 연산을 해야 하는 리스트와 연산을 하지 않는 QuoteNode가 붙은 리스트를 따로 처리한다.
		                  // QuoteNode가 붙은 경우, 비교할 노드 node_2는 runQuote를 통해 QuoteNode 내부 노드를 node_2로 한다.
		                  if(((ListNode)((ListNode)operand.cdr()).car()).car() instanceof QuoteNode) node_2 = runQuote((ListNode)operand.cdr().car());
		                  //QuoteNode가 붙지 않은 경우, 두 번째 노드에 대해 runExpr을 호출하여 node_2로 한다.
		                  else node_2 = runExpr(operand.cdr().car());
		               }
		            }else {//lookupTable을 통해 불러온 값이 ListNode가 아닌 경우, 즉 IntNode나 BooleanNode 처럼 상수 값인 경우,
		               
		               if(operand.cdr().car() instanceof IdNode) { //두 번째 노드가 IdNode일 경우,
		                  node_2 = lookupTable(((IdNode)operand.cdr().car()).toString());// lookupTable을 통해 두 번재 노드에 대해 저장된 값을 불러온다.
		                  if(node_2 == null) node_2 = operand.cdr().car();// 저장된 값이 없는 경우, IdNode 그대로 node_2에 저장한다.
		                  
		                  if(node_2 instanceof ListNode) {//lookupTable에서 불러온 값이 ListNode인 경우,
		                     node_2 = runQuote((ListNode)node_2); //변수에 값을 저장할 땐 연산이 끝난 상태로 저장되므로, 저장된 값이 리스트일 경우, 항상 QuoteNode가 붙어있다.
		                  }
		               }else if(operand.cdr().car() instanceof ListNode) { //두 번째 노드가 ListNode일 경우,
		                  //연산을 해야 하는 리스트와 연산을 하지 않는 QuoteNode가 붙은 리스트를 따로 처리한다.
		                  //QuoteNode가 붙은 경우, runQuote 메소드를 통해 QuoteNode 내부 노드를 node_2로 한다.
		                  if(((ListNode)((ListNode)operand.cdr()).car()).car() instanceof QuoteNode) node_2 = runQuote((ListNode)operand.cdr().car());
		                  else {//QuoteNode가 붙지 않은 경우, 연산을 진행한 후 결과 값을 받아와야 하므로 두 번째 노드에 대해 runExpr을 호출하여 node_2로 한다.
		                     node_2 = runExpr(operand.cdr().car());
		                  }
		               }
		               //오류 입력을 고려하지 않으므로 리스트에 담겨 있지 않은 피연산자가 있는 FunctionNode나 BinaryOpNode의 경우를 처리하지 않았다.
		               else node_2 = operand.cdr().car(); //두번째 노드가 IdNode, ListNode가 아닌 경우, 즉 IntNode나 BooleanNode일 경우,또는 피연산자 없이 연산자만 입력된 경우이다.
		            }            
		         //첫 번째 노드가 ListNode인 경우,
		         }else if(operand.car() instanceof ListNode) {

		            //QuoteNode가 붙은 ListNode의 경우, runQuote를 통해 QuoteNode 내부 노드를 node_1로 한다.
		            if(((ListNode)operand.car()).car() instanceof QuoteNode) node_1 = runQuote((ListNode)operand.car());
		            else node_1 = runExpr(operand.car()); // QuoteNode가 붙지 않은 경우, runExpr을 호출하여 연산을 하고 난 결과 값을 node_1로 한다.

		            if(node_1 instanceof IdNode) { //QuoteNode 내부 노드 또는 runExpr의 반환 노드가 IdNode일 경우,
		               //EQ_Q를 operator로 하고, 기존 operand의 첫 번째 노드 대신 새롭게 구한 node_1을 operand의 첫 번째 노드로 한 후, runFunction을 호출하여 반환 값을 리턴한다. 
		               return runFunction(operator, ListNode.cons(node_1, operand.cdr()));
		            }
		            
		            if(!(operand.cdr().car() instanceof ListNode)) {//operand의 두 번째 노드가 ListNode가 아닌 경우,
		               if(operand.cdr().car() instanceof IdNode) {//operand의 두 번째 노드가 IdNode인 경우
		                  node_2 = lookupTable(((IdNode)operand.cdr().car()).toString()); //lookupTable 메소드를 통해 두 번째 노드에 저장된 값을 불러온다.
		                  if(node_2 == null) node_2 = operand.cdr().car();// 저장된 값이 없는 경우 IdNode 그대로 node_2로 한다.
		                  
		                  if(node_2 instanceof ListNode) {//lookupTable의 반환 결과가 ListNode인 경우, 앞에서처럼 QuoteNode가 붙지 않은 리스트는 오류임으로 처리하지 않는다.
		                     node_2 = runQuote((ListNode)node_2); //runQuote메소드를 통해 내부노드를 받아 node_2로 한다.
		                  }
		               }else node_2 = operand.cdr().car(); //IdNode, ListNode가 아닌 경우, 바로 node_2에 저장한다.
		            }
		            else {//두 번째 노드가 ListNode일 경우,
		               //QuoteNode가 붙은 ListNode의 경우, runQuote를 통해 QuoteNode 내부 노드를 node_2로 한다.
		               if(((ListNode)((ListNode)operand.cdr()).car()).car() instanceof QuoteNode) node_2 = runQuote((ListNode)operand.cdr().car());
		               // QuoteNode가 붙지 않은 경우, runExpr을 호출하여 연산을 하고 난 결과 값을 node_2로 한다.
		               else node_2 = runExpr(operand.cdr().car());
		            }
		         }
		         else {//첫 번째 노드가 IntNode 또는 BooleanNode일 경우, (오류 입력은 고려하지 않으므로 리스트에 담겨 있지 않은 FunctionNode나 BinaryOpNode는 고려하지 않는다.) 
		            node_1 = operand.car();// 첫 번째 노드를 바로 node_1에 저장한다
		            if(!(operand.cdr().car() instanceof ListNode)) { // 두 번째 노드가 ListNode가 아닌 경우,
		               if(operand.cdr().car() instanceof IdNode) { // 두 번째 노드가 IdNode인 경우,
		                  node_2 = lookupTable(((IdNode)operand.cdr().car()).toString()); //lookupTable을 통해 두 번째 노드에 저장된 값을 불러온다.
		                  if(node_2 == null) node_2 = operand.cdr().car();// 저장된 값이 없는 경우 IdNode 그대로 node_2에 저장한다
		                  if(node_2 instanceof ListNode) node_2 = runQuote((ListNode)operand.cdr().car()); //lookupTable에서 불러온 값이 ListNode인 경우, QuoteNode가 붙은 것으로 runQuote를 통해 내부노드를 node_2로 한다.
		               }else node_2 = operand.cdr().car(); //두 번째 노드가 IntNode 또는 BooleanNode인 경우,
		            }else {//두 번째 노드가 ListNode인 경우, 
		               //QuoteNode가 붙은 ListNode의 경우, runQuote를 통해 QuoteNode 내부 노드를 node_2로 한다.
		               if(((ListNode)((ListNode)operand.cdr()).car()).car() instanceof QuoteNode) node_2 = runQuote((ListNode)operand.cdr().car());
		               // QuoteNode가 붙지 않은 경우, runExpr을 호출하여 연산을 하고 난 결과 값을 node_2로 한다.
		               else node_2 = runExpr(operand.cdr().car());
		            }
		         }
		         
		         if(node_1.toString().equals(node_2.toString())) return BooleanNode.TRUE_NODE; //같으면 TRUE_NODE를 반환한다.
		         else return BooleanNode.FALSE_NODE; //다른 출력을 가질 경우 FALSE_NODE를 리턴한다.
		   
		      case ATOM_Q: // operator.funcType이 ATOM_Q인경우 

		         //atom?은 리스트가 아닌 노드들에 대해 True값을 반환하는 연산을 수행한다.
		         if(operand.car() instanceof QuoteNode) { //첫 번째 노드가 QuoteNode인 경우, 
		            Node atomNode = runQuote((ListNode)operand);//피연산자에 대해 runQuote를 호출하여 QuoteNode 내부 노드를 받아와  atomNode에 저장한다.
		            if(atomNode instanceof ListNode) {//atomNode가 ListNode일 경우, EMPTY인 경우를 제외한 나머지는 false값을 갖는 BooleanNode를 반환한다.
		               if(((ListNode)atomNode).equals(ListNode.EMPTYLIST)) //EMPTY가 아닌 리스트를 갖는 경우 true값을 갖는 BooleanNode를 반환한다.
		                  return BooleanNode.TRUE_NODE;
		               return BooleanNode.FALSE_NODE; //QuoteNode 내부 노드가 ListNode 일때, EMPTY가 아닌 경우 FALSE_NODE를 리턴한다.
		            }else {//ListNode가 아닌 경우 true값을 갖는 BooleanNode를 반환한다.
		               return BooleanNode.TRUE_NODE;
		            }
		         }
		         break;

		      case DEFINE:  // operator.funcType이 DEFINE인경우
		         
		         //form 2 
		         //lambda 기호 없이 함수 define을 구현하기 위해 추가한 부분이다. 교재 13-2에 나오는 form-2에 해당하는 입력을 처리한다.
		         if(operand.car() instanceof ListNode) {//operand.car이 ListNode인 경우, 이 ListNode 안에는 함수의 이름과 formal parameter가 있다.
		            if(((ListNode)operand.car()).car() instanceof IdNode) {//operand.car().car()은 함수의 이름이다.
		               operator.setValue(TokenType.LAMBDA);//이를 교재 13-2에 나오는 form-1 형식으로 바꾸기 위해 operator를 lambda로 바꾸고 아래를 처리한다.
		               //lambda 연산자와 formal parameter, 그 뒤의 block과 body를 묶어 lambda 함수 선언을 의미하는 ListNode로 변환한다. 이를 함수 이름과 함께 테이블에 저장한다.
		               insertTable(((ListNode)operand.car()).car().toString(), ListNode.cons(operator, ListNode.cons(((ListNode)operand.car()).cdr(), ListNode.cons((ListNode)operand.cdr().car(), ListNode.EMPTYLIST))));
		               break;
		            }
		         }
		         if(operand.cdr().car() instanceof FunctionNode) { //operand.cdr().car()이 FunctionNode객체라면 이는 runFunction을 통해 계산을 한뒤에 HashMap의 노드로 저장해야한다.

		            Node funtion_result = runExpr(operand.cdr()); // runExpr(operand.cdr())을 하여 노드로 저장되어야하는 Node function_result에 저장한다.
		            insertTable( ((IdNode)operand.car()).toString() , funtion_result); //insertTable의 인자값으로 ((IdNode)operand.car()).toString()과 funtion_result을 주어 각각 id와 value로 둘이 매치되게 HashMap에 저장한다.

		         }else if(operand.cdr().car() instanceof IdNode) { //operand.cdr().car()이 IdNode객체라면 operand.cdr().car()이  HashMap에 id값으로 저장되어있는 정의된 변수일수도 있고 저장되어있지않은 id값일수도있다. else if문을 실행하여 이를 파악한다.

		            Node id_node = lookupTable(((IdNode)(operand.cdr().car())).toString()); //((IdNode)(operand.cdr().car())).toString()를 인자값으로 하여 lookupTable을 호출하여 반환값을 Node id_node에 저장한다.

		            if (id_node == null) { //id_node가 null인 경우 operand.cdr().car()가 HashMap에 저장된 정의된 변수 id값이 아니고 일반 정의되지않은 id값이라는 의미이다. 그러므로 원래의 operand.cdr().car()를 노드로 저장한다.
		               insertTable( ((IdNode)operand.car()).toString() , runExpr(operand.cdr().car()) ); //insertTable의 인자값으로 ((IdNode)operand.car()).toString()과 runExpr(operand.cdr().car())을 주어 각각 id와 value로 둘이 매치되게 HashMap에 저장한다.
		            }else { //아닐경우 operand.cdr().car()가  HashMap에 저장된 정의된 변수 id값이고 그에 매치되는 노드가 id_node에 반환되었다는 의미이다. 
		               insertTable( ((IdNode)operand.car()).toString() ,id_node) ; //그러므로 insertTable의 인자값으로 ((IdNode)operand.car()).toString()과 id_node을 주어 각각 id와 value로 둘이 매치되게 HashMap에 저장한다.
		            }

		            //form 1
		         }else if(operand.cdr().car() instanceof ListNode){ // define 연산에서는 operand의 car은 IdNode, 그 뒤에는 IdNode에 저장할 값을 갖는데, 이 값이 ListNode일 경우
		            ListNode lss = (ListNode)operand.cdr().car();//IdNode에 저장할 값이다.
		            if(lss.car() instanceof FunctionNode) {//lss의 car이 lambda일 경우, lss는 lambda 함수 선언 부분의 ListNode이며 교재 13-2에 나오는 form-1 해당하는 입력을 처리한다. 
		               if(((FunctionNode)lss.car()).funcType == FunctionNode.FunctionType.LAMBDA) {
		                  insertTable(operand.car().toString(), lss); //ListNode 안의 연산자가 LAMBDA일 경우 lss를 매칭시킬 함수 이름과 함께 테이블에 저장한다
		               }
		            }else {//FunctionNode가 아닐 경우, 값을 연산해야하는 리스트이거나 상수리스트 이므로 runExpr을 호출하여 idNode의 toString에 매칭시킨다.
		               insertTable(((IdNode)operand.car()).toString() , runExpr(operand.cdr().car()) );
		            }
		         }else {//위의 두 경우가 아닐경우 
		            insertTable( ((IdNode)operand.car()).toString() , runExpr(operand.cdr().car()) );  //insertTable의 인자값으로 ((IdNode)operand.car()).toString()과 runExpr(operand.cdr().car())을 주어 각각 id와 value로 둘이 매치되게 HashMap에 저장한다.
		         }
		         break;
		      case LAMBDA://operator.funcType이 LAMBDA인경우

		         ListNode parameter = null; //formal parameter 리스트를 저장한다.
		         ListNode body = null;//body 리스트를 저장한다

		         if (operand.car() instanceof FunctionNode) { //operand의 car이 functionNode인 경우는 매개변수 입력 없이 함수 선언만 있는 경우로, 이 경우는 연산을 수행하지 않고 list를 통째로 반환한다.
		            if (((FunctionNode) operand.car()).funcType == FunctionNode.FunctionType.LAMBDA) {
		               return operand; // 매개변수로 받은 list를 반환한다.
		            }
		         }
		         if (operand.car() instanceof ListNode) { //operand의 car이 LIstNode일 경우, car은 함수 선언이고 cdr에는 함수의 actual parameter가 있어 연산을 수행한다.

		            if (operand.cdr().equals(ListNode.EMPTYLIST)) {//operand의 cdr이 빈 ListNode인 경우, 이 역시 괄호 안에 함수 선언만 있고 actual parameter가 없는  경우로 list를 통째로 반환한다. 
		               return operand;//list를 통째로 반환한다.
		            }

		            if (((ListNode) operand.car()).cdr().car() instanceof ListNode) { // operand의 car은 함수 선언 ListNode이고, operand의 car의 cdr은 lambda 연산자를 제외한 나머지 노드들의 List이다. 
		               //operand.car().cdr().car()은 formal parameter를 의미한다. 그  뒤, operand.car().cdr().cdr()은 block과 body로 이루어진다
		               parameter = (ListNode) ((ListNode) operand.car()).cdr().car();
		            }
		            //block과 body로 이루어진 ListNode를 executeBlock 메소드에 매개변수로 넣어 block을 실행시키고 body를 반환받는다.
		            body = executeBlock(((ListNode) operand.car()).cdr().cdr());
		         }

		         //formal parameter에 operand.cdr 즉 actual parameter를 바인딩한다.
		         parameterBinding(parameter, operand.cdr());
		         ListNode readedBody = readBody(body); // readBody를 호출하여 body에 actual parameter를 대입시켜 반환받는다.
		         
		         Node endNode = runList(readedBody);//actual parameter를 대입 완료한 body를 runList를 통해 실행시킨 후 결과 노드를 endNode로 저장한다.
		         
		         deleteTable(parameter);//deleteTalbe 메소드를 통해 임시 테이블에 저장된 formal parameter에 대한 actual parameter의 바인딩을 해제한다.
		         return endNode;//endNode를 리턴한다.

		      default: // 위의 경우들이 아닌경우
		         break; // break를 하여 switch문을 나온다.
		      }
		      return null; // null을 리턴한다.
		   }

		   private void insertTable(String id, Node value) { // insertTable메소드는 매개변수로 받은 id와 value를 HashMap에 값을 저장하는 함수이다.
		      fromDefine.put(id, value); //fromDefine.put을 사용하여 HashMap에 id와 value를 저장하여 매치시킨다.

		   }

		   private Node lookupTable (String id) { // lookupTable메소드는 매개변수로 id 값을 받아 이를 HashMap에서 매치시켜 알맞은 노드를 반환한다.
		      return fromDefine.get(id); // fromDefine.get을 id을 인자값으로 하여 호출한뒤 HashMap에서 매치시켜 알맞은 노드를 반환하면 이를 반환한다.
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

		      BinaryOpNode operator = (BinaryOpNode) list.car(); //연산자를 operator에 저장한다
		      Integer result; //연산 결과값을 저장하는 변수

		      ListNode ln = list.cdr(); //피연산자를 ln에 저장한다.
		      IntNode first_operand; //피 연산자를 2개 있으며, 그 중 첫번째 피 연산자를 저장하는 변수다.
		      IntNode second_operand;//두번째 피연산자를 저장하는 변수

		      if(ln.car()  instanceof IdNode) { //피연산자의 첫 번째 노드가 IdNode인 경우, 
		         Node licar = ln.car();// 피 연산자의 첫번째 노드를 licar에 저장한 후, 
		         first_operand = (IntNode)lookupTable(((IdNode)licar).toString()); //lookupTable 메소드를 통해 변수 licar에 저장된 값을 불러와 first_operand에 저장한다. 
		      }else {// 피연산자의 첫번째 노드가 IdNode가 아닐 경우
		         //이 때 오류입력을 없다고 가정하였으므로, 첫번째 노드에 대해 runExpr을 호출했을 때 반환 노드는 IntNode여야 한다.
		         first_operand = (IntNode)runExpr(ln.car()); //피연산자의 첫번째 노드에 대해 runExpr을 호출하여 반환값을 first_operand에 저장한다.          
		      }

		      if((ln.cdr()).car()  instanceof IdNode) { //피 연산자의 두번째 노드가 IdNode인 경우,
		         second_operand = (IntNode)lookupTable(((IdNode)(ln.cdr()).car()).toString()); // 첫번째 노드와 마찬가지로 lookupTable에서 두번째 노드에 저장된 값을 불러와 second_operand에 저장한다.
		      }else {         
		         //이 때 오류입력을 없다고 가정하였으므로, 두번째 노드에 대해 runExpr을 호출했을 때 반환 노드는 IntNode여야 한다.
		         second_operand = (IntNode)runExpr((ln.cdr()).car()); //피연산자의 두 번째 노드에 대해 runExpr을 호출하여 반환값을 second_operand에 저장한다.
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
		   // lambda 함수의 경우, formal parameter에 actual parameter를 임시로 바인딩 시켰다가 함수 수행이 끝나면 해제하도록 한다.
		   // deleteTable은 임시 바인딩을 해제하는 메소드이다.
		   private void deleteTable(ListNode id) { //parameter들을 담고 있는 ListNode를 매개변수로 입력 받는다.
		      if(id.equals(ListNode.EMPTYLIST)) return;//parameter를 모두 탐색하여 빈 리스트만 남으면 리턴하여 종료한다.
		      
		      temp_table.remove(id.car().toString());//parameter의 toString() 값에 바인딩된 노드를 임시 바인딩 테이블 temp_table에서 삭제한다.
		      //재귀를 사용하여 parameter리스트를 모두 탐색한다.
		      deleteTable(id.cdr());
		   }
		   //formal parameter에 actual parameter를 바인딩하는 메소드이다.
		   private void parameterBinding(ListNode id, ListNode actual) {// formal parameter 리스트와 actual parameter 리스트를 매개변수로 입력받는다.
		      if(id.equals(ListNode.EMPTYLIST)|| actual.equals(ListNode.EMPTYLIST)) return; //formal parameter와 actual parameter의 개수는 같아야 하며, 개수가 같지 않더라도 그 뒤의 값은 바인딩하지 않는다. parameter를 모두 탐색하여 빈 리스트만 남으면 리턴하여 종료한다. 
		      
		      temp_table.put(id.car().toString(), actual.car());//temp_table이라는 임시 parameter 바인딩 테이블에 하나의 formal parameter toString 값과 하나의 actual parameter를 묶어 저장한다.
		       
		      id = id.cdr(); //첫 번째 노드에 대한 바인딩이 끝났기 때문에, 첫 번째 노드를 제외한 ListNode를 id로 한다. 
		      actual = actual.cdr();//첫 번째 노드에 대한 바인딩이 끝나, 첫 번째 노드를 제외한 ListNode를 actual로 한다. 
		      
		      //재귀를 사용하여 formal parameter와 actual parameter를 모두 탐색한다.
		      parameterBinding(id, actual);
		   }
		   //parameterBinding 메소드를 통해 formal parameter에 대한 actual parameter값을 temp_table에 저장했다.
		   //저장한 actual parameter를 body에 대입시키기 위한 메소드이다.
		   private ListNode readBody(ListNode body) {//ListNode body를 매개변수로 입력받는다.
		      if(body.equals(ListNode.EMPTYLIST)) return ListNode.EMPTYLIST; //body가 빈 ListNode일 경우 빈 ListNode를 리턴하여 종료한다.
		      
		      Node readed; //ListNode body의 첫번째 노드를 확인하여 temp_table에 있으면 그 값으로 바꾸고, temp_table에 없는 경우 첫 번째 노드를 그대로 사용한다. 이때 readed는 이 과정을 진행한 후의 첫 번째 노드값을 저장한다.
		      
		      if(temp_table.get(body.car().toString())!=null) {//첫번째 노드에 대한 actual parameter가 temp_table에 있는 경우,         
		         readed = temp_table.get(body.car().toString());//readed는 temp_table에서 찾은 값이다.
		         
		      }else {//temp_table에 없는 경우, 즉 대입할 값이 없는 경우
		         if(body.car() instanceof ListNode) {//body의 첫번째 노드가 ListNode일 경우,
		            readed = readBody((ListNode)body.car());//이 ListNode 내부에서도 actual parameter가 대입되어야 하기 때문에 body.car에 대하여 readBody를 호출한다.
		         }else {//ListNode가 아닌 경우
		            readed = body.car();//body.car을 그대로 저장한다.
		         }
		      }
		      return ListNode.cons(readed, readBody(body.cdr()));//readed를 첫번째 노드로 하고, body의 나머지 노드에 대하여 readBody를 호출한 후 반환값을 tail로 하여 반환한다.
		      
		   }
		   
		   //lambda 함수 내부에 define 구문 블럭이 있는 경우, 즉 중첩 구조를 사용하기 위해 추가한 메소드이다.
		   // lambda 함수를 선언할 때, lambda 연산자, formal parameter들, block들 (있을 수도 없을 수도 있다.), body와 같이 선언하는데
		   // executeBlock은 formal parameter 뒤의 노드들을 ListNode로 묶어 이 ListNode를 매개변수로 받아, block을 실행시키고, body를 반환한다. 
		   private ListNode executeBlock(ListNode blocks) {
		      if(blocks.cdr().equals(ListNode.EMPTYLIST)) {//blocks의 cdr이 빈 ListNode일 경우, blocks의 car은 body이다.
		         if(blocks.car() instanceof ListNode) {//body는 ListNode이어야 한다.
		            return (ListNode)blocks.car();// block이 없고 body만 있는 경우로, body를 반환하고 종료한다.
		         }else {//body가 ListNode가 아닌 경우, ListNode.cons를 이용해 ListNode로 만든 후 반환한다.
		            return ListNode.cons(blocks.car(), ListNode.EMPTYLIST);
		         }
		      }
		      
		      if(blocks.car() instanceof ListNode) runList((ListNode)blocks.car()); // blocks의 cdr이 빈 리스트노드가 아닌 경우, blocks의 car은 실행시켜야할 block으로 runList를 호출한다.
		      return executeBlock(blocks.cdr()); //block을 실행시킨 후, blocks의 cdr에 대해서도 위 과정을 반복하도록 호출한다.
		   }

		}