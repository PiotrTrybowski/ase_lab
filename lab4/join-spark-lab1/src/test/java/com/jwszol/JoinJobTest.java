package com.jwszol;


import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scala.Tuple2;

/**
 * Created by kubaw on 29/05/17.
 */
public class JoinJobTest {
    private static final long serialVersionUID = 1L;
    private transient JavaSparkContext sc;

    @Before
    public void setUp() {
        sc = new JavaSparkContext("local", "SparkJoinsTest");
    }

    @After
    public void tearDown() {
        if (sc != null){
            sc.stop();
        }
    }

    @Test
    public void testExampleJob() {
        JoinJob job = new JoinJob(sc);
        JavaPairRDD<Tuple2<Tuple2<Integer, String>, Integer>, Integer> results = job.joinData("./src/main/resources/transactions.txt", "./src/main/resources/users.txt");

        System.out.println("((customer_id, country), product_id) amount");
        for(int i=0; i < results.count(); i++){
            System.out.print(results.collect().get(i)._1);
            System.out.print(" ");
            System.out.println(results.collect().get(i)._2);
        }

    }
}
