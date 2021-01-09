package ast_node;

//public class ListNode extends Node{
//	public Node value;
//}

//public interface ListNode extends Node { 
//	// 새로 수정된 ListNode 
//	static ListNode EMPTYLIST = new ListNode() {    
//		@Override         
//		public Node car() {         
//			return null;       
//		}
//		@Override       
//		public ListNode cdr() {           
//			return null;     
//		}   
//	};   
//	static ListNode ENDLIST = new ListNode() {    
//		@Override     
//		public Node car() {      
//			return null;        
//		}      
//		@Override    
//		public ListNode cdr() {       
//			return null;       
//		}    
//	};    
//	static ListNode cons(Node head, ListNode tail) {   
//		return new ListNode() {      
//
//			@Override           
//			public Node car() { 
//				return head;      
//			}            
//			@Override           
//			public ListNode cdr() {  
//				return tail;      
//			}      
//		}; 
//	}    
//	Node car();     
//	ListNode cdr(); 
//} 

public interface ListNode extends Node {    
	ListNode EMPTYLIST = new ListNode() { 

		@Override     
		public Node car() {     
			return null;  

		}

		@Override      
		public ListNode cdr() {     
			return null;     
		}
	}; 


	static ListNode cons(Node head, ListNode tail) { 
		return new ListNode() { 

			@Override           
			public Node car() {             
				return head;          
			} 

			@Override        
			public ListNode cdr() {             
				return tail;         
			} 

		};     } 

	Node car();     
	ListNode cdr(); 
} 
