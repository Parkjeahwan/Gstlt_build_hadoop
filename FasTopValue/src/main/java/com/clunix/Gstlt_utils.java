package com.clunix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.clunix.NLP.graph.Node;
import com.clunix.NLP.graph.SGraph;

public class Gstlt_utils {
	final private static List<String> V 	// 용언 리스트 
	= Arrays.asList("VV", "VA", "VX", "XSV", "XSA", "VCP", "VCN");
	final private static List<String> Sub //체언 리스트
	= Arrays.asList("NNG", "NNP", "NNB", "NNBC", "NR", "NP", "SN" ,"XSN" ,"SL", "ETN", "XR"); // 151117
	final private static List<String> JC //관계사 리스트
	= Arrays.asList("JKS", "JKC", "JKG", "JKO", "JKB", "JKV", "JKQ",
			"JC", "JX", "EP", "EF", "EC", "ECS", "ETN", "ETM", "XSN");
	final private static List<String> JP //  용언 관계사 리스트
	= Arrays.asList("JKS", "JKC", "JKO", "JKB", "JKV", "JKQ",
			"JX", "EF", "EC", "ECS", "ETN");
	final private static List<String> J // 조사 리스트 
	= Arrays.asList("JKS", "JKC", "JKG", "JKO", "JKB", "JKV", "JKQ", "JC", "JX");
	final private static List<String> SC // 구분자등  리스트
	= Arrays.asList("SF", "SE", "SSO" ,"SSC" , "SC", "SY");
	final private static List<String> E // 어미 리스트
	= Arrays.asList("EP", "EF", "EC" ,"ECS" ,"ETN", "ETM");

	private static HashMap<String, Integer> vlnoun_data;

	public static SGraph loadGstalt(String fn) throws IOException, InterruptedException {
		SGraph g = new SGraph();
		g.filein(fn+".PgstaltMAX");
		//		g.filein(fn+".PgstaltMAX");
		return g;
	}

	public static int offsetOfP(String line,String P) {
		String []a = P.split(" ");
		int len = a.length; 
		if (a.length == 1) return line.lastIndexOf(P);
		return line.substring(0,line.lastIndexOf(a[len-1])).lastIndexOf(a[0]);
	}

	public static SGraph buildGstaltMAX(String line, HashMap<String, Integer> vlnoun_data_out) throws IOException, InterruptedException {
		vlnoun_data = vlnoun_data_out;
		SGraph g = new SGraph();
		int lastpi, prevlastpi;
		int i = 0;
		lastpi = prevlastpi = -1;
		String oline;

		Node na, nb, nm;
		oline = line;
		//			System.out.println(line);
		String lastp,lastn;
		lastp = P_stem(line); 

		lastn = N_stem(line);
		int lpi,lni;
		lpi = lastp==null||lastp.equals("")? -1:line.lastIndexOf(lastp.split(" ")[0]) ;
		lni = lastn==null||lastn.equals("")? -1:line.lastIndexOf(lastn.split(" ")[0]) ;
		if (lni>=1 && lpi < lni) { 
			String substr = line.substring(0,lni-1); String[] ssa = substr.split(" ");
			for (int i0=ssa.length-1;i0>=0;i0--) {
				String mpm = ssa[i0];
				if (mpm.contains("/ETM")) {
					//						prev morpheme continuums =>lastn including lastp;
					na = g.addNode(lastp);
					nm = g.addNode(mpm);
					nb = g.addNode(lastn);
					addGstalt(na,nm,nb,g);
					break;
				}
				else if (mpm.contains("/JC")) break;
				else if (mpm.contains("/J")) {
					String plastn = N_stem(substr);
					if (plastn == null || plastn.equals("")) break; //{System.out.println(line);nm = g.addNode(mpm);	nb = g.addNode(lastn);addGstalt(nm,nb,g);break;} 
					na = g.addNode(plastn);
					nm = g.addNode(mpm);
					nb = g.addNode(lastn);
					addGstalt(na,nm,nb,g);
					break;
				}
				else {
					//						last prev morpheme continuum=>lastn;
					na = g.addNode(mpm);
					nb = g.addNode(lastn);
					addGstalt(na,nb,g);
					break;
				}
			}
		}

		while (!lastp.equals("")) { 
			lastpi = offsetOfP(line,lastp);
			String target, prevlastp, pline0;
			target = prevlastp = pline0 = null;

			boolean NOP = false;
			while (true){
				String nfpr,ap0[];
				if (lastpi+lastp.length() <= line.length()) {
					if (((pline0 = line.substring(lastpi+lastp.length())).indexOf("/E")) >= 0 ||
							pline0.indexOf("게/JKB") >=0 || pline0.indexOf("게/EC") >=0) {
						ap0 = pline0.split(" ");
						for (String mpm:ap0) {
							if (!mpm.contains("/")) continue;
							else if (mpm.contains("/E"))
								if(mpm.contains("/ETM") || mpm.contains("게/EC")){
									NOP = true; break;}
								else if  (mpm.contains("/ECS")) 
									if ((nfpr=NFPR(pline0)).endsWith("/ETM") || nfpr.endsWith("게/JKB")  || nfpr.endsWith("게/ECS") ){ NOP = true; break;} // NFPR returns Nearst Following Predicate's Relators
									else { break; }
								else continue;
							else if (mpm.contains("게/JKB")) {
								NOP = true; break;} 
							else break;
						}
					}
				}
				else System.out.println(line);

				if (!NOP) break;	
				NOP = false;
				line = line.substring(0, lastpi);
				lastp = P_stem(line);
				if (lastp.equals("")) break;
				lastpi = offsetOfP(line,lastp);
			} // end of inner while
			if (lastp.equals("")) break;
			prevlastp = P_stem(line = line.substring(0,lastpi));
			prevlastpi = prevlastp.equals("") ?  0 : offsetOfP(line,prevlastp);
			Node np = g.addNode(lastp); 
			target = line.substring(prevlastpi);
			//		System.out.println("\t\t"+target);
			findGstalt(target,np,np,g);
			if (d != 0 )
				System.out.println("");
			lastp = prevlastp;
		} // end of outer while
		if (i++ % 10000 == 0) System.out.println((i-1) +" th sentence : "+oline+"- predicate "+lastp);

		System.out.println("Total "+g.G.size()+" nodes registered");
		return g;
	}

