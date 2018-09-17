package com.jwszol;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by kubaw on 04/06/17.
 */
public class SqlSparkTest {

    private transient SparkSession ss;

    @Before
    public void setUp() {
        ss = SparkSession
                .builder()
                .appName("Spark SQL Session")
                .config("spark.master", "local")
                .getOrCreate();
    }

    @After
    public void close(){
        ss.stop();
    }

    @Test
    public void testSQLJob() {
        SqlSparkJob sj = new SqlSparkJob(ss);
        System.out.println("task 1");
        sj.amountOfProductsPerCustomer();
        System.out.println("task 2");
        sj.amountOfProductsPerLocation();
        System.out.println("task 3");
        sj.mostPopularProductPerLocation();
    }

}
