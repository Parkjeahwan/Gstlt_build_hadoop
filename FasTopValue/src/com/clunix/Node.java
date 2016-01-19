package com.clunix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

// v1.1 Node class implementation : context has only 1 field
public class Node implements Serializable{
	public int ID;
	public String content;
	public int count;
	public boolean basic;
	public Contexts context;
	
/*	HashMap <Node,EdgeI> next;
	ArrayList <Node> bigN;
*/	
	ArrayList <NodePair> innerEdge;

	public Node(){
		context = new Contexts(-1);
		innerEdge = new ArrayList <NodePair> ();
	}
	public Node(String s){
		content = s;
		count = 1;
		basic = true;
		context = new Contexts((int)-1);
		innerEdge = new ArrayList <NodePair> ();
	}
	public Node(String s, boolean tf){
		content = s;
		count = 1;
		basic = tf;
		context = new Contexts((int)-1);;
		innerEdge = new ArrayList <NodePair> ();
	}
	public Node(String s, boolean tf, int line){
		content = s;
		count = 1;
		basic = tf;
		context = new Contexts(line);;
		innerEdge = new ArrayList <NodePair> ();
	}
	public Node(String s, boolean tf, int line, int offset){
		content = s;
		count = 1;
		basic = tf;
		context = new Contexts(line);;
		innerEdge = new ArrayList <NodePair> ();
	}
	public void fileout(BufferedWriter out) throws IOException {
		out.write(ID+"\n"+content+"\n"+count+"\n"+basic+"\n");
		out.write(context.map.size()+"\n");   						// write the number of appearance of this node
		for (int i :context.map) out.write(i+"\t"); out.write("\n"); // write the appearance locations of this node
		if (innerEdge !=  null) {for (NodePair x:innerEdge) out.write(x.n1.ID+","+x.n2.ID + "\t"); out.write("\n"); }
		else out.write("null"+"\n");
	}
	
	public int filein(BufferedReader br,ArrayList <Node> NodeAt) throws IOException{//HashMap <Integer,Node> NodeOf) throws IOException{
		String line = br.readLine();
		Node a2 = this;
		if (line == null) return -1;
		if (line != null) a2.ID = Integer.parseInt(line);
		if ((line = br.readLine()) != null) a2.content = line;
		if ((line = br.readLine()) != null) a2.count = Integer.parseInt(line);
		if ((line = br.readLine()) != null) a2.basic = Boolean.parseBoolean(line);
		if ((line = br.readLine()) != null) a2.context.map = new HashSet <Integer> (Integer.parseInt(line));
		if ((line = br.readLine()) != null) {  // processing for contexts
			String[]s1 = line.split("\t");
			for (String ts:s1) a2.context.map.add(Integer.parseInt(ts));
		}
		if ((line = br.readLine()) != null) {  // processing for inner edges (complex nodes)
			if (line.equals("null")) a2.innerEdge = null;
			else {
				String[]s1 = line.split("\t");
				for (String ts:s1) {
					String [] s2 = ts.split(",");
					if (s2[0].equals("")) continue;
					else a2.innerEdge.add(new NodePair(NodeAt.get(Integer.parseInt(s2[0])),NodeAt.get(Integer.parseInt(s2[1]))));
				}
			}
		}
		return ID;
	} // END Of filein function
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Node) {
			Node target = (Node) obj;
			return (target.basic == basic && target.content.equals(content) && target.context.equals( context) && target.count == count);
		}
		return super.equals(obj);
	}	
	
