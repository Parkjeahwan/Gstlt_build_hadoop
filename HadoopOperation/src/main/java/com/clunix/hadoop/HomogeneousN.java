package com.clunix.hadoop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.clunix.NLP.graph.EdgeSet;
import com.clunix.NLP.graph.Node;
import com.clunix.NLP.graph.NodeInt;
import com.clunix.NLP.graph.PGraph;
import com.clunix.NLP.graph.SGraph;

public class HomogeneousN extends Configured implements Tool
{
	final private static List<String> V 	// 용언 리스트 
	= Arrays.asList("VV", "VA", "VX", "XSV", "XSA", "VCP", "VCN");
	final private static List<String> Sub //체언 리스트
	= Arrays.asList("NNG", "NNP", "NNB", "NNBC", "NR", "NP", "SN" ,"XSN" ,"SL", "ETN", "XR");

	public static class HomogeneousN_Mapper extends Mapper<Object, Object, Text, Text> {
		SGraph G = new SGraph();
		PGraph CC = new PGraph();	
		List <Node> KNL = new ArrayList <Node> ();
		public static Map <Node,ArrayList<Integer>> fvm = new HashMap <Node,ArrayList<Integer>> ();
		public static Map <Node,ArrayList<Integer>> kvm = new HashMap <Node,ArrayList<Integer>> ();
		public static Set <Node> rareN = new HashSet<Node> ();

		public HomogeneousN_Mapper(){
			try {
				G.filein("/corpus_mecab_pr.txt.PgstaltMAX");
				CC.filein("/CWgraph.txt");

				for (Node x:CC.node) 
					if (!Sub.contains(type(x.content))) continue;
					else {
						KNL.add(x);
						fvm.put(x, svector(G,x));
					}
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public static ArrayList<Integer> svector(SGraph sg,PGraph pg,Node x,int dim) { //
			boolean israreN = false;
			if (kvm.containsKey(x)) {
				if (kvm.get(x).size() < 3) {
					israreN = true;
					rareN.add(x);
				}
				else return kvm.get(x); 
			}
			ArrayList<Integer> v = new ArrayList<Integer> ();
			EdgeSet es = sg.SUCC.get(x.ID);
			if (dim == -1) dim = es.sortedI.size();
			int i=0;
			for (Node n:es.sortedI) {
				if (V.contains(lmtype(n.content)))
					if (israreN || pg.G.containsKey(n.content)) {//for (Node y:pg.node) if (n.content.equals(y.content)) 
						v.add(n.ID);

						if (++i == dim) break;
						//							 break;
					}
			}
			kvm.put(x, v);
			return v;
		}

		public static List<NodeInt> Ndict(SGraph sg, PGraph pg, HashMap<String, List<NodeInt>> dict, String nastem, int cvd, List<Node> KNL) {
			if (dict.containsKey(nastem)) return dict.get(nastem);

			Node x = sg.G.get(nastem);
			//if (!uf.lmtype(x.content).equals("NNG")) return null; 
			ArrayList<Integer> av = svector(sg,pg,x); 
			ArrayList<NodeInt> r = new ArrayList<NodeInt>();
			ArrayList<Integer> yv;
			for (Node yp:KNL) {
				if (yp.content.equals(nastem)) continue; //  주어진 단어 외의 pg 등록 단어들에 대해서
				Node y = sg.G.get(yp.content); // 해당 등록 단어의 G상 등록 노드 y를 찾아
				if (lmtype(y.content).equals("NNG") ) { // if y is NNG type
					if (rareN.contains(nastem)) 
						yv = fvm.get(y.content);
					else yv = svector(sg,pg,y);
					r.add(new NodeInt(y,commonAxisN(sg,pg,av,yv)));
				}
			}
			Collections.sort(r);
			List<NodeInt> ret;
			if (r!=null) {
				dict.put(nastem,(ret=r.subList(0, cvd)));  // cvd는 가장 비슷한 단어를 몇개나 리턴할 것인지 결정
				return ret;
			}
			else return null;
		}

		public static ArrayList <Integer> svector(SGraph sg,PGraph pg,Node x) { //
			return svector(sg,pg,x,200);
		}

		public static ArrayList<Integer> svector(SGraph sg,Node x) { //
			ArrayList<Integer> v = new ArrayList<Integer> ();
			EdgeSet es = sg.SUCC.get(x.ID);
			for (Node n:es.sortedI) {
				if (V.contains(lmtype(n.content)))
					v.add(n.ID);
			}
			kvm.put(x, v);
			return v;
		}

		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {
			Text key1 = new Text();
			Text result = new Text();

			String line = value.toString();
			String infos[] = line.split("\t");

			List<NodeInt> nl;
			ArrayList <StrInt> r = new ArrayList <StrInt>();
			HashMap<String, List<NodeInt>> dict = new HashMap<String, List<NodeInt>>();

			nl = Ndict(G, CC, dict, infos[1], 20, KNL);  // function to MAP
			if (nl != null && !nl.isEmpty()) {
				for (NodeInt i:nl) {
					if (i != null) {
						r.add(new StrInt(i.n.content,i.w)); 
					}
				}
			}
			String s;
			key1.set(infos[1]);
			s = "\t"+infos[2]+" th 체언: "+infos[1] + " /"+G.SUCC.get(Integer.valueOf(infos[0])).count.size()+" ~ ";
			for (StrInt si:r){
				s += si.str + "/" +si.num+ ", ";
			}
			result.set(s);
			context.write(key1, result);
		}
	}

	public static class HomogeneousN_Reducer extends Reducer<Text, Text, Text, Text> {
		Text result = new Text();
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			for (Text val :values ) {
				result.set(val.toString());
				context.write(key, result);
			}
		}
	}

	public static int commonAxisN(SGraph sg, PGraph pg,ArrayList <Integer >av,ArrayList <Integer >bv) {
		if (av!= null && bv != null ) 
			return commonAxisN(av,bv);
		else return 0;
	}

	public static int commonAxisN(ArrayList<Integer>a,ArrayList<Integer>b) {
		int x = 0;
		for (int ai:a) if (b.contains(ai)) x++;
		return x;
	}

	public static String lmtype(String s){	// 단어의 마지막 형태소를 리턴
		if (s != null && !s.equals("")) {
			String[]a = s.split(" ");
			return type(a[a.length-1]);
		}
		else return "";
	}

	public static String type(String str)
	{ 
		if (str != null && !str.equals("")) {
			String[] arr = str.split("/");
			if (arr.length > 1) return arr[arr.length - 1];
			else return "";
		}
		else return "";
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new HomogeneousN(), args);
		System.exit(res);
	}
	public int run(String[] args) throws Exception
	{
		Configuration conf = this.getConf();
		conf.set("fs.hdfs.impl",
				org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.file.impl",
				org.apache.hadoop.fs.LocalFileSystem.class.getName());
		conf.set("mapred.textoutputformat.separator", "\t");
		Job job = Job.getInstance(conf, "Homogeneous N");
		job.setJarByClass(HomogeneousN.class);

		job.setMapperClass(HomogeneousN_Mapper.class);
		job.setReducerClass(HomogeneousN_Reducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		Path inputPath = new Path(args[0]);
		Path outputPath = new Path(args[1]);

		FileInputFormat.addInputPath(job, inputPath);
		FileOutputFormat.setOutputPath(job, outputPath);

		// TODO Auto-generated method stub
		return job.waitForCompletion(true) ? 0 : 1;
	}
}
