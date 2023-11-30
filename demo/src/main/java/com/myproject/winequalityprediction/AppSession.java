package com.myproject.winequalityprediction;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Component;

@Component
public class AppSession {

    private SparkSession sparkSession;

    public void setSparkSession(String master, String driverBindAddress) {
        this.sparkSession = SparkSession.builder()
                .appName("wine-app")
                .master(master)
                .config(new SparkConf().set("spark.driver.bindAddress", driverBindAddress))
                .getOrCreate();
    }

    public SparkSession getSparkSession() {
        return sparkSession;
    }
}
