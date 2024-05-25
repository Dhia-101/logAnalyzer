package com.test

import java.io.IOException
import java.sql.Timestamp
import java.util.{Date, Properties}
import com.test.config.ConfigurationFactory
import com.test.models.LogRecord
import com.test.utils.JsonUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.Logger
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types._
import org.apache.spark.sql.{Row, SQLContext, SaveMode, SparkSession}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferBrokers
import org.apache.spark.streaming.{Seconds, StreamingContext}

object App {
  private[this] lazy val logger = Logger.getLogger(getClass)

  private[this] val config = ConfigurationFactory.load()

  def jsonDecode(text: String): LogRecord = {
    try {
      JsonUtils.deserialize(text, classOf[LogRecord])
    } catch {
      case e: IOException =>
        logger.error(e.getMessage, e)
        null
    }
  }

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("logAnalyzer")
      .master("local[*]")
      .config("spark.sql.warehouse.dir", "file:///C:/spark/spark-warehouse")
      .getOrCreate

    val streaming = new StreamingContext(spark.sparkContext, Seconds(config.getStreaming.getWindow))

    val servers = config.getProducer.getHosts.toArray.mkString(",")

    val params = Map[String, Object](
      "bootstrap.servers" -> servers,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "auto.offset.reset" -> "latest",
      "group.id" -> "dashboard",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val topics = Array(config.getProducer.getTopic)

    val stream = KafkaUtils.createDirectStream[String, String](
      streaming, PreferBrokers, Subscribe[String, String](topics, params))

    val schema = StructType(
      StructField("timestamp", TimestampType) ::
        StructField("logLevel", StringType) ::
        StructField("source", StringType) ::
        StructField("message", StringType) :: Nil
    )

    val host = config.getStreaming.getDb.getHost
    val db = config.getStreaming.getDb.getDb
    val url = s"jdbc:mysql://$host/$db"
    val table = config.getStreaming.getDb.getTable

    val props = new Properties
    props.setProperty("driver", "com.mysql.cj.jdbc.Driver")
    props.setProperty("user", config.getStreaming.getDb.getUser)
    props.setProperty("password", config.getStreaming.getDb.getPass)

    type Record = ConsumerRecord[String, String]

    stream.foreachRDD((rdd: RDD[Record]) => {
      val pairs = rdd
        .map(row => {
          (row.timestamp(), jsonDecode(row.value()))
        })
        .map(row => {
          (row._2.getLogLevel, (1, row._2, row._1))
        })


//      val flatten = pairs
//        .reduceByKey((x, y) =>
//          (x._1 + y._1, x._2 + y._2, (y._3 + x._3) / 2))
//        .map(f => Row.fromSeq(Seq(f._1, f._2._2 / f._2._1, new Timestamp(f._2._3))))

//        val flatten =  pairs.reduceByKey((x, y) => (x._1 + y._1, if (x._3 > y._3) x._2 else y._2, (y._3 + x._3) / 2));

//      val flatten = pairs
//        .reduceByKey((x, y) => (x._1 + y._1, if (x._3 > y._3) x._2 else y._2, (y._3 + x._3) / 2))
//        .map { case (logLevel, (count, logRecord, avgTimestamp)) =>
//          Row(new Timestamp(avgTimestamp), logLevel, logRecord.getSource, logRecord.getMessage)
//        }

      // create sql context from active spark context
      val sparkSession = spark

//      val rowRdd = rdd.map(element => Row.fromSeq(Seq(element._1, element._2, element._3, element._4)))


      System.out.println("count: " + pairs.count())


      val rowRdd = pairs.map { case (logLevel, (count, logRecord, avgTimestamp)) =>
        Row(new Timestamp(avgTimestamp) ,logLevel, logRecord.getSource, logRecord.getMessage)
      }

      sparkSession.createDataFrame(rowRdd, schema)
        .repartition(1)
        .write
        .mode(SaveMode.Append)
        .jdbc(url, table, props)
    })

    // create streaming context and submit streaming jobs
    streaming.start()

    // wait to killing signals etc.
    streaming.awaitTermination()
  }
}