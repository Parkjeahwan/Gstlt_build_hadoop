package com.clunix.hadoop;

import org.anarres.lzo.hadoop.codec.LzoCodec;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

public class MorphemeCount extends Configured implements Tool
{
	public static class NFasDataNew_Mapper extends Mapper<Object, Text, Text, LongWritable> {
		private final static LongWritable one = new LongWritable(1);

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			//context.getTaskAttemptID().getTaskID().getId();
			Text key1 = new Text();
			String line = value.toString();
			String m[] = line.split(" ");

			int u = 2;
			for (int k0=1;k0<=u;k0++) {
				for (int i=0;i<m.length-k0+1;i++) {
					String m1 = "";
					for (int ii=i;ii<i+k0;ii++) m1 += m1.equals("")? m[ii] : " "+m[ii];
					key1.set(m1);
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
		int res = ToolRunner.run(new Configuration(), new MorphemeCount(), args);
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
		job.getConfiguration().setClass("mapred.output.compression.codec", LzoCodec.class, CompressionCodec.class);
		job.setJarByClass(MorphemeCount.class);

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
