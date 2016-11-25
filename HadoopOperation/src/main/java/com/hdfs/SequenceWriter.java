package com.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * Created by parkjh on 16. 9. 27.
 */
public class SequenceWriter extends Configured implements Tool {

    @Override
    public int run(String[] args) throws Exception {
        Path inputpath = new Path(args[0]);
        Path outputpath = new Path(args[1]);

        Configuration conf = getConf();
        Job weblogJob = Job.getInstance(conf, "Sequence File Writer");
        weblogJob.setJarByClass(getClass());
        weblogJob.setNumReduceTasks(0);
        //weblogJob.setMapperClass(IdentityMapper.class);
        weblogJob.setMapOutputKeyClass(LongWritable.class);
        weblogJob.setMapOutputValueClass(Text.class);

        return 0;
    }

    public static void main(String[] args) throws Exception {
        int returnCode = ToolRunner.run(new SequenceWriter(), args);
        System.exit(returnCode);
    }
}
