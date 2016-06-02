package com.ai.gestalt;
import com.clunix.NLP.sentence_analyzer.StrStr;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.*;
import java.util.*;
public class Semanteme {
	static final String delimiter = " ";
	long ID;
	public String label;
	public int count;
	public long length;
	public boolean isSet = false;
	public List <Semanteme> set = new ArrayList <Semanteme>();
	public List <Semanteme> element= new ArrayList <Semanteme>();
	public List <Semanteme> part = new ArrayList <Semanteme>();
	
	public Semanteme() { }
	public Semanteme(String a) { label = a; count = 0; length = a.split(delimiter).length;}
	public Semanteme(String a, int b) { label = a;  count = b;length = a.split(delimiter).length;}
	public Semanteme(String a,boolean t) { label = a; count = 0; isSet = t;length = a.split(delimiter).length;}
	public Semanteme(String a, int b, boolean t) { label = a;  count = b;isSet = t;length = a.split(delimiter).length;}
	public Semanteme(Semanteme a) { 
		label = a.label;  count = a.count; isSet = a.isSet;length = a.length;
		set.addAll(a.set);
		element.addAll(a.element);
		part.addAll(a.part);
	}
	public Semanteme(String a,String b,HashMap<String,Semanteme> S) { 
		label = a+delimiter+b; 
		isSet = label.contains(")*") || label.contains("*(");
		count = 0;
		length = a.split(delimiter).length+b.split(delimiter).length;
		part.add((S.containsKey(a))? S.get(a): new Semanteme(a));
		part.add((S.containsKey(b))? S.get(b): new Semanteme(b));
	}
	public Semanteme(String a,String b,HashMap<String,Semanteme> S,int n) { 
		label = a+delimiter+b; 
		isSet = label.contains(")*") || label.contains("*(");
		count = n;
		length = a.split(delimiter).length+b.split(delimiter).length;
		part.add((S.containsKey(a))? S.get(a): new Semanteme(a));
		part.add((S.containsKey(b))? S.get(b): new Semanteme(b));
	}
	public Semanteme(List<String> a,HashMap<String,Semanteme> S,int n) { 
		label = "";
		for (String x:a) 
			label += label.equals("")? x:delimiter+x; 
		isSet = label.contains(")*") || label.contains("*(");
		count = n;
		for (String x:a) {
			length++;
			part.add((S.containsKey(x))? S.get(x): new Semanteme(x));
		}
		isSet = label.contains(")*") || label.contains("*(");
	}
	public Semanteme(HashSet<String> a,HashMap<String,Semanteme> S,int n) { 
		label = "";
		for (String x:a) 
			label += label.equals("")? x:delimiter+x; 
		count = n;
		isSet = label.contains(")*") || label.contains("*(");
		for (String x:a){
			length++;
			part.add((S.containsKey(x))? S.get(x): new Semanteme(x));
		}
	}
	
	public boolean isSameAs(Semanteme x) {
		boolean ret = label.equals(x.label) && length == x.length && isSet == x.isSet && count == x.count;
		if (ret) for (Semanteme a:x.set) ret = ret && set.contains(a);
		if (ret) for (Semanteme a:x.element) ret = ret && element.contains(a);
		if (ret) for (Semanteme a:x.part) ret = ret && part.contains(a);

		return ret;	

	}
	
	public boolean isPartOf(Semanteme x) {
		return x.part.contains(this);
	}
	
	public boolean isMemberOf(Semanteme x) {	return x.element.contains(this); }
	
	public String source(String s) {
		String ret = "";
		int level = 1;
		if (s.startsWith("*(")){
			String t = s.substring(2);
			for(int i=0;i<t.length() ;i++){
				if (t.charAt(i) == '(') level++;
				else if (t.charAt(i) == ')') level--;
				if (level == 0 ) break;
				else ret += t.charAt(i);
			}
		}
		else if (s.endsWith(")*")) {
			String t = s.substring(0,s.length()-2);
			for(int i=t.length()-1;i>=0 ;i--){
				if (t.charAt(i) == '(') level--;
				else if (t.charAt(i) == ')') level++;
				if (level == 0 ) break;
				else ret = t.charAt(i)+ret;
			}
		}
		else return null;
		return ret;
	}
	public String source(Semanteme x){ return (x.isSet)? source(x.label) : null;}
	
// 
	
