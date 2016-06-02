package com.ai.gestalt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class Sequence {
	static final String delimiter = " ";
	public String body;
	public List <String> e;
	public Sequence() { }
	public Sequence(String a) { 
		if (a == null || a.equals("")) {a.substring(1);System.out.println("new Sequence for NULL is not allowed");System.exit(0);}
		body = a;
		e = Arrays.asList(a.split(delimiter));
	}
	public Sequence(String a, String b) { body = a+delimiter+b; e = new ArrayList <String>(); e.add(a); e.add(b);}
	public Sequence(List <String>x ) {
		body = "";
		for (String s:x) body += body.equals("")? s:" "+s;
		e = new ArrayList<String>(); 
		e.addAll(x); 
	}
	
	public int compareTo(Sequence a) {
		if (e.size() < a.e.size()) {
			return 1;
		} else if (this.e.size() == a.e.size()) {
			return 0;
		} else {
			return -1;
		}
	}
	@Override
	public boolean equals(Object b) {
		Sequence a = (Sequence) b;
		return body.equals(a.body);
	}
	@Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            append(body).toHashCode();
    }
}
