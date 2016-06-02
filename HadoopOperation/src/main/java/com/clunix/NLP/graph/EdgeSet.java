package com.clunix.NLP.graph;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

// EdgeSet v2.0 Contexts is just hashset of contexts
public class EdgeSet implements Serializable { // contains a set of edges
												// connected to a 'src' node.
												// There are as many
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Edgesets as src nodes.
	public Node src;
	public HashMap<Node, Integer> count;
	public ArrayList<Node> sortedI;
	public HashMap<Node, Contexts> contexts;

	// methods
	/*
	 * public EdgeSet(){ count = new HashMap<Node,Integer> (); sortedI = new
	 * ArrayList <Node>() ; contexts = new HashMap <Node, Contexts> (); }
	 */
	public EdgeSet(Node x) {
		src = x;
		count = new HashMap<Node, Integer>();
		sortedI = new ArrayList<Node>();
		contexts = new HashMap<Node, Contexts>();
	}

	public EdgeSet(int n) {
		count = new HashMap<Node, Integer>(n);
		sortedI = new ArrayList<Node>(n);
		contexts = new HashMap<Node, Contexts>(n);
	}

	public void fileout(Writer out) throws IOException {
		// System.out.println(src.ID);
		// out.write(src.ID+"\n");
		if (sortedI == null)
			out.write("null" + "\n");
		else {
			out.write(sortedI.size() + "\n"); // write sortedI size
			if (sortedI.size() == 0)
				return;
			for (Node x : sortedI)
				out.write(x.ID + "\t"); // write sortedI
			out.write("\n");
			// }
			// if (count == null) out.write("null"+"\n");
			// else {
			for (Node x : count.keySet()) { // write next nodes' informations
				out.write(x.ID + "\t"); // write next node's ID
				out.write(count.get(x) + "\t"); // write next node's weight
				if (contexts != null && contexts.get(x) != null) {
					out.write(contexts.get(x).map.size() + "\t"); // write
																	// number of
																	// next
																	// nodes'
																	// appearance
					for (int i : contexts.get(x).map)
						out.write(i + ","); // write locations next node
											// appeared at
				}
				out.write("\n");
			}
		}
	}

	public void filein(BufferedReader br, List<Node> NodeAt) throws IOException {
		String line;// = br.readLine();
		// if (line == null || line.equals("null")) return;
		// else src = NodeAt.get(Integer.parseInt(line));
		int size = 0;
		if ((line = br.readLine()) != null) {
			// System.out.println("line: "+line);
			if (line.equals("null")) {
				sortedI = new ArrayList<Node>();
				count = new HashMap<Node, Integer>();
				contexts = new HashMap<Node, Contexts>();
			} else {
				sortedI = new ArrayList<Node>(size = Integer.parseInt(line));
				if (size == 0) {
					count = new HashMap<Node, Integer>();
					contexts = new HashMap<Node, Contexts>();
					return;
				}
				if ((line = br.readLine()) != null) { // processing for bigN
					String[] s1 = line.split("\t");
					for (String ts : s1) {
						if (!ts.equals(""))
							sortedI.add(NodeAt.get(Integer.parseInt(ts)));
						else
							break;
					}
				}
				count = new HashMap<Node, Integer>(size);
				if (size == 0)
					return;
				for (int i = 0; i < size; i++)
					if ((line = br.readLine()) != null) { // processing for next
						String[] s1 = line.split("\t");
						Node b = NodeAt.get(Integer.parseInt(s1[0]));
						int occurrence = s1[1].equals("null")? 0 : Integer.parseInt(s1[1]); // number of
																	// occurrence
						count.put(b, occurrence);
						if (s1.length >= 4) {
							contexts = new HashMap<Node, Contexts>(occurrence); // contexts
																				// number
																				// ==
																				// count
																				// numver
							Contexts c = new Contexts();
							c.map = new HashSet<Integer>(Integer.parseInt(s1[2]));
							String[] map = s1[3].split(",");
							for (String ts : map) {
								if (!ts.equals(""))
									c.map.add(Integer.parseInt(ts));
								else
									break;
							}
							contexts.put(b, c);
						}
					}
			}
		} // END
		// Of
	} // filein
		// function

