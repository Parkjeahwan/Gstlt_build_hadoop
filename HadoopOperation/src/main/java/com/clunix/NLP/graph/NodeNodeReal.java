package com.clunix.NLP.graph;

public class NodeNodeReal implements Comparable<NodeNodeReal> {
	public Node n1;
	public Node n2;
	public double w;


	public NodeNodeReal(Node n1, Node n2, double a){
		this.n1 = n1;
		this.n2 = n2;
		this.w= a;
	}
	@Override	
	public int compareTo(NodeNodeReal m) {

		if (this.w < m.w) {
			return 1;
		} else if (this.w == m.w) {
			return 0;
		} else {
			return -1;
		}
	}
}
