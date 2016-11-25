package com.clunix;

import com.clunix.Gstlt_build.Gstlt_build_Mapper;
import com.clunix.Gstlt_build.Gstlt_build_Reducer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mrunit.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.fail;

public class Gstlt_utilsTest
{
	MapDriver<Object, Object, Text, Object> mapDriver;
	ReduceDriver<Text, Object, Text, Object> reduceDriver;
	MapReduceDriver<Object, Object, Text, Object, Text, Object> mapReduceDriver;
	
	@Before
	public void setUp() {
		Gstlt_build_Mapper mapper = new Gstlt_build_Mapper();
		Gstlt_build_Reducer reducer = new Gstlt_build_Reducer();
		mapDriver = MapDriver.newMapDriver((Mapper<Object, Object, Text, Object>) mapper);
		reduceDriver = ReduceDriver.newReduceDriver(reducer);
		mapReduceDriver = MapReduceDriver.newMapReduceDriver(mapper, reducer);
	}

	@Ignore
	@Test
	public void test()
	{
		fail("Not yet implemented");
	}
}
