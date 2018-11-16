package processor

import org.apache.spark.sql.types.{StructField, StructType}
import org.apache.spark.sql.{Row, SQLContext, SaveMode}
import processor.StartProcess._

import scala.util.{Failure, Success, Try}

object FixedWidthProcessor {

  /**
    * This method is for creating dataframe and writing data into HDFS and adding hive partiiton
    * Gets the table list from table xml which is passed as paramater
    * For those table extracts column names,Data types from column json passed as an argrument
    *
    * @param columnjsonPath
    * @param tablesxmlPath
    * @param runDate
    * @return
    **/

  def startFixedWidthProcess(columnjsonPath: String, tablesxmlPath: String, runDate: String) = {

    val json_parser = sqlContext.read.json(columnjsonPath)


    val tablenamesp = utils.FetchTables.getTableList(fs, tablexmlPath)


    val columnsFromTable = tablenamesp.map(passtableNames => utils.JSONParser.parseJSON(json_parser, sqlContext, passtableNames))


    columnsFromTable.map(eachCol => gettheDataFrameCreated(eachCol._1, sqlContext, eachCol._2))

    /**
      * Creates a DataFrame for a table
      * Schema -Build Struct Type based on the column names and data types
      * Data - Roww RDD is provided as input to create Data Frame
      *
      * @param col
      * @param SQLContext
      * @param tablename
      * @return
      **/
    def gettheDataFrameCreated(col: List[Row], SQLContext: SQLContext, tablename: String) = {

      val schema = StructType(col.map(_.mkString.split(",")).map(x => (x(0), x(1), x(2), x(3)))

        .map(x => StructField(x._1.trim.substring(3), utils.FixedWidthParserConstants.mapofdataTypes(x._2.trim.replaceAll("]", "")).asInstanceOf[org.apache.spark.sql.types.DataType], false)))

      logger.info("Schema for the table ")
      schema.printTreeString()


      val pos = utils.JSONParser.getPositionofColumns(json_parser, sqlContext, tablename).map(_.toString.toInt).sorted


      val data = spark.textFile(utils.FixedWidthParserConstants.baseHdfsPath + "/" + tablename + ".txt").map(x => utils.ParseFixedWidthFile.getRowfromDataFrame(x, pos, col))

      val output = Try {
        sqlContext.createDataFrame(data, schema)
          .write.mode(SaveMode.Overwrite)
          .format("orc").save(s"${utils.FixedWidthParserConstants.baseoutputHdfsPath}/${tablename}/run_date=${runDate}")

      }


      output match {

        case Success(v) => logger.info(s"Data loaded successfully for the table $tablename" + v)
          logger.info(s"Adding hive partition for the table $tablename")

          try {
            sqlContext.sql(s"msck repair table ${utils.FixedWidthParserConstants.database_fiserv}.${tablename}")
          }
          catch {
            case e: Exception => logger.error(s"Adding partition failed for the table $tablename" + e.printStackTrace())
          }


        case Failure(v) => logger.error(s"Data Loading failed for the table $tablename....." + v)


      }

    }


  }

}
