package com.clunix.NLP.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class PGraph extends AGraph
{
	private static final String STR_SUCC_NODES = "Succ Nodes";
	private static final String STR_PREV_NODES = "Prev Nodes";
	public List<EdgeSet> PREV; // PREV.get(i) contains EdgeSet of a node ID
								// = i
	public List<EdgeSet> SUCC;

	public PGraph()
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
			delNodeFromEdgeSet(PREV, nb);
			delNodeFromEdgeSet(SUCC, nb);
			G.put(nb.content, null);
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
	 * @deprecated Use <code>PGraphFile.write</code> instead.
	 */
	@Deprecated
	public void fileout(String fn) throws IOException
	{
		BufferedWriter filen = new BufferedWriter(new FileWriter(fn + ".node"));
		BufferedWriter fileP = new BufferedWriter(new FileWriter(fn + ".PREV"));
		BufferedWriter fileS = new BufferedWriter(new FileWriter(fn + ".SUCC"));
		System.out.println("\tSGRaph file write BEGAN");
		System.out.println("SGraph file SAVED to " + fn);
		long start = System.currentTimeMillis(); // Write 시작시간
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
		for (EdgeSet e : PREV) { // if (i++ %10000 == 0) System.out.println(i-1
									// + "-th node :" +
									// node.get(e.src.ID).content);
			e.fileout(fileP);
		}
		fileP.close();
		fileS.write(SUCC.size() + "\n");
		for (EdgeSet e : SUCC)
			e.fileout(fileS);
		fileS.close();
		long end = System.currentTimeMillis(); // Write 종료시간
		System.out.println("\t저장소요 시간 : " + (end - start) + " milliseconds");
		System.out.println("SGraph 저장 완료");
		System.out.println("SGraph file SAVING ENDED to " + fn);
	}

	/**
	 * @deprecated Use <code>PGraphFile.read</code> instead.
	 */
	@Deprecated
	public void filein(String fn) throws IOException
	{
		FileSystem hdfs;
		hdfs = FileSystem.get(new Configuration());
		Path homeDir = hdfs.getHomeDirectory();
		Path path = new Path(homeDir + fn + ".node");
		BufferedReader filen = new BufferedReader(new InputStreamReader(hdfs.open(path)));
		path = new Path(homeDir + fn + ".PREV");
		BufferedReader fileP = new BufferedReader(new InputStreamReader(hdfs.open(path)));
		path = new Path(homeDir + fn + ".SUCC");
		BufferedReader fileS = new BufferedReader(new InputStreamReader(hdfs.open(path)));
		String str = filen.readLine();
		int size = 0;
		node = new ArrayList<Node>(size = Integer.parseInt(str));
		G = new HashMap<String, Node>(size);
		long start = System.currentTimeMillis(); // Write 시작시간
		System.out.println(fn + "PGraph 로딩  시작시간:" + start);
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
		System.out.println(size + " nodes reload completed");
		long end = System.currentTimeMillis(); // Write 종료시간
		System.out.println("PGraph 종료시간:" + end);
		System.out.println("소요 시간 : " + (end - start) + " milliseconds");
		System.out.println("PGraph 로딩 완료");
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
	}

	public void sortEdge()
	{
		for (Node x : node) {
			int nid = x.ID;
			PREV.get(nid).sort();
			SUCC.get(nid).sort();
		}
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof SGraph) {
			SGraph target = (SGraph) obj;
			return (ObjectUtils.equals(target.node, node)
					// && ObjectUtils.equals(target.G, G)
					&& ObjectUtils.equals(target.PREV, PREV)
					&& ObjectUtils.equals(target.SUCC, SUCC));
		}
		return super.equals(obj);
	}

	@Override
	public String toString()
	{
		return String.format("%s{node=[%s],G=[%s],SUCC=[%s],PREV=[%s]}",
				this.getClass().getSimpleName(),
				node, G, SUCC, PREV);
	}
}