	public static String NFPR(String pstr) {
		String ret="";
		if (pstr == null || pstr.equals("")) return pstr;
		String w[] = pstr.split(" ");
		for (int i=0;i<w.length;i++) {
			String x=w[i]; 
			if (!x.contains("/V") && !x.contains("/XS")) continue;
			for (int j=i+1;j<w.length-1;j++) { String y = w[j];
			if (y.contains("/E") || y.contains("/JX")) 
				ret += ret.equals("") ? y : (" "+y);	
			else if (y.equals("") || y.equals(" ")) continue;
			else return ret; 
			}
		}
		return ret;
	}


	static int d = 0;
	public static void findGstalt(String line,Node np, Node norip, SGraph g) throws InterruptedException { // 문장 스트링 line을 받아서 입력 서술어 np에 연결되는 연속체를 붙여 g에 넣어 돌려주는 함수
		Node nje,nb;
		nje=nb= null;
		String M[] = line.split(" ");
		String cw, leftline, lastJE ;
		cw = leftline = lastJE = "";
		int prl = 0;
		d++;
		for (int i = M.length-1;i>=0;i--){
			String m = M[i];
			leftline = line.substring(0, line.lastIndexOf(m));
			if (prl != 0) prl += (1+m.length()); 
			if (JP.contains(type(m))) { // if m이 서술어 연결 관계사(JP)를 가지고 있는 경우 nb=해당관계사에 결합한 의미소; 로 설정해 게쉬탈트에 추가
				if ((m.equals("을를/JKO") || m.equals("은는/JX")) && (m.equals(lastJE)|| np.content.startsWith(m))) continue;
				nje = g.addNode(m);
				lastJE = m;
				if (J.contains(type(m))) cw = N_stem(leftline);
				else cw = P_stem(leftline);
				if (cw == null || cw.equals("")) if (i>1) cw = M[i-1]; 
				else continue;

				//				if (np.content.equals("있/VV") && M[M.length-1].equals("에/JKB") && M[M.length-2].equals("중/NNB")) // for (int j=i+1;j<M.length-1;j++) if (M[j].contains("중/NNB") && M[j+1].contains("에/JKB")) continue;
				nb = g.addNode(cw);
				addGstalt(nb,nje,np,g);
				//	System.out.println("\t\t\t"+nb.content+"=>"+nje.content+"=>"+np.content);
				if (!norip.content.equals(np.content)) addGstalt(nb,nje,norip,g);
				np = g.addNode(m+" "+np.content);
			}
			else if (i == M.length-1 && d == 1 || type(m).equals("MAG")) {
				nje = g.addNode(m);			
				addGstalt(nje,np,g);
				//		System.out.println("\t\t\t"+nje.content+"=>"+np.content);

				if (!norip.content.equals(np.content)) 
					addGstalt(nje,norip,g);
			}
			//			else if (i == M.length-1){
			//				nje = g.addNode(m);
			//				completeGstalt(nje,np,g);
			//				leftline = line;
			//			}
			else continue;// 부사도 아니고 용언관계사(JP)를 포함하지 않으면 다음 형태소를 검사

			findGstalt(leftline,np,norip,g); // 게쉬탈트에 추가된 부분을 제외한 나머지 line에 대해 다시 게쉬탈트 찾기
			break;
		}
		d--;
	}
	public static void addGstalt(Node nj,Node np, SGraph g) throws InterruptedException {
		g.SUCC.get(nj.ID).addNext(np);
		g.PREV.get(np.ID).addNext(nj);
	}

