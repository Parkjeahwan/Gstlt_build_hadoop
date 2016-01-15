package com.clunix;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

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

public class FasTopValue extends Configured implements Tool {
	public static class FasTopValueMapper extends Mapper<Object, Text, Text, Text> {
		HashMap<Integer, Integer> morpheme_num;
		HashMap<String, PatternInfo> pattern_table;
		int corpus_linenum;
		
		public FasTopValueMapper(){
			BufferedReader corpusFile;
			BufferedReader pattenFile;
			String line;
			try {
				FileSystem hdfs = FileSystem.get(new Configuration());
				Path homeDir = hdfs.getHomeDirectory();
				Path path1 = new Path(homeDir + "/corpus_mecab_pr.txt");
				Path path2 = new Path(homeDir + "/mecab_morpheme3_top_10000.txt");
				corpusFile = new BufferedReader(new InputStreamReader(hdfs.open(path1)));
				pattenFile = new BufferedReader(new InputStreamReader(hdfs.open(path2)));
				
				int linenumber = 1;
				morpheme_num = new HashMap<Integer, Integer>();
				pattern_table = new HashMap<String, PatternInfo>();
				
				while ((line = corpusFile.readLine()) != null) {
					String[] infos = line.split(" ");
					morpheme_num.put(linenumber, infos.length);
					linenumber++;
				}
				
				corpus_linenum = linenumber - 1;
				
				while ((line = pattenFile.readLine()) != null) {
					String[] infos = line.split("\t");
					PatternInfo input = new PatternInfo();
					
					//input.setInfo(Integer.parseInt(infos[1]), infos[2]);
					
					int count = Integer.parseInt(infos[1]);
					String loc = infos[2];
					input.count = count;
					input.locs = new int[count][2];

					String[] l = loc.split(",");
					for (int i = 0; i < l.length; i++) {
						String[] infos0 = l[i].split("/");

						input.locs[i][0] = Integer.parseInt(infos0[0]);
						input.locs[i][1] = Integer.parseInt(infos0[1]);
					}
					input.locs_length = l.length;
					
					
					pattern_table.put(infos[0], input);	
				}
			}	catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			Text word = new Text();
			Text result = new Text();
			double fas;
			String s;
			
			String line = value.toString();
			String[] infos = line.split("\t");
			
			PatternInfo key1 = pattern_table.get(infos[0]);
			Iterator<String> itr = pattern_table.keySet().iterator();
			while (itr.hasNext()) {
				String k = itr.next();
				if(!k.equals(infos[0])){
					PatternInfo key2 = pattern_table.get(k);
					fas = computationX(key1, key2) / computationY(key1, key2);
					if (fas >= 1.3) {
						word.set(infos[0]+" "+k);
						s = String.format("%s\t%s\t%.2f", infos[0], k, fas);
						result.set(s);
						context.write(word, result);
					}
				}
			}
		}
		
		public double computationX(PatternInfo key1, PatternInfo key2) {
			double ret = 0;
			int index1 = 0, index2 = 0;
			int val1, val2;
			int lastIndex1 = key1.locs_length - 1;
			int lastIndex2 = key2.locs_length - 1;
			
			while(true){
				val1 = key1.locs[index1][0];
				val2 = key2.locs[index2][0];
				
				if (val1 > val2) {
					if (index1 >= lastIndex1) break;
					else index1++;
				} else if (val1 > val2) {
					if (index2 >= lastIndex2) break;
					else index2++;
				} else {
					int offset1 = key1.locs[index1][1];
					int offset2 = key2.locs[index2][1];
					if (offset1 < offset2) {
						ret += 1.0 / Math.pow(offset2 - offset1, 1.3);
					}
					if (index1 < lastIndex1) index1++;
					else if (index2 < lastIndex2) index2++;
				}
				
				if (index1 >= lastIndex1 && index2 >= lastIndex2) break;
			}
			
			return ret * (double)corpus_linenum;
		}
		
		public double computationY(PatternInfo key1, PatternInfo key2) {
			double sum1 = 0;
			double sum2 = key2.locs_length;
			
			for (int i = 0; i < key1.locs_length; i++) {
				int rowNum = key1.locs[i][0];
				int len = morpheme_num.get(rowNum);
				sum1 += Math.pow(2.0 / len, 1.3);
			}
			
			return sum1 * sum2;
		}
		
		public class PatternInfo {
			int count;
			int[][] locs;
			int locs_length;
			
/*			public void setInfo(int count, String loc) {
				this.count = count;
				this.locs = new int[count][2];

				String[] l = loc.split(",");
				for (int i = 0; i < l.length; i++) {
					String[] infos = l[i].split("/");

					locs[i][0] = Integer.parseInt(infos[0]);
					locs[i][1] = Integer.parseInt(infos[1]);
				}
				this.locs_length = l.length;
			}
*/		}
	}
	
	public static class FasTopValueCombiner extends Reducer<Text, Text, Text, Text> {
		Text result = new Text();
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			for (Text val : values) {
				result.set(val.toString());
				context.write(key, result);
			}
		}
	}
	
	public static class FasTopValueReducer extends Reducer<Text, Text, Text, Text> {
		Text result = new Text();
		public void reduce(Text key, Iterable<Text> values, Context context)
				throws IOException, InterruptedException {
			for (Text val : values) {
				result.set(val.toString());
				context.write(key, result);
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new FasTopValue(), args);
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
		job.setJarByClass(FasTopValue.class);
		job.setCombinerClass(FasTopValueCombiner.class);
		job.setMapperClass(FasTopValueMapper.class);
		job.setReducerClass(FasTopValueReducer.class);
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
