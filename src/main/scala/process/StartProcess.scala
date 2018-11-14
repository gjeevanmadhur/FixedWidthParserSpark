package process


import org.apache.hadoop.fs.FileSystem
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql._
import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.{Failure, Success, Try}

object StartProcess extends App {


  Logger.getLogger("org").setLevel(Level.ERROR)


  val conf = new SparkConf().setMaster("local[*]").setAppName("FixedWidth")

  val spark = new SparkContext(conf)


  val sqlContext = new org.apache.spark.sql.hive.HiveContext(spark)

  val fs: FileSystem = utils.FileSystemUtils.fetchFileSystem

  val json_parser = sqlContext.read.json(args(1)) // Parse JSON for schema


  val tablenamesp = utils.FetchTables.getTableList("cf_account,cf_transaction", fs)


  val columnsFromTable = tablenamesp.map(passtableNames => utils.JSONParser.parseJSON(json_parser, sqlContext, passtableNames))

  columnsFromTable.map(eachCol => gettheDataFrameCreated(eachCol._1, sqlContext, eachCol._2))

  def gettheDataFrameCreated(col: List[Row], SQLContext: SQLContext, tablename: String) = {

    val schema = StructType(col.map(_.mkString.split(",")).map(x => (x(0), x(1), x(2), x(3)))

      .map(x => StructField(x._1.trim.substring(3), utils.Constants.mapofdataTypes(x._2.trim.replaceAll("]", "")).asInstanceOf[org.apache.spark.sql.types.DataType], false)))

    schema.printTreeString()


    val pos = utils.JSONParser.getPositionofColumns(json_parser, sqlContext, tablename).map(_.toString.toInt).sorted


    val data = spark.textFile(args(0)).map(x => utils.ParseFixedWidth.getRowfromDataFrame(x, pos, col))


    val output = Try {
      sqlContext.createDataFrame(data, schema)
        .write.mode(SaveMode.Overwrite).format("orc").saveAsTable(s"fiserv_datamart.$tablename")
       // .save(s"/data/transformation/fiserv_datamart/cf_transaction/run_date=2018-11-08")
      sqlContext.sql(s"msck repair table fiserv_datamart.$tablename")

    }


    output match {

      case Success(v) => println("Program executed successfully " + v)
        sqlContext.sql($"msck repair table fiserv_datamart.cf_transaction_tmp")

      case Failure(v) => println("Failed....." + v)


    }

  }


  //reading file


  // Way of reading fixed width file --- harcoded

  //    val schema= StructType(StructField("accountNo",StringType,true):: StructField("transaction_date",StringType,true):: Nil)---- First Part-- Need to get column name
  //
  //
  //
  //
  //    val data=spark.sparkContext.textFile("/home/jeevan/input.txt").map( line => Row(line.substring(0,5).trim,line.substring(5,15)))-- we need to get startPos and EndPos to read
  //
  //    spark.createDataFrame(data,schema).show(false)


  //StructField("1",DecimalType)
  // StructField("2",IntegerType)

}