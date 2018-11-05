//package fixtures
//
//import org.apache.spark.{SparkConf, SparkContext}
//import org.scalatest.{BeforeAndAfterAll, Suite}
//
//trait CommomSparkContext extends BeforeAndAfterAll {
//
//  self: Suite =>
//
//
//  val sparkConf = new SparkConf()
//    .set("spark.serializer", "org.apache.spark.serializer.KyroSerializer")
//    .set("mapreduce.fileoutputcomitter.algorithm.versoin", "2")
//    .set("mapreduce.fileoutputcomitter.makesucessfuljobs", "false")
//    .set("spark.sql.autoBroadcastJoinThreshold", "-1")
//    .set("spark.driver.extraJavaOptions", "XX:+UseG1GC")
//    .set("spark.executor.extraJavaOptions", "-XX:+UseG1GC")
//    .set("spark.scheduler.mode", "FAIR")
//    .set("spark.sql.shuffle.partitions", "1")
//    .set("spark.default.parallelism", "1")
//
//
//  override def beforeAll(): Unit = {
//
//    sparkConf
//      .setMaster("local[*]")
//
//    val sc = new SparkContext(sparkConf)
//
//
//    sc.setLogLevel("ERROR")
//
//  }
//
//
//}
