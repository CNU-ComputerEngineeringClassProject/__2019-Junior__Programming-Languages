package ast_node;

import java.util.Objects;

//public class IdNode extends Node{
//	public String value;
//
//	@Override
//	public String toString(){
//		return "ID:" + value;
//	}
//}

//public class IdNode implements ValueNode {   
//	// 새로 수정된 IdNode Class
//	String idString;   
//	
//	public IdNode(String text) { 
//		idString = text; 
//		} 
//	@Override  
//	public String toString(){   
//	return "ID:" + idString; 
//	} 
//}

public class IdNode implements ValueNode {    
	private String idString; 
	public IdNode(String text) {      
		idString = text;  
	}
	@Override 
	public boolean equals(Object o) {    
		if (this == o) return true;
		if (!(o instanceof IdNode)) return false;
		IdNode idNode = (IdNode) o;
		return Objects.equals(idString, idNode.idString);  
	}   
	@Override    
	public String toString() {    

		return idString;     


	} 
}