	public static boolean one_morpheme(String str){	
		String[] arr = str.split(" ");
		String tag="";
		if(arr.length==1){	// 형태소 길이가 하나라면
			return true;
		}else{					// 형태소 종류가 여러개인지 판단
			String[] arr2 = arr[0].split("/");
			tag = arr2[arr2.length-1];

			for(int j=1; j<arr.length; j++){
				arr2 = arr[j].split("/");
				if(!tag.equals(arr2[arr2.length-1])){
					return false;
				}
			}
			return true;
		}
	}

	public static void addGstalt(Node nb,Node nj,Node np, SGraph g) throws InterruptedException {
		Node nt = null;

		g.SUCC.get(nb.ID).addNext(nj);
		g.PREV.get(nj.ID).addNext(nb);

		g.SUCC.get(nj.ID).addNext(np);
		g.PREV.get(np.ID).addNext(nj);

		nt = g.addNode(nb.content+" "+nj.content);
		g.SUCC.get(nt.ID).addNext(np);
		g.PREV.get(np.ID).addNext(nt);

		nt = g.addNode(nj.content+" "+np.content);
		g.SUCC.get(nb.ID).addNext(nt);
		g.PREV.get(nt.ID).addNext(nb);
	}

	public static String type(String str)
	{ 
		String[] arr = str.split("/");
		if (arr.length >= 1) return arr[arr.length - 1];
		else return null;
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


	public static String N_stem(String s){	// 체언 어간 151114 추가 함수 
		String str = s;
		if (s == null || s == "" ) return null; // 151106
		String[] arr = str.split(" ");
		boolean hasN = false;
		for (String NT:Sub) 
			if (s.contains(NT)) {
				hasN = true;
				break;
			}
		if (!hasN) return null;

		if (one_morpheme(str) && Sub.contains(type(str))) { //형태소종류가 하나 일경우
			return str;
		}

		for(int j=arr.length-1; j>=0; j--){
			String ttype = type(arr[j]);
			if(ttype == null || !ttype.equals("XSN") &&  !ttype.equals("ETN") &&
					(JC.contains(ttype) || SC.contains(ttype)))
				continue;
			//System.out.println("main_frame arr[j] : " + arr[j]);

			if(Sub.contains(type(arr[j]))){	// 체언을 포함할 경우				
				if (arr[j].contains("/XSN")) { // 뒤에서부터 본 형태소가 XSN 일 경우					
					if (j-1 >= 0 && Sub.contains(arr[j-1])) { //x의 바로 앞에 선행하는 형태소y가 체언일 경우
						return arr[j-1];
					}
					else continue;
				}
				else if (arr[j].contains("/ETN")) {					
					for (int k = j-1; k>=0 && k < arr.length-1; k--) { // 1509303 수정 
						if (arr[k].contains("/V") || arr[k].contains("/XSV") || arr[k].contains("/XSA")){ 
							return P_stem(s.substring(0, s.lastIndexOf(arr[j]))) + " " + arr[j];  
						}
					}
					return P_stem(s.substring(0, s.lastIndexOf(arr[j]))) + " " + arr[j];
					//System.out.println("main_frame return d : " + V_frame(i-1, S, tab) + arr[j]); //return y
				}					
				else {
					//System.out.println("main_frame return e : " + arr[j]);
					return arr[j];
				}
			}
			else if(arr[j].contains("/V") || arr[j].contains("/XSV") || arr[j].contains("/XSA")) {	// 용언을 포함할 경우
				//System.out.println("main_frame return f : " + V_frame(i,S,tab));
				continue;			
			}
			else {
				//System.out.println("main_frame return g : " + arr[j]);
				return arr[j];
			}
		}			

		//System.out.println("main_frame h : " + arr[arr.length -1]);
		return arr[arr.length -1]; 
	}
	public static String P_stem(String str) {    //형태소 분석된 문장(일부)를 받아서 가장 후위의 용언 어간을 리턴하는 함수
		// pi 는 용언 어절의 index
		// pp 는 용언 형태소
		String pp ="";
		String[] arr = str.split(" ");       
		int ppi=0;
		if (arr.length >= 1) for(int i=arr.length-1; i>=0; i--){        // 용언형태소 pp를 저장하기 위한 for문
			if(V.contains(type(arr[i]))){
				pp = arr[i];
				ppi=i;
				break;
			}
		}
		if (pp == "") 
			return "";
		String pptype = type(pp);
		if (pp.length() >0 && (pp.equals("같/VA") || pp.equals("없/VA") || pp.equals("있/VA") || pp.equals("있/VV"))) { //else if (pp == "같/VA" "없/VA" "있/VA")  // 1509303 수정 
			String prev_pi;
			if (ppi == 0) prev_pi = "";
			else prev_pi = str.substring(0,str.lastIndexOf(pp));
			//			else prev_pi = str.substring(0, ppi-1);
			String [] arr_prev_pi = prev_pi.split(" ");
			int arr_length = arr_prev_pi.length;
			if (arr_length >= 1) {
				String lastm;
				String typ = type(lastm=arr_prev_pi[arr_length-1]);
				if (typ != null && typ != "" && typ.equals("")&& (typ.equals("NNG") || typ.equals("XR"))) {
					return lastm + " " + pp;               
				}
				else if (lastm.equals("에/JKB") && arr_length >= 3 && arr_prev_pi[arr_length-2].equals("중/NNB") && pp.equals("있/VV") && NNGLV_2(arr_prev_pi[arr_length-3]) ) 
					return arr_prev_pi[arr_length-3]+" " +"중/NNB 에/JKB 있/VV";
			}
			else return pp;
		}
		else if (pptype.equals("VX") || pptype.equals("VV") || pptype.equals("VA")) 
			return pp.trim();
		//System.out.println("V_frame : input pi : " + S.get(pi));
		// 만약 pp가 동사, 형용사, 보조동사가 아닌 용언일 경우

		if(pptype.equals("XSA") || pptype.equals("XSV") || pptype.equals("VCP") || pptype.equals("VCN")){
			String eojeol = arr[ppi];	           			

			for(int i=ppi-1; i>=0; i--){                // ppi는 arr 어절에서의 용언형태소 위치
				String ttype = type(arr[i]);
				//if((Sub.contains(type(arr[i])) ||type(arr[i]).equals("XR")) && !type(arr[i]).equals("XSN")){
				if((ttype != null && !ttype.equals("") && (Sub.contains(ttype) ||type(arr[i]).equals("XR")) && !type(arr[i]).equals("XSN"))){
					eojeol = arr[i]+" "+eojeol;                   
					//System.out.println(tab+"[용언어간] S내의 최근접 선행 체언부터 pp까지\t"+eojeol.trim());
					return eojeol.trim();
				}
				if (V.contains(type(arr[i]))|| E.contains(type(arr[i])) ||J.contains(type(arr[i]))) 
					return eojeol.trim();
				//else if (!type(arr[i]).equals("SY") && !type(arr[i]).equals("XSN")) 
				else if (ttype != null && !ttype.equals("") && (!type(arr[i]).equals("SY") && !arr[i].equals("들/XSN")))
					eojeol = arr[i]+" "+eojeol;
			}
		}
		return pp.trim();
	}

	private static void set_vlnoun(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line;
		while ((line = br.readLine()) != null) {
			vlnoun_data.put(line, 1);
		}
		br.close();
	}

	public static boolean NNGLV_2(String morp) {
		if (vlnoun_data.containsKey(morp)) {
			return true;
		}
		return false;
	}

	public static SGraph buildGstalt(String fn) throws IOException, InterruptedException {
		ArrayList <String> C = loadCorpus(fn);
		SGraph g = new SGraph();
		int i = 0;

		for (String line : C) {
			String lastp = P_stem(line);
			if (lastp == null || lastp.equals("")) 
			{i++; continue;}

			int lastpi = line.lastIndexOf(lastp.split(" ")[0]);
			String partline;
			String prevlastp = P_stem(partline = line.substring(0,line.lastIndexOf(lastp.split(" ")[0])));
			int prevlastpi = partline.lastIndexOf(prevlastp.split(" ")[0]);
			if (prevlastpi < 0) prevlastpi = 0;
			Node np;
			np = null;
			np = g.addNode(lastp); 

			String target = line.substring(prevlastpi, lastpi);//+prevlastp.length(),lastpi);

			findGstalt(target,np,np,g);
			if (i++ % 10000 == 0) System.out.println((i-1) +" th sentence : "+line+"- predicate "+lastp);
		}
		System.out.println("Total "+g.G.size()+" nodes registered");
		g.fileout(fn+".Pgstalt");
		return g;
	}
}
