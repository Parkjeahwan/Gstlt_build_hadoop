package com.hadoop;

public class StrInt implements Comparable<StrInt>{
	public String str;
	public int num;
	public StrInt() {}
	public StrInt(String a,int n) {str = a; num = n;	}
	public boolean equals(String s) { 	return str.equals(s);	}
	public boolean equals(int x) {		return num == x;	}
	public boolean equals(StrInt x) {		return num == x.num && str.equals(x.str);	}
	public int compareTo(StrInt man) {		return (man != null)? man.num - num :-1;}
}
