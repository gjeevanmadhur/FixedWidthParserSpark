package utils

import org.apache.log4j.Logger
import org.apache.spark.sql.Row

/** This class will build the Row by using positions from config **/
object ParseFixedWidthFile {

  def getRowfromDataFrame(x: String, pos: List[Int], col: List[Row]): Row = {

    val logger:Logger = Logger.getLogger(ParseFixedWidthFile.getClass)

    import scala.collection.immutable.ListMap


      val t_map = pos.grouped(2).map {
        case List(key, value) => key -> value
      }.toMap

      val smap = ListMap(t_map.toSeq.sortBy(_._1): _*)


      val v = smap.map {
        case (k, v) => x.substring(k.toString.toInt, v.toString.toInt)
      }


      val schmemaRegister = List(col.map(_.mkString.split(",")).map(x => (x(0), x(1), x(2), x(3))).map(x => (x._2.trim.replaceAll("]", ""),
        x._3.replaceAll("]", "").toInt, x._4.replaceAll("]", "").toInt)))


      val builddata = v.zip(schmemaRegister.flatten).map {

        x =>
          x._2._1 match {

            case "StringType" => x._1.mkString

            case "IntegerType" => x._1.toInt

            case "DecimalType" => //x._1.mkString

              BigDecimal("+" + x._1.substring(0, x._2._2.toInt) + "." + x._1.substring(x._2._2.toInt, x._1.length - 1))

            case "LongType" => x._1.toLong


            case _ => x._1.mkString
          }
      }

      Row.fromSeq(builddata.toSeq)


  }


}
