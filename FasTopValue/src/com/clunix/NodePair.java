package com.clunix;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

public class NodePair implements Serializable{
	Node n1;
	Node n2;
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
