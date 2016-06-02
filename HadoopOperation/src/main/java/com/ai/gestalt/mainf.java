package com.ai.gestalt;

import com.clunix.NLP.graph.EdgeSet;
import com.clunix.NLP.graph.Node;
import com.clunix.NLP.graph.PGraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class mainf {
	public static int NOTnull = 0;
	public static int calledN = 0;
	//private static Commander cmnd = new Commander();
	public static String target = "청와대/NNP 대변인/NNG 은는/JX 박근혜/NNP 대통령/NNG 이가/JKS 지나/VV ㄴ/ETM 15/SN 일/NNBC 한국/NNP 에/JKB 도착/NNG 하/XSV 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF";

	//	public static String target = "청와대/NNP 대변인/NNG 은는/JX 박근혜/NNP 대통령/NNG 이가/JKS 지나/VV ㄴ/ETM 15/SN 일/NNBC 대전/NNP 창조/NNG 경제/NNG 타운/NNG 을를/JKO 방문/NNG 하/XSV 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF";
	//	public static String target = "대통령/NNG 이가/JKS 한국/NNP 에/JKB 오/VV 었았/EP 다/EF";// 었았/EP 다라고/ECS 밝히/VV 었았/EP 다/EF";
	//	public static String target = "나/NP 은는/JX 한국/NNP 에/JKB 도착/NNG 하/XSV 었았/EP 다/EF";
	//	public static String target = "그/NP 은는/JX 한국/NNP 에/JKB 오/VV 었았/EP 다/EF";
	//	public static String target = "나/NP 은는/JX 그/NP 이가/JKS 주/VV ㄴ/ETM 상자/NNG 에/JKB 넣/VV 었았/EP 다/EF";
	//	public static String target = "그/NP 이가/JKS 한국/NNP 에/JKB 오/VV 었았/EP 다/EF";
	public static String fn, pathn;
	public static int  ws = 11;//3;//4;//(float) 2.5;
	public static boolean NOTSHOWED = true;
	public static HashSet<String> result = new HashSet<String>();
	public static HashSet<StrStrFloat> res = new HashSet<StrStrFloat>();
	//public static List <iNode> output = new ArrayList<iNode>();
	public static float cv = (float) 0.009;
	public static int cvi = (int) (1/cv);
	public static int MO = 4;
	static Learner lr = new Learner();
	public static HashSet <String> sfinal = new HashSet <String> ();  
	public static HashSet <String> sfinal2 = new HashSet <String> ();  
	public static int linesTot = 191345; // shorts.txt.10 문장 수 //4901123;// whole 말뭉치의 문장수	
	public static int semsTot = 1479842;// shorts.txt.10 형태소 수 //128469473; //Whole 말뭉치의 형태소수	0;//

	public static void main(String args[]) throws IOException, InterruptedException{
		Semanteme smtm = new Semanteme();
		GstaltFinder gu = new GstaltFinder();
		Student st = new Student();
		HashMap <String,Semanteme> S = st.S = new HashMap <String,Semanteme> (); 
		HashMap <Semanteme,HashMap<Semanteme,Integer>> AP = st.AP = new HashMap <Semanteme,HashMap<Semanteme,Integer>>(); 
		HashMap <Semanteme,HashMap<Semanteme,Integer>> OP = st.OP = new HashMap <Semanteme,HashMap<Semanteme,Integer>>();

		pathn = "/shr/data/";//cmnd.pathn;
		String ffn = "FullGraph.dat";
		String gfn = "GrammarGraph.dat";
		String hfn = "HierarchyGraph.dat";
		//String fnsp = cmnd.fnsp;
		//		String test = "/shr/data/test";
		SeqSem x = new SeqSem(new Sequence("aaa"),new Semanteme("xxx"));
		SeqSem y = new SeqSem(new Sequence("aaa"),new Semanteme("xxx"));
		SeqSemPair xy = new SeqSemPair(x,y);
		SeqSemPair yx = new SeqSemPair(y,x);


		HashSet <SeqSemPair> XY = new HashSet <SeqSemPair> ();
		XY.add(xy);
		println("x==y "+x.equals(y)+" , xy==yz "+xy.equals(yx) + " , {xy} contains yx : "+XY.contains(yx));
		//		test1("a b c d e f g h");

		PGraph G = new PGraph();  // 의미소간 인접 연결 빈도를 담고 있는 초기 그래프산
		PGraph H = new PGraph();  // 의미소들의 카테고리와 형태소들이 어느 카테고리에 속하는지 담고 있는, Hierarchy 그래프  
		PGraph F = new PGraph();  // G 그래프에서 확실한 연결만 남긴 게쉬탈트 그래프 

		String str;
		Scanner keyboard = new Scanner(System.in);
		System.out.println("choose fn option\n(1 is shorts.txt.10, 2 is whole_mecab_pr.txt, 3 is article_mecab_pr.txt, 4 is article20K.txt, 5 is cmnd.fn, 6 is 4debug.data, 7 is test2.txt)");
		str = keyboard.nextLine();
		fn = UtilCommand.choosefn(str);
		
		//  20K 등 소형  말뭉치 학습시 student len = 2^iter 이고, *.10 등 10만 라인 이상 큰 말뭉치 학습시 student.len = 20 으로 고정한다. 
		//fn = "shorts.txt.10";// "whole_mecab_pr.txt";//"article_mecab_pr.txt";//article20K.txt";//cmnd.fn;// "4debug.data";//"test2.txt";//
		ArrayList <String> C = new ArrayList<String>();//gu.loadCorpus(pathn+fn);
		//		ArrayList <String> C = gu.loadCorpus(pathn+fn);
		//		shortS(); // article과 corpus 두개의 말뭉치에서 길이 6, 10, 16, 22, 26 이하 문장들로만 이뤄진 문장으로 말뭉치를 만드는 함



		// 		st.findBasicSemanteme(C,st.S);
		// 		println(linesTot+","+semsTot);

		//		st.learn1time(1,C);
		//		smtm.fileout(pathn+fn+".1", st.S);
		//		smtm.savefull(pathn+fn+".1", st.memo);



		//		smtm.savefull(pathn+fn+".1.bonlink", st.AP);
		//		smtm.savefull(pathn+fn+".1.mallink", st.OP);
		/*		
		st.S = smtm.filein(pathn+fn+".1");
		st.memo = smtm.loadfull(pathn+fn+".1", st.S);
		st.AP = smtm.loadfull(pathn+fn+".1.bonlink", st.S);
st.findWrongPair(4, st.S, st.memo, st.OP);
//smtm.savefull(pathn+fn+".1.mallink", st.OP);

//		st.OP = smtm.loadfull(pathn+fn+".1.mallink", st.S);
		st.buildPREVSUCC();
List<Semanteme> a = st.topPREV("하/VV", 20);
List<Semanteme> b = st.topPREV("가/VV", 20);
String totest = "한국/NNP";
findWrongs(totest,st);


	HashMap<Semanteme,HashMap<Semanteme,Integer>> ap = smtm.loadfull(pathn+fn+".1", st.S);
//	testAP(st.memo,ap,st.S);
	HashMap<String,Semanteme> SS = smtm.filein(pathn+fn+".1");
	testDICT(st.S,SS);
//		st.memo = smtm.loadfull(pathn+fn+".1", st.S);
st.findWrongPair(10, st.S, st.memo, st.OP);
smtm.savefull(pathn+fn+".1.malLink", st.OP);
		st.learn1time(2,C);
		smtm.fileout(pathn+fn+".2", st.S);
		smtm.savefull(pathn+fn+".2", st.memo);
	SS = smtm.filein(pathn+fn+".2");
	testDICT(st.S,SS);
	ap = smtm.loadfull(pathn+fn+".2", st.S);
	testAP(st.memo,ap,st.S);

		st.S = smtm.filein(pathn+fn+".2");
		st.memo = smtm.loadfull(pathn+fn+".2", st.S);

		st.learn1time(3,C);
		smtm.fileout(pathn+fn+".3", st.S);
		smtm.savefull(pathn+fn+".3", st.memo);
		smtm.savefull(pathn+fn+".3.bonlink", st.AP);
		smtm.savefull(pathn+fn+".3.mallink", st.OP);


		st.S = smtm.filein(pathn+fn+".3");
//		st.memo = smtm.loadfull(pathn+fn+".3", st.S);
		st.AP = smtm.loadfull(pathn+fn+".3.bonlink", st.S);
		st.OP = smtm.loadfull(pathn+fn+".3.mallink", st.S);

st.setprint("*(하/VV) 하/VV");
st.setprint("(*(이가/JKS) 이가/JKS (*(이가/JKS) 이가/JKS)*)*");
st.setprint("(*(하/VV (하/VV)*) 하/VV (하/VV)*)*");
st.setprint("(*(이가/JKS) 이가/JKS 하/VV (하/VV)*)*");
st.setprint("(*(게/EC) 게/EC)*");
st.setprint("*(*(게/EC) 게/EC)");
st.setprint("*(게/EC)");
		st.buildPREVSUCC();
a = st.topPREV("하/VV", 20);
b = st.topPREV("가/VV", 20);

List<Semanteme> d = st.malSUCC(totest, 20);
println("A = ");st.setprint(a);
println("B = ");st.setprint(b);
a.retainAll(b);
println("A 교집합 B = ");st.setprint(a);
println("B-A = ");
b.removeAll(a);
st.setprint(b);

//st.S = smtm.mergeVocabulary(st.S, smtm.filein(pathn+"whole_mecab_pr.txt.1"));
//smtm.fileout(pathn+"korean.3", st.S);
//st.memo = smtm.mergeGrammar(pathn+fn+".3",pathn+"whole_mecab_pr.txt.1" , st.S);
//smtm.savefull(pathn+"korean.3", st.memo);
		st.learn1time(4,C);
		smtm.fileout(pathn+fn+".4", st.S);
		smtm.savefull(pathn+fn+".4", st.memo);
		smtm.savefull(pathn+fn+".4.bonlink", st.AP);
		smtm.savefull(pathn+fn+".4.mallink", st.OP);
		 */
		st.S = smtm.filein(pathn+fn+".3");
		st.memo = smtm.loadfull(pathn+fn+".3",st.S);
		st.AP = smtm.loadfull(pathn+fn+".3.bonlink",st.S);
		st.buildPREVSUCC();
		//printPREV("며으며/EC",st);
		/*printPREV("한국/NNP (한국/NNP)*",st);		
		/*printPREV("한국/NNP",st);		
		printPREV("교수/NNG (교수/NNG)*",st);		
		printPREV("교수/NNG",st);		
		printPREV("거의/MAG",st);		
		printPREV("결코/MAG",st);
		printPREV("새로/MAG",st);
		printPREV("별로/MAG",st);
		printPREV("고/EC",st);
		printPREV("며으며/EC",st);
		printPREV("ㄴ/ETM",st);
		printPREV("하/VV",st);
		printSUCC("*(*(을를/JKO) 을를/JKO) *(을를/JKO) 을를/JKO",st);
		printSUCC("기/ETN (기/ETN)*",st);
		printSUCC("*(말/NNG (말/NNG)*) 말/NNG (말/NNG)*",st);
		printsuccR("*(이가/JKS) 이가/JKS","하/VV",st);
		printsuccR("그/NP 이가/JKS","하/VV",st);
		printsuccR("그/NP 이가/JKS","사과/NNG 하/XSV",st);
		printsuccR("그/NP 이가/JKS","*(하/XSV) 하/XSV",st);
		printsuccR("*(이가/JKS) 이가/JKS","*(하/VV) 하/VV",st);
		printsuccR("*(이가/JKS) 이가/JKS","*(을를/JKO) 을를/JKO 하/VV",st);
		printsuccR("*(이가/JKS) 이가/JKS","*(을를/JKO) 을를/JKO",st);
		printsuccR("*(이가/JKS) 이가/JKS","*(은는/ETM) 은는/ETM",st);
		printsuccR("*(이가/JKS) 이가/JKS","*(을를/ETM) 을를/ETM",st);
		printsuccR("하/VV","게/EC",st);
		printsuccR("하/XSV","게/EC",st);
		printsuccR("*(이가/JKS) 이가/JKS","*(./SF) ./SF",st);
		printsuccR("*(이가/JKS) 이가/JKS","*(하/XSV 게/EC) 하/XSV 게/EC",st);
		printsuccR("그/NP 이가/JKS","*(하/XSV 게/EC) 하/XSV 게/EC",st);
		printsuccR("*(이가/JKS) 이가/JKS","*(게/EC) 게/EC",st);
		printsuccR("그/NP 이가/JKS","*(게/EC) 게/EC",st);
		printSUCC("그/NP 이가/JKS",st);		
		printSUCC("정부/NNG 이가/JKS",st);		
		printSUCC("한국/NNP (한국/NNP)*",st);		
		printSUCC("한국/NNP",st);		
		printSUCC("한국/NNP 은는/JX",st);		
		printSUCC("한국/NNP 이가/JKS",st);		
		printSUCC("*(이가/JKS) 이가/JKS",st);		
		printSUCC("한국/NNP 을를/JKO",st);		
		printSUCC("한국/NNP 로으로/JKB",st);		
		printSUCC("교수/NNG (교수/NNG)*",st);		
		printSUCC("교수/NNG",st);		
		printSUCC("거의/MAG",st);		
		printSUCC("결코/MAG",st);
		printSUCC("새로/MAG",st);
		printSUCC("별로/MAG",st);
//		printSUCC("고/EC",st);
		printSUCC("며으며/EC",st);
		printSUCC("ㄴ/ETM",st);*/
		
		System.out.print("-S is SUCC, -P is PREV, -r is printsuccR, End input \"exit\" or \".\", Now Input command>");
		while (!(str=keyboard.nextLine()).equals(".")) {
			String arg[] = str.split("\t");
			if(str.equals("exit")) System.exit(1);
			if (!UtilCommand.command(arg, st)) {
				System.out.println("Wrong command!");
			}
			System.out.print("-S is SUCC, -P is PREV, -r is printsuccR, End input \"exit\" or \".\", Now Input command>");
		}

		/*		
st.S = smtm.mergeVocabulary(st.S, smtm.filein(pathn+"whole_mecab_pr.txt.1"));
smtm.fileout(pathn+"korean.4", st.S);
st.memo = smtm.mergeGrammar(pathn+fn+".4",pathn+"whole_mecab_pr.txt.1" , st.S);
smtm.savefull(pathn+"korean.4", st.memo);
		 */
		/*st.learn1time(5,C);
		smtm.fileout(pathn+fn+".5", st.S);
		st.S = smtm.filein(pathn+fn+".5");
		st.learn1time(6,C);
		smtm.fileout(pathn+fn+".6", st.S);
		st.S = smtm.filein(pathn+fn+".6");
		st.learn1time(7,C);
		smtm.fileout(pathn+fn+".7", st.S);*/
	}

	static void printsuccR(String string, String string2, Student st) {
		Semanteme s1 = st.S.get(string);
		Semanteme s2 = st.S.get(string2);
		if (s1 == null || s2 == null) return;
		if (!(st.memo.containsKey(s1) 
				&& st.memo.get(s1).containsKey(s2))) 
			return;

		double fas;int i;
		if ((i=st.memo.get(s1).get(s2)) >  (fas=((double)s1.count)*s2.count/semsTot)) 
			print(string+" : "+i+"번 등장해서 associable to :"+string2+" expected as "+fas); 
		else print(string+ "은 " + i+" 번 등장해 NOT associable to : "+string2+" 의 " + fas);
		println(", 결합 비중은 "+((float)i)/s1.count);
	}

	public static void printSUCC(String x, Student st) {print(x+" 다음에 많이 온 것 = { "); st.setprintW(x,st.topSUCC(x, 40));}
	public static void printPREV(String x, Student st) {print(x+" 앞에 많이 온 것 = { "); st.setprintW(x,st.topPREV(x, 40));}

	static void findWrongs(String totest,Student st){
		List<Semanteme> c = st.malSUCC(totest, 20);
		List<Semanteme> SUCCtotest = st.topSUCC(totest, 20);
		List<Semanteme> ores = st.malSUCC(totest, 20);
		List<Semanteme> ans = new ArrayList<Semanteme> ();
		if (ores == null) return;
		st.topSUCC(totest, 20);
		int olen = ores.size();
		print(totest+" 다음에 오면 안된다는 초기 판단 = "); st.setprint(c);
		for (Semanteme k:c) { // 한국에 후속 불가라 추정한 것 k에 대해 
			List<Semanteme> result = new ArrayList<Semanteme>(); //totest에게만 결합 가능한 것들 
			result.addAll(SUCCtotest);  // 초기에는 모든 결합 가능한 것들을 저장 
			olen = ores.size();
			println(k.label+" 앞에 주로 쓰인 것들의 다음에  가장 많이 오는 것들을 '한국' 다음에 가장 많이 오는 것들에서 뺀 결과 => ");
			List<Semanteme> pk = st.topPREV(k, 20);
			double s0=0; int iu = 0;
			if (pk != null)  for (Semanteme dd:pk) { // totest에 후속 불가로 추정된 것의 최빈 선행 의미소 dd에 대해
				print("\t"+dd.label+"와 "+totest+" 다음에 공통 후속 가능한 것들  = ");
				List<Semanteme> ppk = st.topSUCC(dd, -1);
				ppk.retainAll(SUCCtotest);
				st.setprint(ppk);

				print("\t"+totest+"=>다음에서만 붙을 수 있는 것들  = ");
				ppk = st.topSUCC(dd,-1);
				result.removeAll(ppk); // dd에도 결합 가능한 것들을 배제 
				st.setprint(result);
				println ("\t"+dd.label+"과의 유사도 = "+st.sim(st.S.get(totest), dd, st.SUCC, 20));
				s0 += st.sim(st.S.get(totest), dd, st.SUCC, 20); iu++; // 평균 유사도 계산을 위한 누적 
			}
			double df= (s0/iu);
			if (pk == null || pk.isEmpty()) println ("\t"+k.label+" 앞에 주로 쓰인 것이 없어요");
			else println ("\t"+SUCCtotest.size()+" 개중 "+result.size()+" 개 남음, "+k.label+" 선행  의미소들과의 유사도 평균  = "+df);
			if (df > 0.25) ans.add(k); // 평균 유사도가 0.1 이상이면 dd들에 공통 결합가능한 것은 totest에도 결합 가능하다고 판단.
		} 
		System.out.println("결합 가능하다고 판별된 것들 = ");st.setprint(ans);
		ores.removeAll(ans);
		System.out.println("여전히 결합 불가능하다고 남은  것들 = ");st.setprint(ores);
	}


	private static void shortS() throws IOException, InterruptedException {
		String rfn = "/shr/data/sejong_mecab_pr.txt";
		String wfn;// = "/shr/data/corpus_mecab_pr.txt.short";
		List <String> C = new ArrayList<String> ();
		for (int j=1;j<9;j++){
			BufferedWriter out = null;
			Integer len =(Integer)(j*4+2) 				+1;
			out = new BufferedWriter(new FileWriter(wfn="/shr/data/shorts.txt."+len.toString()));
			for (int i=0;i<2;i++) {
				C = GstaltFinder.loadCorpus(rfn);
				//				BufferedReader in = new BufferedReader(new FileReader(fn));
				for (String line:C) {
					String[] s = line.split(" ");
					if (s.length == len && line.contains("/J")&&(line.contains("/E")))
						out.write(line+"\n");
				}
				if (rfn.equals("/shr/data/article_mecab_pr.txt")) rfn =  "/shr/data/sejong_mecab_pr.txt";
				else if (rfn.equals("/shr/data/sejong_mecab_pr.txt")) rfn =  "/shr/data/article_mecab_pr.txt";
				else System.exit(0);
			}
			out.close();
		}
	}

	private static void testAP(HashMap<Semanteme, HashMap<Semanteme, Integer>> AP,
			HashMap<Semanteme, HashMap<Semanteme, Integer>> ap, HashMap<String,Semanteme> S) {
		for (Semanteme x:AP.keySet()) 
			for (Semanteme y:AP.get(x).keySet())
				if (AP.get(x).get(y) - ap.get(S.get(x.label)).get(S.get(y.label)) != 0)
					println(x.label+"-"+y.label+" DIFFERENT!! Difference is "+(AP.get(x).get(y)/*+" , "+*/-ap.get(S.get(x.label)).get(S.get(y.label))));
	}

	private static void testDICT(HashMap<String,Semanteme> AP,HashMap<String,Semanteme> ap){
		for (Semanteme x:AP.values()) 
			if (!ap.get(x.label).isSameAs(x)) 
				println(x.label+" : DIFFERENT!! Difference is ");
	}

	public static void test1(String s){
		List<String> a = Arrays.asList(s.split(" "));
		for (int i=1;i<=a.size();i++) {
			println("For "+i);
			int j = 0; int d = 0;
			HashSet <String> f =   new HashSet<String>();
			for (String x:find(a.subList(0, i))) {
				print(("\t"+j++) + " : "+x);
				if (f.contains(x)) {
					d++; 
					println("  => "+d+"번째 중복 발견 `");
				}else {
					f.add(x);println();
				}
			}
			int k=0;
			println();
			for (String x:f) {
				println(("\t"+k++) + " : "+x);
			}
			j=0;
		}
	}

	public static List<String> find(List<String> s) {
		List<String> ret = new ArrayList<String> ();
		int size = s.size();
		if (size == 1) ret.add(s.get(0));
		else if (size == 2) {
			ret.add(s.get(0)+" "+s.get(1));
			ret.add(s.get(0)+" "+fa(s.get(0)));
			ret.add(ba(s.get(1))+" "+s.get(1));
		}
		else {
			List<String> X,Y;
			for (int i=1;i<size;i++) {
				X = find(s.subList(0, i));
				Y = find(s.subList(i, size));
				for (String x:X){
					for (String y:Y){
						ret.add(x+" "+y);
						ret.add(x+" "+fa(x));
						ret.add(ba(y)+" "+y);
					}
				}
			}
		}
		return ret;
	}
	public static String fa(String x) { return "("+x+")*";}

	public static String ba(String x) { return "*("+x+")"; }
	/*public static void A(PGraph F,PGraph G,PGraph H) throws IOException, InterruptedException {
		List <iNode>P ;

		Scanner keyboard = new Scanner(System.in);
		String str;
		System.out.println("문장을 입력하세요 :  (OR input comand: -c / -sup / -context / -dn / -de / -ae / -sg / -a / -def / -fan / -nfan / -fs / -sortfg)");
		morphemes_analyzer mecab = new morphemes_analyzer();
		while (!(str=keyboard.nextLine()).equals(".")) { 
			known.clear();
			pre_modif Modif = new pre_modif();										// 전처리 함수
			Modif.set_modif_config();			

			Modif.register_undefined(str);

			List<String> list = mecab.analyzer_space(str, 1);		// 형태소 분석 함수

			String rm_str = mecab.trans_ECS(list.get(0)); 			// 형태소 분석후 알맞게 전처리 하는 함수
			rm_str = mecab.trans_JKB(rm_str);							// 형태소 분석후 알맞게 전처리 하는 함수
			rm_str = mecab.trans_vxecv(rm_str);							// 형태소 분석후 알맞게 전처리 하는 함수

			String[] arr2 = rm_str.split(" ");
			String tmp_t ="";
			for(int i=0; i<arr2.length; i++){			
				tmp_t = tmp_t+morphemes_analyzer.phonetic_reduced(arr2[i])+" ";
			}
			System.out.println("mecab : " + tmp_t);

			rm_str = tmp_t;					
			rm_str = post_modif.revise_parsing3(rm_str);					// 후처리 함수
			rm_str = post_modif.revise_parsing(rm_str);					// 후처리 함수
			rm_str = post_modif.revise_parsing4(rm_str);

			String[] arr = rm_str.split(" ");
			String tmp ="";
			List <String> S = new ArrayList<String> ();
			for(int i=0; i<arr.length; i++){
				if(arr[i].equals("###/SPACE")){
					continue;
				}else S.add(arr[i]);
			}
			P = iNodes(S,F);
			NOTSHOWED = true;

			List <iNode>candids = buildTrees(P,F,G,H);
			if (candids == null || candids.isEmpty()) 
				println("NOTHING FOUND");
			else {
				print("******* 말이 되는 결과를 찾았습니다 : \n");
				for (iNode x:candids) 
					if (x.head != null) sfinal.add(x.head.label+" => "+x.tail.label);
					else if (x.element != null && !x.element.isEmpty())
						for (iNode sd:x.element) 
							sfinal.add(sd.label+"=>");
					else sfinal.add(x.label);
				for (iNode x:candids) sfinal2.add(terminal(x));
				for (String x:sfinal) {
					print("\t\t");	println(x);

				}
				println(sfinal.size()+" 개 ");
				for (String x:sfinal2) {
					println("\t\t"+x);

				}
				println("세부 구조를 표시합니다");int ii=0;
				for (iNode x:candids) {
					x.print();println();
					if (ii++ %10 == 0) 
						println(ii);
				}
				println(sfinal2.size()+" 개 ");
			}
			println("세부 구조를 표시합니다");int ii=0;
			for (iNode x:candids) {
				x.print();println();
				if (ii++ %10 == 0) 
					println(ii);
			}
			println(ii+" 개 ");
			println("최종 구조가 다른 것만  표시합니다") ; ii = 0;;
			List <String > ss = new ArrayList <String>();
			List <String > sss = new ArrayList <String>();
			for (iNode x:candids) {
				String oo = x.sprintc();
				if (!ss.contains(oo)) {
					println(oo);
					ss.add(oo);;
					if (ii++ %10 == 0) 
						println(ii);
				}
			}
			println(ii+" 개 ");

		}
	}*/
	/*private static String terminal(iNode x) {
		String ret = "[ ";
		if (x.head == null && x.element == null && (x.element == null || x.element.isEmpty())) 
			return "[ "+x.label+" ] ";
		else if (x.head != null) return "[ "+terminal(x.head)+"+"+terminal(x.tail)+", "+x.power+" ] ";
		else {
			for (iNode e:x.element) {
				ret += terminal(e) + " ";
			}
		}
		ret += "]";
		return ret;
	}*/
	private static float AP(String m1,String m2,PGraph f) {
		float v = 0;
		Node na, nb;
		na = f.G.containsKey(m1)? f.G.get(m1): null;
		nb = f.G.containsKey(m2)? f.G.get(m2): null;
		EdgeSet esa = (na != null)? f.SUCC.get(na.ID):null;
		v = (esa != null && esa.count.containsKey(nb) && na != null)? ((float)esa.count.get(nb))/na.count : 0;		
		return v;
	}
	/*private static List<iNodesFloat> addLeaf(iNode a, iNode b, float p, List<iNodesFloat> inf) {
		List<iNodesFloat> ret = new ArrayList<iNodesFloat> ();
		if (inf.isEmpty()) {
			iNodesFloat treef1 = new iNodesFloat (new ArrayList<iNode>(),-1);
			inf.add(treef1);
		}
		for (iNodesFloat treef:inf) {
			String m1 = a.label;
			String m2 = b.label;
			iNode n0 = new iNode(m1+ " "+m2);
			iNode n1 = new iNode(m1+ " "+lr.fa(m1));
			iNode n2 = new iNode(lr.ba(m2)+ " "+m2);
			n2.head = n1.head = n0.head = a;
			n2.tail = n1.tail = n0.tail = b;
			iNodesFloat treef1 = new iNodesFloat ((ArrayList<iNode>) treef.list,Math.max(p, treef.power)); 
			iNodesFloat treef2 = new iNodesFloat ((ArrayList<iNode>) treef.list,Math.max(p, treef.power)); 
			treef.list.add(n0);
			treef.power = Math.max(p, treef.power);
			treef1.list.add(n1);
			treef2.list.add(n2);
			ret.add(treef);
			ret.add(treef2);
			ret.add(treef1);
		}

		return ret;
	}*/
	/*private static List<iNodesFloat> addLeaf(HashSet <String> A, float p, List<iNodesFloat> inf) {
		List<iNodesFloat> ret = new ArrayList<iNodesFloat> ();
		if (inf.isEmpty()) {
			iNodesFloat treef1 = new iNodesFloat (new ArrayList<iNode>(),-1);
			inf.add(treef1);
		}
		for (iNodesFloat treef:inf)
			for (String a:A) {
				iNodesFloat treef1 = new iNodesFloat ((ArrayList<iNode>) treef.list,Math.max(p, treef.power)); 
				iNode n0 = new iNode(a);
				treef1.list.add(n0);
				treef1.power = Math.max(p, treef.power);
				ret.add(treef1);
			}

		return ret;
	}*/
	/*private static List<iNodesFloat> addLeaf(HashSet <String> A, List<iNode> e, float p, List<iNodesFloat> inf) {
		List<iNodesFloat> ret = new ArrayList<iNodesFloat> ();
		if (inf.isEmpty()) {
			iNodesFloat treef1 = new iNodesFloat (new ArrayList<iNode>(),-1);
			inf.add(treef1);
		}
		for (iNodesFloat treef:inf)
			for (String a:A) {
				iNodesFloat treef1 = new iNodesFloat ((ArrayList<iNode>) treef.list,Math.max(p, treef.power)); 
				iNode n0 = new iNode(a,e,p);
				treef1.list.add(n0);
				treef1.power = Math.max(p, treef.power);
				ret.add(treef1);
			}
		return ret;
	}
	private static List<iNodesFloat> addLeaf(List <iNode> A, float p, List<iNodesFloat> inf) {
		List<iNodesFloat> ret = new ArrayList<iNodesFloat> ();
		if (inf.isEmpty()) {
			iNodesFloat treef1 = new iNodesFloat (new ArrayList<iNode>(),-1);
			inf.add(treef1);
		}
		for (iNodesFloat treef:inf)
			for (iNode a:A) {
				iNodesFloat treef1 = new iNodesFloat ((ArrayList<iNode>) treef.list,Math.max(p, treef.power)); 
				iNode n0 = new iNode(a);
				treef1.list.add(n0);
				treef1.power = Math.max(p, treef.power);
				ret.add(treef1);
			}

		return ret;
	}
	private static List<iNodesFloat> addLeaf(iNode a, float p, List<iNodesFloat> inf) {
		List<iNodesFloat> ret = new ArrayList<iNodesFloat> ();
		if (inf.isEmpty()) {
			iNodesFloat treef1 = new iNodesFloat (new ArrayList<iNode>(),-1);
			inf.add(treef1);
		}
		for (iNodesFloat treef:inf) {
			treef.list.add(a);
			ret.add(treef);
		}
		return ret;
	}*/
	public static HashSet<String> known = new HashSet<String>();
	/*private static List<iNode> buildTrees(List<iNode> input, PGraph f, PGraph g, PGraph h) {  // 구문분석기
		List<iNode> ret = new ArrayList<iNode>();
		if (input.size() == 1) {
			ret.add(input.get(0));
			return ret;
		}
		PGraph ff = f;
		f = g;
		String toAdd = "";
		for (iNode x:input) 
			toAdd += toAdd.equals("")? x.label:"+"+x.label;
		if (known.contains(toAdd)) return null;

		calledN++;
		if (input == null) return null;
		int len = input.size();

		List<ArrayList<iNode>> inputs = new ArrayList<ArrayList<iNode>>();

		List<iNodesFloat> inw = new ArrayList<iNodesFloat> (); 
		List<iNodesFloat> inw2 = new ArrayList<iNodesFloat> (); 

		float v = 0;
		float max = 0;
		// Start of 실험적 버전 . 리커전 덜하게 
		ArrayList<iNode> t0,t1,t2;
		float v1,v2,v3;
		v1=v2=v3=-1;
		List<iNode> W = new ArrayList <iNode>(); 
		t0 = new ArrayList<iNode>();  //기본트리  
		String whole = input.get(0).label;
		if (!whole.startsWith("*") && !whole.startsWith("청와대/NNP")) 
			return null;
		for (int i=1;i<len;i++)
			whole += " "+input.get(i).label;
		List<iNode> elts = new ArrayList<iNode>();
		for (int i=0;i<len; ) {
			String m0,m1,m2,m3;
			iNode ii;
			String w1 = m0 = (ii=input.get(i)).label;//"";//s1 = s2 = "";
			if (elts == null) elts = new ArrayList<iNode>();
			elts.add(input.get(i++));
			float lmax = 0;
			for (;i<len && (v1 = AP(m0, m1=(input.get(i).label),g))>cv;i++) {
				w1 +=" "+m1;
				elts.add(input.get(i));
				if (v1 > max) max = v1;
				if (v1 > lmax) lmax = v1;
				if (v1 >0) v2 = v1;
				m0=m1;
			}  // w1 contains longest meaningful continuum
			if (elts.size() == 1) {
				elts = new ArrayList<iNode>();
				if (ii.element != null) elts.addAll(ii.element);
				else elts  = null;
			}
			if (whole.equals(w1)) {
				ret.add(new iNode(whole,elts,lmax));
				return ret;
			}
			HashSet <String> dcm1 = new HashSet<String>();
			if (m0.equals(w1)) {
				dcm1.add(w1);
				v2 = -1;
			}
			else dcm1 = lr.Decomposition(w1,g,h);
			HashSet <String> dcm2 = new HashSet<String>();
			for (String xs:dcm1) {
				if (g.G.containsKey(xs)) dcm2.add(xs);
			}
			if (dcm2 == null || dcm2.isEmpty()) {
				known.add(toAdd);
				return null;
			}
			inw2 = inw;
			inw = addLeaf(dcm2,elts,v2,inw);
			if (elts != null) elts = new ArrayList<iNode>();
		}		
		if (max < cv) {
			known.add(toAdd);
			return null; // 주어진 트리들로부터 결합가능한 쌍을 못 찾았을 때, return null; 즉, 실패
		}
		else Collections.sort(inw);

		for (iNodesFloat y:inw) {
			List <iNode> p = y.list;
			if (p == null) 
				continue;
			if (p.get(0).label.equals("*(은는/JX) 은는/JX"))
				if (p.size() < 5) 
					NOTnull =NOTnull+0;
			List <iNode> r = buildTrees(p, f, g, h);
			if (r != null && !r.isEmpty()) {
				ret.addAll(r);
			}
		}
		known.add(toAdd);
		return ret;
	}*/

	static void print(Object x) {	System.out.print(x);	}
	static void println() {	System.out.println();	}
	static void println(Object x) {	System.out.println(x);	}

	/*private static List<iNode> iNodes(String t,PGraph f) {
		List <iNode> ret = new ArrayList<iNode>();
		List <String> m = Arrays.asList(t.split(" "));
		for (String x:m) 
			if (f.G.containsKey(x)) ret.add(new iNode(f.G.get(x)));
			else ret.add(new iNode(x));
		return ret;
	}

	private static List<iNode> iNodes(List<String> m,PGraph f) {
		List <iNode> ret = new ArrayList<iNode>();
		for (String x:m) 
			if (f.G.containsKey(x)) ret.add(new iNode(f.G.get(x)));
			else ret.add(new iNode(x));
		return ret;
	}*/

	public static void analyze(String s,PGraph f, PGraph h) {
		String m[] = s.split(" ");

		for (int i=0;i<m.length-1;i++) {
			String a = m[i];
			String b = m[i+1];

			Node na = f.G.get(a);
			Node nb = f.G.get(b);

			if (f.SUCC.get(na.ID).hasNext(nb)) System.out.println("\t"+a+"=>"+b);

		}
	}
}