	public ArrayList<Node> common(int n) {
		ArrayList<Node> res = new ArrayList<Node>(n);
		for (int i = 0; i < n; i++)
			res.add(sortedI.get(i));
		return (res.size() == 0) ? null : res;
	}

	public HashSet<Node> intersect(EdgeSet a) {
		if (count == null || a == null)
			return null;
		HashSet<Node> res = new HashSet<Node>();
		for (Node x : count.keySet())
			if (a.count.containsKey(x))
				res.add(x);
		return res;
	}

	public HashSet<Node> intersect(EdgeSet a, int n) {
		if (count == null || a == null)
			return null;
		int size = count.size();
		HashSet<Node> res = new HashSet<Node>();
		if (n < 0)
			n = size;
		for (int i = 0; i < size && i < n; i++)
			if (a.count.containsKey(sortedI.get(i)))
				res.add(sortedI.get(i));
		return res;
	}

	public HashSet<Node> intersect(HashSet<Node> a, int n) {
		if (count == null || a == null)
			return null;
		int size = count.size();
		HashSet<Node> res = new HashSet<Node>();
		n = n >= 0 ? n : size;
		for (int i = 0; i < size && i < n; i++)
			if (a.contains(sortedI.get(i)))
				res.add(sortedI.get(i));
		return res;
	}

	public boolean isIntersectNull(EdgeSet a) {
		if (a == null)
			return true;
		for (Node x : count.keySet())
			if (a.count.containsKey(x))
				return false;
		return true;
	}

	public Node get(int n) {
		return (sortedI != null) ? sortedI.get(n) : null;
	} // return n-th frequently co-occurred node

	public boolean hasNext(Node a) {
		return count == null ? false : count.containsKey(a);
	}

	public void addNext(Node a, Integer line, int offset) throws InterruptedException {
		addNext(a, line);
	}

	public void addNext(Node a, Integer line) throws InterruptedException {
		addNextCore(a);
		if (contexts.containsKey(a))
			contexts.get(a).map.add(line);
		else
			contexts.put(a, new Contexts(line));
	}

	public void setEdge(Node a, int n) {
		if (count == null) {
			count = new HashMap<Node, Integer>();
			sortedI = new ArrayList<Node>();
			contexts = new HashMap<Node, Contexts>();
		}
		count.put(a, n);
	}
	
	public void addNext(Node a) throws InterruptedException {
		addNextCore(a);
		if (contexts.containsKey(a))
			contexts.get(a).map.add(-1);
		else
			contexts.put(a, new Contexts(-1));
	}

	public void addNextCore(Node a) {
		if (count == null) {
			count = new HashMap<Node, Integer>();
			sortedI = new ArrayList<Node>();
			contexts = new HashMap<Node, Contexts>();
		}
		if (count.containsKey(a)) {
			int nc = count.get(a) == null ? 0 : count.get(a);
			count.put(a, ++nc);
		} else {
			count.put(a, 1);
		}
	}

	public void addNexto(Node a, Integer line, int offset) throws InterruptedException {
		addNexto(a, line);
	}

	public void addNexto(Node a, Integer line) throws InterruptedException {
		addNextCoreo(a);
		if (contexts.containsKey(a))
			contexts.get(a).map.add(line);
		else
			contexts.put(a, new Contexts(line));
	}

	public void addNexto(Node a) throws InterruptedException {
		addNextCoreo(a);
		if (contexts.containsKey(a))
			contexts.get(a).map.add(-1);
		else
			contexts.put(a, new Contexts(-1));
	}

