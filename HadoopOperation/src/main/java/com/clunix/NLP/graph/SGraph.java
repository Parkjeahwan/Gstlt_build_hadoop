package com.clunix.NLP.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Externalizable;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;

public class SGraph extends AGraph implements Externalizable
{
	private static final String STR_SUCC_NODES = "Succ Nodes";
	private static final String STR_PREV_NODES = "Prev Nodes";
	// PREV.get(i) contains EdgeSet of a node ID = i
	public List<EdgeSet> PREV;
	public List<EdgeSet> SUCC;

	public SGraph()
	{
		super();
		PREV = new ArrayList<EdgeSet>();
		SUCC = new ArrayList<EdgeSet>();
	}

	public Node addNode(String cw)
	{
		Node nb;
		if (!G.containsKey(cw)) {
			nb = createNewNode(cw);
			PREV.add(new EdgeSet(nb));
			SUCC.add(new EdgeSet(nb));
		}
		else nb = G.get(cw);
		return nb;
	}

	public Node addNode(Node nb)
	{
		if (!G.containsKey(nb.content)) {
			createNewNode(nb);
			PREV.add(new EdgeSet(nb));
			SUCC.add(new EdgeSet(nb));
		}
		return nb;
	}

	public boolean delNode(Node nb)
	{
		if (nb == null) return false;
		if (G.containsKey(nb.content)) {
			delNodeFromEdgeSet(EQUI, nb);
			delNodeFromEdgeSet(PREV, nb);
			delNodeFromEdgeSet(SUCC, nb);
			G.remove(nb);
			node.set(nb.ID, null);
			return true;
		}
		return false;
	}

	public void showNext(String s)
	{
		Node ns = G.get(s);
		showNextEdgeSet(ns, PREV, STR_PREV_NODES);
		showNextEdgeSet(ns, SUCC, STR_SUCC_NODES);
	}

	public void showNext(String s, int n)
	{
		Node ns = G.get(s);
		showNextEdgeSet(ns, PREV, STR_PREV_NODES, n);
		showNextEdgeSet(ns, SUCC, STR_SUCC_NODES, n);
	}

	public void showNext(Node ns)
	{
		super.showNext(ns);
		showNextEdgeSet(ns, PREV, STR_PREV_NODES);
		showNextEdgeSet(ns, SUCC, STR_SUCC_NODES);
	}

	public void showNext(Node ns, int n)
	{
		showNextEdgeSet(ns, PREV, STR_PREV_NODES);
		showNextEdgeSet(ns, SUCC, STR_SUCC_NODES);
	}

	/**
	 * @deprecated Use SGraphFile.write instead.
	 */
	@Deprecated
	public void fileout(String fn) throws IOException
	{
		BufferedWriter filen = new BufferedWriter(new FileWriter(fn + ".node"));
		BufferedWriter fileP = new BufferedWriter(new FileWriter(fn + ".PREV"));
		BufferedWriter fileS = new BufferedWriter(new FileWriter(fn + ".SUCC"));
		BufferedWriter fileE = new BufferedWriter(new FileWriter(fn + ".EQUI"));
		System.out.println("\tSGRaph file write BEGAN");
		System.out.println("SGraph file SAVED to " + fn);
		// Write 시작시간
		long start = System.currentTimeMillis();
		System.out.println(fn + "SGraph 저장  시작시간:" + start);
		filen.write(node.size() + "\n");
		int i = 0;
		for (Node x : node) {
			if (i++ % 50000 == 0) System.out.println(i - 1 + ":" + x.content);
			x.fileout(filen);
		}
		filen.close();
		fileP.write(PREV.size() + "\n");
		i = 0;
		for (EdgeSet e : PREV) {
			// if (i++ %10000 == 0) System.out.println(i-1
			// + "-th node :" +
			// node.get(e.src.ID).content);
			e.fileout(fileP);
		}
		fileP.close();
		fileS.write(SUCC.size() + "\n");
		for (EdgeSet e : SUCC)
			e.fileout(fileS);
		fileS.close();
		fileE.write(EQUI.size() + "\n");
		for (EdgeSet e : EQUI)
			e.fileout(fileE);
		fileE.close();
		// Write 종료시간
		long end = System.currentTimeMillis(); 
		System.out.println("\t저장소요 시간 : " + (end - start) + " milliseconds");
		System.out.println("SGraph 저장 완료");
		System.out.println("SGraph file SAVING ENDED to " + fn);
	}

