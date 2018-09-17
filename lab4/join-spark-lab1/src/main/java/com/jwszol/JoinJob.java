package com.jwszol;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.Function;


import com.google.common.base.Optional;
import scala.Tuple2;

import java.util.Arrays;

/**
 * Created by kubaw on 29/05/17.
 */
public class JoinJob {

    private static JavaSparkContext sc;

    public JoinJob(JavaSparkContext sc){
        this.sc = sc;
    }


    public static final PairFunction<Tuple2<Integer,Tuple2<Tuple2<Integer, Integer>, String>>, Tuple2<Tuple2<Integer, String>, Integer>, Integer> KEY_VALUE_PAIRER =
            new PairFunction<Tuple2<Integer,Tuple2<Tuple2<Integer, Integer>, String>>, Tuple2<Tuple2<Integer, String>, Integer>, Integer> () {
                public Tuple2<Tuple2<Tuple2<Integer, String>, Integer>, Integer> call(
                        Tuple2<Integer,Tuple2<Tuple2<Integer, Integer>, String>> a) throws Exception {
                        Tuple2<Integer, String> customer = new Tuple2<>(a._1, a._2._2);
                        Tuple2<Tuple2<Integer, String>, Integer> customuerProductPair = new Tuple2<>(customer, a._2._1._1);

                    return new Tuple2<Tuple2<Tuple2<Integer, String>, Integer>, Integer>(customuerProductPair, a._2._1._2);
                }
            };



    public static JavaPairRDD<Tuple2<Tuple2<Integer, String>, Integer>, Integer> joinData(String t, String u){

        JavaRDD<String> customerInputFile = sc.textFile(u);
        JavaPairRDD<Integer, String> customerPairs = customerInputFile.mapToPair(new PairFunction<String, Integer, String>() {
            public Tuple2<Integer, String> call(String s) {
                String[] customerSplit = s.split("\t");
                return new Tuple2<Integer, String>(Integer.valueOf(customerSplit[0]), customerSplit[3]);
            }
        });

        JavaRDD<String> transactionInputFile = sc.textFile(t);
        JavaPairRDD<Integer, Tuple2<Integer, Integer>> transactions = transactionInputFile.mapToPair(new PairFunction<String, Integer, Tuple2<Integer, Integer>> () {
            public Tuple2<Integer, Tuple2<Integer, Integer>> call(String s) {
                String[] transactionSplit = s.split("\t");
                Tuple2<Integer, Integer> productInfoTuple= new Tuple2<Integer, Integer>(Integer.valueOf(transactionSplit[1]), Integer.valueOf(transactionSplit[3]));
                return new Tuple2<Integer, Tuple2<Integer, Integer>>(Integer.valueOf(transactionSplit[2]), productInfoTuple);
            }
        });

        JavaPairRDD<Integer,Tuple2<Tuple2<Integer, Integer>, String>> joinedDirty = transactions.join(customerPairs);

        JavaPairRDD<Tuple2<Tuple2<Integer, String>, Integer>, Integer> pairTransactionAmount = joinedDirty.mapToPair(KEY_VALUE_PAIRER);

        JavaPairRDD<Tuple2<Tuple2<Integer, String>, Integer>, Integer> summedUp = pairTransactionAmount.reduceByKey( new Function2<Integer, Integer, Integer>() {
            public Integer call(Integer uno, Integer dos) {
                return uno + dos;
            }
        });

        return summedUp;
    }
}