	public void addNextCoreo(Node a) {
		if (count == null) {
			count = new HashMap<Node, Integer>();
			sortedI = new ArrayList<Node>();
			contexts = new HashMap<Node, Contexts>();
		}
		if (count.containsKey(a)) {
			int nc = count.get(a);
			count.put(a, ++nc);
			if (!sortedI.contains(a))
				System.exit(0);
			int i;
			int b = sortedI.size();
			for (i = 0; i < b; i++) {
				if (sortedI.get(i) == a) {// .content.equals(a.content))
					if ((i >= 1 && count.get(sortedI.get(i - 1)) >= nc) || i == 0)
						break;
					int j;
					for (j = i - 1; j >= 0 && !sortedI.isEmpty() && count.get(sortedI.get(j)) < nc; j--)
						;
					j = (j < 0) ? 0 : j + 1;
					sortedI.set(i, sortedI.get(j));
					sortedI.set(j, a);
					break;
				}
			}
		} else {
			count.put(a, 1);
			sortedI.add(a);
		}
	}

	public void delNext(Node a) { // count, location���� a�� Ű�� value����
									// ����;}
		count.remove(a);
	}

	public ArrayList<Node> sort() {
		if (count.isEmpty())
			return sortedI;
		List <NodeInt> toSort = new ArrayList<NodeInt>(count.keySet().size());
		for (Node x:count.keySet()) toSort.add(new NodeInt(x,count.get(x)));
		Collections.sort(toSort);
		sortedI = new ArrayList<Node>(count.keySet().size());
		for (NodeInt x:toSort) sortedI.add(x.n);
		if (sortedI != null && sortedI.size() > 1 && count.get(sortedI.get(0)) < count.get(sortedI.get(1))) 
			System.exit(0);
/*		
		if (sortedI == null || sortedI.isEmpty()) {
			sortedI = new ArrayList<Node>(count.size());
			sortedI.addAll(count.keySet());
		}
		Collections.sort(sortedI, new Comparator<Node>() {
			@Override
			public int compare(Node o1, Node o2) {
				int a = count.get(o1)==null? 0 : count.get(o1);
				int b = count.get(o2)==null? 0 : count.get(o2);
				return b - a;
			}
		});
		*/
		return sortedI;
	}

