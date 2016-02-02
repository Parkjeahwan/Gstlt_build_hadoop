package com.clunix.NLP.graph;
import java.io.Serializable;

public class NodePair implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Node n1;
	public Node n2;
	public NodePair(Node a,  Node b) { n1 = a; n2 = b; }
	public NodePair() { n1 = new Node(); n2 = new Node(); }
	/*@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(n1);
		out.writeObject(n2);
		
	}
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		n1 = (Node)in.readObject(); 
		n2 = (Node)in.readObject(); 
	}*/
	
}
