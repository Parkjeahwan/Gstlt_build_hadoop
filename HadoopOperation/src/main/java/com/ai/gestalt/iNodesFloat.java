package com.ai.gestalt;

import java.util.ArrayList;
import java.util.List;

import com.clunix.NLP.graph.iNode;

public class iNodesFloat implements Comparable <iNodesFloat> {
	public List<iNode> list;
	public float power;
	
	public iNodesFloat() { }
	
	public iNodesFloat(ArrayList<iNode> t0, float v) { 
		list = list == null? new ArrayList<iNode>():list;
		if (t0 != null) list.addAll(t0); 
		else list = null; 
		power = v;
	}
	public iNodesFloat(iNodesFloat x) { list.addAll(x.list); power = x.power; }

	@Override
	public int compareTo(iNodesFloat a) {

		if (this.power < a.power) {
			return 1;
		} else if (this.power == a.power) {
			return 0;
		} else {
			return -1;
		}
	}
	@Override
	public boolean equals(Object b) {
		iNodesFloat a = (iNodesFloat) b;
		return list.equals(a.list) && power == a.power;
	}

}