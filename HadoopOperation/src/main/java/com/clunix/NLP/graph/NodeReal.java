package com.clunix.NLP.graph;

public class NodeReal implements Comparable<NodeReal> {
		public Node n;
		public double w;


		public NodeReal(Node n1, double a){
			this.n = n1;
			this.w= a;
		}
		@Override	
		public int compareTo(NodeReal m) {

			if (this.w < m.w) {
				return 1;
			} else if (this.w == m.w) {
				return 0;
			} else {
				return -1;
			}
		}
	
}