/*	Beginning of next and bigN DS included version
	public void addNext(Node a, Integer line) throws InterruptedException { 
		if (next == null) {
			next = new HashMap <Node, EdgeI>();
			bigN = new ArrayList<Node>();
		}
		if (next.containsKey(a)) {
			if (!bigN.contains(a)) System.exit(0);
			EdgeI edgei = next.get(a);
			int nc = ++edgei.weight;
			edgei.context.map.add(line);
			next.put(a,edgei);
			
			int i;
			int b = bigN.size();
			for (i=0;i<b;i++) {
				if (bigN.get(i)==a){//.content.equals(a.content))
					if ((i>=1 && next.get(bigN.get(i-1)).weight >= nc) || i == 0)	break;
//					for (Node xx:sortedI) System.out.print(xx.content+","); System.out.println("=> After remove");
					int j;
					for (j=i-1;j>=0 && !bigN.isEmpty() && next.get(bigN.get(j)).weight < nc; j--) ;
					j= (j<0)? 0:j+1;
//					for (Node xx:sortedI) {	System.out.print("("+xx.content+","+count.get(xx)+")");	}System.out.println(this+"=> Before Move");
					bigN.set(i, bigN.get(j));
					bigN.set(j, a);
//					for (Node xx:sortedI) {	System.out.print("("+xx.content+","+count.get(xx)+")");	}System.out.println(this+"=> After Move");
					break; 
					}
			}
		}
		else { 
			next.put(a,new EdgeI(1,line));
			bigN.add(a);
//			System.out.println(a.content);
		}
	}
	
	public void addNext(Node a) throws InterruptedException { 
		if (next == null) {
			next = new HashMap <Node, EdgeI>();
			bigN = new ArrayList<Node>();
		}
		
		if (next.containsKey(a)) {
			if (!bigN.contains(a)) System.exit(0);
			int nc;
			EdgeI edgei = next.get(a);
			nc = ++edgei.weight;
			next.put(a,edgei);
			int i;
			int b = bigN.size();
			for (i=0;i<b;i++) {
				if (bigN.get(i)==a){//.content.equals(a.content))
					if ((i>=1 && next.get(bigN.get(i-1)).weight >= nc) || i == 0)	break;
//					for (Node xx:bigN) System.out.print(xx.content+","); System.out.println("=> After remove");
					int j;
					for (j=i-1;j>=0 && !bigN.isEmpty() && next.get(bigN.get(j)).weight < nc; j--) ;
					j= (j<0)? 0:j+1;
//					for (Node xx:bigN) {	System.out.print("("+xx.content+","+count.get(xx)+")");	}System.out.println(this+"=> Before Move");
					bigN.set(i, bigN.get(j));
					bigN.set(j, a);
//					for (Node xx:bigN) {	System.out.print("("+xx.content+","+count.get(xx)+")");	}System.out.println(this+"=> After Move");
					break; 
					}
			}
		}
		else { 
			next.put(a,new EdgeI(1,-1));  
			bigN.add(a);
		}
	}
	public void delNext(Node a) { //count, location���� a�� Ű�� value���� ����;}
		next.remove(a);
	}
	
	public void print(ArrayList <String> C){
		for (Node x: next.keySet()) {
			System.out.println(content + " is next to ");
			int i = 0;
			System.out.print(x.content+" x "+next.get(x).weight+", ");
			if (i++%40 ==0) System.out.println( );
		}
		int i = 0;
		System.out.print(content+" was fount at line : ");
		for (int x:context.map) {
			System.out.print(C.get((int)x));
			if (++i>5) {System.out.println();break; }
		}
	}
	
	
	public ArrayList<Node> closeNext(int n) {
		ArrayList<Node> res = new ArrayList<Node>(n); 
		for (int i=0;i<n;i++) res.add(bigN.get(i));
		return (res.size()==0)? null:res;
	}
	public ArrayList<Node> commonNext(Node a) {  
		ArrayList<Node> res = new ArrayList<Node>(); 
		for (Node x:next.keySet()) if (a.next.containsKey(x)) res.add(x);
		return (res.size()==0)? null:res;
	}
	public ArrayList<Node> commonNext(Node a, int n) {  
		ArrayList<Node> res = new ArrayList<Node>();
		for (int i=0;i<next.size() && i<n;i++) if (a.next.containsKey(bigN.get(i))) res.add(bigN.get(i));
		return (res.size()==0)? null:res;
	}
	public boolean hasCommonNext(Node a) { 
		for (Node x:next.keySet()) if (a.next.containsKey(x)) return true;
		return false;
	}
	
	public Node getNext(int n){	return (bigN != null)? bigN.get(n):null; }  // return n-th frequently co-occurred node
	
	public boolean hasNext(Node a) { return next.containsKey(a)? true:false; }

	public void addNext(Node a, Integer line, int offset) throws InterruptedException {
		addNext(a,line);
	}

	public void fileout(BufferedWriter out) throws IOException {
		out.write(ID+"\n"+content+"\n"+count+"\n"+basic+"\n");
		out.write(bigN.size()+"\n");  //
		for (Node x:bigN) out.write(x.ID+"\t");			out.write("\n");
		
		out.write(next.size()+"\n");  // write number of next (neighbor) nodes
		
		for (Node x:next.keySet()) {  // write next nodes' informations
			out.write(x.ID+"\t");  					// write next node's ID 
			out.write(next.get(x).weight + "\t"); 	// write next node's weight
			out.write(next.get(x).context.map.size()+"\t");  // write number of next nodes' appearance
			for (int i:next.get(x).context.map) out.write(i+","); // write locations next node appeared at 
		}		out.write("\n");
		out.write(context.map.size()+"\n");   						// write the number of appearance of this node
		for (int i :context.map) out.write(i+"\t"); out.write("\n"); // write the appearance locations of this node
		for (NodePair x:innerEdge) out.write(x.n1.ID+","+x.n2.ID + "\t"); out.write("\n");
	}
	
	public int filein(BufferedReader br,HashMap <Integer,Node> NodeOf) throws IOException{
		String line = br.readLine();
		Node a2 = this;
		if (line == null) return -1;
		if (line != null) a2.ID = Integer.parseInt(line);
		if ((line = br.readLine()) != null) a2.content = line;
		if ((line = br.readLine()) != null) a2.count = Integer.parseInt(line);
		if ((line = br.readLine()) != null) a2.basic = Boolean.parseBoolean(line);
		if ((line = br.readLine()) != null) a2.bigN = new ArrayList <Node> (Integer.parseInt(line));
		if ((line = br.readLine()) != null) {  // processing for bigN
			String[]s1 = line.split("\t");
			for (String ts:s1) {
				if (!ts.equals("")) a2.bigN.add(NodeOf.get(Integer.parseInt(ts)));
				else break;
			}				
		}
		if ((line = br.readLine()) != null) a2.next = new HashMap <Node,EdgeI> (Integer.parseInt(line));
		if ((line = br.readLine()) != null) {  // processing for next
			String[]s1 = line.split("\t");
			EdgeI edgei = new EdgeI();
			int id = Integer.parseInt(s1[0]);
			edgei.weight = Integer.parseInt(s1[1]);
			edgei.context.map = new HashSet<Integer>(Integer.parseInt(s1[2]));
			String[] map = s1[3].split(",");
			for (String ts:map) {
				if (!ts.equals("")) edgei.context.map.add(Integer.parseInt(ts));
				else break;
			}
			a2.next.put(NodeOf.get(id), edgei);
		}
		if ((line = br.readLine()) != null) a2.context.map = new HashSet <Integer> (Integer.parseInt(line));
		if ((line = br.readLine()) != null) {  // processing for contexts
			String[]s1 = line.split("\t");
			for (String ts:s1) a2.context.map.add(Integer.parseInt(ts));
		}
		if ((line = br.readLine()) != null) {  // processing for contexts
			String[]s1 = line.split("\t");
			for (String ts:s1) {
				String [] s2 = line.split(",");
				a2.innerEdge.add(new NodePair(NodeOf.get(Integer.parseInt(s2[0])),NodeOf.get(Integer.parseInt(s2[1]))));
			}
		}
		
		return ID;
	} // END Of filein function
	*/ //End of bigN and next DS included version
	//ksu-External
	/*@Override
    public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(ID);
            out.writeObject(content);
            out.writeInt(count);
            out.writeBoolean(basic);
            out.writeObject(context);
            out.writeObject(edges);
            out.writeObject(innerEdge);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            ID = in.readInt();
            content = (String) in.readObject();
            count = in.readInt();
            basic = in.readBoolean();
            context = (Contexts)in.readObject();
            edges = (ArrayList<EdgeSet>) in.readObject();
            innerEdge = (ArrayList<NodePair>) in.readObject();
    }*/
	
}

