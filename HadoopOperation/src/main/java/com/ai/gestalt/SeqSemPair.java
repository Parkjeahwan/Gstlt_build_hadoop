package com.ai.gestalt;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class SeqSemPair {
	static final String delimiter = " ";
	SeqSem p1;
	SeqSem p2;
	public SeqSemPair() { }
	public SeqSemPair(SeqSem a) { p1 = p2 = a; }
	public SeqSemPair(SeqSem a, SeqSem b) { p1 = a; p2 = b;}
	public SeqSemPair(String a,Semanteme b) { p1 = new SeqSem(a,b); p2 = new SeqSem(a,b); }
	public SeqSemPair(String a,Semanteme b,String c,Semanteme d) { p1 = new SeqSem(a,b); p2 = new SeqSem(c,d); }
	public SeqSemPair(SeqSem a, Object b) { p1 = a; if (b == null) p2 = null; else System.exit(0);}
	
	@Override
	public boolean equals(Object b) {
		SeqSemPair a = (SeqSemPair) b;
		return p1.seq.body.equals(a.p1.seq.body) && p1.sem.label.equals(a.p1.sem.label) && 
				p2.seq.body.equals(a.p2.seq.body) && p2.sem.label.equals(a.p2.sem.label) ;
	}
	@Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            append(p1.seq.body).append(p2.seq.body).
            append(p1.sem.label).append(p2.sem.label).toHashCode();
    }
}
