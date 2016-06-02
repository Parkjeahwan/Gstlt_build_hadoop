package com.clunix.hadoop;

import com.ai.gestalt.Semanteme;
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
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by parkjh on 16. 5. 31.
 */
public class ExtractSingleMR extends Configured implements Tool {
    public static class ExtractionSDataMapper extends Mapper<Object, Text, Text, Text> {
        static final double cv = 0.01;
        static final int mo = 3;

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            Text key1 = new Text();
            Text value1 = new Text();
            String line = value.toString();
            String arr[] = line.split("\t\t\t");
            String arr1[] = arr[0].split("\t");
            String x = arr1[0];
            int nx = Integer.valueOf(arr1[1]);
            int xcv = (int)(nx * cv);
            if (arr.length > 1) {
                String arr2[] = arr[1].split("\t\t");
                for (String str : arr2) {
                    String arr3[] = str.split("\t");
                    int nxy = Integer.valueOf(arr3[1]);
                    if (mo < nxy && xcv < nxy) {
                        String xyl = x + " " + arr3[0];
                        key1.set(xyl);
                        value1.set(x + "\t" + nx + "\t" + arr3[0] + "\t" + nxy);
                        context.write(key1, value1);
                    }
                }
            }
        }
    }
    public static class ExtractionSDataCombiner extends Reducer<Text, Text, Text, Text> {
        Text result = new Text();
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            String sum = "";
            HashMap<String, Semanteme> S = new HashMap <String, Semanteme> ();

            for (Text val : values) {
                String arr[] = val.toString().split("\t");
                Semanteme sx = new Semanteme(arr[0], Integer.valueOf(arr[1]));
                Semanteme sy = new Semanteme(arr[2]);
                int nxy = Integer.valueOf(arr[3]);
                updateSet(sx, sy, nxy, S);
            }
            Collection<Semanteme> SS =  S.values();

            for (Semanteme s : SS) {
                sum = (s.label+ "\t\t"+s.count+ "\t\t"+s.length+ "\t\t"+s.isSet+ "\t\t");

                if (s.set == null || s.set.isEmpty() ) {
                    sum +="NULL";
                } else for (int i=0;i<s.set.size();i++) {
                    Semanteme y = s.set.get(i);
                    String TOWRITE = y != null ? s.label : "NULL";
                    sum += (i != 0)? "\t"+TOWRITE:TOWRITE;
                }
                sum += "\t\t";

                if (s.element == null || s.element.isEmpty() ) {
                    sum += "NULL";
                }
                else for (int i=0;i<s.element.size();i++) {
                    Semanteme y = s.element.get(i);
                    String TOWRITE = y != null ? y.label : "NULL";
                    sum += (i != 0)? "\t"+TOWRITE:TOWRITE;
                }
                sum += "\t\t";

                if (s.part == null || s.part.isEmpty() ) {
                    sum += "NULL";
                }
                else for (int i=0;i<s.part.size();i++) {
                    Semanteme y = s.part.get(i);
                    String TOWRITE = y != null ? y.label : "NULL";
                    sum += (i != 0)? "\t"+TOWRITE:TOWRITE;
                }
            }

            S.clear();
            SS.clear();
            result.set(sum);
            context.write(key, result);
        }

        public static String fa(String x) {return "(" + x + ")*";}

        public static String ba(String x) {return "*(" + x + ")";}

        private boolean updateSet(Semanteme x, Semanteme y, int nxy, HashMap <String, Semanteme> S) {
            if (x == null || y == null ) return true;
            String fax = fa(x.label);
            String bay = ba(y.label);
            String xyl = x.label + " " + y.label;
            String xfax = x.label + " " + fax;
            String bayy = bay + " " + y.label;
            Semanteme faxS , xfaxS, xy;
            boolean Exy = S.containsKey(xyl);
            xy = Exy? S.get(xyl):new Semanteme(xyl,nxy,x.isSet||y.isSet);
            if (!Exy) {
                S.put(xyl, xy);
                xy.part.add(x); xy.part.add(y);
            }
            if (!S.containsKey(fax)) {
                S.put(fax, faxS = new Semanteme(fax, nxy ,true));
                S.put(xfax, xfaxS = new Semanteme(xfax, nxy, true));
                xfaxS.part.add(x);
                xfaxS.part.add(faxS);
                faxS.element.add(y);
                y.set.add(faxS);
                xfaxS.element.add(xy);
                xy.set.add(xfaxS);
            }
            else if (!Exy || Exy && !y.isPartOf(xy)){
                faxS = S.get(fax);
                xfaxS = S.get(xfax);
                faxS.count = (xfaxS.count += nxy);
                faxS.element.add(y);
                y.set.add(faxS);
                xfaxS.element.add(xy);
                xy.set.add(xfaxS);
                if (Exy) {
                    xy.part.add(x); xy.part.add(y);
                }
            }
            Semanteme bayS,bayyS;
            if (!S.containsKey(bay)) {
                S.put(bay, bayS = new Semanteme(bay, nxy, true));
                S.put(bayy, bayyS = new Semanteme(bayy, nxy, true));
                bayyS.part.add(bayS);
                bayyS.part.add(y);
                bayS.element.add(x);
                x.set.add(bayS);
                bayyS.element.add(xy);
                xy.set.add(bayyS);
            }
            else if (!Exy|| Exy && !x.isPartOf(xy=S.get(xyl))){
                bayS = S.get(bay);
                bayyS = S.get(bayy);
                bayS.count = (bayyS.count += nxy);
                bayS.element.add(x);
                x.set.add(bayS);
                bayyS.element.add(xy);
                xy.set.add(bayyS);
                if (Exy) {
                    xy.part.add(x); xy.part.add(y);
                }
            }
            return !Exy;
        }
    }

    public static class ExtractionSDataReducer extends Reducer<Text, Text, Text, Text> {
        Text result = new Text();
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            String sum = "";
            HashMap <String, Semanteme> S = new HashMap <String, Semanteme> ();

            for (Text val : values) {
                String arr[] = val.toString().split("\t");
                Semanteme sx = new Semanteme(arr[0], Integer.valueOf(arr[1]));
                Semanteme sy = new Semanteme(arr[2]);
                int nxy = Integer.valueOf(arr[3]);
                updateSet(sx, sy, nxy, S);
            }
            Collection<Semanteme> SS =  S.values();

            for (Semanteme s : SS) {
                sum = (s.label+ "\t\t"+s.count+ "\t\t"+s.length+ "\t\t"+s.isSet+ "\t\t");

                if (s.set == null || s.set.isEmpty() ) {
                    sum +="NULL";
                } else for (int i=0;i<s.set.size();i++) {
                    Semanteme y = s.set.get(i);
                    String TOWRITE = y != null ? s.label : "NULL";
                    sum += (i != 0)? "\t"+TOWRITE:TOWRITE;
                }
                sum += "\t\t";

                if (s.element == null || s.element.isEmpty() ) {
                    sum += "NULL";
                }
                else for (int i=0;i<s.element.size();i++) {
                    Semanteme y = s.element.get(i);
                    String TOWRITE = y != null ? y.label : "NULL";
                    sum += (i != 0)? "\t"+TOWRITE:TOWRITE;
                }
                sum += "\t\t";

                if (s.part == null || s.part.isEmpty() ) {
                    sum += "NULL";
                }
                else for (int i=0;i<s.part.size();i++) {
                    Semanteme y = s.part.get(i);
                    String TOWRITE = y != null ? y.label : "NULL";
                    sum += (i != 0)? "\t"+TOWRITE:TOWRITE;
                }
            }

            result.set(sum);
            context.write(key, result);
        }

        public static String fa(String x) {return "(" + x + ")*";}

        public static String ba(String x) {return "*(" + x + ")";}

        private boolean updateSet(Semanteme x, Semanteme y, int nxy, HashMap <String, Semanteme> S) {
            if (x == null || y == null ) return true;
            String fax = fa(x.label);
            String bay = ba(y.label);
            String xyl = x.label + " " + y.label;
            String xfax = x.label + " " + fax;
            String bayy = bay + " " + y.label;
            Semanteme faxS , xfaxS, xy;
            boolean Exy = S.containsKey(xyl);
            xy = Exy? S.get(xyl):new Semanteme(xyl,nxy,x.isSet||y.isSet);
            if (!Exy) {
                S.put(xyl, xy);
                xy.part.add(x); xy.part.add(y);
            }
            if (!S.containsKey(fax)) {
                S.put(fax, faxS = new Semanteme(fax, nxy ,true));
                S.put(xfax, xfaxS = new Semanteme(xfax, nxy, true));
                xfaxS.part.add(x);
                xfaxS.part.add(faxS);
                faxS.element.add(y);
                y.set.add(faxS);
                xfaxS.element.add(xy);
                xy.set.add(xfaxS);
            }
            else if (!Exy || Exy && !y.isPartOf(xy)){
                faxS = S.get(fax);
                xfaxS = S.get(xfax);
                faxS.count = (xfaxS.count += nxy);
                faxS.element.add(y);
                y.set.add(faxS);
                xfaxS.element.add(xy);
                xy.set.add(xfaxS);
                if (Exy) {
                    xy.part.add(x); xy.part.add(y);
                }
            }
            Semanteme bayS,bayyS;
            if (!S.containsKey(bay)) {
                S.put(bay, bayS = new Semanteme(bay, nxy, true));
                S.put(bayy, bayyS = new Semanteme(bayy, nxy, true));
                bayyS.part.add(bayS);
                bayyS.part.add(y);
                bayS.element.add(x);
                x.set.add(bayS);
                bayyS.element.add(xy);
                xy.set.add(bayyS);
            }
            else if (!Exy|| Exy && !x.isPartOf(xy=S.get(xyl))){
                bayS = S.get(bay);
                bayyS = S.get(bayy);
                bayS.count = (bayyS.count += nxy);
                bayS.element.add(x);
                x.set.add(bayS);
                bayyS.element.add(xy);
                xy.set.add(bayyS);
                if (Exy) {
                    xy.part.add(x); xy.part.add(y);
                }
            }
            return !Exy;
        }
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new ExtractSingleMR(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf2 = new Configuration();
        conf2.set("fs.hdfs.impl",
                org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf2.set("fs.file.impl",
                org.apache.hadoop.fs.LocalFileSystem.class.getName());
        conf2.set("mapred.textoutputformat.separator", "##SP");
        Job job2 = Job.getInstance(conf2, "Extraction S Data");
        job2.setJarByClass(ExtractSingleMR.class);

        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);

        job2.setMapperClass(ExtractionSDataMapper.class);
        job2.setCombinerClass(ExtractionSDataCombiner.class);
        job2.setReducerClass(ExtractionSDataReducer.class);

        FileInputFormat.addInputPath(job2, new Path(args[0]));
        FileOutputFormat.setOutputPath(job2, new Path(args[1]));

        return job2.waitForCompletion(true) ? 0 : 1;
    }
}
