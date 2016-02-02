package com.clunix.NLP.graph;

import java.util.HashMap;
import java.util.HashSet;

public class svector {
	public int ID;
	public HashSet <Integer> SUCCV;
	public HashSet <Integer> PREVV;
	
	public svector(Node x,SGraph sg,PGraph pg) {
		EdgeSet es = sg.SUCC.get(x.ID);
		for (Node n:es.sortedI) 
			for (Node y:pg.node) if (n.content.equals(y.content)) SUCCV.add(n.ID);
		es =  sg.PREV.get(x.ID);
		for (Node n:es.sortedI) 
			for (Node y:pg.node) if (n.content.equals(y.content)) PREVV.add(n.ID);
		ID = x.ID;
	}
	public double lh(svector y) {
		int i,j;i=j=0;
		float xsum,ysum; xsum = ysum = 0;
//		for (int i=0;i<x.) {
			
	//	}
	return 0.1;	}

}

 