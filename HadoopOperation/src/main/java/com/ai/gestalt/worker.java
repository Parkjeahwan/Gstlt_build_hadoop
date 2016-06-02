package com.ai.gestalt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class worker {
	HashMap<String, HashSet<SeqSem>> decom;
	static String pathn = "/shr/data/";
	static String fn;
	public worker(){
		decom = new HashMap<String, HashSet<SeqSem>>(); 
	}
	
	
//	public List<ArrayList<Semanteme>> recreate(Sequence input, HashMap<String,Semanteme> S){
	public HashSet<SeqSem> recreate(Sequence input,HashMap<String,Semanteme> S) throws IOException{
		HashSet<SeqSem> ret = recreated2(input,S);//new ArrayList<ArrayList<Semanteme>>();
		HashSet<Sequence> ret2 = new HashSet<Sequence>();
		
		if (ret != null) for (SeqSem x:ret) {
//			mainf.println(x.seq.body+" = "+x.sem.label);
			ret2.add(x.seq);
		}
		
		fn=pathn+"result."+Long.toString(System.currentTimeMillis())+".txt";
		BufferedWriter out = new BufferedWriter(new FileWriter(fn));
		fprint(out,"다음 문장에 대한 분석 결과 :  "+input.body+"\n");
		if (ret != null) for (SeqSem p:ret) {
			Sequence x = p.seq;
//			mainf.println(" "+x.body);//sem.label);
			fprint(out," "+x.body+" \n");
			fprint(out," \t// "+p.sem.label+" \n");
			if (!x.body.contains("(((대변인/NNG") &&
				x.body.contains("((대변인/NNG 은는/JX) ((대통령/NNG 이가/JKS) (방문/NNG ") &&
				(x.body.contains("다라고/ECS))")))
					fprint(out,"\t\t=>  "+p.sem.label+"\n");
		}
		out.close();
		return ret;
	}
	public void fprint(Writer out,String s) throws IOException {//(String fn,String s) throws IOException {
//		BufferedWriter out = new BufferedWriter(new FileWriter(fn));
		out.write(s);
//		out.close();
	}
/*	public Sequence enc(String x) { return new Sequence("("+x+")");}
	public Sequence enc(Sequence x) { return new Sequence("("+x.body+")");}
	public Sequence enc(Semanteme x) { return new Sequence("("+x.label+")");}
	public Sequence enc(String x,String y) { return new Sequence("("+x+"+"+y+")");}
	public Sequence enc(Sequence x,Sequence y) { return new Sequence("("+x.body+"+"+y.body+")");}
	*/
//	public Sequence enc(String x) { return new Sequence("("+x+")");}
//	public Sequence enc(Sequence x) { return new Sequence("("+x.body+")");}
	//public Sequence enc(Semanteme x) { return new Sequence("("+x.label+")");}
	public Sequence enc(String x) { return new Sequence(x);}
	public Sequence enc(Sequence x) { return new Sequence(x.body);}
	public Sequence enc(Semanteme x) { return new Sequence(x.label);}
	public Sequence enc(String x,String y) { return new Sequence("("+x+" "+y+")");}
	public Sequence enc(Sequence x,Sequence y) { return new Sequence("("+x.body+" "+y.body+")");}
	public boolean notin(Sequence x, HashSet<Sequence>s){
		return true;
//		if (s.contains(x)) return false;
//		else return true;
	}
	private HashSet<SeqSem> recreated2(Sequence x, HashMap<String, Semanteme> S) { // 입력을 분해할 sequence와 
		if (x == null)
			return null;
		String label = x.body;
		if (decom.containsKey(label))
			return decom.get(label);
		HashSet<SeqSem> ret = new HashSet<SeqSem>();
		HashSet<Sequence> buf = new HashSet<Sequence>();
		if (S.containsKey(label)) {
			Semanteme sem = S.get(label);
			Sequence encx = enc(x.body);
			if (notin(encx,buf)) {
				ret.add(new SeqSem(encx,sem));
				buf.add(encx);
			}
		}
		Semanteme xys = null;
		int xsize = x.e.size();// .e.size();
		if (xsize == 1)
			if (ret.isEmpty()) ret = null;
			;//if (!buf.contains(enc(x.body)) ret.add(new SeqSem(encx,new Semanteme(x));
		if (xsize == 2) { // x의 길이가 2이고 결합의미소 x0'x1'으로 등록되어 있으면
			String x0, x1, fax0, bax1,x0x1;
			Sequence encxy;// = enc(x0,x1);
			if (S.containsKey(x0 = x.e.get(0)) && S.containsKey(x1 = x.e.get(1)) && notin(encxy=enc(x0,x1),buf)) {
				buf.add(encxy);
				if (S.containsKey(x0x1=x0+" "+x1)) {
					ret.add(new SeqSem(encxy,xys=S.get(x0x1)));
					if (S.containsKey(fax0 = fa(x0)) && S.get(x1).isPartOf(xys)) {
//					if (S.containsKey(fax0 = fa(x0)) && S.get(x1).isMemberOf(S.get(fax0),S)) {
						ret.add(new SeqSem(af(x0,x1),S.get(x0 + " " + fax0)));
					}
					if (S.containsKey(bax1 = ba(x1)) && S.get(x0).isPartOf(xys)) {
//					if (S.containsKey(bax1 = ba(x1)) && S.get(x0).isMemberOf(S.get(bax1),S)) {
						ret.add(new SeqSem(af(x0,x1),S.get(bax1 + " " + x1)));
					}
				} // 딱 2개 구성일 때는, a+(a)* 혹은 *(b)+b 인지 확인해 리턴;
				else ret = null;
			}
		} else if (xsize > 2)
			for (int i = 1; i < xsize; i++) {
				Sequence a = new Sequence(x.e.subList(0, i));
				Sequence b = new Sequence(x.e.subList(i, xsize));
//				if (!S.containsKey(a.body) || !S.containsKey(b.body))
//					continue;
if (a.body.equals("나/NP 은는/JX"))
	i +=0;
if (a.body.equals("대통령/NNG 이가/JKS 방문/NNG 하/XSV") && b.body.equals("었았/EP 다라고/ECS"))
	mainf.println(" => ");
				HashSet<SeqSem> ssa = recreated2(a, S); // a, b가 등록 의미소이면 각각을 다시 의미소로 재구성
				if (ssa == null) continue;
				HashSet<SeqSem> ssb = recreated2(b, S); // a, b가 등록 의미소이면 각각을 다시 의미소로 재구성
				if (ssb == null) continue;
				HashSet<Semanteme> sa = sems(ssa);
if (a.body.equals("대통령/NNG 이가/JKS 방문/NNG 하/XSV 었았/EP 다라고/ECS") && 
		(sa.contains(new Semanteme("*(다라고/ECS) 다라고/ECS")) || sa.contains(new Semanteme("*(었았/EP 다라고/ECS) 었았/EP 다라고/ECS")))) {
	sa.clear();
	sa.add(new Semanteme("*(다라고/ECS) 다라고/ECS"));
}
				HashSet<Semanteme> sb = sems(ssb);
				for (SeqSem ssp: ssa) { // p는 a를 p0+p1으로 분해하는 p0,p1을 담고 있음...
					Semanteme p = ssp.sem;
					String plabel = p.label;// (p.p2 == null)? p.p1.sem.label :
					// p.p1.sem.label + "
					// "+p.p2.sem.label;
					for (SeqSem ssq : ssb) {
						Semanteme q = ssq.sem;
						String qlabel = q.label;
						String baq, fap, baqq, pfap, pq;
						Sequence abseq = af(ssp.seq,ssq.seq);
						if (S.containsKey(pq=plabel + " " + qlabel) && notin(abseq,buf)) {
if (plabel.equals("*(은는/JX) 은는/JX") && qlabel.equals("*(밝히/VV 었았/EP 다/EF) 밝히/VV 었았/EP 다/EF"))
	mainf.println(ssp.seq.body +" => "+ssq.seq.body);
if (plabel.equals("*(은는/JX) 은는/JX") && qlabel.equals("*(밝히/VV (밝히/VV)*)) 밝히/VV (밝히/VV)*)"))
	mainf.println(ssp.seq.body +" => "+ssq.seq.body);
if (plabel.equals("*(은는/JX) 은는/JX") && qlabel.equals("밝히/VV 었았/EP (밝히/VV 었았/EP)*"))
	mainf.println(ssp.seq.body +" => "+ssq.seq.body);
if (ssp.seq.body.equals("대통령/NNG 이가/JKS 방문/NNG 하/XSV") && ssq.seq.body.equals("었았/EP 다라고/ECS"))
	mainf.println(ssp.seq.body +" => "+ssq.seq.body);
							
							buf.add(abseq);
							Semanteme pqs, baqqs, pfaps;
							SeqSem added = null;
							if (!ret.contains(pqs = S.get(pq)))
								ret.add(added = new SeqSem(abseq,pqs));
							if (S.containsKey(fap = fa(plabel)) && q.isPartOf(pqs) && S.containsKey(pfap = plabel + " " + fap)
//							if (S.containsKey(fap = fa(plabel)) && q.isMemberOf(S.get(fap),S) && S.containsKey(pfap = plabel + " " + fap)
									&& !ret.contains(pfaps = S.get(pfap)))
								ret.add(added = new SeqSem(abseq,pfaps));
							if (S.containsKey(baq = ba(qlabel)) && p.isPartOf(pqs) && S.containsKey(baqq = baq + " " + qlabel)
//							if (S.containsKey(fap = fa(plabel)) && q.isMemberOf(S.get(fap),S) && S.containsKey(pfap = plabel + " " + fap)
									&& !ret.contains(baqqs = S.get(baqq)))
								ret.add(added = new SeqSem(abseq,baqqs));
if (added!=null)
if (a.body.equals("대통령/NNG 이가/JKS 방문/NNG 하/XSV") && b.body.equals("었았/EP 다라고/ECS") && added.sem.label.equals("*(었았/EP 다라고/ECS) 었았/EP 다라고/ECS") ||
		a.body.equals("대통령/NNG 이가/JKS 방문/NNG 하/XSV") && b.body.equals("었았/EP 다라고/ECS") && added.sem.label.equals("*(었았/EP 다라고/ECS) 었았/EP 다라고/ECS") )
	mainf.println(added.sem.label);
						}
					}
				}
			}
		if (ret != null && ret.isEmpty()) ret = null;
		decom.put(label, ret);

		return ret;
	}
//	private Sequence af(String x, String y) { return new Sequence("("+x+"+"+y+")");	}
//	private Sequence af(Sequence x, Sequence y) { return new Sequence("("+x.body+"+"+y.body+")");	}
	private Sequence af(String x, String y) { return new Sequence("("+x+" "+y+")");	}
	private Sequence af(Sequence x, Sequence y) { return new Sequence("("+x.body+" "+y.body+")");	}
	private SeqSem asf(String x, String y, HashMap<String,Semanteme> S) {
		return new SeqSem(af(x,y),S.get(x+" "+y));
	}

	private HashSet<Semanteme> sems(HashSet<SeqSem> ssa) {
		if (ssa == null) return null;
		HashSet<Semanteme> r = new HashSet<Semanteme>();
		for(SeqSem s:ssa) r.add(s.sem);
		return r;
	}

	public static String fa(String x) {return "(" + x + ")*";	}

	public static String ba(String x) {return "*(" + x + ")";}

/*	private HashSet<SeqSem> recreated2(Sequence x, HashMap<String, Semanteme> S) { // 입력을 분해할 sequence와 
		if (x == null)
			return null;
		String label = x.body;
		if (decom.containsKey(label))
			return decom.get(label);
		HashSet<SeqSem> ret = new HashSet<SeqSem>();
		if (S.containsKey(label)) {
			Semanteme sem = S.get(label);
			ret.add(new SeqSem(new Sequence("("+x.body+")"),sem));
		}
		int xsize = x.e.size();// .e.size();
		if (xsize == 1)
			return ret;
		if (xsize == 2) { // x의 길이가 2이고 결합의미소 x0'x1'으로 등록되어 있으면
			String x0, x1, fax0, bax1;
			if (S.containsKey(x0 = x.e.get(0)) && S.containsKey(x1 = x.e.get(1))) {
				if (S.containsKey(fax0 = fa(x0)))
					ret.add(new SeqSem(af(x0,x1),S.get(x0 + " " + fax0)));
				if (S.containsKey(bax1 = ba(x1)))
					ret.add(new SeqSem(af(x0,x1),S.get(bax1 + " " + x1)));
			}
		} else if (xsize > 2)
			for (int i = 1; i <= xsize; i++) {
				Sequence a = new Sequence(x.e.subList(0, i));
				Sequence b = new Sequence(x.e.subList(i, xsize));
				if (!S.containsKey(a.body) || !S.containsKey(b.body))
					continue;

				HashSet<SeqSem> ssa = recreated2(a, S); // a, b가 등록 의미소이면 각각을 다시 의미소로 재구성
				HashSet<SeqSem> ssb = recreated2(b, S); // a, b가 등록 의미소이면 각각을 다시 의미소로 재구성
				HashSet<Semanteme> sa = sems(ssa);
				HashSet<Semanteme> sb = sems(ssb);
				for (SeqSem ssp: ssa) { // p는 a를 p0+p1으로 분해하는 p0,p1을 담고 있음...
					Semanteme p = ssp.sem;
					String plabel = p.label;// (p.p2 == null)? p.p1.sem.label :
											// p.p1.sem.label + "
											// "+p.p2.sem.label;
					for (SeqSem ssq : ssb) {
						Semanteme q = ssq.sem;
						String qlabel = q.label;
						if (S.containsKey(plabel + " " + qlabel)) {
							String baq, fap, baqq, pfap, pq;
							Semanteme pqs, baqqs, pfaps;
							if (S.containsKey(pq = plabel + " " + qlabel) && !ret.contains(pqs = S.get(pq)))
								ret.add(new SeqSem(af(ssp.seq,ssq.seq),pqs));
							if (S.containsKey(fap = fa(plabel)) && S.containsKey(pfap = plabel + " " + fap)
									&& !ret.contains(pfaps = S.get(pfap)))
								ret.add(new SeqSem(af(ssp.seq,ssq.seq),pfaps));
							if (S.containsKey(baq = ba(qlabel)) && S.containsKey(baqq = baq + " " + qlabel)
									&& !ret.contains(baqqs = S.get(baqq)))
								ret.add(new SeqSem(af(ssp.seq,ssq.seq),baqqs));
						}
					}
				}
			}
		decom.put(label, ret);
		return ret;
	}
*/
}
