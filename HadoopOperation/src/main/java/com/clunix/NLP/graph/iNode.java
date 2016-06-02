package com.clunix.NLP.graph;

import java.util.ArrayList;
import java.util.List;

public class iNode {
	public String label = null;;
	public iNode head = null;
	public iNode tail = null;
	public iNode prev = null;
	public iNode succ = null;
	public float power = 0;
	public int count = 0;
	public List<iNode> element = null;
	
	public iNode() {	}
	public iNode(String a,List<iNode> e) { 
		label = a;	element = new ArrayList<iNode>();
		for (iNode x:e) element.add(new iNode(x));
		power = 1;count = 1;}
	public iNode(String a) { label = a;	power = 1;count = 1;}
	public iNode(String a, float p) { label = a;	power = p;count = 1;}
	public iNode(Node a) { label = a.content;	power = 1; count = a.count; }
	public iNode(String a, iNode h, iNode t, iNode p, iNode s, float po, int c) { 
		label = a;
		head = h;
		tail = t;
		prev = p;
		succ = s;
		power = po;
		count = c;
	}
	public iNode(iNode e) { 
		label = e.label;
		head = e.head;
		tail = e.tail;
		prev = e.prev;
		succ = e.succ;
		power = e.power;
		count = e.count;
		element = new ArrayList<iNode>();
		element.addAll(e.element);
	}
	public iNode(String x,List<iNode>e,float p) { 
		label = x;
		power = p;
		if (e != null) {
			element = new ArrayList<iNode>();
			element.addAll(e);
		}else element = null;
	}
	public void printc() {
		if (head != null) {
//			System.out.print("["+label+" = ");
			System.out.print("[");
			head.printc();
			System.out.print("+");
			tail.printc();
			System.out.print(","+power);
			System.out.print("]");
		}
		else if (element != null && !element.isEmpty()) {
			int i = 0;
			for (iNode x:element) {
				if (i++ > 0) System.out.print("+");
				else {
//					System.out.print("["+label+" = ");
					System.out.print("[");
				}
				x.printc();
			}		
				System.out.print("]");
		}
		else { 
			System.out.print("[");
			System.out.print(label);
			System.out.print("]");
		}
	}
	public String sprintc() {
		String ret = "";
		if (head != null) {
//			ret += "["+label+" = ");
			ret += "[";
			ret += head.sprintc();
			ret += "+";
			ret += tail.sprintc();
			ret += ","+power;
			ret += "]";
		}
		else if (element != null && !element.isEmpty()) {
			int i = 0;
			for (iNode x:element) {
				if (i++ > 0) ret += "+";
				else {
//					ret += "["+label+" = ");
					ret += "[";
				}
				ret +=  x.sprintc();
			}		
				ret += "]";
		}
		else { 
			ret += "[";
			ret += label;
			ret += "]";
		}
		return ret;
	}
	public void print() {
		if (head != null) {
//			System.out.print("["+label+" = ");
			System.out.print("[");
			head.print();
			System.out.print("+");
			tail.print();
			System.out.print(","+power);
			System.out.print("]");
		}
		else if (element != null && !element.isEmpty()) {
			int i = 0;
			for (iNode x:element) {
				if (i++ > 0) System.out.print("+");
				else {
					System.out.print("["+label+" = ");
//					System.out.print("[");
				}
				x.print();
			}		
				System.out.print("]");
		}
		else { 
			System.out.print("[");
			System.out.print(label);
			System.out.print("]");
		}
	}
}
