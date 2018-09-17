package com.jwszol;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import scala.Int;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.max;
import static org.apache.spark.sql.functions.sum;

/**
 * Created by kubaw on 04/06/17.
 */


public class SqlSparkJob {

    private Dataset<Row> dfUsers = null;
    private Dataset<Row> dfTrans = null;
    
    private SparkSession spark = null;

    public SqlSparkJob(SparkSession spark){
        this.spark = spark;
        this.dfUsers = this.spark.read().option("delimiter","|").option("header","true").csv("./src/main/resources/users.txt");
        this.dfTrans = this.spark.read().option("delimiter","|").option("header","true").csv("./src/main/resources/transactions.txt");
    }

    public void amountOfProductsPerCustomer() {
        Dataset<Row>  CompulsiveBuyingDisorder = this.dfTrans.select(col("user_id"), col("purchase_amount"));
        Dataset<Row> CBDsummedUp = CompulsiveBuyingDisorder.groupBy("user_id").agg(sum("purchase_amount"));
        CBDsummedUp.show();
    }

    public void amountOfProductsPerLocation() {
        dfUsers.registerTempTable("users");
        dfTrans.registerTempTable("trans");
        Dataset<Row>  TuristicAspectOfLocation = this.spark.sql("SELECT users.location, trans.purchase_amount FROM trans INNER JOIN users ON trans.user_id=users.id");;
        Dataset<Row> TAOLsummedUp = TuristicAspectOfLocation.groupBy("location").agg(sum("purchase_amount"));
        TAOLsummedUp.show();
    }

    public void mostPopularProductPerLocation(){
        dfUsers.registerTempTable("users");
        dfTrans.registerTempTable("trans");
        Dataset<Row>  TuristicAspectOfLocation = this.spark.sql("SELECT users.location, trans.prod_id, trans.purchase_amount FROM trans INNER JOIN users ON trans.user_id=users.id");;
        Dataset<Row> TAOLsummedUp = TuristicAspectOfLocation.groupBy(col("location"), col("prod_id")).agg(sum("purchase_amount")).withColumnRenamed("sum(purchase_amount)", "amount");
        TAOLsummedUp.registerTempTable("t");
        Dataset<Row> MostPopularProducts = this.spark.sql("SELECT a.location, a.prod_id, a.amount\n" +
                        "FROM t a\n" +
                        "INNER JOIN (\n" +
                        "    SELECT location, MAX(amount) amount\n" +
                        "    FROM t\n" +
                        "    GROUP BY location\n" +
                        ") b ON a.location = b.location AND a.amount = b.amount");
        MostPopularProducts.show();
    }
}
