package utils

import org.apache.spark.sql._

import scala.collection.mutable

/** This class is used to parse the JSON file to fetch columns from the JSON and Col position **/

object JSONParser {

  import org.apache.spark.sql.functions.udf

  val concatUDF = udf(concat_array _)
  /**
    * UDF to concat different columns
    **/
  val zip = udf((xs: Seq[String], ys: Seq[String], zs: Seq[String], as: Seq[String]) => xs.zip(ys).zip(zs).zip(as))


  /**
    * This method is used to fetch columns from JSON file
    *
    * @param json_parser
    * @param sqlContext
    * @param tablename
    * @return
    **/
  def parseJSON(json_parser: DataFrame, sqlContext: SQLContext, tablename: String) = {

    import org.apache.spark.sql.functions._
    import sqlContext.implicits._

    val jsontableview = json_parser
      .select($"source", 'fields.getItem("name") as 'name, 'fields.getItem("startPos") as 'startPos,
        'fields.getItem("endPos") as 'endPos, 'fields.getItem("dataType") as 'dataType,
        'fields.getItem("length") as 'length, 'fields.getItem("precision") as 'precision)

    val columnsOfTable = jsontableview.withColumn("name", explode(zip($"name", $"dataType", $"length", $"precision")))
      .select($"name", $"source")
      .filter(jsontableview("source") === s"$tablename").drop($"source")
      .collect()
      .toList

    (columnsOfTable, tablename)


  }

  /**
    * This method is used to fetch column positions from JSON file
    *
    * @param json_parser
    * @param sqlContext
    * @param tablename
    * @return
    **/
  def getPositionofColumns(json_parser: DataFrame, sqlContext: SQLContext, tablename: String) = {
    import org.apache.spark.sql.functions._
    import sqlContext.implicits._

    val jsontableview = json_parser
      .select($"source", 'fields.getItem("name") as 'name, 'fields.getItem("startPos") as 'startPos, 'fields.getItem("endPos") as 'endPos)


    val df = jsontableview.select($"startPos", $"endPos", $"source").filter(jsontableview("source") === s"$tablename")

    df.withColumn("concat_array", concatUDF($"startPos", $"endPos")).drop($"startPos").drop($"endPos").drop($"source")
      .select(explode($"concat_array")).collect().map(x => x.get(0)).toList


  }


  /**
    * endPos and startPos returns WrappedArray which needs to be merged and sorted
    **/
  def concat_array(firstarray: mutable.WrappedArray[String],
                   secondarray: mutable.WrappedArray[String]): mutable.WrappedArray[String] = {
    (firstarray ++ secondarray)
  }
}
