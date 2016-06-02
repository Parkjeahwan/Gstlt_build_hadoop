package com.ai.gestalt;

import org.apache.commons.lang.builder.HashCodeBuilder;

import com.ai.gestalt.Semanteme;

public class StrInt implements Comparable<StrInt>{
	public String str;
	public int num;
	public StrInt() {}
	public StrInt(String a,int n) {str = a; num = n;	}

	@Override
	public int compareTo(StrInt man) {		return (man != null)? man.num - num :-1;}
	
	
	@Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
            // if deriving: appendSuper(super.hashCode()).
            append(str).append(num).toHashCode();
    }
	
	@Override
	public boolean equals(Object b) {
//		if (b == null) return false;
		StrInt a = (StrInt) b;
		return str.equals(a.str) && num == a.num;
	}
/*
	public boolean equals(String s) { 	return str.equals(s);	}
	public boolean equals(int x) {		return num == x;	}
	public boolean equals(StrInt x) {		return num == x.num && str.equals(x.str);	}
	public int compareTo(StrInt a) {
		if (num < a.num) {
			return 1;
		} else if (this.num== a.num) {
			return 0;
		} else {
			return -1;
		}
	}
*/
}
