package com.myproject.config;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SparkConfig {

   @Value("${spark.app.name}")
   private String appName;

   @Value("${spark.master}")
   private String master;

   @Bean
   public SparkConf sparkConf() {
       SparkConf sparkConf = new SparkConf()
               .setAppName(appName)
               .setMaster(master)
               .set("spark.driver.bindAddress", "127.0.0.1")
               .set("spark.driver.port", "7077");

       return sparkConf;
   }

   @Bean
   public SparkSession sparkSession(SparkConf sparkConf) {
       return SparkSession.builder()
               .config(sparkConf)
               .getOrCreate();
   }
}
