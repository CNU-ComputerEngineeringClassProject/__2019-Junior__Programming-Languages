package ast_node;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class NodePrinter {
	private final String OUTPUT_FILENAME = "output08.txt";
	private StringBuffer sb = new StringBuffer();
	private Node root;
	public NodePrinter(Node root){
		this.root = root;
	}


	// ListNode, QuoteNode, Node에 대한 printNode 함수를 각각 overload 형식으로 작성

	private void printList(ListNode listNode) { //ListNode일때 출력하기위한 void형 printList메소드이다. 매개변수로 ListNode listNode를 받는다.  
		if (listNode == ListNode.EMPTYLIST) { //만약 listNode가 ListNode에서 정의한 비어있는 노드일경우
			sb.append("( )"); // "( )"을 버퍼 sb에 저장한다.
			return; //리턴한다. (함수를 종료한다.)
		}  
//		if (listNode == ListNode.ENDLIST) { //만약 listNode가 ListNode에서 정의한 끝난리스트 일경우 
//			return;  //리턴한다.(함수를 종료한다.)
//		}  
		
		/* 이후 부분을 주어진 출력 형식에 맞게 코드를 작성하시오. */
		//위의 두경우가 아니라면 listNode에는 원소node가 있다는 의미이다.
		sb.append("("); //버퍼sb에 리스트 시작을 알리는 "("를 저장한다.
		sb.append(" "); //" "를 버퍼에 저장하여 사이를 띄우도록 한다.
		printNode(listNode.car()); //listNode 안에서 처음 원소인 head를 버퍼에 저장하기위한 listNode.car()를 인자로 하여 printNode를 호출한다.
		ListNode listN = listNode.cdr(); //이제 listNode의 tail부분인 listNode.cdr()를 사용하여 listNode의 다음 원소들을 차례로 버퍼에 저장할것이다. ListNode listN객체를 생성하고 list.cdr()을 가져와 저장한다.
		while(listN.car() != null) { //while문을 이용하여 listNode의 원소node들을 모두 버퍼에 저장할때까지 반복한다. 이는 listN.car()이 null이 될때까지 반복하는 것으로 표현하였다.
			printNode(listN.car()); //listN 안에서의 처음 원소인 head를 버퍼에 저장하기위한 listN.car()를 인자로 하여 printNode를 호출한다.
			listN = listN.cdr();//listN에 listN.cdr()을 저장한다. 이는 리스트의 다음 원소를 head로하는 리스트 노드를 만들어 head를 버퍼에 저장하기 위함이다. 
		}
		sb.append(")"); //listNode의 원소node를 다 출력하였으면 마지막으로 ")"를 버퍼에 저장하여 리스트의 끝을 알린다.
	} 
	
	
	private void printNode(QuoteNode quoteNode) { //QuoteNode일때 출력하기위한 void형 printNode메소드이다. 매개변수로 QuoteNode quoteNode를 받는다. 
		if (quoteNode.nodeInside() == null)   //quoteNode.nodeInside()가 null일경우 quote노드 안에 원소가 없다는 의미로 기호 apostrophe(')와 quote노드 안에 원소노드를 출력하지 않는다.
			return; //리턴한다.(함수를 종료한다.)
		
		/* 이후 부분을 주어진 출력 형식에 맞게 코드를 작성하시오.*/  
		//위의 경우가 아니라면  quote노드 안에 원소가 있다는 의미로 quote노드 안의 리스트를 출력할것이다.
		sb.append("'");// quote노드는 기호 apostrophe(')를 버퍼sb에 저장한다.
		printNode(quoteNode.nodeInside()); //quoteNode.nodeInside()출력하기위하여 버퍼안에 저장할것이다. 이를 인자로 하여 printNode 함수를 호출한다. 
	}
	
	
	private void printNode(Node node) { //Node일때 출력하기위한 void형 printNode메소드이다. 매개변수로 Node node를 받는다.   
		if (node == null) //node가 null일경우 출력할것이 없다.
			return; //리턴한다.(함수를 종료한다.)
		
		/* 이후 부분을 주어진 출력 형식에 맞게 코드를 작성하시오.  */

		if (node instanceof ListNode) { //만약 node가 ListNode형 객체라면 true가 반환되여 if문을 실행한다.
			ListNode ln = (ListNode) node; //ListNode객체 ln을 생성하고 node를 ListNode로 형변환하여 ln에 저장한다.
			if (ln.car() instanceof QuoteNode ) { //만약 리스트에 처음에 들어있는데 quote인경우 괄호를 출력하지 않는다. 그러므로 ln.car()이  QuoteNode형 객체라면 true가 반환되여 if문을 실행한다. ln.car()는 ln리스트노드의 맨처음 원소node head를 가리킨다.
				QuoteNode qn = (QuoteNode) ln.car(); //QuoteNode객체 qn을 생성하고 ln.car()를 QuoteNode로 형변환하여 qn에 저장한다.
				printNode(qn); //QuoteNode형 qn을 (출력하기 위해 버퍼에 저장)버퍼에 저장하기위하여 qn을 인자로 하는 printNode를 호출한다.
			}else {//아니라면
				printList(ln); //리스트안에 있는 원소node를 (출력하기 위해 버퍼에 저장)버퍼에 저장하기위하여 ln을 인자로하는 printList를 호출한다.
			}
		}
//		else if(node instanceof QuoteNode) {
//			QuoteNode qn = (QuoteNode)node;
//			printNode(qn);
//		}
		else{ //node가 ListNode객체도 QuoteNode객체도 아니라면 node는 ValueNode라는 의미이다.
//			sb.append("[" + (ValueNode)node + "]"); //node를 ValueNode로 형변환하고 node의 앞과 뒤에 "[" , "]"괄호를 더하여 sb버퍼에 저장한다.
			sb.append((ValueNode)node); 
		}

		if(node != null){ //node가 null이 아니라면 
			sb.append(" "); //" "를 버퍼에 저장하여 사이를 띄우도록 한다.
		}

	} 
	public void prettyPrint() {   
		printNode(root);       
		System.out.println(sb.toString());
		
//		try (
//				FileWriter fw = new FileWriter(OUTPUT_FILENAME);   
//				PrintWriter pw = new PrintWriter(fw)) {  
//			pw.write(sb.toString());       
//		} catch (IOException e) {        
//			e.printStackTrace();       
//		}
	}
} 