	/**
	 * @deprecated Use SGraphFile.read instead.
	 */
	@Deprecated
	public void filein(String fn) throws IOException
	{
		BufferedReader filen = new BufferedReader(new FileReader(fn + ".node"));
		BufferedReader fileP = new BufferedReader(new FileReader(fn + ".PREV"));
		BufferedReader fileS = new BufferedReader(new FileReader(fn + ".SUCC"));
		BufferedReader fileE = new BufferedReader(new FileReader(fn + ".EQUI"));
		String str = filen.readLine();
		int size = 0;
		node = new ArrayList<Node>(size = Integer.parseInt(str));
		G = new HashMap<String, Node>(size);
		long start = System.currentTimeMillis(); // Write 시작시간
		System.out.println(fn + "SGraph 로딩  시작시간:" + start);
		System.out.println(size + " nodes reload began");
		for (int i = 0; i < size; i++)
			node.add(new Node());
		for (Node x : node) {
			x.filein(filen, node);
			G.put(x.content, x);
		}
		filen.close();
		int i = 0;
		str = fileP.readLine();
		PREV = new ArrayList<EdgeSet>(size);
		for (i = 0; i < size; i++)
			PREV.add(new EdgeSet(node.get(i)));
		for (EdgeSet e : PREV) {
			e.filein(fileP, node);
		}
		fileP.close();
		str = fileS.readLine();
		SUCC = new ArrayList<EdgeSet>(size);
		for (i = 0; i < size; i++)
			SUCC.add(new EdgeSet(node.get(i)));
		for (EdgeSet e : SUCC)
			e.filein(fileS, node);
		fileS.close();
		str = fileE.readLine();
		EQUI = new ArrayList<EdgeSet>(size);
		for (i = 0; i < size; i++)
			EQUI.add(new EdgeSet(node.get(i)));
		for (EdgeSet e : EQUI)
			e.filein(fileE, node);
		fileE.close();
		System.out.println(size + " nodes reload completed");
		long end = System.currentTimeMillis(); // Write 종료시간
		System.out.println("SGraph 종료시간:" + end);
		System.out.println("소요 시간 : " + (end - start) + " milliseconds");
		System.out.println("SGraph 로딩 완료");
	}

	public void delEdge(String s1, String s2)
	{
		Node n1, n2;
		if (G.containsKey(s1)) n1 = G.get(s1);
		else {
			System.out.println(s1 + " is not registered node");
			return;
		}
		if (G.containsKey(s2)) n2 = G.get(s2);
		else {
			System.out.println(s2 + " is not registered node");
			return;
		}
		PREV.get(n1.ID).contexts.remove(n2);
		PREV.get(n1.ID).count.remove(n2);
		PREV.get(n1.ID).sortedI.remove(n2);
		SUCC.get(n1.ID).contexts.remove(n2);
		SUCC.get(n1.ID).count.remove(n2);
		SUCC.get(n1.ID).sortedI.remove(n2);
		EQUI.get(n1.ID).contexts.remove(n2);
		EQUI.get(n1.ID).count.remove(n2);
		EQUI.get(n1.ID).sortedI.remove(n2);
	}

	public void sortEdge()
	{
		for (Node x : node) {
			int nid = x.ID;
			EQUI.get(nid).sort();
			PREV.get(nid).sort();
			SUCC.get(nid).sort();
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof SGraph) {
			SGraph target = (SGraph) obj;
			// NOTE: After deleting node, G is changed but size G is not
			// changed. Anyway G is not part of comparison.
			return (ObjectUtils.equals(target.EQUI, EQUI)
					// && ObjectUtils.equals(target.G, G)
					&& ObjectUtils.equals(target.node, node)
					&& ObjectUtils.equals(target.PREV, PREV)
					&& ObjectUtils.equals(target.SUCC, SUCC));
		}
		return super.equals(obj);
	}

	@Override
	public String toString()
	{
		return String.format("%s{node=[%s],G=[%s],SUCC=[%s],PREV=[%s],EQUI=[%s]}",
				this.getClass().getSimpleName(),
				node, G, SUCC, PREV, EQUI);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException
	{
		super.writeExternal(out);
		out.writeObject(PREV);
		out.writeObject(SUCC);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		super.readExternal(in);
		PREV = (List<EdgeSet>) in.readObject();
		SUCC = (List<EdgeSet>) in.readObject();
	}
}
