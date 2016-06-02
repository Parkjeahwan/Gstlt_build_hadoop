package com.ai.gestalt;
import java.util.ArrayList;
import java.util.List;

import com.clunix.NLP.sentence_analyzer.StrStr;
public class StrStrPair {
	static final String delimiter = " ";
	StrStr p1;
	StrStr p2;
	public StrStrPair() { }
	public StrStrPair(StrStr a) { p1 = p2 = a; }
	public StrStrPair(StrStr a, StrStr b) { p1 = a; p2 = b;}
	public StrStrPair(String a) { p1 = new StrStr(a,a); p2 = new StrStr(a,a); }
	public StrStrPair(String a,String b) { p1 = new StrStr(a,b); p2 = new StrStr(a,b); }
	public StrStrPair(String a,String b,String c,String d) { p1 = new StrStr(a,b); p2 = new StrStr(c,d); }
	
	@Override
	public boolean equals(Object b) {
		StrStrPair a = (StrStrPair) b;
		return p1.s1.equals(p2.s1) && p1.s2.equals(p2.s2);
	}

/*	public int compareTo(StrStrPair a) {
		if (e.size() < a.e.size()) {
			return 1;
		} else if (this.e.size() == a.e.size()) {
			return 0;
		} else {
			return -1;
		}
	}*/
}
