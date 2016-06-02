package com.clunix.hadoop;

import com.ai.gestalt.Semanteme;
import com.ai.gestalt.SeqSem;
import com.ai.gestalt.Sequence;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by parkjh on 16. 5. 27.
 */
public class LengthNPairFinder {

    public static class FasDataNew_Mapper extends Mapper<Object, Text, Text, Text> {
        ArrayList <String> C = new ArrayList<String>();
        HashMap <String, Semanteme> S = new HashMap <String, Semanteme> ();

        public FasDataNew_Mapper(){
            FileSystem hdfs;
            try {
                hdfs = FileSystem.get(new Configuration());
                Path homeDir = hdfs.getHomeDirectory();
                Path path = new Path(homeDir + "/corpus_mecab_pr.txt");
                BufferedReader br1 = new BufferedReader(new InputStreamReader(hdfs.open(path)));

                String line;
                while ((line = br1.readLine()) != null) {
                    C.add(line);
                }
                br1.close();

                Path path2 = new Path(homeDir + "/corpus_mecab_pr.txt.1.SEMANTEME");
                BufferedReader br = new BufferedReader(new InputStreamReader(hdfs.open(path2)));
                HashMap <String,String[]> Sets = new HashMap <String,String[]> ();
                HashMap <String,String[]> Elements = new HashMap <String,String[]> ();
                HashMap <String,String[]> Parts = new HashMap <String,String[]> ();
                while ((line = br.readLine()) != null) {
                    Semanteme s = new Semanteme();
                    String f[] = line.split("\t\t");
                    s.label = f[0];
                    if (s.label.equals("관/NNG"))
                        s.count +=0;
                    s.count = Integer.parseInt(f[1]);
                    s.length = Integer.parseInt(f[2]);
                    s.isSet= Boolean.parseBoolean(f[3]);
                    if (f.length > 4 && !f[4].equals("NULL")) Sets.put(s.label, f[4].split("\t"));
                    if (f.length > 5 && !f[5].equals("NULL")) Elements.put(s.label, f[5].split("\t"));
                    if (f.length > 6 && !f[6].equals("NULL")) Parts.put(s.label, f[6].split("\t"));
                    S.put(s.label, s);
                }
                for (Semanteme s:S.values()) {
                    if (Sets.containsKey(s.label)) for (String a:Sets.get(s.label))
                        if (a!= null && !a.equals("") && !a.equals("\t") &&  !a.equals("\t\t")) s.set.add(S.get(a));
                    if (Elements.containsKey(s.label)) for (String a:Elements.get(s.label))
                        if (a!= null && !a.equals("") && !a.equals("\t") &&  !a.equals("\t\t")) s.element.add(S.get(a));
                    if (Parts.containsKey(s.label)) for (String a:Parts.get(s.label))
                        if (a!= null && !a.equals("") && !a.equals("\t") &&  !a.equals("\t\t")) s.part.add(S.get(a));
                }
                br.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static String fa(String x) {return "(" + x + ")*";}

        public static String ba(String x) {return "*(" + x + ")";}

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            Text key1 = new Text();
            Text value1 = new Text();
            //Student st = new Student();
            String line = value.toString();

            int u = 4;
            Sequence s = new Sequence(line);
            while (!s.e.isEmpty()) {
                HashMap<Sequence,List<SeqSem>>LK = new HashMap<Sequence,List<SeqSem>> ();
                List<SeqSem> XS = leadingSemAndFol(s, LK, u);
                //System.out.println(line);
                for (int j = 0; XS != null && j < XS.size(); j++) {
                    SeqSem xx = XS.get(j);
                    String x = xx.seq.body;
                    List<SeqSem> YS = LK.get(xx.seq);
                    if (YS == null || YS.isEmpty()) {
                        Sequence f = (x.length() < s.body.length()) ? new Sequence(s.body.substring(1 + x.length())) : null;
                        if (f != null && !f.body.isEmpty())
                            YS = leadingSemanteme(f, u);
                    }
                    int oj = j;
                    for (; j < XS.size() && (XS.get(j).seq.body).equals(x); j++){
                        String result1 = "";
                        result1 = registerFollowers(XS.get(j).sem, YS);
                        value1.set(result1);

                        String result2 = XS.get(j).sem.label;
                        key1.set(result2);
                        context.write(key1, value1);
                    }
                    if (oj != j) j--;
                }
                s.body = s.body.substring(1 + s.body.indexOf(" "));
                s.e = s.e.subList(1, s.e.size());
            }
        }

        public List<SeqSem> leadingSemanteme(Sequence s, int n) { // 패턴시퀀스s를 시작하는, 길이  n이하  모든 의미소를 리턴
            // 주어진 패턴 시퀀스 s로부터 길이 n이내에, s의 시작 부분에 대응 가능한 모든 의미소를 찾아 리턴하는 함수
            if (s == null || s.body == null || s.body.equals(""))
                return null;
            List<Sequence> L = leadingPattern(s, n, S); // L의 원소 l은 등록 패턴인데,
            List<SeqSem> ret = new ArrayList<SeqSem>();// Semanteme> ();
            for (Sequence ls : L) {
                HashSet<Semanteme> recreated = recreated(ls, S); // 리딩 패턴을 a+b 형태로  분해.
                if (recreated != null) for (Semanteme x : recreated)
                    ret.add(new SeqSem(ls, x));
            }
            return ret.isEmpty() ? null : ret;
        }

        static int it = 0;
        private List<SeqSem> leadingSemAndFol(Sequence s, HashMap<Sequence, List<SeqSem>> LL, int nnn) {
            // 주어진 패턴 시퀀스 s로부터 길이 n이내에, s의 시작 부분에 대응 가능한 모든 의미소를 찾아 리턴하는 함수
            if (s == null || s.body == null || s.body.equals("")) return null;
            List<Sequence> L = leadingPattern(s, nnn, S); // L의 원소 l은 등록 패턴인데,
            if (it > 2 && s.body.startsWith("되/VV"))
                nnn +=0;
            List<SeqSem> ret = new ArrayList<SeqSem>();// Semanteme> ();
            HashSet<Semanteme> recreated2 = null;
            HashSet<SeqSem> tempq = new HashSet<SeqSem>(); // 같은 값이 중복 리턴되는 것을 막기 위한 해쉬셋
            for (int index=L.size()-1;index>=0;index--){  // s 시작부분을 포함하는 모든 등록 패턴 ls를 구해
                Sequence ls = L.get(index);
                HashSet<Semanteme> recreated = recreated(ls, S);
                if (recreated == null) continue;
                for (Semanteme x : recreated) { // ls의 의미소 표현(해석) x에 대해
                    if (x == null) break;
                    SeqSem a = new SeqSem(ls, x);
                    if (!tempq.contains(a)) {
                        ret.add(a);
                        tempq.add(a);
                    }
                }  // 결과로 리턴할, 모든 등록 패턴에 대해 이하에서 후속 패턴(의미소해석)을 찾음
                String rest = s.body.substring(ls.body.length());
                List<Sequence> L2 = rest != null && !rest.isEmpty()? leadingPattern(new Sequence(rest),nnn,S):null;
                List<SeqSem> lk = new ArrayList<SeqSem>();
                if (L2 != null) for (Sequence lss:L2) { // ls의 후속 패턴 lss에 대해
                    recreated2 = recreated(lss,S); // lss를 의미소로 재창조(해석) 하고,
                    if (recreated2 != null) for (Semanteme s2:recreated2){  // 그 해석된 의미소 조합 s2들을
                        lk.add(new SeqSem(lss,s2)); // 원시 패턴 lss와 함께 <lss,i(lss)> 형태로 lk에 추가;
                        for (Semanteme s1:recreated){ //
                            if (s1.label.contains("*("))
                                it+=0;
                            if (s1.label.equals("*(은는/JX) 은는/JX"))
                                it+=0;

                            String s1s2 = s1.label+" "+s2.label;
                            if ( S.containsKey(fa(s1.label)) && S.containsKey(s1s2)? s2.isPartOf(S.get(s1s2))&&s1.isPartOf(S.get(s1s2)):false){//s2.isMemberOf(S.get(fa(s1.label)),S)) {  // ls를 뒤따르는 패턴 lss의 내부 표상이 (ls)*의 원소라면
                                //						if ( S.containsKey(fa(s1.label)) && s2.isMemberOf(S.get(fa(s1.label)),S)) {  // ls를 뒤따르는 패턴 lss의 내부 표상이 (ls)*의 원소라면
                                SeqSem tempp = new SeqSem(new Sequence(ls.body+" "+lss.body),S.get(s1.label +" "+fa(s1.label)));
                                if (tempq.isEmpty() || !tempq.contains(tempp))	{
                                    ret.add(tempp); // ls+lss 패턴을 ls+fa(ls)로서 결과에 포함시킴. ??->키고 그 후속 개념을 등록;
                                    tempq.add(tempp);
                                }
                                break;
                            }
                        }
                    }
                }
                LL.put(ls,lk);
            }
            return ret.isEmpty() ? null : ret;
        }

        private HashSet<Semanteme> recreated(Sequence x, HashMap<String, Semanteme> S) {
            if (x == null)return null;
            String label = x.body;
            //if (decomp.containsKey(label)) return decomp.get(label);
            HashSet<Semanteme> ret = new HashSet<Semanteme>();
            Semanteme sem = S.get(label);
            if (sem != null) ret.add(sem);
            int xsize = x.e.size();// .e.size();
            if (xsize == 1 )
                if (ret.isEmpty()) ret = null;
            if (xsize == 2) { // x의 길이가 2이고 결합의미소 x0'x1'으로 등록되어 있으면
                String x0, x1, fax0, bax1;
                if (S.containsKey(x0 = x.e.get(0)) && S.containsKey(x1 = x.e.get(1)) && S.containsKey(label)) {
                    if (S.containsKey(fax0 = fa(x0))) ret.add(S.get(x0 + " " + fax0));
                    if (S.containsKey(bax1 = ba(x1))) ret.add(S.get(bax1 + " " + x1));
                }
                else ret = null;
            } else if (xsize > 2)
                for (int i = 1; i < xsize; i++) {
                    String pq;
                    Sequence a = new Sequence(x.e.subList(0, i));
                    Sequence b = new Sequence(x.e.subList(i, xsize));
                    //				if (!(S.containsKey(a.body) || isPairOfSemanteme(a)) || !(S.containsKey(b.body) || isPairOfSemanteme(b))) continue;
                    HashSet<Semanteme> sa = recreated(a, S); // a, b가 등록 의미소이면 각각을  다시 의미소로 재구성
                    if (sa == null) continue;
                    HashSet<Semanteme> sb = recreated(b, S); // a, b가 등록 의미소이면 각각을  다시 의미소로 재구성
                    if (sb == null) continue;
                    for (Semanteme p : sa) { // p는 a를 p0+p1으로 분해하는 p0,p1을 담고 있음...
                        String plabel = p.label;
                        for (Semanteme q : sb) {
                            String qlabel = q.label;
                            if (S.containsKey(pq = plabel + " " + qlabel)) {
                                String baq, fap, baqq, pfap;
                                Semanteme pqs, baqqs, pfaps;
                                if (!ret.contains(pqs = S.get(pq)))
                                    ret.add(pqs);
                                if (S.containsKey(fap = fa(plabel)) && S.containsKey(pfap = plabel + " " + fap) &&
                                        p.isPartOf(pqs) && q.isPartOf(pqs) && !ret.contains(pfaps = S.get(pfap)))
                                    ret.add(pfaps);
                                if (S.containsKey(baq = ba(qlabel)) && S.containsKey(baqq = baq + " " + qlabel) &&
                                        p.isPartOf(pqs) && q.isPartOf(pqs) && !ret.contains(baqqs = S.get(baqq)))
                                    ret.add(baqqs);
                            }
                        }
                    }
                }
            if (ret == null || ret.isEmpty()) ret = null;
            //decomp.put(label, ret);
            return ret;
        }

        private List<Sequence> leadingPattern(Sequence s, int n, HashMap<String, Semanteme> S) { // 패턴시퀀스s를 시작하는,  길이  n이하 모든 의미소를 리턴
            // 주어진 패턴 시퀀스 s의 시작부분부터 길이 n이하인 패턴 s중 대응 의미소가 있는 모든 패턴을 리턴하는 함수
            List<Sequence> ret = new ArrayList<Sequence>();// Semanteme> ();
            if (s == null || n <= 0) return null;
            String t = "";
            for (int idx = 0; idx <= n && idx < s.e.size(); idx++) {
                t += t.equals("") ? s.e.get(idx) : " " + s.e.get(idx);
                //			if (S.containsKey(t))
                if (!t.equals("")) ret.add(new Sequence(t));
            }
            return ret.isEmpty() ? null : ret;
        }

        private String registerFollowers(Semanteme x, List<SeqSem> YS) {
            if (YS == null || YS.isEmpty()) return x.count + " " + 1;
            HashMap<Semanteme, Integer> tm = new HashMap<Semanteme, Integer>();
            for (SeqSem ys : YS) {
                Semanteme y = ys.sem;
                if (tm.containsKey(y)) tm.put(y, tm.get(y) + 1);
                else tm.put(y, 1);
            }

            String value = x.count + " " + 1 + "\t\t\t";
            String c = "";
            for(Semanteme y:tm.keySet()){
                value += c + y.label + "\t" + tm.get(y);
                c = "\t\t";
            }

            return value;
        }
    }

