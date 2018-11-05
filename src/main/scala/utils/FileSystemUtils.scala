package utils

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem

/* Common singleton object for hdfs*/
object FileSystemUtils {

  val fetchFileSystem: FileSystem=FileSystem.get(new Configuration())

}
