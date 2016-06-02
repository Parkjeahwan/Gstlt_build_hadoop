package com.ai.gestalt;

import com.clunix.NLP.graph.EdgeSet;
import com.clunix.NLP.graph.Node;
import com.clunix.NLP.graph.PGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class GstaltFinder {
	public void findAssociables(List <String>C, PGraph f, PGraph g, PGraph h) throws InterruptedException{
		int ten, den, aen;
		ten = den = aen = 0;
		HashSet <Node> node2add2 = new HashSet <Node>();
		for (Node x:g.node) {
			ten++;
			if (x.content.equals("15/SN"))
				ten = ten*1;
			EdgeSet xnext = g.SUCC.get(x.ID);
			if (xnext.sortedI.size() > 1) { // 게쉬탈트 그래프에서 x가 2 이상의 후속 노드를 가지면
				den++;
				Node c = h.addNode(x.content+".*.", false);  // x의 associable set 노드 c ('x.*.'를 레이블로 하는 노드)를 종류 그래프 h에 생성 
				for (Node y:xnext.sortedI) {
					Node yy = h.G.get(y.content);
					if (!h.SUCC.get(c.ID).hasNext(yy)) {
						int xyn = xnext.count.get(y);
						c.count += xyn;
						h.SUCC.get(c.ID).addNext(yy);	// x의 후속 노드 y를 c=FA(x)=x*:의 원소(=SUCC)로 설정  
						h.SUCC.get(c.ID).count.put(yy,xyn);
						h.PREV.get(yy.ID).addNext(c);	// x의 후속 노드 y의 포함 상위 개념(=PREV) 을 c=FA(x)=x*:로 설정
						h.PREV.get(yy.ID).count.put(c,xyn);
					}
				}
				Node gc = f.addNode(new Node(c));  	// c를 g에도 생성, 즉 x의 associable set node를 g에도 생성 
				node2add2.add(new Node(c)); // c를 f에도 생성하기 위해 리스트에 추가
				Node fxc = new Node(x.content+" "+c.content); //x+FA(x) 도 g,f 그래프에 추가하기 위해 새 노드를 생성  
				fxc.count = c.count; // 새로 생성된 노드의 출현값은 x 어소셔블의 출현값과 동일 
				node2add2.add(fxc); // g에 새로 생성한 노드 추가 
				f.addNode(new Node(fxc)); // f에도 추가 
//				h.addNode(new Node(fxc));
			}
			if ((xnext=g.PREV.get(x.ID)).sortedI.size() > 1) {
				aen++;
				Node c = h.addNode(".*."+x.content, false);
				for (Node y:xnext.sortedI) { // x에 선행 결합 가능한  y에 대해
					Node yy = h.G.get(y.content);
					if (!h.SUCC.get(c.ID).hasNext(yy)) {
						int xyn = xnext.count.get(y);
						c.count += xyn;
						h.SUCC.get(c.ID).addNext(yy); // x에 선행 결합 가능한 것들을 c(== *:x)의 원소(SUCC)로 함
						h.SUCC.get(c.ID).count.put(yy,xyn);
						h.PREV.get(yy.ID).addNext(c);// x에 선행 결합 가능한 것들의 상위 개념(PREV)을  c로 함
						h.PREV.get(yy.ID).count.put(c,xyn);
					}
				}
				Node gc = f.addNode(new Node(c)); // full graph에 x의 associable(== *.x == c)를 추가 
				node2add2.add(new Node(c));
				Node fxc = new Node(c.content+" "+x.content);
				fxc.count = c.count;
				node2add2.add(fxc);
				f.addNode(new Node(fxc));
//				h.addNode(new Node(fxc));
			}

		}
		for (Node xc:node2add2) 
			g.addNode(xc);
		EdgeSet ee = h.SUCC.get(h.G.get("15/SN.*.").ID);
		h.sortEdge();
		ee = h.SUCC.get(h.G.get("15/SN.*.").ID);
		
		System.out.println("전체 "+ten+" 개의 노드에  "+den+" 개  a* 개념과"+aen+" 개 *a 개념이 등록됨.");
	//	for ()

	}
	
	
	public void findPatterns(List <String>C, PGraph f, PGraph g, PGraph h) throws InterruptedException{
		// 2단계 basic 게쉬탈트 그래프 f생성 : 전체 노드에 대해, 후속 연결 노드가 연결 비중의 1%를 넘으면 게쉬탈트 그래프 f에 edge 추가 . 별도 결합노드 생성, f와 g에 추가함.  
		int ten, den, aen, nc;
		Node fx,hx;
		nc = ten = den = aen = 0;
		List <Node> node2add = new ArrayList<Node>();
		HashSet <Node> node2add2 = new HashSet<Node>();
		for (Node x:f.node) {
			hx = h.addNode(new Node(x));
			fx = g.addNode(new Node(x));

			if (ten % 10000 == 0) 
 				System.out.println("\t\t"+ten+" : "+x.content+"노드 까지 "+den+" 삭제, " + aen+" 추가됨");
			ten++;
			EdgeSet xnext = f.SUCC.get(x.ID); 
			if (xnext.sortedI.size() > 0) {
				for(Node y : xnext.sortedI) {
					if (g.G.containsKey(x.content) && g.G.containsKey(y.content) && g.SUCC.get(g.G.get(x.content).ID).hasNext(g.G.get(y.content))) 
						continue;
					int t = xnext.count.get(y);
					if (t <= 4 || x.count/t > 100) break;
					else { 
						aen++;
						Node xy = new Node(x.content+" "+y.content);
						xy.count =  t ;
						node2add.add(xy);// g에 결합노드를 추가하기 위한 리스트에 결합노드 xy를  등록

						// 게쉬탈트 그래프 f에 y,xy,x->y edge 등록
						Node fy = g.addNode(new Node(y));
						Node fxy = g.addNode(new Node(xy)); 
						g.SUCC.get(fx.ID).setEdge(fy,f.SUCC.get(x.ID).count.get(y)); // 게쉬탈트 그래프 f의 노드 x의 후속 노드로 y 설정 & 연결 강도를 g에서 가져옴
						g.PREV.get(fy.ID).setEdge(fx,
								f.PREV.get(y.ID)
								.count.get(x)); // 게쉬탈트 그래프 f의 노드 x의 후속 노드로 y 설정 & 연결 강도를 g에서 가져옴
						
						// 종류 그래프 h에 y,xy 등록
						Node hy = h.addNode(new Node(y));
						Node hxy = h.addNode(new Node(xy)); 
					}
				}
			}
		}
		g.sortEdge();
		for (Node xy:node2add)   // 신규 결합노드를 기본인접연결 그래프 g에 추가
			f.addNode(xy);// 결합노드 등록
		
		System.out.println("전체 "+ten+" 개의 노드에 "+aen+" 개 결합노드가 새로 등록되고 "+den+" 개의 엣지가 삭제됨");//되고 "+aen+" 개가 등록됨.");

	}
	public void sa(PGraph f, PGraph g, PGraph h) throws InterruptedException {
		
		String target = mainf.target;

		String m[] = target.split(" ");
		String oa,ob,a,b;
		Node na, nb;nb = null;

		for (int i=0;i<m.length-1;) {
			int j,k; j = k = 0;
			a = b = "";
			oa = m[i];
			for (j=i;j<m.length-1;j++) {
				a += a.equals("")? oa:" "+m[j];
				if (f.G.containsKey(a)) oa = a;
				else break;
			}
			na = f.G.get(oa);

			ob = m[j];
			for (k=j;k<m.length-1;k++) {
				b += b.equals("")? ob:" "+m[k];
				if (f.G.containsKey(b)) ob = b;
				else break;
			}
			nb = f.G.get(ob);
			System.out.println(oa+" ==> "+ob+"의 연결 가능성을 봅니다");
			mainf.result.addAll(getAllEdges(oa,ob,f,g,h));
			i = j;
			
		}
		for (String x:mainf.result) System.out.println("\t\t현재 분된 결합 개념들 : "+x);
	}
		
	
	public  void findMorePair(List <String>C, PGraph f, PGraph g, PGraph h) throws InterruptedException {
		sa(f,g,h);
		// 1단계: 전체 문장의 구성 형태소에 대해 인접 형태소간의 연결 엣지와 연결 빈도를 담은 그래프 g구성
		int lc = 0;
		Node nt = null;
		for (String s:C) { 
			String m[] = s.split(" ");
			String oa,ob,a,b;
			Node na, nb;nb = null;
			for (int i=0;i<m.length-1;) {
				int j,k; j = k = 0;
				a = b = "";
				oa = m[i];
				for (j=i;j<m.length-1;j++) {
					a += a.equals("")? oa:" "+m[j];
					if (f.G.containsKey(a)) oa = a; 
					else break;
					 
				}
				na = f.G.get(oa);
				
				Node noa = null;
				for (;j<m.length && j>i+1 && f.G.containsKey(m[j-1]+" "+m[j]);j++) {
					oa += m[j]; // if abcd가 등록이고, abcde(==oa)는 미등록이고, de가 등록이면 abcde를 등록
					noa = f.addNode(oa);
					if (j>i+1) {
						Node t= f.G.get(m[j]);
						f.SUCC.get(na.ID).addNext(t); // 이어서 abcd->e 엣지 추가
						f.PREV.get(t.ID).addNext(na);
					}
				}
				if (noa != null) na = noa;
				
				if (j == m.length ) {
					//ob의 allDecompostions에 대해 노드 카운트 출현수를 1씩 증가시킴 
					break;
				}
				ob = m[j];
				for (k=j;k<m.length-1;k++) {
					b += b.equals("")? ob:" "+m[k];
					if (f.G.containsKey(b)) 
						ob = b;
					else 
						break;
				}
				nb = f.G.get(ob);
				addAllEdges(oa,ob,f,h);
				if (j == m.length ) {
					//ob의 allDecompostions에 대해 노드 카운트 출현수를 1씩 증가시킴 
					break;
				}
				i = j;
			}
		}
		f.sortEdge();
		System.out.println(f.G.keySet().size()+" 노드가 등록되었습니다");
		
		// 2단계 basic 게쉬탈트 그래프 f생성 : 전체 노드에 대해, 후속 연결 노드가 연결 비중의 1%를 넘으면 게쉬탈트 그래프 f에 edge 추가 . 별도 결합노드 생성, f와 g에 추가함.
		findPatterns(C,f,g,h);
		// 3단계 ; 게쉬탈트 그래프f에서 종류 그래프 h생성 & 유노드와 종노드 연결
		findAssociables(C,f,g,h);
	}
	
	public  void findBasicPair(List <String>C, PGraph f, PGraph g, PGraph h) throws InterruptedException {
		// 1단계: 전체 문장의 구성 형태소에 대해 인접 형태소간의 연결 엣지와 연결 빈도를 담은 그래프 g구성
		int lc = 0;
		Node nt = null;
		for (String s:C) { 
			String m[] = s.split(" ");
			String a,b; 
			Node na, nb;nb = null;
			for (int i=0;i<m.length-1;i++) {  
				if (f.G.containsKey(a=m[i])) {
					na = f.G.get(a) ;
					f.node.get(na.ID).count++;
				}
				else na = f.addNode(a);

				nb = (f.G.containsKey(b=m[i+1]))? f.G.get(b) : f.addNode(b);
				addEdge(na,nb,f);
				if (nb.content.equals("의/JKG")) {
					nt = nb;
				}
			}
			if (nb != null) f.node.get(nb.ID).count++;
			if (lc++%100000 == 0 ) System.out.println((lc)+": "+s);
		}
		f.sortEdge();
		System.out.println(f.G.keySet().size()+" 노드가 새로 등록되었습니다");
		
		// 2단계 basic 게쉬탈트 그래프 f생성 : 전체 노드에 대해, 후속 연결 노드가 연결 비중의 1%를 넘으면 게쉬탈트 그래프 f에 edge 추가 . 별도 결합노드 생성, f와 g에 추가함.  
		findPatterns(C,f,g,h);
		// 3단계 ; 게쉬탈트 그래프f에서 종류 그래프 h생성 & 유노드와 종노드 연결
		findAssociables(C,f,g,h);
	}
	public static ArrayList<String> loadCorpus(String path) throws IOException, InterruptedException{	// 151110, Function to load CORPUS
		System.out.println("Corpus Loading began...");

		ArrayList<String> C = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
	    int i = 0;    
		while ((line = br.readLine()) != null) {
			i++;
			C.add(line);
		}
		
		System.out.println("Total "+i+" lines loded to MM");
		System.out.println("Last line:"+ C.get(i-1));
		br.close();
		return C;
	}

	
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
					
/*					g.SUCC.get(g.G.get(A).ID).addNext(g.G.get(B));
					if (B.equals("챔피언/NNG 스리/NNG 스리/NNG.*.") && A.equals("UEFA/SL )/SSC"))
						{int xs = 1;}
					g.PREV.get(
							g.G.get(B).ID).
					addNext(
							g.G.get(A));
							*/
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
}