    public static class FasDataNew_Combiner extends Reducer<Text, Text, Text, Text> {
        Text result = new Text();
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            String sum = "";
            int count = 0;
            HashMap<String, Integer> elementSet = new HashMap<String, Integer>();
            boolean check = true;
            for (Text val : values) {
                //sum += val.toString() + "##SP";
                String arr[] = val.toString().split("\t\t\t");
                String carr[] = arr[0].split(" ");
                if(carr.length > 1) {
                    if (check) {
                        count = Integer.valueOf(carr[0]) + Integer.valueOf(carr[1]);
                    } else {
                        count += Integer.valueOf(carr[1]);
                    }
                }
                if (arr.length > 1) {
                    String arr1[] = arr[1].split("\t\t");
                    for (String element : arr1) {
                        String arr2[] = element.split("\t");
                        if (elementSet.containsKey(arr2[0])) {
                            elementSet.put(arr2[0], elementSet.get(arr2[0]) + Integer.valueOf(arr2[1]));
                        } else {
                            elementSet.put(arr2[0], Integer.valueOf(arr2[1]));
                        }
                    }
                }
            }
            sum = count + "\t\t\t";
            String c = "";
            for (String element : elementSet.keySet()) {
                sum += c + element + "\t" + elementSet.get(element);
                c = "\t\t";
            }
            elementSet.clear();
            result.set(sum);
            context.write(key, result);
        }
    }

