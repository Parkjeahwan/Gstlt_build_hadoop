package com.clunix.NLP.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class AGraph implements Externalizable
{
	private static final String STR_EQUI_NODES = "Equi Nodes";
	public List<Node> node;
	@JsonIgnore
	public HashMap<String, Node> G;
	public List<EdgeSet> EQUI; // EQUI.get(i) contains EdgeSet of a node ID
								// = i

	public AGraph()
	{
		G = new HashMap<String, Node>();
		node = new ArrayList<Node>();
		EQUI = new ArrayList<EdgeSet>();
	}

	protected Node createNewNode(Node nb)
	{
		nb.ID = G.size();
		G.put(nb.content, nb);
		node.add(nb);
		EQUI.add(new EdgeSet(nb));
		return nb;
	}

	protected Node createNewNode(String cw)
	{
		Node nb = new Node(cw);
		nb.content = cw;
		return createNewNode(nb);
	}

	public Node addNode(String cw)
	{
		Node nb;
		if (!G.containsKey(cw)) {
			nb = createNewNode(cw);
		}
		else nb = G.get(cw);
		return nb;
	}

	public Node addNode(Node nb)
	{
		if (!G.containsKey(nb.content)) {
			createNewNode(nb);
		}
		return nb;
	}

	public boolean delNode(Node nb)
	{
		if (nb == null) return false;
		if (G.containsKey(nb.content)) {
			delNodeFromEdgeSet(EQUI, nb);
			G.put(nb.content, null);
			node.set(nb.ID, null);
			return true;
		}
		return false;
	}

	protected void delNodeFromEdgeSet(List<EdgeSet> edgeSet, Node nb)
	{
		for (EdgeSet e : edgeSet) {
			if (e == null) continue;
			if (e.count.containsKey(nb)) e.count.remove(nb);
			if (e.contexts.containsKey(nb)) e.contexts.remove(nb);
			if (e.sortedI.contains(nb)) e.sortedI.remove(nb);
		}
		edgeSet.set(nb.ID, null);
	}

	public boolean delNode(String b)
	{
		if (G.containsKey(b)) {
			Node nb = G.get(b);
			return delNode(nb);
		}
		return false;
	}

	public void showNext(String s)
	{
		Node ns = G.get(s);
		showNextEdgeSet(ns, EQUI, STR_EQUI_NODES);
	}

	protected void showNextEdgeSet(Node ns, List<EdgeSet> edgeSet, String msg)
	{
		showNextEdgeSet(ns, edgeSet, msg, -1);
	}

	protected void showNextEdgeSet(Node ns, List<EdgeSet> edgeSet, String msg, int max)
	{
		System.out.println(msg + ":");
		int i = 0;
		for (Node x : edgeSet.get(ns.ID).sortedI) {
			i++;
			if (max != -1 && i > max) {
				break;
			}
			System.out.println("\t" + x.content + ", " + edgeSet.get(ns.ID).count.get(x));
		}
	}

	public void showNext(String s, int n)
	{
		Node ns = G.get(s);
		showNextEdgeSet(ns, EQUI, STR_EQUI_NODES, n);
	}

	public void showNext(Node ns)
	{
		showNextEdgeSet(ns, EQUI, STR_EQUI_NODES);
	}

	public void showNext(Node ns, int n)
	{
		showNextEdgeSet(ns, EQUI, STR_EQUI_NODES, n);
	}

	public void fileout(String fn) throws IOException
	{
		BufferedWriter filen = new BufferedWriter(new FileWriter(fn + ".node"));
		BufferedWriter fileE = new BufferedWriter(new FileWriter(fn + ".EQUI"));
		filen.write(node.size() + "\n");
		int i = 0;
		long start = System.currentTimeMillis(); // Write 시작시간
		System.out.println("FASG save 시작시간:" + start);
		for (Node x : node) { // if (i++ %100000 == 0) System.out.println(i-1 +
								// ":" + x.content);
			x.fileout(filen);
		}
		filen.close();
		fileE.write(EQUI.size() + "\n");
		i = 0;
		for (EdgeSet e : EQUI) {
			if (i++ % 100000 == 0) System.out.println("\t\t" + (i - 1) + "-th node :" + node.get(e.src.ID).content);
			e.fileout(fileE);
		}
		fileE.close();
	}

	public void efileout(String fn) throws IOException
	{
		long start = System.currentTimeMillis(); // Write 시작시간
		System.out.println("FASG 저장 시작시간:" + start);
		BufferedWriter fileE = new BufferedWriter(new FileWriter(fn + ".EQUI"));
		fileE.write(EQUI.size() + "\n");
		int i = 0;
		for (EdgeSet e : EQUI) {
			if (i++ % 100000 == 0) System.out.println("\t\t" + (i - 1) + "-th node of FAS Graph :" + node.get(e.src.ID).content);
			e.fileout(fileE);
		}
		fileE.close();
		long end = System.currentTimeMillis(); // Write 종료시간
		System.out.println("FASG 저장 완료, 소요 시간 : " + (end - start) + " milliseconds");
	}

	public void filein(String fn) throws IOException
	{
		BufferedReader filen = new BufferedReader(new FileReader(fn + ".node"));
		BufferedReader fileE = new BufferedReader(new FileReader(fn + ".ASG.EQUI"));
		String str = filen.readLine();
		int size;
		node = new ArrayList<Node>(size = Integer.parseInt(str));
		G = new HashMap<String, Node>(size);
		long start = System.currentTimeMillis(); // Write 시작시간
		System.out.println("FASG loading 시작시간:" + start);
		System.out.println("\t" + size + " nodes reload began");
		for (int i = 0; i < size; i++)
			node.add(new Node());
		for (Node x : node) {
			x.filein(filen, node);
			G.put(x.content, x);
		}
		filen.close();
		int i = 0;
		str = fileE.readLine();
		EQUI = new ArrayList<EdgeSet>(size);
		for (i = 0; i < size; i++)
			EQUI.add(new EdgeSet(node.get(i)));
		for (EdgeSet e : EQUI)
			e.filein(fileE, node);
		fileE.close();
		long end = System.currentTimeMillis(); // Write 종료시간
		System.out.println("\tFASG 로딩 완료, 소요 시간 : " + (end - start) + " milliseconds");
	}

	public void efilein(String fn) throws IOException
	{
		BufferedReader fileE = new BufferedReader(new FileReader(fn + ".EQUI"));
		String str = fileE.readLine();
		int size = Integer.parseInt(str);
		long start = System.currentTimeMillis(); // Write 시작시간
		System.out.println(size + "-nodes AGraph EdgeSet만  loading 시작 시간:" + start);
		int i = 0;
		EQUI = new ArrayList<EdgeSet>(size);
		for (i = 0; i < size; i++)
			EQUI.add(new EdgeSet(node.get(i)));
		for (EdgeSet e : EQUI)
			e.filein(fileE, node);
		fileE.close();
		long end = System.currentTimeMillis(); // Write 종료시간
		System.out.println("\t+AGraph EdgeSet만 로딩 완료, 소요 시간 : " + (end - start) + " milliseconds");
	}

	public void nfilein(String fn) throws IOException
	{
		BufferedReader filen = new BufferedReader(new FileReader(fn + ".node"));
		String str = filen.readLine();
		int size = 0;
		long start = System.currentTimeMillis(); // Write 시작시간
		System.out.println(fn + "에서 AGraph 노드들만  로딩 시작시간: " + start);
		// System.out.println(size+" nodes reload began");
		node = new ArrayList<Node>(size = Integer.parseInt(str));
		System.out.println(size + "\t nodes reload began");
		G = new HashMap<String, Node>(size);
		EQUI = new ArrayList<EdgeSet>(size);
		for (int i = 0; i < size; i++)
			node.add(new Node());
		for (Node x : node) {
			x.filein(filen, node);
			G.put(x.content, x);
		}
		filen.close();
		for (int i = 0; i < size; i++)
			EQUI.add(new EdgeSet(node.get(i)));
		long end = System.currentTimeMillis(); // Write 종료시간
		System.out.println("AGraph 노드들만 로딩 완료, 소요 시간 : " + (end - start) + " milliseconds");
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
		EQUI.get(n1.ID).contexts.remove(n2);
		EQUI.get(n1.ID).count.remove(n2);
		EQUI.get(n1.ID).sortedI.remove(n2);
	}

	public void sortEdge()
	{
		for (Node x : node)
			EQUI.get(x.ID).sort();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof AGraph) {
			AGraph target = (AGraph) obj;
			return (ObjectUtils.equals(target.EQUI, EQUI)
					&& ObjectUtils.equals(target.G, G)
					&& ObjectUtils.equals(target.node, node));
		}
		return super.equals(obj);
	}

	@Override
	public String toString()
	{
		return String.format("%s{EQUI=[%s],G=[%s],node=[%s]}", this.getClass().getSimpleName(), EQUI, G, node);
	}

	public String toJson() throws JsonGenerationException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		mapper.writeValue(out, this);
		return out.toString();
	}

	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeObject(node);
		out.writeObject(G);
		out.writeObject(EQUI);
	}

	@SuppressWarnings("unchecked")
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		node = (ArrayList<Node>) in.readObject();
		G = (HashMap<String, Node>) in.readObject();
		EQUI = (List<EdgeSet>) in.readObject();
	}
}
