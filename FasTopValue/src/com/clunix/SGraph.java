package com.clunix;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SGraph {
	public ArrayList <Node> node;
	public HashMap <String,Node> G;
	public ArrayList <EdgeSet> PREV;   // PREV.get(i) contains EdgeSet of a node ID = i
	public ArrayList <EdgeSet> SUCC;
	public ArrayList <EdgeSet> EQUI;
	
	public SGraph(){
		G = new HashMap <String,Node> ();
		node = new ArrayList<Node>();
		PREV = new ArrayList <EdgeSet>();
		SUCC = new ArrayList <EdgeSet>();
		EQUI = new ArrayList <EdgeSet>();
	}
	
	public Node addNode(String cw) {
		Node nb;
		if (!G.containsKey(cw)) {
			nb = new Node(cw);
			nb.ID = G.size();
			G.put(cw, nb);
			node.add(nb);
			PREV.add(new EdgeSet(nb));
			SUCC.add(new EdgeSet(nb));
			EQUI.add(new EdgeSet(nb));
		}
		else nb = G.get(cw);
		return nb;
	}
	
	public Node addNode(Node nb) {
		if (!G.containsKey(nb.content)) {
			nb.ID = G.size();
			G.put(nb.content, nb);
			node.add(nb);
			PREV.add(new EdgeSet(nb));
			SUCC.add(new EdgeSet(nb));
			EQUI.add(new EdgeSet(nb));
		}
		return nb;
	}
	
	public void showNext(String s) {
		Node ns = G.get(s);
		System.out.println("Prev Nodes:");
		for (Node x:PREV.get(ns.ID).sortedI) 
			System.out.println("\t"+x.content+", "+PREV.get(ns.ID).count.get(x));
		
		System.out.println("Succ Nodes:");
		for (Node x:SUCC.get(ns.ID).sortedI) 
			System.out.println("\t"+x.content+", "+SUCC.get(ns.ID).count.get(x));
	}
	public void showNext(String s, int n) {
		Node ns = G.get(s);
		int i = 0;
		System.out.println("Prev Nodes:");
		for (Node x:PREV.get(ns.ID).sortedI) {
			if (i++ > n) break;
			System.out.println("\t"+x.content+", "+PREV.get(ns.ID).count.get(x));
		}
		i = 0;
		System.out.println("Succ Nodes:"); 
		for (Node x:SUCC.get(ns.ID).sortedI) { 
			if (i++ > n) break;
			System.out.println("\t"+x.content+", "+SUCC.get(ns.ID).count.get(x));
		}
	}
	public void showNext(Node ns) {
		System.out.println("Prev Nodes:");
		for (Node x:PREV.get(ns.ID).sortedI) 
			System.out.println("\t"+x.content+", "+PREV.get(ns.ID).count.get(x));
		
		System.out.println("Succ Nodes:");
		for (Node x:SUCC.get(ns.ID).sortedI) 
			System.out.println("\t"+x.content+", "+SUCC.get(ns.ID).count.get(x));
	}
	
	public void showNext(Node ns, int n) {
		int i = 0;
		System.out.println("Prev Nodes:");
		for (Node x:PREV.get(ns.ID).sortedI) { 
			if (i++ > n) break;
			System.out.println("\t"+x.content+", "+PREV.get(ns.ID).count.get(x));
		}
		i = 0;
		System.out.println("Succ Nodes:"); 
		for (Node x:SUCC.get(ns.ID).sortedI) { 
			if (i++ > n) break;
			System.out.println("\t"+x.content+", "+SUCC.get(ns.ID).count.get(x));
		}
	}
	
	public void fileout(String fn) throws IOException {
		BufferedWriter filen = new BufferedWriter(new FileWriter(fn+".node"));
		BufferedWriter fileP = new BufferedWriter(new FileWriter(fn+".PREV"));
		BufferedWriter fileS = new BufferedWriter(new FileWriter(fn+".SUCC"));
		BufferedWriter fileE = new BufferedWriter(new FileWriter(fn+".EQUI"));
		filen.write(node.size()+"\n");
		int i = 0;
		for (Node x:node) { if (i++ %10000 == 0) System.out.println(i-1 + ":" + x.content); x.fileout(filen);} 
		filen.close();
		fileP.write(PREV.size()+"\n");
		for (EdgeSet e:PREV) e.fileout(fileP); 
		fileP.close();
		fileS.write(SUCC.size()+"\n");
		for (EdgeSet e:SUCC) e.fileout(fileS); 
		fileS.close();
		fileE.write(EQUI.size()+"\n");
		for (EdgeSet e:EQUI) e.fileout(fileE); 
		fileE.close();
	}
	
	public void filein(String fn) throws IOException{
		BufferedReader filen = new BufferedReader(new FileReader(fn+".node"));
		BufferedReader fileP = new BufferedReader(new FileReader(fn+".PREV"));
		BufferedReader fileS = new BufferedReader(new FileReader(fn+".SUCC"));
		BufferedReader fileE = new BufferedReader(new FileReader(fn+".EQUI"));
		String str = filen.readLine();
		int size = 0;
		node = new ArrayList <Node> (size = Integer.parseInt(str));
		G = new HashMap<String,Node> (size);
		
		
		long start = System.currentTimeMillis(); // Write 시작시간
		System.out.println("시작시간:" + start);
		
		System.out.println(size+" nodes reload began");
		for (int i=0;i<size;i++) node.add(new Node()); 
		for (Node x:node) { x.filein(filen,node); G.put(x.content,x);} filen.close();
		int i = 0;
		str = fileP.readLine();
		PREV = new ArrayList<EdgeSet>(size); 
		for (i=0;i<size;i++) PREV.add(new EdgeSet(node.get(i)));
		for (EdgeSet e:PREV) {
			e.filein(fileP,node); 
		}
		fileP.close();
		str = fileS.readLine();
		SUCC = new ArrayList<EdgeSet>(size); 
		for (i=0;i<size;i++) SUCC.add(new EdgeSet(node.get(i)));
		for (EdgeSet e:SUCC) e.filein(fileS,node); fileS.close();		
		
		str = fileE.readLine();
		EQUI = new ArrayList<EdgeSet>(size); 
		for (i=0;i<size;i++) EQUI.add(new EdgeSet(node.get(i)));
		for (EdgeSet e:EQUI) e.filein(fileE,node); fileE.close();		
		
		System.out.println(size+" nodes reload completed");
		
		long end = System.currentTimeMillis(); // Write 종료시간
		System.out.println("종료시간:" + end);

		System.out.println("소요 시간 : " + (end - start) + " milliseconds");
		System.out.println("로딩 완료");
		
	}
}
