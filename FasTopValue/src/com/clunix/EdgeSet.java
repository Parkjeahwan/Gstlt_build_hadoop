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


// EdgeSet v2.0 Contexts is just hashset of contexts
public class EdgeSet implements Serializable{ // contains a set of edges connected to a 'src' node. There are as many Edgesets as src nodes.  
	Node src;	
	public HashMap <Node, Integer> count ;
	public ArrayList <Node> sortedI;
	public HashMap <Node, Contexts> contexts ;
										
	// methods
/*	public EdgeSet(){
		count = new HashMap<Node,Integer> ();
		sortedI =  new ArrayList <Node>() ;
		contexts = new HashMap <Node, Contexts> ();
	}*/
	public EdgeSet(Node x){
		src = x;
		count = new HashMap<Node,Integer> ();
		sortedI =  new ArrayList <Node>() ;
		contexts = new HashMap <Node, Contexts> ();
	}
/*	public EdgeSet(int n){
		count = new HashMap<Node,Integer> (n);
		sortedI =  new ArrayList <Node>(n) ;
		contexts = new HashMap <Node, Contexts> (n);
	}
	*/
	
	public void fileout(BufferedWriter out) throws IOException {
//		System.out.println(src.ID);
//		out.write(src.ID+"\n");
		if (sortedI == null ) out.write("null"+"\n");
		else {
			out.write(sortedI.size()+"\n");  			// write sortedI size
			for (Node x:sortedI) out.write(x.ID+"\t");	// write sortedI 
			out.write("\n");
//		} 
//		if (count == null) out.write("null"+"\n");	
//		else {
			for (Node x:count.keySet()) {  // write next nodes' informations
				out.write(x.ID+"\t");  					// write next node's ID 
				out.write(count.get(x) + "\t"); 	// write next node's weight
				out.write(contexts.get(x).map.size()+"\t");  // write number of next nodes' appearance
				for (int i:contexts.get(x).map) out.write(i+","); // write locations next node appeared at
				out.write("\n");
			}
		}
	}
	
	public void filein(BufferedReader br,ArrayList <Node> NodeAt) throws IOException {
		String line;// = br.readLine();
//		if (line == null || line.equals("null")) return;
//		else src = NodeAt.get(Integer.parseInt(line));
		int size = 0;
		
		if ((line = br.readLine()) != null) {
//			System.out.println("line: "+line);
			if (line.equals("null")) { sortedI = null; count = null; contexts = null; }
			else {
				sortedI = new ArrayList <Node> (size = Integer.parseInt(line));
				if ((line = br.readLine()) != null) {  // processing for bigN
					String[]s1 = line.split("\t");
					for (String ts:s1) {
						if (!ts.equals("")) sortedI.add(NodeAt.get(Integer.parseInt(ts)));
						else break;
					}
				}
				count = new HashMap <Node,Integer> (size);
				for (int i=0;i<size;i++)
					if ((line = br.readLine()) != null) {  // processing for next
						String[]s1 = line.split("\t");
						Node b = NodeAt.get(Integer.parseInt(s1[0]));
						int occurrence = Integer.parseInt(s1[1]);  // number of occurrence
						count.put(b, occurrence);
						contexts = new HashMap <Node,Contexts>(occurrence); // contexts number == count numver
						Contexts c = new Contexts();
						c.map = new HashSet(Integer.parseInt(s1[2]));
						String[] map = s1[3].split(",");
						for (String ts:map) {
							if (!ts.equals("")) c.map.add(Integer.parseInt(ts));
							else break;
						}
						contexts.put(b, c);
					}
			}
			
		}
	} // END Of filein function

	
	public ArrayList<Node> common(int n) {
		ArrayList<Node> res = new ArrayList<Node>(n); 
		for (int i=0;i<n;i++) res.add(sortedI.get(i));
		return (res.size()==0)? null:res;
	}
	public HashSet<Node> intersect(EdgeSet a) {  
		if (count == null || a == null) return null;
		HashSet<Node> res = new HashSet<Node>(); 
		for (Node x:count.keySet()) if (a.count.containsKey(x)) res.add(x);
		return (res.size()==0)? null:res;
	}
	public HashSet<Node> intersect(EdgeSet a, int n) {  
		if (count == null || a == null) return null;
		int size = count.size();
		HashSet<Node> res = new HashSet<Node>();
		if (n < 0) n = size;
		for (int i=0;i<size && i<n;i++) if (a.count.containsKey(sortedI.get(i))) res.add(sortedI.get(i));
		return (res.size()==0)? null:res;
	}
	public HashSet<Node> intersect(HashSet<Node> a, int n) {
		if (count == null || a == null) return null;
		int size = count.size();
		HashSet<Node> res = new HashSet<Node>();
		n = n >= 0 ?  n:size; 
		for (int i=0;i<size && i<n;i++) if (a.contains(sortedI.get(i))) res.add(sortedI.get(i));
		return (res.size()==0)? null:res;
	}
	public boolean isIntersectNull(EdgeSet a) { 
		for (Node x:count.keySet()) if (a.count.containsKey(x)) return true;
		return false;
	}
	
