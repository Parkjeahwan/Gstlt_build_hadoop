package com.hadoop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class XFasDataNew3 extends Configured implements Tool
{
	public static class NFasDataNew_Mapper extends Mapper<Object, Text, Text, LongWritable> {
		private final static LongWritable one = new LongWritable(1);
		HashMap<String, Integer> appear = new HashMap<String, Integer>();

		public NFasDataNew_Mapper(){
			FileSystem hdfs;
			try {
				hdfs = FileSystem.get(new Configuration());
				Path homeDir = hdfs.getHomeDirectory();
				Path path = new Path(homeDir + "/count_morpheme4.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(hdfs.open(path)));

				String line;
				System.out.println("start input data");
				while ((line = br.readLine()) != null) {
					String arr[] = line.split("\t");
					appear.put(arr[0], Integer.valueOf(arr[1]));
				}
				br.close();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			Text key1 = new Text();
			String line = value.toString();
			String m[] = line.split("##SP");

			for(String word:m){
				word = word.trim();
				int count = 0;
				if(appear.containsKey(word)){
					count = appear.get(word);
				} else {	
					continue;
				}
				if (count == 1) {
					key1.set("morpheme1");
					context.write(key1, one);
				} else if (count == 2) {
					key1.set("morpheme2");
					context.write(key1, one);
				} else if (count == 3) {
					key1.set("morpheme3");
					context.write(key1, one);
				} else if (count == 4) {
					key1.set("morpheme4");
					context.write(key1, one);
				} else if (count == 5) {
					key1.set("morpheme5");
					context.write(key1, one);
				} else if (count == 6) {
					key1.set("morpheme6");
					context.write(key1, one);
				} else if (count == 7) {
					key1.set("morpheme7");
					context.write(key1, one);
				} else if (count == 8) {
					key1.set("morpheme8");
					context.write(key1, one);
				} else if (count == 9) {
					key1.set("morpheme9");
					context.write(key1, one);
				} else if (count == 10) {
					key1.set("morpheme10");
					context.write(key1, one);
				} else {
					key1.set("morpheme11");
					context.write(key1, one);
				}
			}
			
		}
	}
	
	public static class NFasDataNew_Combiner extends Reducer<Text, LongWritable, Text, LongWritable> {
		private LongWritable result = new LongWritable();
		public void reduce(Text key, Iterable<LongWritable> values, Context context)
				throws IOException, InterruptedException {
			long sum = 0;
			for (LongWritable val :values ) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	public static class NFasDataNew_Reducer extends Reducer<Text, LongWritable, Text, LongWritable> {
		private LongWritable result = new LongWritable();

		public void reduce(Text key, Iterable<LongWritable> values, Context context)
				throws IOException, InterruptedException {
			long sum = 0;
			for (LongWritable val :values ) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new XFasDataNew3(), args);
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
		conf.set("dfs.replication", "1");
		Job job = Job.getInstance(conf, "Fas Data New get");
		job.getConfiguration().setBoolean("mapred.output.compress", true);
		job.getConfiguration().setClass("mapred.output.compression.codec", GzipCodec.class, CompressionCodec.class);
		job.setJarByClass(XFasDataNew3.class);

		job.setMapperClass(NFasDataNew_Mapper.class);
		job.setCombinerClass(NFasDataNew_Combiner.class);
		job.setReducerClass(NFasDataNew_Reducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);

		Path inputPath = new Path(args[0]);
		Path outputPath = new Path(args[1]);

		FileInputFormat.addInputPath(job, inputPath);
		FileOutputFormat.setOutputPath(job, outputPath);

		// TODO Auto-generated method stub
		return job.waitForCompletion(true) ? 0 : 1;
	}
}
