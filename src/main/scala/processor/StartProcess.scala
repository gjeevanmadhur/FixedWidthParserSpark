package processor


import org.apache.hadoop.fs.FileSystem
import org.apache.log4j.Logger
import org.apache.spark.{SparkConf, SparkContext}
import org.joda.time.DateTime
import processor.FixedWidthProcessor._

import scala.util.Try

/**
  * Entry point for the Fixed Width Parsing process
  */
object StartProcess extends App {

  val logger: Logger = Logger.getLogger(StartProcess.getClass)

  val nAargs = 3

  if (args.length != nAargs) {

    logger.error("Usage : Fixed Width Parser required arguements are: <Column JSON File> ,<table Xml>,<Run Date> ")
    sys.error("Usage : Fixed Width Parser required arguements are: <Column JSON File> ,<table Xml>,<Run Date> ")
    System.exit(1)
  }
  else {
    logger.info(s"Start time of the job ${DateTime.now()}")
    logger.info("Spark Driver arguemnts -" + args.mkString("|"))
  }

  val conf = new SparkConf()
    .setMaster("local[*]")
    .setAppName("FixedWidth")
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .set("mapreduce.fileoutputcommitter.algorithm.version", "2")
    .set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false")

  val spark = new SparkContext(conf)

  val sqlContext = new org.apache.spark.sql.hive.HiveContext(spark)

  val (columnjsonPath, tablexmlPath, runDate) = parseParamters()

  startFixedWidthProcess(columnjsonPath, tablexmlPath, runDate)

  val fs: FileSystem = utils.FileSystemUtils.fetchFileSystem

  /**
    * Parses parameters and submits the Fixed Width  Job
    */

  def parseParamters() = {

    val Array(columnjsonPath, tablexmlPath, runDate) = args

    val partitionDate: String = getRunDate(runDate, utils.FixedWidthParserConstants.businessDatePattern) match {
      case Some(partition) => partition
      case None => throw new scala.Exception("Run Date format is incorrect! Expected in yyyy-MM-dd")
    }

    (columnjsonPath, tablexmlPath, partitionDate)

  }

  def getRunDate(rund: String, timeformat: String): Option[String] = {
    val runDate = Try(utils.FixedWidthParserConstants.formatter(timeformat) parseDateTime rund).toOption
    runDate.map(utils.FixedWidthParserConstants.DateFormat.print)
  }

}