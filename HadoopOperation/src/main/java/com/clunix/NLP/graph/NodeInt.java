package com.clunix.NLP.graph;

public class NodeInt implements Comparable<NodeInt> {
		public Node n;
		public int w;


		public NodeInt(Node n, int a){
			this.n = n;
			this.w= a;
		}
		public NodeInt(){
			this.n = null;
			this.w= 0;
		}
		
		public int compareTo(NodeInt man) {

			if (this.w < man.w) {
				return 1;
			} else if (this.w == man.w) {
				return 0;
			} else {
				return -1;
			}
		}
}
