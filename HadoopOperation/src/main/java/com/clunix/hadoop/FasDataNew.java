package com.clunix.hadoop;

import java.io.IOException;
import java.util.HashSet;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class FasDataNew extends Configured implements Tool
{
	public static class FasDataNew_Mapper extends Mapper<Object, Text, Text, LongWritable> {
		private final static LongWritable one = new LongWritable(1);

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			Text key1 = new Text();
			String line = value.toString();
			String m[] = line.split(" ");
			
			for (int i=0;i<m.length;i++) {
				String m1 = m[i];
				HashSet <String> tested = new HashSet <String> (); 
				for (int j=i+1;j<m.length;j++) {
					String m2 = m[j]; 
					if (m1.equals(m2) || tested.contains(m2)) continue;
					else tested.add(m2);
					String key2 = m1 + " " + m2;
					key1.set(key2);
					context.write(key1, one);
				}
			}	
		}
	}
	
	public static class FasDataNew_Reducer extends Reducer<Text, LongWritable, Text, LongWritable> {
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
		int res = ToolRunner.run(new Configuration(), new FasDataNew(), args);
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
		Job job = Job.getInstance(conf, "Fas Data New get");
		job.setJarByClass(FasDataNew.class);

		job.setMapperClass(FasDataNew_Mapper.class);
		job.setReducerClass(FasDataNew_Reducer.class);

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