	public Node get(int n){	return (sortedI != null)? sortedI.get(n):null; }  // return n-th frequently co-occurred node
	
	public boolean hasNext(Node a) { return count == null ? false: count.containsKey(a); }

	public void addNext(Node a, Integer line, int offset) throws InterruptedException {
		addNext(a,line);
	}
	
	public void addNext(Node a, Integer line) throws InterruptedException { 
		if (count == null) {
			count = new HashMap <Node, Integer>();
			sortedI = new ArrayList<Node>();
			contexts = new HashMap <Node,Contexts>();
		}
		if (count.containsKey(a)) {
			if (!sortedI.contains(a)) System.exit(0);
			int nc= count.get(a);
			count.put(a,++nc);
			
			int i;
			int b = sortedI.size();
			for (i=0;i<b;i++) {
				if (sortedI.get(i)==a){//.content.equals(a.content))
					if ((i>=1 && count.get(sortedI.get(i-1)) >= nc) || i == 0)	break;
//					for (Node xx:sortedI) System.out.print(xx.content+","); System.out.println("=> After remove");
					int j;
					for (j=i-1;j>=0 && !sortedI.isEmpty() && count.get(sortedI.get(j)) < nc; j--) ;
					j= (j<0)? 0:j+1;
//					for (Node xx:sortedI) {	System.out.print("("+xx.content+","+count.get(xx)+")");	}System.out.println(this+"=> Before Move");
					sortedI.set(i, sortedI.get(j));
					sortedI.set(j, a);
//					for (Node xx:sortedI) {	System.out.print("("+xx.content+","+count.get(xx)+")");	}System.out.println(this+"=> After Move");
					break; 
					}
			}
		}
		else { 
			count.put(a,1);
			sortedI.add(a);
//			System.out.println(a.content);
		}
		if (contexts.containsKey(a)) contexts.get(a).map.add(line);
		else contexts.put(a, new Contexts(line));
	}
	
	public void addNext(Node a) throws InterruptedException { 
		if (count == null) {
			count = new HashMap <Node, Integer>();
			sortedI = new ArrayList<Node>();
			contexts = new HashMap <Node,Contexts>();
		}
		
		if (count.containsKey(a)) {
			if (!sortedI.contains(a)) System.exit(0);
			int nc;

			count.put(a,nc = count.get(a)+1);
			int i;
			int b = sortedI.size();
			for (i=0;i<b;i++) {
				if (sortedI.get(i)==a){//.content.equals(a.content))
					if ((i>=1 && count.get(sortedI.get(i-1)) >= nc) || i == 0)	break;
//					for (Node xx:sortedI) System.out.print(xx.content+","); System.out.println("=> After remove");
					int j;
					for (j=i-1;j>=0 && !sortedI.isEmpty() && count.get(sortedI.get(j)) < nc; j--) ;
					j= (j<0)? 0:j+1;
//					for (Node xx:sortedI) {	System.out.print("("+xx.content+","+count.get(xx)+")");	}System.out.println(this+"=> Before Move");
					sortedI.set(i, sortedI.get(j));
					sortedI.set(j, a);
//					for (Node xx:sortedI) {	System.out.print("("+xx.content+","+count.get(xx)+")");	}System.out.println(this+"=> After Move");
					break; 
					}
			}
		}
		else { 
			count.put(a,1);  
			sortedI.add(a);
		}
		if (contexts.containsKey(a)) contexts.get(a).map.add(-1);
		else contexts.put(a, new Contexts(-1));
	}
	public void delNext(Node a) { //count, location���� a�� Ű�� value���� ����;}
		count.remove(a);
	}

	
	/*@Override
    public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(size);
            out.writeObject(src);
            out.writeObject(count);
            out.writeObject(sortedI);
            out.writeObject(contexts);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            size = in.readInt();
            src = (Node)in.readObject();                
            count = (HashMap<Node, Integer>)in.readObject();
            
            sortedI = (ArrayList<Node>)in.readObject();
            contexts = (HashMap<Node, Contexts>)in.readObject();
            
    } */
}


