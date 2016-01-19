package com.clunix;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PGraph {
	public ArrayList <Node> node;
	public HashMap <String,Node> G;
	public ArrayList <EdgeSet> PREVP; 
	public ArrayList <EdgeSet> SUCCP;
	
	public PGraph(){}
	
	public void fileout(String fn) throws IOException {
		BufferedWriter filen = new BufferedWriter(new FileWriter(fn+".node"));
		BufferedWriter fileP = new BufferedWriter(new FileWriter(fn+".PREVP"));
		BufferedWriter fileS = new BufferedWriter(new FileWriter(fn+".SUCCP"));
		filen.write(node.size()+"\n");
		for (Node x:node) x.fileout(filen); 
		filen.close();
		fileP.write(PREVP.size()+"\n");
		for (EdgeSet e:PREVP) e.fileout(fileP); 
		fileP.close();
		fileS.write(SUCCP.size()+"\n");
		for (EdgeSet e:SUCCP) e.fileout(fileS); 
		fileS.close();
	}
	
	public void filein(String fn) throws IOException{
		BufferedReader filen = new BufferedReader(new FileReader(fn+".node"));
		BufferedReader fileP = new BufferedReader(new FileReader(fn+".PREVP"));
		BufferedReader fileS = new BufferedReader(new FileReader(fn+".SUCCP"));
		String str = filen.readLine();
		int size = 0;
		node = new ArrayList <Node> (size = Integer.parseInt(str));
		for (int i=0;i<size;i++) node.add(new Node());
		for (Node x:node) x.filein(filen,node); filen.close();
		int i = 0;
		str = fileP.readLine();
		PREVP = new ArrayList<EdgeSet>(size); 
		for (i=0;i<size;i++) PREVP.add(new EdgeSet(node.get(i)));
		for (EdgeSet e:PREVP) {
			e.filein(fileP,node); 
		}
		fileP.close();
		str = fileS.readLine();
		SUCCP = new ArrayList<EdgeSet>(size); 
		for (i=0;i<size;i++) SUCCP.add(new EdgeSet(node.get(i)));
		for (EdgeSet e:SUCCP) e.filein(fileS,node); fileS.close();		
	}
}
