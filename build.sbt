name := "Fiserv_1.6_FixedWidth"

version := "0.1"

scalaVersion := "2.10.5"

val sparkVersion = "1.6.3"


resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
  "Maven Central" at "https://repo1.maven.org/maven2/",
  Resolver.sonatypeRepo("releases"),
  //"apache-snapshots" at "http://repository.apache.org/snapshots/",
  "Hortonworks Releases" at "http://repo.hortonworks.com/content/repositories/releases",
 "Jetty Releases" at "http://repo.hortonworks.com/content/repositories/jetty-hadoop/"
  //"mvn-repository at https://mvnrepository.com/artifact/org.apache.spark/spark-hive"
)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
 // "net.hydromatic" % "eigenbase-properties" % "1.1.5",
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-hive-thriftserver" % sparkVersion,
  "org.apache.hadoop" % "hadoop-client" % "2.7.1",
  "org.apache.hadoop" % "hadoop-hdfs" % "2.7.1"

)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}