// EdgeSet v1.0 : This version uses context of format <line, list <offset>>
/*
public class EdgeSet implements Serializable{
	int size;
	Node src;	
	HashMap <Node, Integer> count ;
	ArrayList <Node> sortedI;
	HashMap <Node, Contexts> contexts ;
										
	// methods
	public ArrayList<Node> common(int n) {
		ArrayList<Node> res = new ArrayList<Node>(n); 
		for (int i=0;i<n;i++) res.add(sortedI.get(i));
		return (res.size()==0)? null:res;
	}
	public ArrayList<Node> intersect(EdgeSet a) {  
		ArrayList<Node> res = new ArrayList<Node>(); 
		for (Node x:count.keySet()) if (a.count.containsKey(x)) res.add(x);
		return (res.size()==0)? null:res;
	}
	public ArrayList<Node> intersect(EdgeSet a, int n) {  
		ArrayList<Node> res = new ArrayList<Node>();
		for (int i=0;i<size && i<n;i++) if (a.count.containsKey(sortedI.get(i))) res.add(sortedI.get(i));
		return (res.size()==0)? null:res;
	}
	public boolean isIntersectNull(EdgeSet a) { 
		for (Node x:count.keySet()) if (a.count.containsKey(x)) return true;
		return false;
	}
	public boolean hasNext(Node a) { return count.containsKey(a)? true:false; }

	public void addNext(Node a, Integer line, int offset) throws InterruptedException { 
		if (count == null) {
			count = new HashMap <Node, Integer>();
			sortedI = new ArrayList<Node>();
			contexts = new HashMap <Node,Contexts>();
		}
		if (count.containsKey(a)) {
			if (!sortedI.contains(a)) System.exit(0);
			int nc= count.get(a);
			count.put(a,++nc);
			ArrayList <Integer> x = contexts.get(a).map.get(line) ;
			if (x != null) x.add(offset);
			else {
				x = new ArrayList<Integer>();
				x.add(offset);
				contexts.get(a).map.put(line, x);
			}

			int i;
			int b = sortedI.size();
			for (i=0;i<b;i++) {
				if (sortedI.get(i)==a){//.content.equals(a.content))
					if ((i>=1 && count.get(sortedI.get(i-1)) >= nc) || i == 0)	break;
//					for (Node xx:sortedI) System.out.print(xx.content+","); System.out.println("=> After remove");
					int j;
					for (j=i-1;j>=0 && !sortedI.isEmpty() && count.get(sortedI.get(j)) < nc; j--) ;
					j= (j<0)? 0:j+1;
//					for (Node xx:sortedI) {	System.out.print("("+xx.content+","+count.get(xx)+")");	}System.out.println(this+"=> Before Move");
					sortedI.set(i, sortedI.get(j));
					sortedI.set(j, a);
//					for (Node xx:sortedI) {	System.out.print("("+xx.content+","+count.get(xx)+")");	}System.out.println(this+"=> After Move");
					break; 
					}
			}
		}
		else { 
			count.put(a,1);
			Contexts c = new Contexts(line,offset);
			contexts.put(a, c);
			sortedI.add(a);
//			System.out.println(a.content);
			size++;
		}
	}
	
	public void addNext(Node a) throws InterruptedException { 
		if (count == null) {
			count = new HashMap <Node, Integer>();
			sortedI = new ArrayList<Node>();
			contexts = new HashMap <Node,Contexts>();
		}
		
		if (count.containsKey(a)) {
			if (!sortedI.contains(a)) System.exit(0);
			int nc;
			count.put(a,nc = count.get(a)+1);
			int i;
			int b = sortedI.size();
			for (i=0;i<b;i++) {
				if (sortedI.get(i)==a){//.content.equals(a.content))
					if ((i>=1 && count.get(sortedI.get(i-1)) >= nc) || i == 0)	break;
//					for (Node xx:sortedI) System.out.print(xx.content+","); System.out.println("=> After remove");
					int j;
					for (j=i-1;j>=0 && !sortedI.isEmpty() && count.get(sortedI.get(j)) < nc; j--) ;
					j= (j<0)? 0:j+1;
//					for (Node xx:sortedI) {	System.out.print("("+xx.content+","+count.get(xx)+")");	}System.out.println(this+"=> Before Move");
					sortedI.set(i, sortedI.get(j));
					sortedI.set(j, a);
//					for (Node xx:sortedI) {	System.out.print("("+xx.content+","+count.get(xx)+")");	}System.out.println(this+"=> After Move");
					break; 
					}
			}
		}
		else { 
			count.put(a,1);  
			sortedI.add(a);
			size++;
		}
		contexts.get(a).map.put((int)-1, null);

	}
	public void delNext(Node a) { //count, location���� a�� Ű�� value���� ����;}
		count.remove(a);
	}

}
*/