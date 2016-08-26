package com.hadoop;

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

import java.io.IOException;
import java.util.*;

/**
 * Created by parkjh on 16. 6. 9.
 */
public class MecabMorphemeWinsize3 extends Configured implements Tool {
    public static class MecabMorphemeRelBackMapper extends
            Mapper<Object, Text, Text, Text> {
        public static String[] tag = {"SF", "SE", "SSC", "SC", "SSO"};

        public boolean checkMorp(String morp) {
            String[] words = morp.split("/");

            if (words.length == 2) {
                for (int i=0; i<tag.length; i++) {
                    if (words[1].equals(tag[i]))
                        return true;
                }

                if (words[0].contains("!") || words[0].contains("?") ||
                        words[0].contains(",") || words[0].contains("\"") ||
                        words[0].contains("'") || words[0].contains("-") ||
                        words[0].contains("(") || words[0].contains(")") ||
                        words[0].contains("~"))
                    return true;
            } else
                return true;

            return false;
        }

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            Text word = new Text();
            Text result = new Text();

            HashMap<String, Integer> connList = new HashMap<String, Integer>();
            HashMap<String, TreeMap<String, Integer>> connLoc = new HashMap<String, TreeMap<String, Integer>>();
            HashMap<String, ArrayList<String>> connLocList = new HashMap<String, ArrayList<String>>();

            String line = value.toString();
            String[] infos = line.split("\t");
            String[] words = infos[0].split(" ");

            int lineNum = Integer.parseInt(infos[1]);
            int startIdx = 0;
            int secIdx = 0;
            int findSpace = 0;
            int spaceCnt = 0;
            int limitCount = 3;

            for (int i=0; i<words.length; i++) {
                if (words[i].equals("###/SPACE")) {
                    findSpace++;
                }

                if (findSpace == limitCount || i == words.length - 1) {
                    String morpstr = null;
                    boolean isFirst = true;

                    for (int j=startIdx; j<i; j++) {
                        if (words[j].equals("###/SPACE")) {
                            if (isFirst) {
                                isFirst = false;
                                secIdx = j+1;
                            }
                            continue;
                        }

                        if (morpstr == null)
                            morpstr = words[j];
                        else
                            morpstr = morpstr + " " + words[j];
                    }

                    if (i == words.length-1) {
                        if (morpstr == null)
                            morpstr = words[i];
                        else
                            morpstr = morpstr + " " + words[i];
                    }

                    String[] morps = morpstr.split(" ");
                    int windowSize = morps.length;

                    for (int l=1; l<=30; l++) {
                        for (int k=0; k<windowSize - (l-1); k++) {
                            boolean failed = false;
                            String str = null;

                            for (int j=0; j<l; j++) {
                                failed = checkMorp(morps[j+k]);

                                if (failed)
                                    break;

                                if (str == null)
                                    str = morps[j+k];
                                else
                                    str = str + " " + morps[j+k];
                            }

                            if (failed)
                                continue;

                            if (!connList.containsKey(str)) {
                                connList.put(str, 1);
                                TreeMap<String, Integer> connMap = new TreeMap<String, Integer>();
                                ArrayList<String> locList = new ArrayList<String>();

                                connMap.put(lineNum + "/" + (k +startIdx-spaceCnt), 1);
                                locList.add(lineNum + "/" + (k +startIdx-spaceCnt));
                                connLoc.put(str, connMap);
                                connLocList.put(str, locList);
                            } else {
                                TreeMap<String, Integer> connMap = connLoc.get(str);
                                ArrayList<String> locList = connLocList.get(str);
                                String loc = lineNum + "/" + (k + startIdx - spaceCnt);

                                if (!connMap.containsKey(loc)) {
                                    //connList.put(str, connList.get(str) + 1);
                                    connList.put(str, 1);
                                    connMap.put(loc, 1);
                                    locList.add(loc);
                                    connLoc.put(str, connMap);
                                    connLocList.put(str, locList);
                                }
                            }
                        }
                    }

                    startIdx = secIdx;
                    findSpace--;
                    spaceCnt++;
                }
            }

            Iterator<String> itr = connList.keySet().iterator();

            while (itr.hasNext()) {
                String k = itr.next();
                ArrayList<String> locList = connLocList.get(k);

                word.set(k);

                for (String loc : locList) {
                    result.set(loc);
                    context.write(word, result);
                }
            }
        }
    }

    public static class MecabMorphemeWinsize3Combiner extends
            Reducer<Text, Text, Text, Text> {
        Text result = new Text();

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            HashMap<String, Integer> list = new HashMap<String, Integer>();
            for (Text val : values) {
                String s = val.toString();

                if (!list.containsKey(s)) {
                    list.put(s, 1);
                } else
                    list.put(s, list.get(s) + 1);
            }

            Iterator<String> itr = list.keySet().iterator();

            while (itr.hasNext()) {
                String k = itr.next();

                result.set(k + "\t" + list.get(k));
                context.write(key, result);
            }
        }
    }

    public static class MecabMorphemeWinsize3Reducer extends
            Reducer<Text, Text, Text, Text> {

        public static class ContinuumInfo implements Comparator<Object> {
            private String name;
            private int count;

            public ContinuumInfo() {
                super();
            }

            public ContinuumInfo(String name, int count) {
                super();
                this.name = name;
                this.count = count;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }

            @Override
            public int compare(Object o1, Object o2) {
                int num;

                num = ((ContinuumInfo)o1).getCount() - ((ContinuumInfo)o2).getCount();

                if (num == 0)
                    num = ((ContinuumInfo)o1).getName().length() - ((ContinuumInfo)o2).getName().length();

                return num;
            }

            @Override
            public String toString() {
                return name + "\t" + count;
            }
        }

        @SuppressWarnings("unchecked")
        public ArrayList<ContinuumInfo> sortByCount(HashMap<String, Integer> list){
            ArrayList<ContinuumInfo> resList = new ArrayList<ContinuumInfo>();

            Iterator<String> itr = list.keySet().iterator();

            while (itr.hasNext()) {
                String k = itr.next();

                resList.add(new ContinuumInfo(k, list.get(k)));
            }

            Collections.sort(resList, new ContinuumInfo());
            Collections.reverse(resList);

            return resList;
        }

        Text result = new Text();

        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            //int cnt = 0;
            //ArrayList<String> list = new ArrayList<String>();

            String valueMerge = "";
            for (Text val : values) {
                //list.add(val.toString());
                //cnt++;
                if(valueMerge == "") {
                    valueMerge += val.toString();
                } else {
                    valueMerge += "," + val.toString();
                }
            }
            result.set(valueMerge);
            context.write(key, result);

            //if (cnt < 3)
            //	return;

            /*for (String s : list){
                result.set(s);
                context.write(key, result);
            }*/

			/*
			HashMap<String, Integer> list = new HashMap<String, Integer>();

			for (Text val : values) {
				String s = val.toString();
				String[] infos = s.split("\t");

				if (!list.containsKey(infos[0]))
					list.put(infos[0], Integer.parseInt(infos[1]));
				else
					list.put(infos[0], list.get(infos[0]) + Integer.parseInt(infos[1]));
			}

			ArrayList<ContinuumInfo> flist = sortByCount(list);

			for (int i=0; i<flist.size(); i++) {
				ContinuumInfo ci = flist.get(i);
				if (ci == null)
					continue;
				if (i >= 30)
					break;

				result.set(ci.toString());
				context.write(key, result);
			}
			*/
        }
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new MecabMorphemeWinsize3(), args);
        System.exit(res);

    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();
        conf.set("fs.hdfs.impl",
                org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl",
                org.apache.hadoop.fs.LocalFileSystem.class.getName());
        conf.set("mapred.textoutputformat.separator", "\t");
        Job job = Job.getInstance(conf, "Mecab Morpheme Winsize 3");
        job.setJarByClass(MecabMorphemeWinsize3.class);
        job.setMapperClass(MecabMorphemeRelBackMapper.class);
        //job.setCombinerClass(MecabMorphemeWinsize3Combiner.class);
        job.setReducerClass(MecabMorphemeWinsize3Reducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        Path inputPath = new Path(args[0]);
        Path outputPath = new Path(args[1]);

        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        return job.waitForCompletion(true) ? 0 : 1;
    }

}
