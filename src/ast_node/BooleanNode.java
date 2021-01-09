package ast_node;

//public class BooleanNode extends Node{
//	public boolean value;
//
//	@Override
//	public String toString(){
//		return value ? "#T" : "#F";
//	}
//}


public class BooleanNode implements ValueNode { 
	// 새로 수정된 BooleanNode Class   
	public static BooleanNode FALSE_NODE = new BooleanNode(false); 
	public static BooleanNode TRUE_NODE = new BooleanNode(true); 

	Boolean value;    

	private BooleanNode(Boolean b){ 
		value = b;  
	}

	
	@Override 
	public String toString(){ 
		return value ? "#T" : "#F"; 

	}  
	
	
} 
