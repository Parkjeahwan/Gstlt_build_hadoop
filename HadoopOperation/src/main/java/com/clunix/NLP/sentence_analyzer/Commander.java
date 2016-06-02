package com.clunix.NLP.sentence_analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.clunix.NLP.graph.Node;
import com.clunix.NLP.graph.NodeInt;
import com.clunix.NLP.graph.PGraph;
import com.clunix.NLP.graph.SGraph;

public class Commander {
	//public static sentence_analyzer sa = new sentence_analyzer(); 
	public static String pathn = "/shr/data/";
//	public static String fn = "corpus_mecab_pr.txt";
//	public static String fn = "article_mecab_pr_splitLess.txt";
	public static String fn = "article_mecab_pr_50K.txt";
	public static String fnsp = "corpus_mecab_space_pr.txt";
	public static String sapath = "/shr/data/";
	public static boolean cvb = true;
	public static int cv0 = 100;
	public static double cvc = 0.5;
	public static int cvd = 10;
	public static int cvv = 20;
	/*public static boolean command(String[]a,SGraph G,PGraph CC, PGraph kg, ArrayList<String>C) throws IOException, InterruptedException{
		
		//GstaltUtil gu = new GstaltUtil();
		int ii = 0;
		if (!a[0].startsWith("-")) return false;
		if (a[0].equals("-n")) {  // n	의미소a => 의미소a의 전후 노드들을 보여줌
			Node in = G.G.get(a[1]);
			if (in != null) { System.out.println("Node "+in.content +" : " +in.count);	G.showNext(in);	}
			else { System.out.print(a[1]+" is registered : ");System.out.println(G.G.containsKey(a[1]));}
			return true;
		}

		else if (a[0].equals("-c")) { //c	의미소a	의미소b	=> 의미소a와 의미소b의 공통(common) 선행노드와 공통 후속 노드를 보여줌
			Node in1 = G.G.get(a[1]);Node in2 = G.G.get(a[2]);
			if (in1 != null) {
				if (in2 != null) {
					System.out.print("'"+a[1]+"'과 '"+a[2]+"'의 공통선행자 : ");
					HashSet <Node> res = G.PREV.get(in1.ID).intersect(G.PREV.get(in2.ID));
					if (res != null) { 
						System.out.print("("+res.size()+"/"+G.PREV.get(in1.ID).count.size()+","+G.PREV.get(in2.ID).count.size()+")\t");
						for (Node x:res) 
							if (a.length >3 && uf.type(a[3]).equals(x.content.split("/")[x.content.split("/").length-1]) || a.length == 3)
							{ System.out.print(x.content+", "); if (++ii % 10 == 0) System.out.print("\n\t\t"); } 
						System.out.println(""); ii = 0;
					} 
					else System.out.println("NONE");
					System.out.print("'"+a[1]+"'과 '"+a[2]+"'의 공통후속자 :\t");
					res = G.SUCC.get(in1.ID).intersect(G.SUCC.get(in2.ID)); 
					if (res != null) { 
						System.out.print("("+res.size()+"/"+G.SUCC.get(in1.ID).count.size()+","+G.SUCC.get(in2.ID).count.size()+")\t");
						for (Node x:res)  
							if (a.length >3 && uf.type(a[3]).equals(x.content.split("/")[x.content.split("/").length-1]) || a.length == 3)
							{	System.out.print(x.content+", "); if (++ii % 10 == 0) System.out.print("\n\t\t"); } 
						System.out.println("");	ii = 0; return true;
					}
					System.out.println("NONE");return true;

				} else { System.out.println(a[2]+" was not registered"); return true; }
			} else { System.out.println(a[1]+" was not registered"); return true; }
		}
		else if (a[0].equals("-sup")) { // sup	의미소a의 동류어를 보여줌
			Node in1 = G.G.get(a[1]);
			HashSet<Node> ress = dm.kin(G,a[1]);
			if (in1 != null) { System.out.print(a[1]+"의 동류어 :\t");for (Node x:ress) { System.out.print(x.content+", "); if (++ii % 10 == 0) System.out.print("\n\t\t"); } System.out.println("");dm.rep(G,a[1],ress); ii = 0; return true;
			} else { System.out.println(a[1]+" was not registered"); return true; }
		}
		else if (a[0].equals("-context")) { ////context	의미소a	의미소b	=> 의미소a가 나오고 의미소b가 후속되는 문장을 보여줌
			if (a.length > 2) { System.out.println("'"+a[1]+"'과 '"+a[2]+"'의 공통 출현 문장 :\t");
				for (String line:C) if (line.contains(" "+a[1]) && line.substring(line.indexOf(" "+a[1])).contains(" "+a[2]) && 
					!line.substring(line.indexOf(" "+a[1]),line.lastIndexOf(" "+a[2])).contains("/V") && !line.substring(line.indexOf(" "+a[1]),line.lastIndexOf(" "+a[2])).contains("/XS")) System.out.println("\t"+line);
			}
			return true;
		}
		else if (a[0].equals("-dn")) {  // delete node having contents of a[1]
			G.delNode(a[1]); System.out.println("Node "+a[1]+" removed successfully");
			return true;
		}
		else if (a[0].equals("-de")) {	// delete edge of node1(having contents of a[1])->node2(having contents of a[2])
			G.delEdge(a[1],a[2]); System.out.println("Edge "+a[1]+", "+a[2]+" removed successfully");
			return true;
		}
		// add edge of node1(having contents of a[1])->node2(having contents of a[2])
		else if (a[0].equals("-ae")) {	
			return true;				
		}
		// save gstalt)
		else if (a[0].equals("-sg")) {
			G.fileout(pathn + fn + GestaltConstants.LEGACY_P_GESTALT_MAX_FILE_NAME_SUFFIX); 
//			SGraphFile.write(G, pathn + fn + GestaltConstants.P_GESTALT_MAX_V1_0_FILE_NAME_SUFFIX);
			System.out.println("New GSTALT saved with name '"+pathn+fn+".PgstaltMAX'");
			return true;
		}
		else if (a[0].equals("-a")) {// associable
			if (a.length == 4 && dm.asble(G,kg,a[1],a[2],a[3]) || a.length == 3 && dm.asble(G,kg,a[1],a[2]))
				System.out.println("==> They are ASSOCIable");
			else System.out.println("==> NOT ASSOCIable");
			return true;
		}
		else if (a[0].equals("-def")) {// associable
			if (dm.def(G,a[1]) == null) {System.out.println("NO DEF SET");return true;}
			HashSet <Node> da = dm.def(G,a[1]);	
			dm.rep(G,a[1],da);
			return true;
		}
		else if (a[0].equals("-fan")) {// show frequently associated nodes
			if (a.length < 2) return true;
			if (sa.FAL.get(G.G.get(a[1]).ID) == null) {System.out.println("NO FAS SET");return true;}
			else { int agi = 0;
			for (NodeInt x:sa.FAL.get(G.G.get(a[1]).ID)) { if (++agi%30 == 0) System.out.println();
			System.out.print(x.n.content + " :"+x.w + " , ");}
			}
			return true;
		}
		else if (a[0].equals("-nfan")) {// show frequently associated nodes
			if (a.length < 2) return true;
			if (sa.NFAL.get(G.G.get(a[1]).ID) == null) {System.out.println("NO FAS SET");return true;}
			else { int agi = 0;
			for (NodeInt x:sa.NFAL.get(G.G.get(a[1]).ID)) { if (++agi%30 == 0) System.out.println();
			System.out.print(x.n.content + " :"+x.w + " , ");}
			}
			return true;
		}

		else if (a[0].equals("-fs")) { // fas graph save
			//		AG.fileout(pathn+fn+".PgstaltMAX"); return true;
			return true;
		}
		else if (a[0].equals("-sortfg")) {  // sort fas graph
			//		AG.sortEdge();return true;
			return true;
		}
		else if (a[0].equals("!!")) {  // execute last command
			return true;
		}
		else if (a[0].equals("-nid")) {  // execute last command
			System.out.println("Node "+G.G.get(a[1]).content+"'s ID == "+G.G.get(a[1]).ID);
			return true;
		}
		else if (a[0].equals("-bt") || a[0].equals("-ipr")) {
			if (a.length < 3) {System.out.println("WRONG ARGUMENTS");return true;}
			int x,y,ca; double min; Node n1,n2;
			System.out.println("\t일치도 = "+(ca=gu.commonAxisN(gu.svector(G,kg,n1=G.G.get(a[1])), gu.svector(G,kg,n2=G.G.get(a[2]))))+" , "+(x=gu.svector(G,kg,n1).size())+" : "+(y=gu.svector(G,kg,n2).size())+" => "+(ca/(min=((double)Math.min(x, y)))));
			System.out.println("\tIPR = "+gu.ipr(gu.svector(G,kg,n1), gu.svector(G,kg,n2)));
			System.out.println("\tEIPR = "+gu.eipr(G,kg,n1, n2, 1)); // 1 for SUCC
			return true;
		}
		else if (a[0].equals("-sett")) {
			gu.CV = Double.parseDouble(a[1]);
			return true;
		}
		else if (a[0].equals("-setk")) {
			if (a.length >= 2) cv0 = Integer.parseInt(a[1])==0? cv0:Integer.parseInt(a[1]);
			if (a.length >= 3) cvc = Double.parseDouble(a[2])==0? cvc:Double.parseDouble(a[2]);
			if (a.length >= 4)cvb = Boolean.parseBoolean(a[3]);
			
			return true;
		}
		else if (a[0].equals("-setm")) {
			if (a.length >= 2) cvd = Integer.parseInt(a[1])==0? cvd:Integer.parseInt(a[1]);
			
			return true;
		}
		else if (a[0].equals("-setv")) {
			if (a.length >= 2) cvv = Integer.parseInt(a[1])==0? cvv:Integer.parseInt(a[1]);
			
			return true;
		}
		else if (a[0].equals("-test")) {
			BufferedReader filen = new BufferedReader(new FileReader("/shr/data/asble.txt"));
			String str; 
			while ((str = filen.readLine())!=null && !str.equals("")) {
				String b[] = str.split("\t");
				if (Boolean.parseBoolean(b[2]) != dm.asble(G, kg, b[0], b[1])) System.out.println("\t\t=======> DIFFERENT");
				else System.out.println("\t\t\t\t\t==> GGGGGGGGOOOOOOODD:");
			}
			filen.close();
			return true;
		}
		else if (a[0].equals("-f")) {
			dm.dict.clear();
			return true;
		}
		else if (a[0].equals("-sd")) {
			gu.fileoutSNIAHM(dm.dict, pathn+fn+".Pgstalt.dict");
			return true;
		}
		else if (a[0].equals("-near")) {
			for (NodeInt x: GstaltUtil.Ndict(G,kg,dm.dict,a[1],cvd))
			return true;
		}
		return false;
	}*/

}
