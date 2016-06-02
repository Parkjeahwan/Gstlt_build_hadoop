package com.clunix.NLP.sentence_analyzer;

public class StrStr {
	public String s1;
	public String s2;
	
	public StrStr() {}
	public StrStr(String a,String b) {	s1 = a; s2 = b;	}
	public boolean equals(StrStr x) { return s1==x.s1 && s2==x.s2;} 
}
