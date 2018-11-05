package utils

import org.apache.spark.sql.types._

// This class is used to Map the relevant datatypes from config with Spark SQL compatiable data types
object Constants {

  val decimalType: DecimalType = DataTypes.createDecimalType(38, 7)


  val mapofdataTypes = Map("StringType" -> StringType, "IntegerType" -> IntegerType, "DecimalType" -> decimalType,"LongType" -> LongType)


}