	public boolean isMemberOf(Semanteme x,HashMap<String,Semanteme>S) { 
//return x.element.contains(this);
//		/*
		String src = source(x);
		if ( src != null ) // 예를 들어 x = *(이가) 라면 경찰 이가 있는지 보고, (ㄴ)* 이라면 ㄴ 경찰이 있는지 보고...
			return S.containsKey(x.label.startsWith("*(")? label+" "+src:src+" "+label);
		return false;
//		*/
	}
	
	private StrStr associatedForm(String label) { // *(x)나 (x)* 형태의  비결합, 단독 집합 을 입력 받아 x(x)* 형태의 결합형을 리턴하는 함수  
		StrStr ret = null;
		String x = "";
		String xx = null;
		int level = 1;
		if (label.startsWith("*(")){
			String t = label.substring(2);
			for(int i=0;i<t.length() ;i++){
				if (t.charAt(i) == ')') level--;
				if (level == 0 ) {
					xx = t.substring(i+2);
					break;
				}
				x += t.charAt(i);
			}
			if (x.equals(xx)) return new StrStr(ba(x),x);
		}
		else if (label.endsWith(")*")) {
			String t = label.substring(0,label.length()-2);
			for(int i=t.length()-1;i>=0 ;i--){
				if (t.charAt(i) == '(') level--;
				if (level == 0 ) {
					xx = t.substring(0,i-1);
					break;
				}
				x = t.charAt(i)+x;
			}
			if (x.equals(xx)) return new StrStr(x,fa(x));
		}
		return null;
	}
	public String fa(String x) { return "("+x+")*";}
	
	public String ba(String x) { return "*("+x+")"; }
		
