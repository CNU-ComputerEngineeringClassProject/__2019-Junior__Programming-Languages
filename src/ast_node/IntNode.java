package ast_node;

import java.util.Objects;

//public class IntNode extends Node {
//	public int value;
//
//	@Override
//	public String toString(){
//		return "INT:" + value;
//	}
//}

//public class IntNode implements ValueNode { 
//	// 새로 수정된 IntNode 
//	private Integer value; 
//	@Override  
//	public String toString(){  
//		return "INT:" + value; 
//	}
//	public IntNode(String text) { 
//		this.value = new Integer(text);  
//	}
//
//}

public class IntNode implements ValueNode {
	private Integer value;  
	public IntNode(String text) {   
		this.value = new Integer(text); 
	}    
	public Integer getValue() {    
		return value; 
	}
	@Override   
	public boolean equals(Object o) {     
		if (this == o) return true;  
		if (!(o instanceof IntNode)) return false;
		IntNode intNode = (IntNode) o;     
		return Objects.equals(value, intNode.value); 
	}  
	@Override     
	public String toString() {   
		return value.toString();    
	}
} 
