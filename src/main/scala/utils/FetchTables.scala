package utils

import org.apache.commons.lang3.StringUtils
import org.apache.hadoop.fs.FileSystem

/* This class will fetch the table names from the list*/
object FetchTables {

  def getTableList(tableList: String, fs: FileSystem): scala.List[String] = tableList match {

    case x if StringUtils.isEmpty(x) => List()
   // case x if x.contains(",") =>

case x =>
      x.split(",").map(_.trim).toList

  //  case _ => throw new IllegalStateException(s"Table list is not found")
  }


}
