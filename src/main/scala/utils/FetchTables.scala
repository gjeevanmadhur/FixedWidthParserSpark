package utils

import org.apache.hadoop.fs.FileSystem

import scala.xml._

/* This class will fetch the table names from the list*/
object FetchTables {

  /**
    * This method is used to fetch the tables from table XMl which is passed as paramater to the framework
    *
    * @param fs
    * @param path
    * @return
    **/

  def getTableList(fs: FileSystem, path: String): List[String] = {

    val inputTableXml = XML.load(path)

    val tables = (inputTableXml \ "table")

    tables.map(x => x.text).toList

  }


}
