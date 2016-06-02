package com.ai.gestalt;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.clunix.NLP.graph.EdgeSet;
import com.clunix.NLP.graph.Node;
import com.clunix.NLP.graph.PGraph;
import com.clunix.NLP.graph.iNode;
public class Learner {
	public static HashMap<String,HashSet<String>> decomp = new HashMap<String,HashSet<String>> ();
	public static List<String> loadCorpus(String corpusFileName, int sizeOfCorpus) throws IOException
	{
		System.out.println("Corpus Loading began...");

		ArrayList<String> C = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(corpusFileName));
		String line;
		int i = 0;    
		while ((line = br.readLine()) != null) {
			if (i >= sizeOfCorpus){
				break;
			}
			i++;
			C.add(line);
		}

		System.out.println("Total "+i+" lines loded to MM");
		System.out.println("Last line:"+ C.get(i-1));
		br.close();
		return C;
	}

	public void addAllEdges(Node na, Node nb, PGraph g, PGraph h) {
		String ac = na.content;
		String bc = nb.content;
		String a[] = ac.split(" ");
		String b[] = bc.split(" ");
	}

	public void addAllEdges(String ac, String bc, PGraph g, PGraph h) throws InterruptedException {
		String a[] = ac.split(" ");
		String b[] = bc.split(" ");

		HashSet<String> ca = incrementalDecompostions(Arrays.asList(a),g,h);
		HashSet<String> cb = allDecompostions(Arrays.asList(b),g,h);

		for (String A:ca) 
			for (String B:cb) 
				if (A.contains(" ") || B.contains(" ")) {
					if (g.G.containsKey(A)) g.G.get(A).count++;
					else g.addNode(new Node(A));

					g.SUCC.get(g.G.get(A).ID).addNext(g.G.get(B));
					if (B.equals("챔피언/NNG 스리/NNG 스리/NNG.*.") && A.equals("UEFA/SL )/SSC"))
					{int xs = 1;}
					g.PREV.get(
							g.G.get(B).ID).
					addNext(
							g.G.get(A));
				}

	}
	
	public HashSet<String> getAllEdges(String ac, String bc, PGraph f, PGraph g, PGraph h) throws InterruptedException {
		HashSet <String > ret = new HashSet<String>();
		String a[] = ac.split(" ");
		String b[] = bc.split(" ");

		HashSet<String> ca = incrementalDecompostions(Arrays.asList(a),g,h);
		HashSet<String> cb = allDecompostions(Arrays.asList(b),g,h);

		for (String A:ca) 
			for (String B:cb) 
				if (
						f.SUCC.get(g.G.get(A).ID)
						.hasNext(g.G.get(B))){ 
					System.out.println("\t"+A+"=>"+B);
					ret.add("\t"+A+"=>"+B);
				}
		return ret;
	}
	public HashSet<String> incrementalDecompostions(List <String> s,PGraph g, PGraph h) {
		HashSet<String> res = new HashSet <String> ();
		String seed = "";
		int ss = s.size()-1;
		if (ss>=0) seed = s.get(ss);
		res.add(seed);
		for (int i=ss-1;i>=0;i--){
			res.add(seed = s.get(i)+" "+seed);
		}
		return res;
	}

	public HashSet<String> allDecompostions(List <String> s,PGraph g, PGraph h) {
		List<String> r = new ArrayList<String> ();
		HashSet<String> res = new HashSet <String> ();
		int sl = s.size();
		if (sl == 2){
			r.add(s.get(0)+" "+s.get(1));
			String astar = s.get(0)+".*.";
			EdgeSet te = null;
			if (h.G.containsKey(astar) 
					&& (te = h.SUCC.get(h.G.get(astar).ID)).
					hasNext(h.G.get(s.get(1))))
				r.add(s.get(0)+" "+astar);
			astar = ".*."+s.get(1);
			if (h.G.containsKey(astar) && h.SUCC.get(h.G.get(astar).ID).hasNext(h.G.get(s.get(0))))
				r.add(astar+" "+s.get(1));
		}
		else  if (sl > 2) {
			HashSet<String> D;

			String s0 = s.get(0);
			List<String> stail = s.subList(1, sl);
			for (String x:D=allDecompostions(stail,g,h)) { 
				r.add(s0+" "+x);
				String astar = s0+".*.";
				if (h.G.containsKey(astar) && h.SUCC.get(h.G.get(astar).ID).hasNext(h.G.get(x)))
					r.add(s0+" "+astar);
				astar = ".*."+x;
				if (h.G.containsKey(astar) && h.SUCC.get(h.G.get(astar).ID).hasNext(h.G.get(s0)))
					r.add(astar+" "+x);
			}
			String sf = s.get(sl-1);
			List<String> shead = s.subList(0, sl-1);
			for (String x:D=allDecompostions(shead,g,h)) { 
				r.add(x+" "+sf);
				String astar = x+".*.";
				if (h.G.containsKey(astar) && h.SUCC.get(h.G.get(astar).ID).hasNext(h.G.get(sf)))
					r.add(x+" "+astar);
				astar = ".*."+sf;
				if (h.G.containsKey(astar) && h.SUCC.get(h.G.get(astar).ID).hasNext(h.G.get(x)))
					r.add(astar+" "+sf);
			}
		}
		else r.add(s.get(0));
		for (String x:r) 
			if (g.G.containsKey(x)) res.add(x);
		return res;
	}
	
	public void addEdge (Node nj, Node np, PGraph g) throws InterruptedException {
		Node nt = null;
		g.SUCC.get(nj.ID).addNext(np);
		g.PREV.get(np.ID).addNext(nj);
	}
	////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////

	public StrStrFloat showAllEdge(String x, String y, PGraph f, PGraph g, PGraph h) throws InterruptedException {
		StrStrFloat ret = new StrStrFloat(); 
		HashSet <String> X = new HashSet<String>();//toLeftIncDecomp(x,g);
		X.add(x);
		HashSet <String> Y = Decomposition(y,g,h);//subsetDecomposition(y,g,h);
		String s = x+" "+y;
		StrStrFloat curPair, lastPair; 
		curPair = lastPair = null;
		for (String a:X) {
			Node na = g.G.get(a);
			Node fa = f.G.get(a);
			if (fa == null) continue;
			for (String b:Y) {
				Node nb = g.G.get(b);
				Node fb = f.G.get(b);
				if (fb == null) continue;
				int oc = f.SUCC.get(fa.ID).hasNext(fb)? f.SUCC.get(fa.ID).count.get(fb):0;
				float max = 0;
				if (na != null && nb != null && g.SUCC.get(na.ID).hasNext(nb) || 
						oc >= 3 && fa.count/oc <100) {
					System.out.println("\t\t"+x+"=>"+y+"는 연결 후보  ("+fa.content+"=>"+fb.content+", "+oc+"/"+	fa.count+")");
					curPair = new StrStrFloat(a,b,((float)oc)/fa.count);
//					ret.add(curPair);
					if (max < curPair.power) max = curPair.power;
/*					if (lastPair == null) {
						lastPair = curPair;
					}
					else if (curPair.power > lastPair.power) {
						System.out.println("직전 페어"+lastPair.s1+"=>"+lastPair.s2+"보다 결합력이 높아 현 페어가 연결됨."); 
						ret.add(curPair);
					}
					else {
						System.out.println("직전 페어"+lastPair.s1+"=>"+lastPair.s2+"가  결합력이 더 높아 이전  페어가 연결됨.");
						ret.add(lastPair);
					}
					String s1 = fa.content+ " "+fb.content;
	*/				
				}
				else System.out.println("\t\t"+x+"=>"+y+" 연결되지 않음.  ("+fa.content+"=>"+fb.content+", "+oc+"/"+	fa.count+")");
				if (max != 0) return (new StrStrFloat(x,y,max));
			}
		}
		if (ret == null && curPair != null) return curPair;
		else return null;
	}
	
	public void SA(int iter,String s, PGraph f, PGraph g, PGraph h) throws InterruptedException {
		String M[] = s.split(" ");
		List<String> m = Arrays.asList(M);
		int ml = m.size();
		String oa,ob,a,b;
		int alength = 0;
		String lastFound = "";
		int lastLen = 0;
		StrStrFloat lastPair, curPair;
		lastPair = curPair = null;
		for (String ss = s;!ss.equals("");ss = ss.substring(lastLen)) { 
			oa = biggestLeadingConcept(mainf.ws,ss,f,g,h);
			HashSet <String> X = toLeftIncDecomp(oa,f);

			if (oa.length() == ss.length()) 
				break;
			ob = biggestLeadingConcept(mainf.ws,ss.substring(lastLen = oa.length()+1),f,g,h);
			HashSet <String> Y = subsetDecomposition(ob,f,h);
			
			curPair = showAllEdge(oa,ob,f,g,h);
			if (curPair == null && lastPair != null) {
				mainf.res.add(lastPair);
				lastPair = null;
			}
			else if (lastPair == null) 
				lastPair = curPair;
			else if (curPair.power > lastPair.power) {
				System.out.println("직전 페어"+lastPair.s1+"=>"+lastPair.s2+"보다 결합력이 높아 현 페어가 연결됨."); 
				mainf.res.add(curPair);
			}
		else {
			System.out.println("직전 페어"+lastPair.s1+"=>"+lastPair.s2+"가  결합력이 더 높아 이전  페어가 연결됨.");
			mainf.res.add(lastPair);
		}
			
		}
		System.out.println("**********  "+iter+" 번째 분석 결과 결합된 개념 : ");
		for (StrStrFloat ss:mainf.res) System.out.println("\t\t\t"+ss.s1+"=>"+ss.s2+", "+ss.power);
		System.out.println();
	}
	public void registerAllSimples(List <String>C, PGraph f) {
		for (String s:C) { 
			String m[] = s.split(" ");
			for (int i=0;i<m.length;i++) 
				f.addNode(m[i]);
		}
	}
	public void clearAllEdges(PGraph f) {
		for (Node x:f.node) {
			f.SUCC.set(x.ID, new EdgeSet(x));
			f.PREV.set(x.ID, new EdgeSet(x));
		}
	}
	public void resetNodeCounters(PGraph f) {		
		for (Node x:f.node) x.count = 0;	
	}
	
	HashSet <String> toLeftIncDecomp(String x, PGraph f) {
		HashSet <String> ret = new HashSet <String>();
		String m[] = x.split(" ");
		for (int i=m.length-1;i>=0;i--) {
			String t0 = m[i];
			for (int j=i+1;j<m.length;j++) 
				t0 += " "+m[j]; 
			if (f.G.containsKey(t0)) ret.add(t0);
		}
		return ret;
	}
	String fa(String a) { return "("+a+")*" ; }
	EdgeSet faSet(String faa,PGraph h) { 
		EdgeSet es = null;
		if (h.G.containsKey(faa)) es=h.PREV.get(h.G.get(faa).ID);
		
		return es;
	}
	String ba(String a) { return "*("+a+")" ; }
	EdgeSet baSet(String faa,PGraph h) { 
		if (h.G.containsKey(faa)) return h.PREV.get(h.G.get(faa).ID);
		else return null;
	}
	HashSet <String> subsetDecomposition(String x, PGraph f,PGraph h) {
		HashSet <String> ret = new HashSet <String>();
		String m[] = x.split(" ");
		int ml = m.length;
		if (ml < 1) return null;
		String is = "";
		for (int i=0;i<ml;i++) {
			is += is.equals("")? m[i]:" "+m[i];
			ret.addAll(Decomposition(is,f,h));
		}
		return ret;
	}
	HashSet <String> Decomposition(String s, PGraph f,PGraph h) {
//		if (s.equals("한국/NNP 이가/JKS 주/VV ㄴ/ETM"))
//			mainf.println(s);
				
		if (decomp.containsKey(s)) return decomp.get(s);
		HashSet <String> ret = new HashSet <String>();
		String m[] = s.split(" ");
		// for String s, make 2 groups of Strings and make Decomposition of each group;
		int ml = m.length;
		if (ml < 1) return null;
		else if (ml == 1) ret.add(m[0]);
		else if (ml == 2) {
			String fa = fa(m[0]);
			if (h.G.containsKey(fa)) {
				EdgeSet es = faSet(fa,h) ; 
				if (es != null && es.hasNext(h.G.get(m[1])))
					ret.add(m[0]+" "+fa);
			}
			String ba = ba(m[1]);
			if (h.G.containsKey(ba)) {
				EdgeSet es = baSet(ba,h) ; 
				if (es != null && es.hasNext(h.G.get(m[0])))
					ret.add(ba+" "+m[1]);
			}
		}
		else {
			for (int i=0;i<ml-1;i++) {
				String a = "";
				for (int j=0;j<=i;j++) 
					a += a.equals("")? m[j]:" "+m[j];
				String b = "";
				for (int j=i+1;j<ml;j++)
					b += b.equals("")? m[j]:" "+m[j];
				HashSet <String> A = Decomposition(a,f,h);
				HashSet <String> B = Decomposition(b,f,h);
				for (String x:A) {
					String fx = fa(x);
					if (h.G.containsKey(x) && h.G.containsKey(fx)) {
						EdgeSet es = faSet(fx,h) ;
						String t0 = x+" "+fx;
						if (!ret.contains(t0) && es != null)
							for (String y:B) 
								if (es.hasNext(h.G.get(y)))
									ret.add(t0);
						for (String y:B) { 
							String by = ba(y);
							if (h.G.containsKey(y) && h.G.containsKey(by) 
								&& !ret.contains(by+" "+y) && baSet(by,h)!= null 
								&& baSet(by,h).hasNext(h.G.get(x)))
								  ret.add(by+" "+y);
							ret.add(x+" "+y);
						}
					}
				}
			}
		}
		if (f.G.containsKey("*(이가/JKS) 이가/JKS *(ㄴ/ETM) ㄴ/ETM"))
		if (ret.contains("*(이가/JKS) 이가/JKS *(ㄴ/ETM) ㄴ/ETM")) 
			m = null;
		ret.add(s);
//		if (s.contains(")*") || s.contains("*(")) 
			decomp.put(s, ret);
		return ret;
	}
	HashSet <String> Decomposition2(String x, PGraph f,PGraph h) {
//		if (x.equals("한국/NNP 에/JKB 도착/NNG 하/XSV 었았/EP 다/EF"))
//				x = x;
		if (decomp.containsKey(x)) return decomp.get(x);
		if (x.equals("하/XSV 었았/EP 다/EF")) {
			int i = 1;
		}
			
		HashSet <String> ret = new HashSet <String>();
		String m[] = x.split(" ");
		int ml = m.length;
		if (ml < 1) return null;
		else if (ml == 1) ret.add(m[0]);
		else if (ml == 2) {
			String fa = fa(m[0]);
			if (h.G.containsKey(fa)) {
				EdgeSet es = faSet(fa,h) ; 
				if (es != null && es.hasNext(h.G.get(m[1])))
					ret.add(m[0]+" "+fa);
			}
			String ba = ba(m[1]);
			if (h.G.containsKey(ba)) {
				EdgeSet es = baSet(ba,h) ; 
				if (es != null && es.hasNext(h.G.get(m[0])))
					ret.add(ba+" "+m[1]);
			}
		}
		else {
			String fa = fa(m[0]);
			if (h.G.containsKey(fa)) {
				String ts = "";
				for (int i=1;i<ml;i++) ts += ts.equals("")? m[i]:" "+m[i];  // m0를 제외한 나머지 부분을 ts에 넣음 
				HashSet <String> hs = Decomposition(ts,f,h) ; // ts의 decomp를 hs에 넣음 
				EdgeSet es = faSet(fa,h) ;  
				String t0,t1;
				if (es != null)
					for (String s:hs) { 
						if (!ret.contains(t0=m[0]+" "+fa) && es.hasNext(h.G.get(s)))
							ret.add(t0);
						ret.add(m[0]+" "+s);
						String bs = ba(s);
						if (!ret.contains(t1=bs+" "+s) && baSet(bs,h)!= null && baSet(bs,h).hasNext(h.G.get(m[0])))
							ret.add(t1);
					}
			}
			String ba = ba(m[ml-1]);
			if (h.G.containsKey(ba)) {
				String ts = "";
				for (int i=0;i<ml-1;i++) ts += ts.equals("")? m[i]:" "+m[i];
				HashSet <String> hs = Decomposition(ts,f,h) ;
				EdgeSet es = baSet(ba,h) ;
				String t0,t1;
				if (es != null) 
					for (String s:hs) {
						if (!ret.contains(t0=ba+" "+m[ml-1]) && es.hasNext(h.G.get(s)))
							ret.add(t0);
						ret.add(s+" "+m[ml-1]);
						String fs = fa(s);
						if (!ret.contains(t1=s+" "+fs) && 
								faSet(fs,h)!= null && 
								faSet(fs,h).hasNext(h.G.get(m[ml-1])))
							ret.add(t1);
					}
			}
		}
		ret.add(x);
		decomp.put(x, ret);
		return ret;
	}
	public void addAllEdge(HashSet<String> X, HashSet<String> Y, PGraph f, PGraph h) throws InterruptedException {
		for (String a:X) { 
			addAllEdge(a,Y,f,h);
			/*
			Node na = f.G.get(a);
			if (na == null) continue;
			for (String b:Y) {
				Node nb = f.G.get(b);
				if (nb == null) continue;
				f.SUCC.get(na.ID).addNext(nb);
				f.PREV.get(nb.ID).addNext(na);
			}
			*/
		}
	}
	public void addAllEdge(String a, HashSet<String> Y, PGraph f, PGraph h) throws InterruptedException {
		Node na = f.G.get(a);
		HashSet <String> linked = new HashSet<String>();
		if (na != null) 
			for (String bb:Y) {
				String B[] = bb.split("  ");
				String b = B[1];
				Node nb = f.G.get(b);
				if (nb != null) {
					f.SUCC.get(na.ID).addNext(nb);
					f.PREV.get(nb.ID).addNext(na);
				}
				if ((b = B[0]).equals(B[1])) continue;
				if (linked.contains(b)) continue;
				linked.add(b);
				nb = f.G.get(b);
				if (nb == null) continue;
				f.SUCC.get(na.ID).addNext(nb);
				f.PREV.get(nb.ID).addNext(na);
			}
	}
	public void addAllEdge(String x, String y, PGraph f, PGraph h) throws InterruptedException {
		HashSet <String> X = new HashSet<String>();//toLeftIncDecomp(x,g);
		X.add(x);
		HashSet <String> Y = subsetDecomposition(y,f,h);
		for (String a:X) {
			Node na = f.G.get(a);
			if (na == null) continue;
			for (String b:Y) {
				Node nb = f.G.get(b);
				if (nb == null) continue;
				f.SUCC.get(na.ID).addNext(nb);
				f.PREV.get(nb.ID).addNext(na);
			}
		}
	}

	public String biggestLeadingConcept(int len,String s,PGraph f, PGraph g, PGraph h) throws InterruptedException {
		int MAX = s.length()>len? len:s.length();
		
		int sloc;
		String m[] = s.split(" ");
		String test = m[0];
		String found = test;
		
		for (int i=1;i<MAX && i<m.length;i++) {
			String t = test+" "+m[i];
			//		if (f.G.containsKey(t))
			//			found = t;
			for(String tt:Decomposition(t, f, h)) 
				if (f.G.containsKey(tt))
					found = t;
			test = t;
		}
		return found;
	}
	public HashSet<String> biggestLeadingConcepts(int len,String s,PGraph f, PGraph g, PGraph h) throws InterruptedException {
		HashSet<String> ret = new HashSet<String> ();
		int MAX = s.length()>len? len:s.length();
		
		int sloc;
		String m[] = s.split(" ");
		String test = m[0];
		String found = test;
		
		for (int i=1;i<MAX && i<m.length;i++) {
			String t = test+" "+m[i];
			//		if (f.G.containsKey(t))
			//			found = t;
			HashSet <String> decm = Decomposition(t, f, h); 
			for(String tt:decm) 
				if (f.G.containsKey(tt)) { 
					ret.clear();
					for(String ttt:decm) ret.add(ttt); // 현재까지 발견된 가장 긴, 풀 디컴포지션이 등록된 패턴의 풀 디컴포지션을 ret에 삽입
				}
			test = t;
		}
		return ret;
	}
	public HashSet<String> LeadingConcepts(int len,String s,PGraph f, PGraph g, PGraph h) throws InterruptedException {
		HashSet<String> ret = new HashSet<String> ();
		int MAX = s.length()>len? len:s.length();
		
		int sloc;
		String m[] = s.split(" ");
		String test = "";
		String found = "";
		
		for (int i=0;i<MAX && i<m.length;i++) {
			String t = i == 0? m[0] : test+" "+m[i];
			HashSet <String> decm = Decomposition(t, f, h); 
			if (decm == null) return null;
			for(String tt:decm) 
				if (f.G.containsKey(tt))  
					ret.add(t+"  "+tt); // 현재까지 발견된 가장 긴, 풀 디컴포지션이 등록된 패턴의 풀 디컴포지션을 ret에 삽입
			test = t;
		}
		return ret;
	}
	public String biggestLeadingConcept4L(int len,List<String> m,PGraph f, PGraph h) throws InterruptedException {
		if (m.size()==0){
			return null;
		}
		int MAX = m.size()>len? len:m.size();
		int sloc;
		String test = m.get(1);
		String found = test;
		for (int i=0;i<MAX && i<m.size();i++) {
			String t = test+" "+m.get(i);
//			if (f.G.containsKey(t))
//				found = t;
			HashSet <String> decm = Decomposition(t, f, h); 
			for(String tt:decm) 
				if (f.G.containsKey(tt))
					found = t;
			test = t;
		}
		return found;
	}
	public HashSet<String> LeadingConcepts4L(int len,List<String> m,PGraph f, PGraph h) throws InterruptedException {
		HashSet <String> ret  = new HashSet<String>();
		if (m.size()==0){
			return null;
		}
		if (len == 1) {ret.add(m.get(0)+"  "+m.get(0)); return ret;}
		int MAX = m.size()>len? len:m.size();
		String test = "";
		for (int i=0;i<MAX && i<m.size();i++) {
			String t = i==0? m.get(0) : test+" "+m.get(i);
//			if (f.G.containsKey(t))
//				found = t;
			HashSet <String> decm = Decomposition(t, f, h); 

			for(String tt:decm) 
				if (f.G.containsKey(tt))
					ret.add(t+"  "+tt);
			test = t;
		}
		return ret;
	}
	public HashSet<String> LeadingConcepts4LiNode(int len,List<iNode> m,PGraph f, PGraph h) throws InterruptedException {
		HashSet <String> ret  = new HashSet<String>();
		if (m.size()==0){
			return null;
		}
		if (len == 1) {ret.add(m.get(0).label+"  "+m.get(0).label); return ret;}
		int MAX = m.size()>len? len:m.size();
		String test = "";
		for (int i=0;i<MAX && i<m.size();i++) {
			String t = i==0? m.get(0).label : test+" "+m.get(i).label;
//			if (f.G.containsKey(t))
//				found = t;
			HashSet <String> decm = Decomposition(t, f, h); 

			for(String tt:decm) 
				if (f.G.containsKey(tt))
					ret.add(t+"  "+tt);
			test = t;
		}
		return ret;
	}
	
	public String toLeftSmallerConcept(String s,PGraph f, PGraph g, PGraph h) throws InterruptedException {
		String m[] = s.split(" ");
		if (m.length == 1) return null; 
		int ml =  m.length;
		String test = m[0];
		String found = test;
		for (int i=1;i<ml-1;i++) {
			String t = test+" "+m[i];
			if (f.G.containsKey(t))
				found = t;
			test = t;
		}
		return found;
	}
	public String toRightSmallerConcept(String s,PGraph f, PGraph g, PGraph h) throws InterruptedException {
		String m[] = s.split(" ");
		int ml =  m.length;
		if (ml <= 1) return null; 
		String test = m[ml-1];
		String found = test;
		for (int i=ml-2;i>0;i--) {
			String t = m[i]+" "+test;
			if (f.G.containsKey(t))
				found = t;
			test = t;
		}
		return found;
	}

	public void linkFollowerOfSubset(int iter,String seg,String ss,PGraph f,PGraph h) throws InterruptedException{
//		if (ss.contains("한국/NNP 이가/JKS 주/VV ㄴ/ETM") && 
		if (seg.equals( "한국/NNP 이가/JKS"))
			iter = iter+1-1;
		HashSet<String> ret = new HashSet<String> ();
		List <String >m = Arrays.asList(seg.split(" "));
		List <String >M = Arrays.asList(ss.split(" "));
		int ML = M.size();
		int ml = m.size();
		
		for (int len=1;len<=ml;len++) {
			for (int begin=0;begin < ml; begin++) {
				String s = "";
				int i=0;
				for (;i<len;i++) 
					if (i+begin < ml) s += s.equals("")? m.get(i+begin):" "+m.get(i+begin);
				if (ss.contains("한국/NNP 이가/JKS 주/VV ㄴ/ETM") && s.equals( "한국/NNP 이가/JKS"))
					i=i+1-1;
				int u = mainf.ws;
				String ob = null;
				HashSet <String> Y= new HashSet<String>();
				if (begin+len != ML) { 
					ob = biggestLeadingConcept4L(u,M.subList(begin+len,Math.min(M.size(),begin+len+u)) ,f,h);
					Y = subsetDecomposition(ob,f,h);
				}
				for (String s1:Decomposition(s,f,h)) {
					if (i == len && f.G.containsKey(s1)) {
						f.G.get(s1).count++;
						if (s1.equals("*(이가/JKS) 이가/JKS"))
//						if (s1.equals("*(이가/JKS) 이가/JKS *(ㄴ/ETM) ㄴ/ETM"))
							i = i+1-1;
						if (!Y.isEmpty()) addAllEdge(s1,Y,f,h);
					}
				}
				if (ob == null) return;
			}
		}
	}
	public int count(String tomatch,String whole) {
		int ret = 0;
		for (int i=whole.indexOf(tomatch);i>=0;i=whole.indexOf(tomatch)) {
			ret++;
			whole = whole.substring(i+tomatch.length());
		}
		return ret;
	}
	public String maxs(HashSet<String> seg) {
		if (seg == null) return null;
		int max = 0;
		String maxs = "";
		for (String ms:seg){
			String cs = ms.split("  ")[0];
			List <String> s = Arrays.asList(cs.split(" "));
			if (s.size() > max) {
				maxs = cs;
				max = s.size();
			}
		}
		return maxs;
	}
	public static int oc = 0;
	public void linkFollowerOfSet(int iter,HashSet<String> seg,List<String> M,PGraph f,PGraph h) throws InterruptedException{
		//이 함수에서 로컬 해쉬셋을 운영하는 것이 정답일 수 있음
		HashSet<String> ret = new HashSet<String> ();
		String maxs = maxs(seg);
//		HashMap<String,HashSet<String>> known = new HashMap<String,HashSet<String>>();
//		HashSet <String> counted = new HashSet <String>();
		for (String ms:seg){//int len=1;len<=ml;len++) {
			int u = mainf.ws;
			String temp[] = ms.split("  ");
			String op = temp[0]; // original pattern
			String tp = temp[1]; // transformed pattern
			
			List <String >m = Arrays.asList(op.split(" "));

			f.G.get(tp).count++;
			if (f.G.containsKey(op) && !op.equals(tp)) 
				f.G.get(op).count += count(op,maxs);

			if (tp.equals("*(은는/JX) 은는/JX"))
				mainf.calledN += 0;
			
			/*
			List<String> input;
			HashSet<String> Y;
			if (!known.containsKey(input=M.subList(m.size(),Math.min(M.size(),m.size()+u)))) {
				Y = LeadingConcepts4L(u,M.subList(m.size(),Math.min(M.size(),m.size()+u)) ,f,h);
				known.put(input, Y);
			}
			else Y = known.get(Y);*/
//			HashSet<String> TP = toRightSmallerConcepts(tp,f,h);
//			HashSet<String> OP = !tp.equals(op)? toRightSmallerConcepts(op,f,h) : null; 
			
			HashSet<String> Y = LeadingConcepts4L(u,M.subList(m.size(),Math.min(M.size(),m.size()+u)) ,f,h);
			if (Y != null && !Y.isEmpty()) {
//				if (TP != null ) addAllEdge(TP,Y,f,h);
//				if (OP != null ) addAllEdge(OP,Y,f,h);
//				for (String tp2:TP) 
//					if (!tp2.equals(tp)) 
//						f.G.get(tp2).count++;
				
////				if (!counted.contains(op)) {
					addAllEdge(tp,Y,f,h);
////					counted.add(op);
////				}
////				if (!counted.contains(tp)) 
			}
//			if (f.G.get("*(은는/JX) 은는/JX").count != oc) {
//				System.out.println(f.G.get("*(은는/JX) 은는/JX").count);
//				oc = f.G.get("*(은는/JX) 은는/JX").count;
//			}
		}
	}
	public HashSet<String> toRightSmallerConcepts(String s,PGraph f, PGraph h) throws InterruptedException {
		if (s == null || s.equals("")) return null;
		HashSet<String> ret = new HashSet<String> ();
		ret.add(s);
		int i = s.indexOf(" ");
		if (i < 0) ret.add(s);
		else for (String is = s.substring(i+1);i>0;is = is.substring(i+1)) {
			ret.add(is);
			if ((i = is.indexOf(" "))<0) break ;
		}
		return ret;
	}
	public void linkSimples(int iter, List <String>C, PGraph f, PGraph g, PGraph h) throws InterruptedException {
		if (iter == 1) registerAllSimples(C,f);
		else {
			clearAllEdges(f);
			resetNodeCounters(f);
		}
		int ix = 0;
		int u = iter == 1? 1:mainf.ws;
		for (String s:C) { 
			List <String> S;
			if (iter >= 1 && ix++ %500 == 0 ) 
				System.out.println(ix + ": "+s);
			int lastLen = 0;
			for (S= Arrays.asList(s.split(" "));!S.equals("");S = S.subList(1, S.size())) {//String ss = s;!ss.equals("");ss = ss.substring(lastLen)) { 
//			for (S= Arrays.asList(s.split(" "));!S.equals("");S = S.subList(lastLen, S.size())) {//String ss = s;!ss.equals("");ss = ss.substring(lastLen)) { 
//				oa = biggestLeadingConcept((iter-1)*mainf.ws+2,ss,f,g,h);
				HashSet<String> OA = LeadingConcepts4L(u,S,f,h);
				lastLen = count(" ",maxs(OA))+1;
//				linkFollowerOfSubset(iter,oa,ss,f,h);
	//			mainf.println(f.G.get("은는/JX").count);

				linkFollowerOfSet(iter,OA,S,f,h);
	//			int c = f.G.get("*(은는/JX) 은는/JX").count; 
	//			if (c > g.G.get("*(은는/JX) 은는/JX").count)
	//			if (c > ix)
	//				mainf.println(s);
// mainf.println(f.G.get("은는/JX").count);
				if (lastLen >= S.size()) break;
			}
		}
		f.sortEdge();
		System.out.println("sorting ended");
	}

	public void findPatterns(List <String>C, PGraph f, PGraph g, PGraph h) throws InterruptedException{
		// 2단계 basic 게쉬탈트 그래프 f생성 : 전체 노드에 대해, 후속 연결 노드가 연결 비중의 1%를 넘으면 게쉬탈트 그래프 f에 edge 추가 . 별도 결합노드 생성, f와 g에 추가함.  
		Node fx,hx,fy;
		List <Node> node2add = new ArrayList<Node>();
		HashSet <Node> node2add2 = new HashSet<Node>();
		for (Node x:f.node) 
			if (!g.G.containsKey(x.content)) g.addNode(new Node(x));
			
		for (Node x:f.node) {
			fx = g.G.get(x.content);
			EdgeSet xnext = f.SUCC.get(x.ID); 
			if (xnext.sortedI.size() > 0) {
				for(Node y : xnext.sortedI) {
					int t = xnext.count.containsKey(y)? xnext.count.get(y):0;
					
					if (g.SUCC.get(fx.ID).hasNext(fy=g.G.get(y.content)) && 
							t == g.SUCC.get(fx.ID).count.get(fy)) 
						continue;
					if (t <= mainf.MO || x.count/t > mainf.cvi) break;
					else {
						fy = g.G.get(y.content); 
						Node xy = new Node(x.content+" "+y.content);
						xy.count =  t ;
						node2add.add(xy);// g에 결합노드를 추가하기 위한 리스트에 결합노드 xy를  등록

						// 게쉬탈트 그래프 f에 y,xy,x->y edge 등록
						Node fxy = g.addNode(new Node(xy)); 
						g.SUCC.get(fx.ID).setEdge(fy,t);//f.SUCC.get(x.ID).count.get(y)); // 게쉬탈트 그래프 f의 노드 x의 후속 노드로 y 설정 & 연결 강도를 g에서 가져옴
						g.PREV.get(fy.ID).setEdge(fx,t);//f.PREV.get(y.ID).count.get(x)); // 게쉬탈트 그래프 f의 노드 x의 후속 노드로 y 설정 & 연결 강도를 g에서 가져옴
					}
				}
			}
		}
		for (Node xy:node2add)   // 신규 결합노드를 기본인접연결 그래프 g에 추가
			f.addNode(xy);// 결합노드 등록
		g.sortEdge();
	}

	public void findAssociables(List <String>C, PGraph f, PGraph g, PGraph h) throws InterruptedException{
		// 3단계 hierarchy graph 생성 , 3단계에서 생성된 노드는 풀 그래프에 넣고, G 그래프에는 넣지 않았다가 2단계에서 f그래프를 통해 넣게 한다.
		Node c;
//		h = new PGraph();
		for (Node x:g.node) 
			if (!h.G.containsKey(x.content))// && (!g.SUCC.get(x.ID).sortedI.isEmpty() || !g.PREV.get(x.ID).sortedI.isEmpty() )) 
				h.addNode(new Node(x));
		HashSet <Node> node2add2 = new HashSet <Node>();
		for (Node x:g.node) {
			EdgeSet xnext = g.SUCC.get(x.ID);
			if (xnext.sortedI.size() > 0) { // 게쉬탈트 그래프에서 x가 2 이상의 후속 노드를 가지면
				String fax = fa(x.content);
				if (!h.G.containsKey(fax)) c = h.addNode(fax, false);  // x의 associable set 노드 c ('x.*.'를 레이블로 하는 노드)를 종류 그래프 h에 생성 
				else c = h.G.get(fax);
				for (Node y:xnext.sortedI) { // x의 후속 어소셔블 노드 y들에 대해
					Node yy = h.G.get(y.content);
					if (!h.PREV.get(c.ID).hasNext(yy)) { // fa(x)로 y가 설정되어 있지 않으면, 
						int xyn = xnext.count.get(y);
						c.count += xyn;
						h.PREV.get(c.ID).addNext(yy);	// x의 후속 노드 y를 c=FA(x)=x*:의 원소(=PREV)로 설정  
						h.PREV.get(c.ID).count.put(yy,xyn);
						h.SUCC.get(yy.ID).addNext(c);	// x의 후속 노드 y의 포함 상위 개념(=SUCC) 을 c=FA(x)=x*:로 설정
						h.SUCC.get(yy.ID).count.put(c,xyn);
					}
				}
				Node fc = f.addNode(new Node(c));  	// c를 f에도 생성, 즉 x의 associable set node를 g에도 생성 
				Node fxc = f.addNode(x.content+" "+c.content); //x+FA(x) 도 f 그래프에 추가  
				fxc.count = c.count; // 새로 생성된 노드의 출현값은 x 어소셔블의 출현값과 동일 
			}
			if ((xnext=g.PREV.get(x.ID)).sortedI.size() > 1) {
				String bax = ba(x.content);
				if (!h.G.containsKey(bax)) c = h.addNode(bax, false);
				else c = h.G.get(bax);
				int xnum = 0;
				for (Node y:xnext.sortedI) { // x에 선행 결합 가능한  y에 대해
					Node yy = h.G.get(y.content);
					if (!h.PREV.get(c.ID).hasNext(yy)) {
						int xyn = xnext.count.get(y);
						c.count += xyn;
						h.PREV.get(c.ID).addNext(yy); // x에 선행 결합 가능한 것들을 c(== *:x)의 원소(SUCC)로 함
						h.PREV.get(c.ID).count.put(yy,xyn);
						h.SUCC.get(yy.ID).addNext(c);// x에 선행 결합 가능한 것들의 상위 개념(PREV)을  c로 함
						h.SUCC.get(yy.ID).count.put(c,xyn);
					}
				}
				Node fc = f.addNode(new Node(c)); // full graph에 x의 associable(== *.x == c)를 추가 
				Node fxc = f.addNode(c.content+" "+x.content);
//				fxc.count = c.count;
			}
		}
		h.sortEdge();
	}
	public void test(int iter, List <String>C, PGraph f, PGraph g, PGraph h) throws InterruptedException {
//		String test = "하/XSV (하/XSV)*";
		String test = "*(은는/JX) 은는/JX";
//		String test = "*(이가/JKS) 이가/JKS *(ㄴ/ETM) ㄴ/ETM";
		if (f.G.containsKey(test) ){
			if (0 < f.SUCC.get(f.G.get(test).ID).sortedI.size() && 0 < g.SUCC.get(g.G.get(test).ID).sortedI.size() ) 
				System.out.print("****   "+iter+" 번째 반복 후  "+test+" : "+f.G.get(test).count+" in F, but in G "+g.G.get(test).count+" 의 최빈후속노드 :  " );
			Node t = f.G.get(test);
			for (int i=0;i<50;i++) {
				if (i < f.SUCC.get(f.G.get(test).ID).sortedI.size() ) 
					System.out.print("\t\t"+(1+i)+"위:f에서 "+f.SUCC.get(t.ID).count.get(f.SUCC.get(t.ID).sortedI.get(i))+"번 출현한 "+f.SUCC.get(t.ID).sortedI.get(i).content+ ", 그앞 최다출현은 "+
							f.PREV.get(f.SUCC.get(t.ID).sortedI.get(i).ID).sortedI.get(0).content+" 과 "+f.PREV.get(f.SUCC.get(t.ID).sortedI.get(i).ID).sortedI.get(1).content+" // ");
				else {
					System.out.println("f에 더 없음");
					break;
				}mainf.println();
				if (i < g.SUCC.get(g.G.get(test).ID).sortedI.size() )
					System.out.print("\t\t"+(1+i)+"위 :g에서 "+g.SUCC.get(t.ID).count.get(g.SUCC.get(t.ID).sortedI.get(i))+"번 출현한 "+g.SUCC.get(t.ID).sortedI.get(i).content+ ", 그앞 최다출현은 "+
							g.PREV.get(g.SUCC.get(t.ID).sortedI.get(i).ID).sortedI.get(0).content+" 과 "+g.PREV.get(g.SUCC.get(t.ID).sortedI.get(i).ID).sortedI.get(1).content+" // ");
				
				else {
					System.out.println("g에 더 없음");
				}mainf.println();
			}
		}
	}
	public  void findPairs(int iter, List <String>C, PGraph f, PGraph g, PGraph h) throws InterruptedException {
		decomp.clear();
		int edges = 0; System.out.println();
		if (f.G.containsKey("*(은는/JX) 은는/JX"))
			mainf.println(f.G.get("*(은는/JX) 은는/JX").count+ " in F");
		if (g.G.containsKey("*(은는/JX) 은는/JX"))
			mainf.println(g.G.get("*(은는/JX) 은는/JX").count+ " in G");

		// 1단계: 전체 문장의 구성 형태소에 대해 인접 형태소간의 연결 엣지와 연결 빈도를 담은 그래프 g구성
		linkSimples(iter,C,f,g,h);
		if (f.G.containsKey("*(은는/JX) 은는/JX"))
			mainf.println(f.G.get("*(은는/JX) 은는/JX").count+ " in F");
		if (g.G.containsKey("*(은는/JX) 은는/JX"))
			mainf.println(g.G.get("*(은는/JX) 은는/JX").count+ " in G");
		int nodes = 0;
		for (Node x:f.node) {
			if (nodes++ % 10000 == 0) mainf.println("1단계 후 노드 확인 : "+nodes +"-th node : "+x.content); 
			edges += f.SUCC.get(x.ID).sortedI.size();
		}
		System.out.println(iter+" 번째 반복 : 1단계 후  f의  총 노드수 " + f.G.keySet().size()+", 총 엣지 수 "+edges);
//		test(iter,C,f,g,h);
		if (f.G.containsKey("*(은는/JX) 은는/JX"))
			mainf.println(f.G.get("*(은는/JX) 은는/JX").count+ " in F");
		if (g.G.containsKey("*(은는/JX) 은는/JX"))
			mainf.println(g.G.get("*(은는/JX) 은는/JX").count+ " in G");

//		SA(iter, mainf.target,f,g,h);

		// 2단계 basic 게쉬탈트 그래프 f생성 : 전체 노드에 대해, 후속 연결 노드가 연결 비중의 1%를 넘으면 게쉬탈트 그래프 f에 edge 추가 . 별도 결합노드 생성, f와 g에 추가함.
		findPatterns(C,f,g,h);
		;
		if (f.G.containsKey("*(은는/JX) 은는/JX"))
			mainf.println(f.G.get("*(은는/JX) 은는/JX").count+ " in F");
		if (g.G.containsKey("*(은는/JX) 은는/JX"))
			mainf.println(g.G.get("*(은는/JX) 은는/JX").count+ " in G");
		edges = 0;
		for (Node x:f.node) edges += f.SUCC.get(x.ID).sortedI.size(); 
		System.out.println(iter+" 번째 반복 : 2단계 후  f의  총 노드수 " + f.G.keySet().size()+", 총 엣지 수 "+edges);

		edges = 0;
		for (Node x:g.node) edges += g.SUCC.get(x.ID).sortedI.size(); 
		System.out.println("\t"+iter+" 번째 반복 : 2단계 후  g의  총 노드수 " + g.G.keySet().size()+", 총 엣지 수 "+edges);

		// 3단계 ; 게쉬탈트 그래프f에서 종류 그래프 h생성 & 유노드와 종노드 연결
		edges = 0;
		findAssociables(C,f,g,h);
		for (Node x:f.node) edges += f.SUCC.get(x.ID).sortedI.size(); 
		System.out.println(iter+" 번째 반복 : 3단계 후  f의  총 노드수 " + f.G.keySet().size()+", 총 엣지 수 "+edges);
		edges = 0;
		for (Node x:g.node) edges += g.SUCC.get(x.ID).sortedI.size(); 
		System.out.println("\t"+iter+" 번째 반복 : 3단계 후  g의  총 노드수 " + g.G.keySet().size()+", 총 엣지 수 "+edges);
		edges = 0;
		for (Node x:h.node) edges += h.SUCC.get(x.ID).sortedI.size(); 
		System.out.println("\t\t"+iter+" 번째 반복 : 3단계 후  h의  총 노드수 " + h.G.keySet().size()+", 총 엣지 수 "+edges);
		if (f.G.containsKey("*(은는/JX) 은는/JX"))
			mainf.println(f.G.get("*(은는/JX) 은는/JX").count+ " in F");
		if (g.G.containsKey("*(은는/JX) 은는/JX"))
			mainf.println(g.G.get("*(은는/JX) 은는/JX").count+ " in G");
		if (f.G.containsKey("은는/JX"))
			mainf.println(f.G.get("은는/JX").count+ " in F");
		if (g.G.containsKey("은는/JX"))
			mainf.println(g.G.get("은는/JX").count+ " in G");
		if (f.G.containsKey("*(은는/JX)"))
			mainf.println(f.G.get("*(은는/JX)").count+ " in F");
		if (g.G.containsKey("*(은는/JX)"))
			mainf.println(g.G.get("*(은는/JX)").count+ " in G");
		}

	public void buildTrees(List<iNode> input, PGraph f, PGraph g, PGraph h) {
		// TODO Auto-generated method stub
		
	}
}