    public static class FasDataNew_Reducer extends Reducer<Text, Text, Text, Text> {
        Text result = new Text();
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            String sum = "";
            int count = 0;
            HashMap<String, Integer> elementSet = new HashMap<String, Integer>();
            boolean check = true;
            for (Text val : values) {
                //sum += val.toString() + "##SP";
                String arr[] = val.toString().split("\t\t\t");
                String carr[] = arr[0].split(" ");
                if(carr.length > 1) {
                    if (check) {
                        count = Integer.valueOf(carr[0]) + Integer.valueOf(carr[1]);
                    } else {
                        count += Integer.valueOf(carr[1]);
                    }
                }
                if (arr.length > 1) {
                    String arr1[] = arr[1].split("\t\t");
                    for (String element : arr1) {
                        String arr2[] = element.split("\t");
                        if (elementSet.containsKey(arr2[0])) {
                            elementSet.put(arr2[0], elementSet.get(arr2[0]) + Integer.valueOf(arr2[1]));
                        } else {
                            elementSet.put(arr2[0], Integer.valueOf(arr2[1]));
                        }
                    }
                }
            }
            sum = count + "\t\t\t";
            String c = "";
            for (String element : elementSet.keySet()) {
                sum += c + element + "\t" + elementSet.get(element);
                c = "\t\t";
            }
            elementSet.clear();
            result.set(sum);
            context.write(key, result);
        }
    }

    public static void main(String[] args) throws Exception {
        //int res = ToolRunner.run(new Configuration(), new LengthNPairFinder(), args);
        //System.exit(res);
        Configuration conf = new Configuration();
        conf.set("fs.hdfs.impl",
                org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf.set("fs.file.impl",
                org.apache.hadoop.fs.LocalFileSystem.class.getName());
        conf.set("mapred.max.split.size", "17976134");
        conf.set("mapred.textoutputformat.separator", "\t");
        Job job = Job.getInstance(conf, "Length N Pair Find");
        job.setJarByClass(LengthNPairFinder.class);
        job.setNumReduceTasks(36);

        job.setMapperClass(FasDataNew_Mapper.class);
        job.setCombinerClass(FasDataNew_Combiner.class);
        job.setReducerClass(FasDataNew_Reducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        Path inputPath = new Path(args[0]);
        Path outputPath = new Path(args[1]);

        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        if (!job.waitForCompletion(true)) {
            return;
        }

 /*       Configuration conf2 = new Configuration();
        conf2.set("fs.hdfs.impl",
                org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
        conf2.set("fs.file.impl",
                org.apache.hadoop.fs.LocalFileSystem.class.getName());
        conf2.set("mapred.max.split.size", "17976134");
        conf2.set("mapred.textoutputformat.separator", "##SP");
        Job job2 = Job.getInstance(conf2, "Extraction S Data");
        job2.setJarByClass(ExtractionSData.class);

        job2.setNumReduceTasks(18);
        job2.setOutputKeyClass(Text.class);
        job2.setOutputValueClass(Text.class);

        job2.setMapperClass(ExtractionSData.ExtractionSDataMapper.class);
        job2.setCombinerClass(ExtractionSData.ExtractionSDataCombiner.class);
        job2.setReducerClass(ExtractionSData.ExtractionSDataReducer.class);

        FileInputFormat.addInputPath(job2, new Path(args[1]));
        FileOutputFormat.setOutputPath(job2, new Path(args[1] + "//Extract"));

        if (!job2.waitForCompletion(true)) {
            return;
        }*/
    }
}