	public boolean hasElement(Semanteme x) {
		return element.contains(x);
	}
	
//	public boolean isRegistered(HashMap<String,Semanteme>S) { return S.containsKey(label) ;}
	@Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            append(label).append(isSet).toHashCode();
    }
	
	/*@Override
	public boolean equals(Object b) {
//		if (b == null) return false;
		Semanteme a = (Semanteme) b;
		return label.equals(a.label);
	}*/

	public int compareTo(Semanteme a) {
		if (count < a.count) {
			return 1;
		} else if (this.count == a.count) {
			return 0;
		} else {
			return -1;
		}
	}
	public  void fileouti(String fn, HashMap<String,Semanteme>S) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(fn+".semanteme"));
		Semanteme ls = null; int n = 0;
		for (Semanteme s:S.values()){
//			if (s.label.equals("관/NNG"))
//				s.count+=0;
			out.write(s.label+ "\t\t"+s.count+ "\t\t"+s.length+ "\t\t"+s.isSet+ "\t\t");
			
			if (s.set == null || s.set.isEmpty() ) out.write("NULL");
			else for (int i=0;i<s.set.size();i++) {
				Semanteme x = s.set.get(i);
				String towrite = x != null ? x.label : "NULL";
				out.write((i != 0)? "\t"+towrite:towrite);
			}out.write("\t\t");
			
			if (s.element == null || s.element.isEmpty() ) out.write("NULL");
			else for (int i=0;i<s.element.size();i++) {
				Semanteme x = s.element.get(i);
				String towrite = x != null ? x.label : "NULL";
				out.write((i != 0)? "\t"+towrite:towrite);
			}out.write("\t\t");
			
			if (s.part == null || s.part.isEmpty() ) out.write("NULL");
			else for (int i=0;i<s.part.size();i++) {
				Semanteme x = s.part.get(i);
				String towrite = x != null ? x.label : "NULL";
				out.write((i != 0)? "\t"+towrite:towrite);
			}out.write("\n");
			ls = s; n++;
		}out.close();
		System.out.println("Total "+n+" semantemes saved. Last one is "+ls.label);
	} // End Of FileOut
	
	public  HashMap<String,Semanteme> fileini(String fn) throws IOException{
		String line; int n = 0; Semanteme ls = null;
		HashMap<String,Semanteme>S = new HashMap<String,Semanteme>();
		BufferedReader br = new BufferedReader(new FileReader(fn + ".semanteme"));
		HashMap <String,String[]> Sets = new HashMap <String,String[]> ();
		HashMap <String,String[]> Elements = new HashMap <String,String[]> ();
		HashMap <String,String[]> Parts = new HashMap <String,String[]> ();
		while ((line = br.readLine()) != null) {
			Semanteme s = new Semanteme();
			String SETS[],ELEMENTS[],PARTS[];
			String f[] = line.split("\t\t");
			s.label = f[0];
			if (s.label.equals("관/NNG"))
				s.count +=0;
			s.count = Integer.parseInt(f[1]);
			s.length = Integer.parseInt(f[2]);
			s.isSet= Boolean.parseBoolean(f[3]);
			if (f.length > 4 && !f[4].equals("NULL")) Sets.put(s.label,SETS = f[4].split("\t"));
			if (f.length > 5 && !f[5].equals("NULL")) Elements.put(s.label,ELEMENTS = f[5].split("\t"));
			if (f.length > 6 && !f[6].equals("NULL")) Parts.put(s.label,PARTS = f[6].split("\t"));
			S.put(s.label, s);
			ls = s; n++;
		}
		for (Semanteme s:S.values()) {
//			if (s.label.equals("관/NNG"))
//				s.count +=0;
			String[] t;
			if (Sets.containsKey(s.label)) for (String a:t=Sets.get(s.label)) 
				if (a!= null && !a.equals("") && !a.equals("\t") &&  !a.equals("\t\t")) s.set.add(S.get(a));
			if (Elements.containsKey(s.label)) for (String a:t=Elements.get(s.label)) 
				if (a!= null && !a.equals("") && !a.equals("\t") &&  !a.equals("\t\t")) s.element.add(S.get(a));
			if (Parts.containsKey(s.label)) for (String a:t=Parts.get(s.label)) 
				if (a!= null && !a.equals("") && !a.equals("\t") &&  !a.equals("\t\t")) s.part.add(S.get(a));
		}
		br.close();
		System.out.println("Total "+n+" lines read. The last one is "+ls.label);
		return S;
	}
	
	public  void  fileout(String fn,HashMap<String,Semanteme>SS) throws IOException{
		if (SS == null) return;
		Collection<Semanteme> S =  SS.values();
		
		BufferedWriter out = new BufferedWriter(new FileWriter(fn+".semanteme"));
		BufferedWriter OUT = new BufferedWriter(new FileWriter(fn+".SEMANTEME"));
		Semanteme ls = null; int n = 0;
		int id = 0;
		HashMap <Semanteme,Integer>h = new HashMap <Semanteme,Integer>();
		for (Semanteme s:S) h.put(s, id++);
		
		for (Semanteme s:S){
//			if (s.label.equals("관/NNG"))
//				s.count+=0;
			out.write(s.label+ "\t\t"+s.count+ "\t\t"+s.length+ "\t\t"+s.isSet+ "\t\t");
			OUT.write(s.label+ "\t\t"+s.count+ "\t\t"+s.length+ "\t\t"+s.isSet+ "\t\t");
			
			if (s.set == null || s.set.isEmpty() ) {
				out.write("NULL");
				OUT.write("NULL");
			}
			else for (int i=0;i<s.set.size();i++) {
				Semanteme x = s.set.get(i);
				String towrite = x != null ? h.get(x).toString() : "NULL";
				String TOWRITE = x != null ? x.label : "NULL";
				out.write((i != 0)? "\t"+towrite:towrite);
				OUT.write((i != 0)? "\t"+TOWRITE:towrite);
			}out.write("\t\t");OUT.write("\t\t");
			
			if (s.element == null || s.element.isEmpty() ) {
				out.write("NULL"); OUT.write("NULL"); 
			}
			else for (int i=0;i<s.element.size();i++) {
				Semanteme x = s.element.get(i);
				String towrite = x != null ? h.get(x).toString() : "NULL";
				String TOWRITE = x != null ? x.label : "NULL";
				out.write((i != 0)? "\t"+towrite:towrite);
				OUT.write((i != 0)? "\t"+TOWRITE:towrite);
			}out.write("\t\t");OUT.write("\t\t");
			
			if (s.part == null || s.part.isEmpty() ) {
				out.write("NULL");
				OUT.write("NULL");
			}
			else for (int i=0;i<s.part.size();i++) {
				Semanteme x = s.part.get(i);
				String towrite = x != null ? h.get(x).toString() : "NULL";
				String TOWRITE = x != null ? x.label : "NULL";
				out.write((i != 0)? "\t"+towrite:towrite);
				OUT.write((i != 0)? "\t"+TOWRITE:towrite);
			}out.write("\n");OUT.write("\n");
			ls = s; n++;
		}out.close(); OUT.close();
		System.out.println("Total "+n+" semantemes saved. Last one is "+ls.label);
	} // End Of FileOut
	
	public  HashMap<String,Semanteme>  filein(String fn) throws IOException{
		String line; int n = 0; Semanteme ls = null; int id = 0;
		HashMap<String,Semanteme>S = new HashMap<String,Semanteme>();
		BufferedReader br = new BufferedReader(new FileReader(fn + ".semanteme"));
		HashMap <String,String[]> Sets = new HashMap <String,String[]> ();
		HashMap <String,String[]> Elements = new HashMap <String,String[]> ();
		HashMap <String,String[]> Parts = new HashMap <String,String[]> ();
		List <Semanteme> sl = new ArrayList<Semanteme> ();
		while ((line = br.readLine()) != null) {
			Semanteme s = new Semanteme();
			sl.add(s);
			String SETS[],ELEMENTS[],PARTS[];
			String f[] = line.split("\t\t");
			s.label = f[0];
			if (s.label.equals("관/NNG"))
				s.count +=0;
			s.count = Integer.parseInt(f[1]);
			s.length = Integer.parseInt(f[2]);
			s.isSet= Boolean.parseBoolean(f[3]);
			if (f.length > 4 && !f[4].equals("NULL")) Sets.put(s.label,SETS = f[4].split("\t"));
			if (f.length > 5 && !f[5].equals("NULL")) Elements.put(s.label,ELEMENTS = f[5].split("\t"));
			if (f.length > 6 && !f[6].equals("NULL")) Parts.put(s.label,PARTS = f[6].split("\t"));
			S.put(s.label, s);
			ls = s; n++;
		}
		for (Semanteme s:S.values()) {
//			if (s.label.equals("관/NNG"))
//				s.count +=0;
			String[] t;
			if (Sets.containsKey(s.label)) for (String a:t=Sets.get(s.label)) 
				if (a!= null && !a.equals("") && !a.equals("\t") &&  !a.equals("\t\t")) s.set.add(sl.get(Integer.parseInt(a)));
			if (Elements.containsKey(s.label)) for (String a:t=Elements.get(s.label)) 
				if (a!= null && !a.equals("") && !a.equals("\t") &&  !a.equals("\t\t")) s.element.add(sl.get(Integer.parseInt(a)));
			if (Parts.containsKey(s.label)) for (String a:t=Parts.get(s.label)) 
				if (a!= null && !a.equals("") && !a.equals("\t") &&  !a.equals("\t\t")) s.part.add(sl.get(Integer.parseInt(a)));
		}
		br.close();
		System.out.println("Total "+n+" lines read. The last one is "+ls.label);
		return S;
	}
	
	 void savefull(String fn,HashMap<Semanteme,HashMap<Semanteme,Integer>>AP) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(fn+".wholeLinks"));
		if (AP == null | AP.isEmpty())
			out.write("NULL");
		else {
			for (Semanteme x:AP.keySet()){
				if (!AP.get(x).isEmpty() || AP.get(x) != null) {
					out.write(x.label+"\t\t\t");
					String c = "";
					for (Semanteme y:AP.get(x).keySet()) {
						out.write(c+ y.label+"\t"+AP.get(x).get(y));
						c = "\t\t";
					}
					out.write("\n");
				}
			}
		}
		out.close();
	}
	
	 HashMap<Semanteme,HashMap<Semanteme,Integer>> loadfull(String fn,HashMap<String,Semanteme> S) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(fn+".wholeLinks"));
		String line;
		HashMap<Semanteme,HashMap<Semanteme,Integer>> ret = new HashMap<Semanteme,HashMap<Semanteme,Integer>>();
		int k = 0;
		int i = 0;
		while ((line=in.readLine())!=null ) {
			i++;
			if (line.equals("NULL")) return null;
			String f[] = line.split("\t\t\t"); 
			String f2[] = f[1].split("\t\t");
			HashMap <Semanteme,Integer> t2 = new HashMap <Semanteme,Integer>(); 
			for (String x:f2) {
				String f3[] = x.split("\t");
				t2.put(S.get(f3[0]),Integer.parseInt(f3[1]));
				k++;
			}
			ret.put(S.get(f[0]),t2);
		}
		in.close();
		System.out.println("Total "+k+" links loaded for "+i+" Semantemes");
		return ret;
	}
	Semanteme mergeSemantemes(Semanteme x){
		if (!label.equals(x.label) ) return this;
		count += x.count;
		for (Semanteme s:x.set) 
			if (!set.contains(s)) set.add(s);
		for (Semanteme s:x.element) 
			if (!element.contains(s)) element.add(s);
		for (Semanteme s:x.part) 
			if (!part.contains(s)) part.add(s);
		return this;
	}
	HashMap<String,Semanteme> mergeVocabulary(HashMap<String,Semanteme>a , HashMap<String,Semanteme> b ){
		if (a == null && b == null) return null;
		HashMap<String,Semanteme> larger = a.keySet().size() >= b.keySet().size() ? a : b;
		HashMap<String,Semanteme> smaller = larger == a? b:a;
		for (String k:smaller.keySet()) 
			if (!a.containsKey(k)) larger.put(k, b.get(k));
		for (String k:smaller.keySet()) 
			if (!larger.get(k).isSameAs(smaller.get(k)) ) 
				larger.put(k,larger.get(k).mergeSemantemes(smaller.get(k)));
		return larger;
	}
	HashMap<String,Semanteme> mergeVocabulary(String f1 , String f2 ) throws IOException{
		//HashMap<String,Semanteme> a = filein(f1); //new HashMap<String,Semanteme> ();
		//HashMap<String,Semanteme> b = filein(f2); //new HashMap<String,Semanteme> ();
		return mergeVocabulary(filein(f1),filein(f2));
	}

	HashMap<Semanteme,HashMap<Semanteme,Integer>> mergeGrammar(String f1 , String f2 , HashMap<String,Semanteme> S) throws IOException{
		return mergeGrammar(loadfull(f1,S), loadfull(f2,S),S);
	}
	HashMap<Semanteme,HashMap<Semanteme,Integer>> mergeGrammar(HashMap<Semanteme,HashMap<Semanteme,Integer>> a ,HashMap<Semanteme,HashMap<Semanteme,Integer>>  b, HashMap<String,Semanteme> S) throws IOException{
		//f1,a 와 f2,b는 의미소 집합을 공유해야 한다. 따라서, mergeVocabulary() 를 선호출한 후 이 함수가 호출돼야 한다.
		HashMap<Semanteme,HashMap<Semanteme,Integer>> larger = (a.size() >= b.size())? a:b;
		HashMap<Semanteme,HashMap<Semanteme,Integer>> smaller = larger == a? b:a;
		if (larger == null) return null;
		if (smaller != null) 
			for (Semanteme x:smaller.keySet()) 
				if (!larger.containsKey(x)) larger.put(x, smaller.get(x));
				else for (Semanteme y:smaller.get(x).keySet()) 
					if (!larger.get(x).containsKey(y)) larger.get(x).put(y, smaller.get(x).get(y));
					else larger.get(x).put(y, larger.get(x).get(y)+smaller.get(x).get(y));
		return larger;
	}
	
}
