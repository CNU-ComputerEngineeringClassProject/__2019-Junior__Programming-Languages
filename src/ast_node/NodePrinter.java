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


	// ListNode, QuoteNode, Node�� ���� printNode �Լ��� ���� overload �������� �ۼ�

	private void printList(ListNode listNode) { //ListNode�϶� ����ϱ����� void�� printList�޼ҵ��̴�. �Ű������� ListNode listNode�� �޴´�.  
		if (listNode == ListNode.EMPTYLIST) { //���� listNode�� ListNode���� ������ ����ִ� ����ϰ��
			sb.append("( )"); // "( )"�� ���� sb�� �����Ѵ�.
			return; //�����Ѵ�. (�Լ��� �����Ѵ�.)
		}  
//		if (listNode == ListNode.ENDLIST) { //���� listNode�� ListNode���� ������ ��������Ʈ �ϰ�� 
//			return;  //�����Ѵ�.(�Լ��� �����Ѵ�.)
//		}  
		
		/* ���� �κ��� �־��� ��� ���Ŀ� �°� �ڵ带 �ۼ��Ͻÿ�. */
		//���� �ΰ�찡 �ƴ϶�� listNode���� ����node�� �ִٴ� �ǹ��̴�.
		sb.append("("); //����sb�� ����Ʈ ������ �˸��� "("�� �����Ѵ�.
		sb.append(" "); //" "�� ���ۿ� �����Ͽ� ���̸� ��쵵�� �Ѵ�.
		printNode(listNode.car()); //listNode �ȿ��� ó�� ������ head�� ���ۿ� �����ϱ����� listNode.car()�� ���ڷ� �Ͽ� printNode�� ȣ���Ѵ�.
		ListNode listN = listNode.cdr(); //���� listNode�� tail�κ��� listNode.cdr()�� ����Ͽ� listNode�� ���� ���ҵ��� ���ʷ� ���ۿ� �����Ұ��̴�. ListNode listN��ü�� �����ϰ� list.cdr()�� ������ �����Ѵ�.
		while(listN.car() != null) { //while���� �̿��Ͽ� listNode�� ����node���� ��� ���ۿ� �����Ҷ����� �ݺ��Ѵ�. �̴� listN.car()�� null�� �ɶ����� �ݺ��ϴ� ������ ǥ���Ͽ���.
			printNode(listN.car()); //listN �ȿ����� ó�� ������ head�� ���ۿ� �����ϱ����� listN.car()�� ���ڷ� �Ͽ� printNode�� ȣ���Ѵ�.
			listN = listN.cdr();//listN�� listN.cdr()�� �����Ѵ�. �̴� ����Ʈ�� ���� ���Ҹ� head���ϴ� ����Ʈ ��带 ����� head�� ���ۿ� �����ϱ� �����̴�. 
		}
		sb.append(")"); //listNode�� ����node�� �� ����Ͽ����� ���������� ")"�� ���ۿ� �����Ͽ� ����Ʈ�� ���� �˸���.
	} 
	
	
	private void printNode(QuoteNode quoteNode) { //QuoteNode�϶� ����ϱ����� void�� printNode�޼ҵ��̴�. �Ű������� QuoteNode quoteNode�� �޴´�. 
		if (quoteNode.nodeInside() == null)   //quoteNode.nodeInside()�� null�ϰ�� quote��� �ȿ� ���Ұ� ���ٴ� �ǹ̷� ��ȣ apostrophe(')�� quote��� �ȿ� ���ҳ�带 ������� �ʴ´�.
			return; //�����Ѵ�.(�Լ��� �����Ѵ�.)
		
		/* ���� �κ��� �־��� ��� ���Ŀ� �°� �ڵ带 �ۼ��Ͻÿ�.*/  
		//���� ��찡 �ƴ϶��  quote��� �ȿ� ���Ұ� �ִٴ� �ǹ̷� quote��� ���� ����Ʈ�� ����Ұ��̴�.
		sb.append("'");// quote���� ��ȣ apostrophe(')�� ����sb�� �����Ѵ�.
		printNode(quoteNode.nodeInside()); //quoteNode.nodeInside()����ϱ����Ͽ� ���۾ȿ� �����Ұ��̴�. �̸� ���ڷ� �Ͽ� printNode �Լ��� ȣ���Ѵ�. 
	}
	
	
	private void printNode(Node node) { //Node�϶� ����ϱ����� void�� printNode�޼ҵ��̴�. �Ű������� Node node�� �޴´�.   
		if (node == null) //node�� null�ϰ�� ����Ұ��� ����.
			return; //�����Ѵ�.(�Լ��� �����Ѵ�.)
		
		/* ���� �κ��� �־��� ��� ���Ŀ� �°� �ڵ带 �ۼ��Ͻÿ�.  */

		if (node instanceof ListNode) { //���� node�� ListNode�� ��ü��� true�� ��ȯ�ǿ� if���� �����Ѵ�.
			ListNode ln = (ListNode) node; //ListNode��ü ln�� �����ϰ� node�� ListNode�� ����ȯ�Ͽ� ln�� �����Ѵ�.
			if (ln.car() instanceof QuoteNode ) { //���� ����Ʈ�� ó���� ����ִµ� quote�ΰ�� ��ȣ�� ������� �ʴ´�. �׷��Ƿ� ln.car()��  QuoteNode�� ��ü��� true�� ��ȯ�ǿ� if���� �����Ѵ�. ln.car()�� ln����Ʈ����� ��ó�� ����node head�� ����Ų��.
				QuoteNode qn = (QuoteNode) ln.car(); //QuoteNode��ü qn�� �����ϰ� ln.car()�� QuoteNode�� ����ȯ�Ͽ� qn�� �����Ѵ�.
				printNode(qn); //QuoteNode�� qn�� (����ϱ� ���� ���ۿ� ����)���ۿ� �����ϱ����Ͽ� qn�� ���ڷ� �ϴ� printNode�� ȣ���Ѵ�.
			}else {//�ƴ϶��
				printList(ln); //����Ʈ�ȿ� �ִ� ����node�� (����ϱ� ���� ���ۿ� ����)���ۿ� �����ϱ����Ͽ� ln�� ���ڷ��ϴ� printList�� ȣ���Ѵ�.
			}
		}
//		else if(node instanceof QuoteNode) {
//			QuoteNode qn = (QuoteNode)node;
//			printNode(qn);
//		}
		else{ //node�� ListNode��ü�� QuoteNode��ü�� �ƴ϶�� node�� ValueNode��� �ǹ��̴�.
//			sb.append("[" + (ValueNode)node + "]"); //node�� ValueNode�� ����ȯ�ϰ� node�� �հ� �ڿ� "[" , "]"��ȣ�� ���Ͽ� sb���ۿ� �����Ѵ�.
			sb.append((ValueNode)node); 
		}

		if(node != null){ //node�� null�� �ƴ϶�� 
			sb.append(" "); //" "�� ���ۿ� �����Ͽ� ���̸� ��쵵�� �Ѵ�.
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