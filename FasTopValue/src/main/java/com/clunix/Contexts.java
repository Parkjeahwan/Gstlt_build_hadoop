package com.clunix;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
// v1.1 every contexts instance has HashSet, not HashMap
public class Contexts implements Serializable{
	HashSet <Integer> map;
//	HashMap <Integer,ArrayList<Integer>> map;
	
	Boolean contains(Integer k) { return (map.contains(k))? true : false;	}
	ArrayList <Integer> intersect(HashSet <Integer> m2) {
		ArrayList <Integer> res = new ArrayList<Integer>();
		if (map.size() < m2.size()) {
			for (Integer k:map) 
				if (m2.contains(k)) 
					res.add(k);
		}
		else for (Integer kk:m2) 
			if (map.contains(kk)) 
				res.add(kk);
		return (res.size() > 0) ? res :null;
	}
	ArrayList <Integer> intersect(Contexts c2) {	return intersect(c2.map);	}
	ArrayList <Integer> intersect(ArrayList<Integer> m2) {
		ArrayList <Integer> res = new ArrayList<Integer>();
		for (Integer k:m2) if (map.contains(k)) res.add(k);
		return (res.size() > 0) ? res :null;
	}
	int intersectN(HashSet <Integer> m2) {  // returns Number of intersect of map and m2
		int res=0;
		for (Integer k:m2) if (map.contains(k)) res++;
		return res;
	}
	int intersectN(Contexts c2) {  // returns Number of intersect of map and m2
		return intersectN(c2.map);
	}
	int intersectN(ArrayList<Integer> m2) {
		int res=0;
		for (Integer k:m2) if (map.contains(k)) 
			res++;
		return res;
	}
	public Contexts() {
		map = new HashSet <Integer>();
	}
	public Contexts(int doc) {
		if (map == null) {
			map = new HashSet<Integer>();
			map.add(doc);
		}
		else map.add(doc);
		if (!map.contains(doc)) map.add(doc);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof Contexts) {
			Contexts target = (Contexts) obj;
			return (target.map.equals(map));
		}
		return super.equals(obj);
	}	
	
	/*@Override
    public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(size);
            out.writeObject(map);
            
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            size = in.readInt();
            map = (HashSet<Integer>)in.readObject();
            
    } */
}
// v1.0 - every contexts instance has <location, offsets> map
/*
public class Contexts implements Serializable{
	int size;
	HashMap <Integer,ArrayList<Integer>> map;
	
	Boolean contains(Integer k) { return (map.containsKey(k))? true : false;	}
	ArrayList <Integer> intersect(HashMap <Integer,List<Integer>> m2) {
		ArrayList <Integer> res = new ArrayList<Integer>();
		for (Integer k:map.keySet()) if (m2.containsKey(k)) res.add(k);
		return (res.size() > 0) ? res :null;
	}
	ArrayList <Integer> intersect(ArrayList<Integer> m2) {
		ArrayList <Integer> res = new ArrayList<Integer>();
		for (Integer k:map.keySet()) if (m2.contains(k)) res.add(k);
		return (res.size() > 0) ? res :null;
	}
	int intersectN(HashMap <Integer,List<Integer>> m2) {
		int res=0;
		for (Integer k:map.keySet()) if (m2.containsKey(k)) res++;
		return res;
	}
	int intersectN(ArrayList<Integer> m2) {
		int res=0;
		for (Integer k:map.keySet()) if (m2.contains(k)) res++;
		return res;
	}
	public Contexts() {
		size = 0;
		map = new HashMap <Integer,ArrayList<Integer>>();
	}
	public Contexts(int doc,int offset) {
		if (map == null) {
			ArrayList <Integer> l = new ArrayList <Integer>();
			l.add(offset);
			map = new HashMap<Integer,ArrayList<Integer>>();
			map.put(doc, l);
		}
		if (map.containsKey(doc)) map.get(doc).add(offset);
		else {
			ArrayList<Integer> x = new ArrayList<Integer>();
			x.add(offset);
			map.put(doc, x);
		}
	}
}
*/
