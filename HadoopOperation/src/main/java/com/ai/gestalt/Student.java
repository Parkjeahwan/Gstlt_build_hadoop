// 학습 알고리즘 2.02 - 말뭉치를 읽어들이면서 선행 의미소와 우속의미소를 연결.
// 읽어들인 의미소 ab가 선행학습을 통해 등록되어 있지 않더라도, 파악된 패턴 a의 후속 가능 패턴(a)*의 원소이면 단일 개념으로 파악하는 버전.
// findLeadingSemAndFol함수에서 a(a)*의 후속 의미소를 제외한 모든 후속 의미소를 다 미리 찾아 리턴하는 점이 2.01과 다름. 
package com.ai.gestalt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class Student {
	static final double cv = 0.01;
	static final int mo = 3;
	static final int mu = 2;
	static final double mv = 0.1;
	static int print10i = 0;
	public HashMap<Semanteme, HashMap<Semanteme, Integer>> memo = new  HashMap<Semanteme, HashMap<Semanteme, Integer>> ();
	public HashMap<Semanteme, HashMap<Semanteme, Integer>> AP, OP;
	public HashMap<Semanteme, List<Semanteme>> SUCC = new HashMap<Semanteme, List<Semanteme>> ();
	public HashMap<Semanteme, List<Semanteme>> PREV = new HashMap<Semanteme, List<Semanteme>> ();
	public HashMap<Semanteme, List<Semanteme>> succ = new HashMap<Semanteme, List<Semanteme>> ();
	public HashMap<Semanteme, List<Semanteme>> prev = new HashMap<Semanteme, List<Semanteme>> ();
	public HashMap<String, Semanteme> S;
	//static HashMap<String, HashSet<Semanteme>> decomp = new HashMap<String, HashSet<Semanteme>>();
	//	public static int linesTot = 0;
	//	public static int semsTot = 0;

	public void learn1time(int iter, ArrayList<String> C) throws IOException {
		it = iter;
		System.out.println(iter+" 번째 학습을 시작합니다.");
		test(iter, S, memo, AP);
		test2(iter,S,memo,AP);





		mainf.println("****************************\n***** " + iter +" 번째 학습을 시작합니다.  *****\n****************************"); 
		int len = (int) Math.pow(2, iter);
		//		int len = 20;
		clearCounter(S, memo, AP, OP);
		findAllPair(len, C, S, memo);
		findCommonPair(len, S, memo, AP);
		findWrongPair(len, S, memo, OP);
		buildprevsucc();
		buildPREVSUCC();
	}
	public List<Semanteme> malSUCC(String x,int n) { return topnaver(S.get(x),n,OP);}

	private List<Semanteme> topnaver(Semanteme x, int n, HashMap<Semanteme, HashMap<Semanteme, Integer>> nei) {
		List<Semanteme> ret = new ArrayList<Semanteme>();
		if (nei != null && nei.containsKey(x)) ret.addAll(nei.get(x).keySet());
		return !ret.isEmpty()? ret: null;
	}
	public List<Semanteme> topSUCC(String x,int n) { return topNEXT(S.get(x),n,SUCC);}
	public List<Semanteme> topPREV(String x,int n) { return topNEXT(S.get(x),n,PREV);}
	public List<Semanteme> topSUCC(Semanteme x,int n) { return topNEXT(x,n,SUCC);}
	public List<Semanteme> topPREV(Semanteme x,int n) { return topNEXT(x,n,PREV);}

	private List<Semanteme> topNEXT(Semanteme x,int n,HashMap<Semanteme,List<Semanteme>> nei) {
		if (nei != null) if (nei.get(x) == null) return null;
		List<Semanteme> ret = new ArrayList<Semanteme>();
		n = n <= 0? nei.get(x).size():Math.min(n, nei.get(x).size());
		if (nei !=null && nei.get(x)!=null)
			for (int i=0;i<n && i<nei.get(x).size();i++) ret.add(nei.get(x).get(i));
		return ret;
	}

	public void buildPREVSUCC() {
		if (SUCC == null) SUCC = new HashMap<Semanteme,List<Semanteme>>();
		if (PREV == null) PREV = new HashMap<Semanteme,List<Semanteme>>();
		sortNext(AP,SUCC);
		sortNext(buildInvert(AP),PREV);
	}	
	public void buildprevsucc() {
		if (succ == null) succ = new HashMap<Semanteme,List<Semanteme>>();
		if (prev == null) prev = new HashMap<Semanteme,List<Semanteme>>();
		sortNext(memo,succ);
		sortNext(buildInvert(memo),prev);
	}

	void sortNext(HashMap<Semanteme,HashMap<Semanteme,Integer>>memo, HashMap<Semanteme,List<Semanteme>>succ){
		for (Semanteme x:memo.keySet()) {
			List<StrInt> l = new ArrayList<StrInt>();
			for (Semanteme y:memo.get(x).keySet()) l.add(new StrInt(y.label,memo.get(x).get(y)));
			Collections.sort(l);
			List<Semanteme> yl = new ArrayList<Semanteme>();
			for (StrInt p:l) yl.add(S.get(p.str));
			succ.put(x, yl);
		}
	}

	HashMap<Semanteme,HashMap<Semanteme,Integer>> buildInvert(HashMap<Semanteme,HashMap<Semanteme,Integer>>memo){
		HashMap<Semanteme,HashMap<Semanteme,Integer>> temp = new HashMap<Semanteme,HashMap<Semanteme,Integer>> ();
		for (Semanteme x:memo.keySet()) 
			for (Semanteme y:memo.get(x).keySet()){
				if (!temp.containsKey(y)) temp.put(y, new HashMap<Semanteme,Integer>());
				temp.get(y).put(x, memo.get(x).get(y));
			}
		return temp;
	}
	public double sim(Semanteme a, Semanteme b, HashMap<Semanteme,List<Semanteme>> dir, int n){
		List<Semanteme> anext = dir.get(a);
		List<Semanteme> bnext = dir.get(b);
		if (anext == null || bnext == null) return 0;
		if (n == -1) n = Math.min(anext.size(), bnext.size());
		int alen = Math.min(anext.size(), n);
		int blen = Math.min(bnext.size(), n);
		long sum1,sum2,sum3;
		sum1=sum2=sum3 = 0;
		Semanteme p;
		for (int id = 0;id < alen ; id++) sum1 += (bnext.contains(p=anext.get(id)))? AP.get(a).get(p)*AP.get(b).get(p):0;
		for (int id = 0;id < alen ; id++) sum2 += Math.pow(AP.get(a).get(anext.get(id)),2);//get(b).get(p):0;
		for (int id = 0;id < blen ; id++) sum3 += Math.pow(AP.get(b).get(bnext.get(id)),2);//
		double res = (double)sum1/Math.sqrt(sum2*sum3);
		return res;

	}
	private void print10(String w,int n) {
		System.out.print(w+" , ");
		if (++print10i % n == 0) System.out.print("\n\t\t");
	}

	Semanteme setprint(String x){
		if (!S.containsKey(x) || !S.get(x).isSet) return null;
		System.out.print("집합 "+x+" = { ");
		List <StrInt> a = new ArrayList<StrInt>();
		for (Semanteme z:S.get(x).element) print10(z.label,5); System.out.println(" }");
		return S.get(x);
	}
	void setprintW(List<Semanteme> s){
		System.out.print("집합 "+" = { ");
		if (s==null) System.out.println("} => NULL");
		else for (Semanteme z:s) print10(z.label,5); System.out.println(" }");
	}
	void setprintW(String a,List<Semanteme> s){
		System.out.print("집합 "+" = { ");
		if (s==null) System.out.println("} => NULL");
		else for (Semanteme z:s) 
			if (AP.containsKey(S.get(a)) && AP.get(S.get(a)).containsKey(z)) print10(z.label+" "+((double)(AP.get(S.get(a)).get(z))/S.get(a).count),5); System.out.println(" }");
	}
	void setprint(List<Semanteme> s){
		System.out.print("집합 "+" = { ");
		if (s==null) System.out.println("} => NULL");
		else for (Semanteme z:s) print10(z.label,5); System.out.println(" }");
	}
	void printfa(String x, HashMap<String,Semanteme>S){
		if (!S.containsKey(x) || !S.containsKey(fa(x))) return;
		System.out.print(x+"의 후속 가능 집합  = { ");
		List <StrInt> a = new ArrayList<StrInt>();
		for (Semanteme z:S.get(fa(x)).element) a.add(new StrInt(z.label,S.get(x+" "+z.label).count));
		Collections.sort(a);
		for (Semanteme z:S.get(fa(x)).element) print10(z.label,5); System.out.println(" }");
	}	
	private void info(String a, String b, HashMap<String, Semanteme> S,
			HashMap<Semanteme, HashMap<Semanteme, Integer>> memo) {
		System.out.println(a+" "+b+" 등록 : "+S.containsKey(a+" "+b));		
		System.out.println(a+" "+fa(a)+" 등록 : "+S.containsKey(a+" "+fa(a)));
		System.out.println(ba(b)+" "+b+" 등록 : "+S.containsKey(ba(b)+" "+b));
		HashSet<Semanteme> ra = recreated(new Sequence (a),S);
		HashSet<Semanteme> rb = recreated(new Sequence (b),S);
		if (ra != null) for (Semanteme x:ra) {
			if (rb != null) for (Semanteme y:rb) {
				System.out.print(x.label+" "+y.label+" 등록 : "+S.containsKey(x.label+" "+y.label)+" => ");
				if (S.containsKey(x.label+" "+y.label))System.out.println(memo.get(x).get(y)+"/"+x.count);
			}
		}
	}



	private void clearCounter(HashMap<String, Semanteme> S, HashMap<Semanteme, HashMap<Semanteme, Integer>> memo,
			HashMap<Semanteme, HashMap<Semanteme, Integer>> AP, HashMap<Semanteme, HashMap<Semanteme, Integer>> OP) {
		if (S != null && !S.isEmpty())
			for (Semanteme s : S.values())
				s.count = 0;
		if (memo != null && !memo.isEmpty())
			for (HashMap<Semanteme, Integer> x : memo.values())
				for (Semanteme y : x.keySet())
					x.put(y, 0);
		if (AP != null && !AP.isEmpty())
			// for (HashMap<Semanteme,Integer> x:AP.values())
			// for (Semanteme y:x.keySet()) x.put(y, 0);
			AP = new HashMap<Semanteme, HashMap<Semanteme, Integer>>();
		if (OP != null && !OP.isEmpty())
			// for (HashMap<Semanteme,Integer> x:OP.values())
			// for (Semanteme y:x.keySet()) x.put(y, 0);
			AP = new HashMap<Semanteme, HashMap<Semanteme, Integer>>();
	}

	// NNG-NNG 쌍의 경우는 아래에 의해 찾아진 쌍이 wrong pair가 아닌 경우가 대부분이다.
	// 의미있는 맞는 연결도 출현 수가 0인 경우가 매우 많은데, 출현을 한번이라도 했다면 연결될 가능성이 꽤 크다... 특히 명사-명사 결합 들의 경우엔...
	public void findWrongPair(int len, HashMap<String, Semanteme> S,
			HashMap<Semanteme, HashMap<Semanteme, Integer>> memo,
			HashMap<Semanteme, HashMap<Semanteme, Integer>> OP) throws IOException {
		if (S == null)
			return;
		if (OP == null)
			OP = new HashMap<Semanteme, HashMap<Semanteme, Integer>>();
		for (Semanteme x : memo.keySet()) {
			HashMap<Semanteme, Integer> tm = OP.containsKey(x) ? OP.get(x) : new HashMap<Semanteme, Integer>();
			for (Semanteme y : memo.get(x).keySet()) {
				int nxy = memo.get(x).get(y);
				if (y.count > 200 && x.count > 200 && x.count * y.count * mv/mainf.semsTot > nxy) 
					tm.put(y, nxy);				
			}
			if (tm != null && !tm.keySet().isEmpty()) OP.put(x, tm);
		}
		Semanteme sm = new Semanteme();
		//		sm.savefull(mainf.pathn+mainf.fn+".malLink", OP);
	}

	public static String fa(String x) {return "(" + x + ")*";	}

	public static String ba(String x) {return "*(" + x + ")";}

	/* findCommonPair() : 말뭉치에서 출현한 모든 (a b) 페어에 대해, a=>b인지 결정하는 함수.
	 * a=>b이고 a(a)* 와 *(b)b가 생성되어 그 출현수가 a=>b의 출현수로 설정된다. 만약 이미 a(a)* 혹은 *(b)b가 있다면 그 출현수에 a=>b의 출현수를 더하면 된다.
	 * 만약 a(a)* 혹은 *(b)b가 있다면 거기에 해당하는 a b 패턴은 이미 파악되어 a(a)* 혹은 *(b)b 출현 수가 a b 출현수만큼 증가되어 있어야 한다.
	 * 즉, a=>b라고 하더라도 이미 ab가 등록되어 있다면 이미 a(a)*와 *(b)b는 등록되어 있고, 그들의 출현수도 반영돼 있다. 따라서, 파생 집합을 만들 필요도, 카운터를 증가시킬 필요도 없다.
	 * a=>b라면, ab가 등록되지 않은 경우에만 ab를 등록하고 카운터를 세팅한 다음,  ab가 등록돼 있더라도
	 * if ( a(a)*나 *(b)b가 등록되어 있지 않다면 ) a(a)*와 *(b)b를 등록하고 카운터를 세팅하고, 
	 * else a(a)*와 *(b)b의 카운터를 n(a b) 만큼 증가시킨다. 또, (a)*와 *(b)도 그만큼 증가시키면 된다.  
	 * 
	 * 
	 * if (ab 가 미등록일 때,) {
	 * 	if (a)*가 미등록이면 등록;
	 *   else n((a)*) = n(a(a)*) += n(ab)
	 *   if *(b)가 미등록이면 등
	 *   else n(*(b)) =  n(*(b)b) += n(ab)
	 *   ab를 등록;
	 * }
	 * else {  // ab 가 등록돼 있다면  
	 * 	if (a)*가 미등록이면 등록;
	 *   else ;
	 *   if *(b)가 미등록이면 등록;
	 *   else ;
	 * }
	 * */
	void findCommonPair(int len, HashMap<String, Semanteme> S,
			HashMap<Semanteme, HashMap<Semanteme, Integer>> memo,
			HashMap<Semanteme, HashMap<Semanteme, Integer>> AP) {
		if (S == null) return;
		if (AP == null) AP = new HashMap<Semanteme, HashMap<Semanteme, Integer>>();
		long tot = 0;
		for (Semanteme x : memo.keySet())	tot += memo.get(x).keySet().size();

		mainf.print("총 " + memo.keySet().size() + " 개의 패턴 엔트리와  " + tot + "개의 총 연결종류 중  ");
		int j = 0;
		for (Semanteme x : memo.keySet()) {
			if (it >=1 && x.label.equals("었았/EP"))
				it +=0;
			if (j % 10000 == 0)
				mainf.println("\t" + j + "번째로 처리된  " + x.label);
			j++;
			HashMap<Semanteme, Integer> tm = AP.containsKey(x) ? AP.get(x) : new HashMap<Semanteme, Integer>();
			int xcv = (int) (x.count * cv);
			for (Semanteme y : memo.get(x).keySet()) {
				int nxy = memo.get(x).get(y);
				if (mo < nxy && xcv < nxy) { 
					String xyl = x.label + " " + y.label;
					updateSet(x,y,nxy,S);
					tm.put(y, nxy);
				}
			}
			if (tm != null && !tm.keySet().isEmpty()) AP.put(x, tm);
		}
	}
	static int it = 0;
	// static int setsi = 0;
	private boolean updateSet(Semanteme x, Semanteme y, int nxy, HashMap<String, Semanteme> S) {
		if (x == null || y == null ) return true;
		String fax = fa(x.label);
		String bay = ba(y.label);
		String xyl = x.label + " " + y.label;
		String xfax = x.label + " " + fax;
		String bayy = bay + " " + y.label;
		Semanteme faxS , xfaxS, xy;
		// (되)* '되 ㄴ용'이 존재해도 ㄴ용 이 아직 (되)*에 속하지 않을 수 있다!!!   a(bc)를 등록하러 왔으니 a(a)*, *(bc)bc,를 모두 n(a bc)만큼 증가시켜야 함
		// 그런데, abc, (a)* 가 모두 등록돼 있으니 a(a)*가 증가되지 않음!
		// a(a)* 를 무조건 증가시키게 하면,  새로 등록돼야 하는 a(bc)외에 ab가 들어와도 (ab는 이미 all 단계에서 카운터 설정됨) ab가 추가됨. 
		// a(a)* abc ab 등이  등록된 상태에서 a,bc 조합이 들어왔을 때 a bc 조합이 새로 들어왔는지 어떤지를 알 수 있는 방법은 없는가?
		// 있다. bc등이 들어오면 무조건 증가시킨다. 단, ab가 들어왔을 때에만 증가시키지 않으면 된다. 
		// 즉, a b1b2...bn가 들어왔을 때, a b1...a bi 까지가 등록돼 있고   
		boolean Exy = S.containsKey(xyl);
		xy = Exy? S.get(xyl):new Semanteme(xyl,nxy,x.isSet||y.isSet);
		if (!Exy) {
			S.put(xyl, xy);
			xy.part.add(x); xy.part.add(y);
		}
		if (!S.containsKey(fax)) {   
			S.put(fax, faxS = new Semanteme(fax, nxy ,true));
			S.put(xfax, xfaxS = new Semanteme(xfax, nxy, true));
			xfaxS.part.add(x);
			xfaxS.part.add(faxS);
			faxS.element.add(y);
			y.set.add(faxS);
			xfaxS.element.add(xy);
			xy.set.add(xfaxS);
		}
		else if (!Exy || Exy && !y.isPartOf(xy)){
			faxS = S.get(fax);
			xfaxS = S.get(xfax);  // (x)*가 등록돼 있으므로, 거기에 속하는 e 들은 모두 n(x e)를 n(x(x)*)에 더한 상태!!!
			faxS.count = (xfaxS.count += nxy); // y가 멤버이면 이미 xfax가 등록되어 있어서 registerFollowers에서 xfax의 카운터값을 설정함
			faxS.element.add(y);
			y.set.add(faxS);
			xfaxS.element.add(xy);
			xy.set.add(xfaxS);
			if (Exy) {
				xy.part.add(x); xy.part.add(y);
			}
		}
		Semanteme bayS,bayyS; 
		if (!S.containsKey(bay)) {
			S.put(bay, bayS = new Semanteme(bay, nxy, true));
			S.put(bayy, bayyS = new Semanteme(bayy, nxy, true));
			bayyS.part.add(bayS);
			bayyS.part.add(y);
			bayS.element.add(x);
			x.set.add(bayS);
			bayyS.element.add(xy);
			xy.set.add(bayyS);
		}
		else if (!Exy|| Exy && !x.isPartOf(xy=S.get(xyl))){
			bayS = S.get(bay);
			bayyS = S.get(bayy);
			bayS.count = (bayyS.count += nxy);
			bayS.element.add(x);
			x.set.add(bayS);
			bayyS.element.add(xy);
			xy.set.add(bayyS);
			if (Exy) {
				xy.part.add(x); xy.part.add(y);
			}
		}
		return !Exy;
	}
	private void updateSet(Semanteme x, Semanteme y, Semanteme xy, HashMap<String, Semanteme> S) {
		if (x == null || y == null ) return;
		String fax = fa(x.label);
		String bay = ba(y.label);  
		String xfax = x.label + " " + fax;
		String bayy = bay + " " + y.label;
		Semanteme faxS , xfaxS;
		if (!S.containsKey(fax)) {
			S.put(fax, faxS = new Semanteme(fax, xy.count ,true));
			S.put(xfax, xfaxS = new Semanteme(xfax, xy.count, true));
			xfaxS.part.add(x);
			xfaxS.part.add(faxS);
		}
		else {
			faxS = S.get(fax);
			xfaxS = S.get(xfax);  // (x)*가 등록돼 있으므로, 거기에 속하는 e 들은 모두 n(x e)를 n(x(x)*)에 더한 상태!!!
			faxS.count += xy.count;  
			xfaxS.count += xy.count; // y가 멤버이면 이미 xfax가 등록되어 있어서 registerFollowers에서 xfax의 카운터값을 설정함
		}
		/*
 		if (!y.isMemberOf(faxS,S)) { // x=>y인 경우 호출 되므로 y는 (x)*의 원소인데, 아니라고 등록돼 있으면...
			faxS.element.add(y); // if y를 이미 퐇함하고 있지 않다면... (x)*에 y를 포함시킴
			y.set.add(faxS);
			xfaxS.element.add(xy);
			xy.set.add(xfaxS);// 개가 가 등장하면, 가는 (개)*의 멤버. 그런데 이미 findAll에서 개(개)*가 카운트 증가돼 있으니 개(개)*는 증가되면 안 됨...
			xfaxS.count += xy.count; // y가 멤버이면 이미 xfax가 등록되어 있어서 registerFollowers에서 xfax의 카운터값을 설정함 
		}
		 */		
		Semanteme bayS,bayyS; 
		if (!S.containsKey(bay)) {
			S.put(bay, bayS = new Semanteme(bay, xy.count, true));
			S.put(bayy, bayyS = new Semanteme(bayy, xy.count, true));
			bayyS.part.add(bayS);
			bayyS.part.add(y);
		}
		else {
			bayS = S.get(bay);
			bayyS = S.get(bayy);
			bayS.count += xy.count;
			bayyS.count += xy.count;
		}
		/*
		if (!x.isMemberOf(bayS,S)) {
			bayS.element.add(x);
			x.set.add(bayS);
			bayyS.element.add(xy);
			xy.set.add(bayyS);
			bayyS.count += xy.count;
		}
		 */
	}

	private void findAllPair(int len, ArrayList<String> C, HashMap<String, Semanteme> S,
			HashMap<Semanteme, HashMap<Semanteme, Integer>> memo) {
		int i = 0;
		for (String line : C) {
			if (i % 10000 == 0)	mainf.println((i) + ":" + line);
			if (len > 6 && i % 2000 == 0 && i % 10000 != 0) mainf.println((i) + ":" + line);
			i++;
			Sequence s = new Sequence(line);
			while (!s.e.isEmpty()) {
				HashMap<Sequence,List<SeqSem>>LK = new HashMap<Sequence,List<SeqSem>> ();				
				HashMap<Sequence,List<SeqSem>>LKr = new HashMap<Sequence,List<SeqSem>> ();				
				List<SeqSem> XS = leadingSemAndFol(s, LK, len, S); // LK는 패턴 key에 후속하는 패턴-의미소 패어들의 리스트를 가진 해쉬맵 
				for (int j = 0; j < XS.size(); j++) {
					SeqSem xx = XS.get(j);
					String x = xx.seq.body;
					List<SeqSem> YS = LK.get(xx.seq);
					if (YS == null || YS.isEmpty()) {
						Sequence f = (x.length() < s.body.length()) ? new Sequence(s.body.substring(1 + x.length())) : null;
						if (f != null && !f.body.isEmpty())
							//							YS = leadingSemAndFol(f, LKr, len, S);
							YS = leadingSemanteme(f, len, S);
					}
					int oj = j;
					for (; j < XS.size() && (XS.get(j).seq.body).equals(x); j++) 
						registerFollowers(XS.get(j).sem, YS, S, memo);
					if (oj != j) j--;
				}
				s.body = s.body.substring(1 + s.body.indexOf(" ")); // 1단위(=형태소)만큼  다음으로 이동해  입력을 재설정
				s.e = s.e.subList(1, s.e.size());
			}
		}
		//decomp = new HashMap<String, HashSet<Semanteme>>();
	}

	public List<SeqSem> leadingSemAndFol(Sequence s, HashMap<Sequence,List<SeqSem>> LL, int nnn, HashMap<String, Semanteme> S) { // 패턴시퀀스s를 시작하는, 길이  n이하  모든 의미소를 리턴
		// 주어진 패턴 시퀀스 s로부터 길이 n이내에, s의 시작 부분에 대응 가능한 모든 의미소를 찾아 리턴하는 함수
		if (s == null || s.body == null || s.body.equals("")) return null;
		List<Sequence> L = leadingPattern(s, nnn, S); // L의 원소 l은 등록 패턴인데,
		if (it > 2 && s.body.startsWith("되/VV"))
			nnn +=0;
		List<SeqSem> ret = new ArrayList<SeqSem>();// Semanteme> ();
		HashSet<Semanteme> recreated2 = null;
		HashSet<SeqSem> tempq = new HashSet<SeqSem>(); // 같은 값이 중복 리턴되는 것을 막기 위한 해쉬셋 
		for (int index=L.size()-1;index>=0;index--){  // s 시작부분을 포함하는 모든 등록 패턴 ls를 구해
			Sequence ls = L.get(index);
			HashSet<Semanteme> recreated = recreated(ls, S); 
			if (recreated == null) continue; 
			for (Semanteme x : recreated) { // ls의 의미소 표현(해석) x에 대해  
				if (x == null) break; 
				SeqSem a = new SeqSem(ls, x); 
				if (!tempq.contains(a)) {
					ret.add(a);
					tempq.add(a);
				}
			}  // 결과로 리턴할, 모든 등록 패턴에 대해 이하에서 후속 패턴(의미소해석)을 찾음 
			String rest = s.body.substring(ls.body.length());
			List<Sequence> L2 = rest != null && !rest.isEmpty()? leadingPattern(new Sequence(rest),nnn,S):null;
			List<SeqSem> lk = new ArrayList<SeqSem>();
			if (L2 != null) for (Sequence lss:L2) { // ls의 후속 패턴 lss에 대해 
				recreated2 = recreated(lss,S); // lss를 의미소로 재창조(해석) 하고,
				if (recreated2 != null) for (Semanteme s2:recreated2){  // 그 해석된 의미소 조합 s2들을  
					lk.add(new SeqSem(lss,s2)); // 원시 패턴 lss와 함께 <lss,i(lss)> 형태로 lk에 추가;
					for (Semanteme s1:recreated){ //
						if (s1.label.contains("*("))
							it+=0;
						if (s1.label.equals("*(은는/JX) 은는/JX"))
							it+=0;


						String s1s2 = s1.label+" "+s2.label;
						if ( S.containsKey(fa(s1.label)) && S.containsKey(s1s2)? s2.isPartOf(S.get(s1s2))&&s1.isPartOf(S.get(s1s2)):false){//s2.isMemberOf(S.get(fa(s1.label)),S)) {  // ls를 뒤따르는 패턴 lss의 내부 표상이 (ls)*의 원소라면
							//						if ( S.containsKey(fa(s1.label)) && s2.isMemberOf(S.get(fa(s1.label)),S)) {  // ls를 뒤따르는 패턴 lss의 내부 표상이 (ls)*의 원소라면
							SeqSem tempp = new SeqSem(new Sequence(ls.body+" "+lss.body),S.get(s1.label +" "+fa(s1.label))); 
							if (tempq.isEmpty() || !tempq.contains(tempp))	{
								ret.add(tempp); // ls+lss 패턴을 ls+fa(ls)로서 결과에 포함시킴. ??->키고 그 후속 개념을 등록;
								tempq.add(tempp);
							}
							break;
						}
					}
				}
			}
			LL.put(ls,lk);
		}
		return ret.isEmpty() ? null : ret;
	}

	private List<Sequence> leadingPattern(Sequence s, int n, HashMap<String, Semanteme> S) { // 패턴시퀀스s를 시작하는,  길이  n이하 모든 의미소를 리턴
		// 주어진 패턴 시퀀스 s의 시작부분부터 길이 n이하인 패턴 s중 대응 의미소가 있는 모든 패턴을 리턴하는 함수
		List<Sequence> ret = new ArrayList<Sequence>();// Semanteme> ();
		if (s == null || n <= 0) return null;
		String t = "";
		for (int idx = 0; idx <= n && idx < s.e.size(); idx++) {
			t += t.equals("") ? s.e.get(idx) : " " + s.e.get(idx);
			//			if (S.containsKey(t)) 
			if (!t.equals("")) ret.add(new Sequence(t));
		}
		return ret.isEmpty() ? null : ret;
	}



	boolean isPairOfSemanteme(Sequence x,HashMap<String,Semanteme>S) {
		int size = x.e.size();
		for (int i=1;i<size;i++) {
			if (S.containsKey(x.e.subList(0, i)) && S.containsKey(x.e.subList(i, size)))
				return true;
		}return false;
	}
	// 단순화 버전 recreated : 입력 패턴 시퀀스 x를 내부 의미소 x0=>x1의 결합 혹은 의미소로 재창조하고, 그 결과를 <입력x,내부표현y> 페어의 리스트에 담아 리턴
	private HashSet<Semanteme> recreated(Sequence x, HashMap<String, Semanteme> S) {
		if (x == null)return null;
		String label = x.body;
		//if (decomp.containsKey(label)) return decomp.get(label);
		HashSet<Semanteme> ret = new HashSet<Semanteme>();
		Semanteme sem = S.get(label);
		if (sem != null) ret.add(sem);
		int xsize = x.e.size();// .e.size();
		if (xsize == 1 )
			if (ret.isEmpty()) ret = null;
		if (xsize == 2) { // x의 길이가 2이고 결합의미소 x0'x1'으로 등록되어 있으면
			String x0, x1, fax0, bax1;
			if (S.containsKey(x0 = x.e.get(0)) && S.containsKey(x1 = x.e.get(1)) && S.containsKey(label)) {
				if (S.containsKey(fax0 = fa(x0))) ret.add(S.get(x0 + " " + fax0));
				if (S.containsKey(bax1 = ba(x1))) ret.add(S.get(bax1 + " " + x1));
			}
			else ret = null;
		} else if (xsize > 2)
			for (int i = 1; i < xsize; i++) {
				String pq;
				Sequence a = new Sequence(x.e.subList(0, i));
				Sequence b = new Sequence(x.e.subList(i, xsize));
				//				if (!(S.containsKey(a.body) || isPairOfSemanteme(a)) || !(S.containsKey(b.body) || isPairOfSemanteme(b))) continue;
				HashSet<Semanteme> sa = recreated(a, S); // a, b가 등록 의미소이면 각각을  다시 의미소로 재구성
				if (sa == null) continue;
				HashSet<Semanteme> sb = recreated(b, S); // a, b가 등록 의미소이면 각각을  다시 의미소로 재구성
				if (sb == null) continue;
				for (Semanteme p : sa) { // p는 a를 p0+p1으로 분해하는 p0,p1을 담고 있음...
					String plabel = p.label;
					for (Semanteme q : sb) {
						String qlabel = q.label;
						if (S.containsKey(pq = plabel + " " + qlabel)) {
							String baq, fap, baqq, pfap;
							Semanteme pqs, baqqs, pfaps;
							if (!ret.contains(pqs = S.get(pq)))
								ret.add(pqs);
							if (S.containsKey(fap = fa(plabel)) && S.containsKey(pfap = plabel + " " + fap) && 
									p.isPartOf(pqs) && q.isPartOf(pqs) && !ret.contains(pfaps = S.get(pfap)))
								ret.add(pfaps);
							if (S.containsKey(baq = ba(qlabel)) && S.containsKey(baqq = baq + " " + qlabel) &&
									p.isPartOf(pqs) && q.isPartOf(pqs) && !ret.contains(baqqs = S.get(baqq)))
								ret.add(baqqs);
						}
					}
				}
			}
		if (ret == null || ret.isEmpty()) ret = null;
		//decomp.put(label, ret);
		return ret;
	}

	private boolean isPairOfSemanteme(Sequence a) {
		// TODO Auto-generated method stub
		return false;
	}

	private void registerFollowers(Semanteme x, List<SeqSem> YS, HashMap<String, Semanteme> S,
			HashMap<Semanteme, HashMap<Semanteme, Integer>> memo) {
		// 의미소 x의 출현 횟수를 1 증가시키고, x에 바로 이어서 출현하는 모든 의미소 y를 찾아, x와 y가 함께 출현한 횟수를  1 증가시켜 기억하는 함수
		x.count++;
		if (YS == null || YS.isEmpty()) return;
		HashMap<Semanteme, Integer> tm = (memo.containsKey(x)) ? memo.get(x) : new HashMap<Semanteme, Integer>();
		for (SeqSem ys : YS) {
			Semanteme y = ys.sem;
			//			if (y.label.startsWith("이가/JKS") && x.label.equals("영웅/NNG")) mainf.println("\t영웅 다음 " + y.label);
			if (tm.containsKey(y)) tm.put(y, tm.get(y) + 1);
			else tm.put(y, 1);
		}
		memo.put(x, tm);
	}


	public Sequence longestSeq(List<Sequence> L) {
		Sequence maxs = null;
		int max = 0;
		for (Sequence seq : L) {
			if (seq.e.size() > max) {
				max = seq.e.size();
				maxs = seq;
			}
		}
		return maxs;
	}


	public List<SeqSem> leadingSemanteme(Sequence s, int n, HashMap<String, Semanteme> S) { // 패턴시퀀스s를 시작하는, 길이  n이하  모든 의미소를 리턴
		// 주어진 패턴 시퀀스 s로부터 길이 n이내에, s의 시작 부분에 대응 가능한 모든 의미소를 찾아 리턴하는 함수
		if (s == null || s.body == null || s.body.equals(""))
			return null;
		List<Sequence> L = leadingPattern(s, n, S); // L의 원소 l은 등록 패턴인데,
		List<SeqSem> ret = new ArrayList<SeqSem>();// Semanteme> ();
		for (Sequence ls : L) {
			HashSet<Semanteme> recreated = recreated(ls, S); // 리딩 패턴을 a+b 형태로  분해.
			if (recreated != null) for (Semanteme x : recreated)
				ret.add(new SeqSem(ls, x));
		}
		return ret.isEmpty() ? null : ret;
	}

	public void findBasicSemanteme(ArrayList<String> C, HashMap<String, Semanteme> S) {
		if (S == null)
			S = new HashMap<String, Semanteme>();
		for (String line : C) {
			mainf.linesTot++;// = ++linesTot;
			String s[] = line.split(" ");
			for (String x : s) {
				mainf.semsTot++;// = ++semsTot;
				if (!S.containsKey(x))
					S.put(x, new Semanteme(x));
			}
		}
	}


	private void test(int iter, HashMap<String, Semanteme> s, HashMap<Semanteme, HashMap<Semanteme, Integer>> mem,
			HashMap<Semanteme, HashMap<Semanteme, Integer>> AP) {
		int i = 0;
		// String a1 = "정부/NNG";
		/*	String c = "정부/NNG 이가/JKS";
		if (s.containsKey(c)) {
			mainf.print(c + ":" + AP.get(s.get("정부/NNG")).get(s.get("이가/JKS")));
			if (AP.containsKey(s.get(c))) {
				for (Semanteme x : AP.get(s.get(c)).keySet()) {
					mainf.println("\t=> " + x.label + " : " + AP.get(s.get(c)).get(x));
				}
			} else
				mainf.println("\nNO successor for " + c);
		}
		String a1 = "영웅/NNG";
		String a2 = a1 + " " + fa(a1);
		String b1 = "이가/JKS (이가/JKS)*"; 
		String d = "정부/NNG";
		if (s.containsKey(fa(d))) 
			System.out.println(d+"의 출현 횟수 = "+s.get(d).count+" , ("+d+")* 의 출현횟수 = "+s.get(fa(d)).count);
		if (s.containsKey(a1 + " " + b1))
			System.out.println(iter + "th반복, " + a1 + b1 + " 출현횟수 : " + s.get(a1 + " " + b1).count);// memo.get(s.get(a1)).get(s.get(b1)));
		if (s.containsKey(a2)) {
			mainf.println(iter + "th반복, " + a2 + " : " + s.get(a2).count);
			for (Semanteme x : s.get(a2).element) { // 영웅 (영웅)* 에 속하는 개념을 출력하는데, 4회 반복 이후 '영웅+이 이*' 가 출현수 0인 문제가 발견됨. 이것의 출현수는 5로 추정됨.  
				mainf.println(x.label + " " + x.count + ",");
				if (++i % 40 == 0)
					mainf.println();
			}

		}
		if (s.containsKey(a2 + " " + b1))
			mainf.println(iter + "th반복, " + a2 + b1 + " : " + AP.get(s.get(a2)).get(s.get(b1)));
		if (s.containsKey("(정부/NNG 은는/JX)*")) 
			for (Semanteme x:s.get("(정부/NNG 은는/JX)*").element) 
				mainf.println("\t\t\t정부는 =>  "+x.label+" : "+s.get("정부/NNG 은는/JX "+x.label).count+"/"+s.get("정부/NNG 은는/JX").count);
		 */
		String[] DS = {"*(은는/JX) 은는/JX","*(을를/JKO) 을를/JKO", "*(이가/JKS) 이가/JKS"};//, "*(하/XSV) 하/XSV","*(다/EF) 다/EF"};
		for (String ds:DS) {
			mainf.print(ds+"의 post 연결 가능 의미소  = {  ");
			i = 0;
			if (s.containsKey(fa(ds)))
				for (Semanteme x:s.get(fa(ds)).element) { 
					mainf.print(x.label+" : "+s.get(ds+" "+x.label).count+"/"+s.get(ds).count+" ,\t");
					if (++i % 5 ==0) mainf.print("\n\t\t"); 
				}
			mainf.println(" }");
			/*			mainf.print(ds+"의 pre-연결 가능 의미소  = {  ");
			i=0;
			if (s.containsKey(ba(ds)))
				for (Semanteme x:s.get(ba(ds)).element) { 
					mainf.print(x.label+" : "+s.get(x.label+" "+ds).count+"/"+s.get(ds).count+" ,\t");
					if (++i % 5 ==0) mainf.print("\n\t\t"); 
				}
			 */			mainf.println(" }");
		}
		/*
		// String
		// a1 = "박근혜/NNP 대통령/NNG 은는/JX";
		a1 = "발표/NNG 하/XSV 었았/EP 다/EF";
		HashSet<Semanteme> x = recreated(new Sequence(a1), s);
		if (x != null) {
			decomp.clear();
			x = s.containsKey(a1) ? recreated(new Sequence(a1), s) : null;
			if (x != null)
				for (Semanteme xx : x) {
					mainf.println(a1 + " = " + xx.label);// +"="+xx.p1.sem.label+")+("+xx.p2.seq.body+"="+xx.p2.sem.label+")");
				}
			else
				mainf.println("NO internal representation");
		}

		a1 = "영웅/NNG ”/SSC 이/VCP 다라고/ECS";
		x = s.containsKey(a1) ? recreated(new Sequence(a1), s) : null;
		if (x != null)
			for (Semanteme xx : x) {

				mainf.println(a1 + " = " + xx.label);
			}
		a1 += "";				*/

	}
	void test2(int iter, HashMap<String,Semanteme>S,HashMap<Semanteme,HashMap<Semanteme,Integer>>memo,HashMap<Semanteme,HashMap<Semanteme,Integer>>AP) throws IOException{
		if (iter > 2) {
			setprint("(*(이가/JKS) 이가/JKS)*");
			setprint("(*(은는/JX) 은는/JX)*");
			setprint("(*(을를/JKO) 을를/JKO)*");
			setprint("*(다/EF)");
			/*			info("박근혜/NNP", "대통령/NNG",S,memo);
			info("박근혜/NNP 대통령/NNG","은는/JX",S,memo);
			info("한양/NNG", "대학교/NNG",S,memo);
			info("한양/NNG 대학교/NNG","은는/JX",S,memo);
			info("한양/NNG 대학교/NNG","이가/JKS",S,memo);
			 */			info("*(이가/JKS) 이가/JKS","*(다라고/ECS) 다라고/ECS",S,memo);
			 worker w = new worker();
			 setprint("*(었았/EP 다라고/ECS)");
			 setprint("(오/VV)*");

			 info("었았/EP", "다라고/ECS",S,memo);
			 Sequence input = new Sequence("경찰/NNG 은는/JX 그/NP 이가/JKS 일/NNG 을를/JKO 하/VV 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF");

			 input = new Sequence("그/NP 이가/JKS 하/VV 게/EC");
			 w.recreate(input, S);
			 input = new Sequence("그/NP 이가/JKS 하/VV 게/EC 하/VV");
			 w.recreate(input, S);
			 input = new Sequence("그/NP 이가/JKS 하/VV 게/EC 하/VV 었았/EP");
			 w.recreate(input, S);
			 input = new Sequence("그/NP 이가/JKS 하/VV 게/EC 하/VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("경찰/NNG 은는/JX 그/NP 이가/JKS 하/VV 게/EC 하/VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("경찰/NNG 은는/JX 그/NP 이가/JKS 하/VV 다라고/ECS 하/VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("나/NP 은는/JX 그/NP 이가/JKS 일/NNG 을를/JKO 하/VV 게/EC 하/VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("일/NNG 을를/JKO 하/VV 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("그/NP 이가/JKS 오 /VV 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("그/NP 이가/JKS 일/NNG 을를/JKO 하/VV 었았/EP 다라고/ECS");
			 w.recreate(input, S);
			 input = new Sequence("었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("그/NP 이가/JKS 일/NNG 을를/JKO 하/VV 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("그/NP 이가/JKS 일/NNG 이가/JKS 있/VV 어아도록/ECS 하/VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("오 /VV 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("그/NP 은는/JX 오 /VV 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("그/NP 이가/JKS 오 /VV 었았/EP 다라고/ECS /VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("그/NP 은는/JX 그/NP 이가/JKS 오 /VV 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("대변인/NNG 은는/JX 대통령/NNG 이가/JKS 방문/NNG 하/XSV 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF");
			 w.recreate(input, S);
			 input = new Sequence("대변인/NNG 은는/JX 박근혜/NNP 대통령/NNG 이가/JKS 지나/VV ㄴ/ETM 2015/SN 년/NNBC 미국/NNP 을를/JKO 방문/NNG 하/XSV 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF");
			 w.recreate(input, S);

			 input = new Sequence("지나/VV ㄴ/ETM 15/SN 년/NNBC");
			 w.recreate(input, S);
			 info("지나/VV ㄴ/ETM","2015/SN 년/NNBC",S,memo);
			 //			input = new Sequence("대변인/NNG 은는/JX 박근혜/NNP 대통령/NNG 이가/JKS 지나/VV ㄴ/ETM 15/SN 일/NNBC 미국/NNG 을를/JKO 방문/NNG 하/XSV 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF");
			 //			w.recreate(input, S);
			 //			input = new Sequence("청와대/NNP 대변인/NNG 은는/JX 박근혜/NNP 대통령/NNG 이가/JKS 지나/VV ㄴ/ETM 15/SN 일/NNB 대전/NNP 창조/NNG 경제/NNG 타운/NNG 을를/JKO 방문/NNG 하/XSV 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF");
			 //			w.recreate(input, S);
			 input = new Sequence("한국/NNP 을를/JKO 방문/NNG 하/XSV 었았/EP");
			 w.recreate(input, S);
			 input = new Sequence("그/NP 이가/JKS 한국/NNP 을를/JKO 방문/NNG 하/XSV 었았/EP 다/EF");
			 w.recreate(input, S);
			 //			input = new Sequence("한국/NNP 을를/JKO 방문/NNG 하/XSV 었았/EP");
			 //			w.recreate(input, S);
			 //			input = new Sequence("한국/NNP 을를/JKO 방문/NNG 하/XSV 었았/EP 다/EF");
			 //			w.recreate(input, S);
			 //			input = new Sequence("한국/NNP 을를/JKO 방문/NNG 하/XSV 었았/EP 다/EF");
			 //			w.recreate(input, S);
			 //			input = new Sequence("한국/NNP 을를/JKO 방문/NNG 하/XSV 었았/EP");
			 //			w.recreate(input, S);
			 for (Semanteme x:S.values()) {
				 String [] xlabel = x.label.split(" ");
				 if (!x.label.contains("*") && xlabel.length > 4)
					 for (int i=0;i<xlabel.length-1;i++)
						 if (xlabel[i].contains("/NNG") && xlabel[i+1].contains("NNG")) {
							 mainf.println(x.label);
							 break;
						 }
			 }

			 Semanteme s0 = S.get("*(을를/JKO) 을를/JKO"); 
			 Semanteme s1 = S.get("*(은는/JX) 은는/JX");
			 List <StrInt> sil = new ArrayList   <StrInt>();
			 for (Semanteme aa:memo.get(s1).keySet()) 
				 sil.add(new StrInt(aa.label,memo.get(s1).get(aa)));
			 Collections.sort(sil);
			 mainf.println(sil.get(0).str+" "+sil.get(0).num);
			 printfa("*(은는/JX) 은는/JX *(이가/JKS) 이가/JKS",S);			
			 Semanteme s2 = S.get("*(하/XSV 었았/EP) 하/XSV 었았/EP");
			 Semanteme s02 = S.get(s0.label+" "+s2.label);//"*(*(하/XSV 었았/EP) 하/XSV 었았/EP) *(하/XSV 었았/EP) 하/XSV 었았/EP");
			 Semanteme s3 = S.get(ba(s2.label)+" "+s2.label);//"*(*(하/XSV 었았/EP) 하/XSV 었았/EP) *(하/XSV 었았/EP) 하/XSV 었았/EP");
			 Semanteme s4 = S.get(s0.label+" "+fa(s0.label));//"*(하/XSV 었았/EP) 하/XSV 었았/EP");
			 mainf.println(s0.label+s2.label+"출현횟수 /"+s0.label+" 출현횟수  = "+memo.get(s0).get(s2) + "/"+s0.count);
			 if (s3 != null) mainf.println(s0.label+s3.label+"출현횟수 /"+s0.label+" 출현횟수  = "+memo.get(s0).get(s3) + "/"+s0.count);
			 mainf.println(s1.label+s2.label+"출현횟수 /"+s1.label+" 출현횟수  = "+memo.get(s1).get(s2) + "/"+s1.count);
			 mainf.println(s1.label+s0.label+"출현횟수 /"+s1.label+" 출현횟수  = "+memo.get(s1).get(s0) + "/"+s1.count);
			 mainf.println(s1.label+s4.label+"출현횟수 /"+s1.label+" 출현횟수  = "+memo.get(s1).get(s4) + "/"+s1.count);
			 if (s02 != null) mainf.println(s1.label+s02.label+"출현횟수 /"+s1.label+" 출현횟수  = "+memo.get(s1).get(s02) + "/"+s1.count);
			 if (s02 != null && s3 != null) mainf.println(s2.label+"출현횟수 ,"+s3.label+" 출현횟수  = "+s02.count+","+s3.count);

		}
	}
} 
/*   
 *  
 *  ~이 되 ㄴ 용 이라는 패턴이 4번 입력됐다 치자.
 *
이 때, 이(이)*는 몇 번 나오는가?
첫 반복이 끝나면 이=>되가 연결되어 이(이)*는 4번이다.
2반복이 끝나면 되=>ㄴ 도 연결되어 이=>되 4번, 이=>된 4번이 출현하고, 이=>된은 이=>되 되(*), 이=>*(ㄴ)ㄴ 까치 해서 12번 출현한다. 
따라서 2반복 후 이(이)*는 16이다. 되(되)*도 마찬가지다. 
 */