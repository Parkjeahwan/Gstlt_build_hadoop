package com.clunix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
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
import com.clunix.NLP.graph.SGraph;

public class Gstlt_build extends Configured implements Tool {
	public static class Gstlt_build_Mapper extends Mapper<Object, Text, Text, Text> {
		HashMap<String, Integer> vlnoun_data = new HashMap<String, Integer>();
		int d = 0;

		public Gstlt_build_Mapper(){
			FileSystem hdfs;
			try {
				hdfs = FileSystem.get(new Configuration());
				Path homeDir = hdfs.getHomeDirectory();
				Path path = new Path(homeDir + "/mecab_vlnoun.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(hdfs.open(path)));

				String line;
				while ((line = br.readLine()) != null) {
					vlnoun_data.put(line, 1);
				}
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			Text key1 = new Text();

			String line = value.toString();
			String infos[] = line.split("\t");

			key1.set(infos[1]);
			
			SGraph g = Gstlt_utils.buildGstaltMAX(infos[0], vlnoun_data);
			FileSystem hdfs;
			hdfs = FileSystem.get(new Configuration());
			Path homeDir = hdfs.getHomeDirectory();

			Path path = new Path(homeDir + "/output/" + key.toString() + ".node");
			BufferedWriter filen = new BufferedWriter(new OutputStreamWriter(hdfs.create(path,true)));
			path = new Path(homeDir + "/output/" + key.toString() + ".PREV");
			BufferedWriter fileP = new BufferedWriter(new OutputStreamWriter(hdfs.create(path,true)));
			path = new Path(homeDir + "/output/" + key.toString() + ".SUCC");
			BufferedWriter fileS = new BufferedWriter(new OutputStreamWriter(hdfs.create(path,true)));
			path = new Path(homeDir + "/output/" + key.toString() + ".EQUI");
			BufferedWriter fileE = new BufferedWriter(new OutputStreamWriter(hdfs.create(path,true)));
			filen.write(g.node.size()+"\n");
			int i = 0;
			for (Node x:g.node) { if (i++ %10000 == 0) System.out.println(i-1 + ":" + x.content); x.fileout(filen);} 
			filen.close();
			fileP.write(g.PREV.size()+"\n");
			for (EdgeSet e:g.PREV) e.fileout(fileP); 
			fileP.close();
			fileS.write(g.SUCC.size()+"\n");
			for (EdgeSet e:g.SUCC) e.fileout(fileS); 
			fileS.close();
			fileE.write(g.EQUI.size()+"\n");
			for (EdgeSet e:g.EQUI) e.fileout(fileE); 
			fileE.close();
			context.write(key1, key1);
		}
	}
	
	public static class Gstlt_build_Combiner extends Reducer<Text, Text, Text, Text> {
		Text result = new Text();
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			for (Text g :values ) {
				result.set(g.toString());
				context.write(key, result);
			}
		}
	}

	public static class Gstlt_build_Reducer extends Reducer<Text, Text, Text, Text> {
		Text result = new Text();
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			for (Text g :values ) {
				result.set(g.toString());
				context.write(key, result);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new Gstlt_build(), args);
		System.exit(res);
	}

	@Override
	public int run(String[] args) throws Exception
	{
		Configuration conf = this.getConf();
		conf.set("fs.hdfs.impl",
				org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.file.impl",
				org.apache.hadoop.fs.LocalFileSystem.class.getName());
		conf.set("mapred.textoutputformat.separator", "\t");
		Job job = Job.getInstance(conf, "Fas Top value");
		job.setJarByClass(Gstlt_build.class);
		job.setCombinerClass(Gstlt_build_Combiner.class);
		job.setMapperClass(Gstlt_build_Mapper.class);
		job.setReducerClass(Gstlt_build_Reducer.class);
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
