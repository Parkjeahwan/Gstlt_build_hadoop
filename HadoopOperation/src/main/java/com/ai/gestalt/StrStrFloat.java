package com.ai.gestalt;

import com.clunix.NLP.graph.Node;

public class StrStrFloat implements Comparable <StrStrFloat> {
	public String s1;
	public String s2;
	public float power;
	
	public StrStrFloat() { }
	public StrStrFloat(String a, String b, float p) { s1 = a; s2 = b; power = p;}
	
	public int compareTo(StrStrFloat a) {

		if (this.power < a.power) {
			return 1;
		} else if (this.power == a.power) {
			return 0;
		} else {
			return -1;
		}
	}
	@Override
	public boolean equals(Object b) {
		StrStrFloat a = (StrStrFloat) b;
		return s1 == a.s1 && s2 == a.s2 && power == a.power;
	}
}