	/*
	 * @Override public void writeExternal(ObjectOutput out) throws IOException
	 * { out.writeInt(size); out.writeObject(src); out.writeObject(count);
	 * out.writeObject(sortedI); out.writeObject(contexts); }
	 * 
	 * @Override public void readExternal(ObjectInput in) throws IOException,
	 * ClassNotFoundException { size = in.readInt(); src =
	 * (Node)in.readObject(); count = (HashMap<Node, Integer>)in.readObject();
	 * 
	 * sortedI = (ArrayList<Node>)in.readObject(); contexts = (HashMap<Node,
	 * Contexts>)in.readObject();
	 * 
	 * }
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EdgeSet) {
			EdgeSet target = (EdgeSet) obj;
			return (ObjectUtils.equals(target.contexts, contexts) && ObjectUtils.equals(target.count, count)
					&& ObjectUtils.equals(target.sortedI, sortedI) && ObjectUtils.equals(target.src, src));
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return String.format("%s{contexts=[%s],count=[%s],sortedI=[%s],src=[%s]}", this.getClass().getSimpleName(),
				contexts, count, sortedI, src);
	}

	public String toJson() throws JsonGenerationException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		mapper.writeValue(out, this);
		return out.toString();
	}

	public HashSet<Node> intersectk(EdgeSet es) {
		HashSet<Node> r = new HashSet<Node>();
		for (Node x:es.sortedI) 
			for (Node y:count.keySet())
				if (x.content.equals(y.content)) r.add(x); 
		return r;
	}
}
// EdgeSet v1.0 : This version uses context of format <line, list <offset>>
/*
 * public class EdgeSet implements Serializable{ int size; Node src; HashMap
 * <Node, Integer> count ; ArrayList <Node> sortedI; HashMap <Node, Contexts>
 * contexts ;
 * 
 * // methods public ArrayList<Node> common(int n) { ArrayList<Node> res = new
 * ArrayList<Node>(n); for (int i=0;i<n;i++) res.add(sortedI.get(i)); return
 * (res.size()==0)? null:res; } public ArrayList<Node> intersect(EdgeSet a) {
 * ArrayList<Node> res = new ArrayList<Node>(); for (Node x:count.keySet()) if
 * (a.count.containsKey(x)) res.add(x); return (res.size()==0)? null:res; }
 * public ArrayList<Node> intersect(EdgeSet a, int n) { ArrayList<Node> res =
 * new ArrayList<Node>(); for (int i=0;i<size && i<n;i++) if
 * (a.count.containsKey(sortedI.get(i))) res.add(sortedI.get(i)); return
 * (res.size()==0)? null:res; } public boolean isIntersectNull(EdgeSet a) { for
 * (Node x:count.keySet()) if (a.count.containsKey(x)) return true; return
 * false; } public boolean hasNext(Node a) { return count.containsKey(a)?
 * true:false; }
 * 
 * public void addNext(Node a, Integer line, int offset) throws
 * InterruptedException { if (count == null) { count = new HashMap <Node,
 * Integer>(); sortedI = new ArrayList<Node>(); contexts = new HashMap
 * <Node,Contexts>(); } if (count.containsKey(a)) { if (!sortedI.contains(a))
 * System.exit(0); int nc= count.get(a); count.put(a,++nc); ArrayList <Integer>
 * x = contexts.get(a).map.get(line) ; if (x != null) x.add(offset); else { x =
 * new ArrayList<Integer>(); x.add(offset); contexts.get(a).map.put(line, x); }
 * 
 * int i; int b = sortedI.size(); for (i=0;i<b;i++) { if
 * (sortedI.get(i)==a){//.content.equals(a.content)) if ((i>=1 &&
 * count.get(sortedI.get(i-1)) >= nc) || i == 0) break; // for (Node xx:sortedI)
 * System.out.print(xx.content+","); System.out.println("=> After remove"); int
 * j; for (j=i-1;j>=0 && !sortedI.isEmpty() && count.get(sortedI.get(j)) < nc;
 * j--) ; j= (j<0)? 0:j+1; // for (Node xx:sortedI) {
 * System.out.print("("+xx.content+","+count.get(xx)+")");
 * }System.out.println(this+"=> Before Move"); sortedI.set(i, sortedI.get(j));
 * sortedI.set(j, a); // for (Node xx:sortedI) {
 * System.out.print("("+xx.content+","+count.get(xx)+")");
 * }System.out.println(this+"=> After Move"); break; } } } else {
 * count.put(a,1); Contexts c = new Contexts(line,offset); contexts.put(a, c);
 * sortedI.add(a); // System.out.println(a.content); size++; } }
 * 
 * public void addNext(Node a) throws InterruptedException { if (count == null)
 * { count = new HashMap <Node, Integer>(); sortedI = new ArrayList<Node>();
 * contexts = new HashMap <Node,Contexts>(); }
 * 
 * if (count.containsKey(a)) { if (!sortedI.contains(a)) System.exit(0); int nc;
 * count.put(a,nc = count.get(a)+1); int i; int b = sortedI.size(); for
 * (i=0;i<b;i++) { if (sortedI.get(i)==a){//.content.equals(a.content)) if
 * ((i>=1 && count.get(sortedI.get(i-1)) >= nc) || i == 0) break; // for (Node
 * xx:sortedI) System.out.print(xx.content+","); System.out.println(
 * "=> After remove"); int j; for (j=i-1;j>=0 && !sortedI.isEmpty() &&
 * count.get(sortedI.get(j)) < nc; j--) ; j= (j<0)? 0:j+1; // for (Node
 * xx:sortedI) { System.out.print("("+xx.content+","+count.get(xx)+")");
 * }System.out.println(this+"=> Before Move"); sortedI.set(i, sortedI.get(j));
 * sortedI.set(j, a); // for (Node xx:sortedI) {
 * System.out.print("("+xx.content+","+count.get(xx)+")");
 * }System.out.println(this+"=> After Move"); break; } } } else {
 * count.put(a,1); sortedI.add(a); size++; } contexts.get(a).map.put((int)-1,
 * null);
 * 
 * } public void delNext(Node a) { //count, location���� a�� Ű�� value����
 * ����;} count.remove(a); }
 * 
 * }
 */