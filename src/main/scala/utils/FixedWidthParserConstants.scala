package utils

import org.apache.spark.sql.types._
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
  *
  * This class is used to Map the relevant datatypes from config with Spark SQL compatiable data types
  * To Define contants which will be used across framework
  **/
object FixedWidthParserConstants {

  lazy val formatter: String => DateTimeFormatter = { str: String => DateTimeFormat forPattern str }
  val businessDatePattern = "yyyy-MM-dd"
  val DateFormat = DateTimeFormat.forPattern("yyyy-MM-dd")

  //FIXME read these values from config file
  val baseHdfsPath = "hdfs:///user/vekambaram/input_files/"
  val baseoutputHdfsPath = "hdfs:///data/transformation/fiserv_datamart/"
  val database_fiserv = "fiserv_datamart"


  //FIXME hardcoded decimal scale and precision for decimal type as hive doesnt add the precision which is defined in hive
  val decimalType: DecimalType = DataTypes.createDecimalType(38, 7)

  /**
    * Mapping of the datatypes from JSON which the Spark data types
    **/
  val mapofdataTypes = Map("StringType" -> StringType, "IntegerType" -> IntegerType, "DecimalType" -> decimalType, "LongType" -> LongType)


}
