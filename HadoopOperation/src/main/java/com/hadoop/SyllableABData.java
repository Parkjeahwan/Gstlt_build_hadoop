package com.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
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

import java.io.IOException;
import java.util.HashSet;

public class SyllableABData extends Configured implements Tool
{
	public static class NFasDataNew_Mapper extends Mapper<Object, Text, Text, LongWritable> {
		private final static LongWritable one = new LongWritable(1);

		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			Text key1 = new Text();
			String line = value.toString();
			line.replace(" ", "");
			String m[] = line.split("");

			int u = 3;
			for (int k0=1;k0<=u;k0++) {
				HashSet <String> checkm1 = new HashSet <String> ();
				for (int i=0;i<m.length-k0+1;i++) {
					HashSet <String> checkm2 = new HashSet <String> ();
					String m1 = "";
					for (int ii=i;ii<i+k0;ii++) m1 += m1.equals("")? m[ii] : " "+m[ii];

					if (checkm1.contains(m1)) continue;
					else checkm1.add(m1);

					for (int k1=1;k1<=u;k1++) {

						for (int j=i+k0;j<m.length-k1+1;j++) {
							String m2 = "";
							for (int jj=j;jj<j+k1;jj++) m2 += m2.equals("")? m[jj]:" "+m[jj];

							if (m1.equals(m2) || checkm2.contains(m2)) continue;
							else checkm2.add(m2);

							String keys = m1 + " ##SP " + m2;
							key1.set(keys);
							context.write(key1, one);
						}
					}
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
		int res = ToolRunner.run(new Configuration(), new SyllableABData(), args);
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
		job.setJarByClass(SyllableABData.class);

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
