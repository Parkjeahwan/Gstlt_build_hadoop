package com.ai.gestalt;

import java.util.HashMap;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class SeqSem { // (pattern)Sequence-Semanteme pair를 저장하는 클래스 
	public Sequence seq; 	// original pattern 시퀀스
	public Semanteme sem;	// internal representation
	
	public SeqSem() { }
	public SeqSem(String a, Semanteme b) { seq = new Sequence(a); sem = b; }
	public SeqSem(Sequence a, Semanteme b) { seq = a; sem = b; }
	public SeqSem(Semanteme a, Semanteme b) { seq = new Sequence(a.label); sem = b; }
	public SeqSem(String a, String b, HashMap<String,Semanteme>m) { seq = new Sequence(a); sem = m.get(b); }
	
/*	public int compareTo(SeqSem a) {
		if (this.power < a.power) {
			return 1;
		} else if (this.power == a.power) {
			return 0;
		} else {
			return -1;
		}
	}*/
	@Override
	public boolean equals(Object b) {
		SeqSem a = (SeqSem) b;
		return seq.equals(a.seq) && sem.label.equals(a.sem.label);
	}
	@Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            append(seq.body).append(sem.label).toHashCode();
    }
}