/* v1.0 Node uses <Line, List <offset>> contexts 
public class Node implements Serializable{
	int ID;
	String content;
	int count;
	boolean basic;
	ArrayList <EdgeSet> edges;
	Contexts context;
	ArrayList <NodePair> innerEdge;


	public Node(){
		edges = new ArrayList<EdgeSet>();
		for (int i=0;i<10;i++) { edges.add(new EdgeSet()); edges.get(i).src = this;}
		context = new Contexts();
		innerEdge = new ArrayList <NodePair> ();
	}
	public Node(String s){
		content = s;
		basic = true;
		edges = new ArrayList<EdgeSet>();
		for (int i=0;i<10;i++) { edges.add(new EdgeSet()); edges.get(i).src = this;}
		context = new Contexts((int)-1,-1);
		innerEdge = new ArrayList <NodePair> ();
	}
	public Node(String s, boolean tf){
		content = s;
		basic = tf;
		edges = new ArrayList<EdgeSet>();
		for (int i=0;i<10;i++) { edges.add(new EdgeSet()); edges.get(i).src = this;}
		context = new Contexts((int)-1,-1);;
		innerEdge = new ArrayList <NodePair> ();
	}
	public Node(String s, boolean tf, int line, int offset){
		content = s;
		basic = tf;
		edges = new ArrayList<EdgeSet>();
		for (int i=0;i<10;i++) { edges.add(new EdgeSet()); edges.get(i).src = this;}
		context = new Contexts(line,offset);;
		innerEdge = new ArrayList <NodePair> ();
	}
	public void print(ArrayList <String> C){
		for (EdgeSet x: edges) {
			System.out.println(content + " is next to ");
			int i = 0;
			for (Node k:x.count.keySet()) {
				System.out.print(k.content+",");
				if (i++%80 ==0) System.out.println( );
			}
		}
		int i = 0;
		for (int x:context.map.keySet()) {
			System.out.println(C.get((int)x)+"'s "+context.map.get(x).get(0));
			if (++i>3) break;
		}
	}
	
	
}
*/