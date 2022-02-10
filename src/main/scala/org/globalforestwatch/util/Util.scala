package org.globalforestwatch.util

import java.io.File
import com.amazonaws.services.s3.AmazonS3URI
import geotrellis.raster.RasterExtent
import geotrellis.store.index.zcurve.Z2
import geotrellis.store.s3.S3ClientProducer
import geotrellis.layer.{LayoutDefinition, SpatialKey}
import geotrellis.vector.{Extent, Feature, Geometry, Point}
import org.apache.spark.Partitioner
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Column, SparkSession}
import org.apache.spark.sql.functions.{col, struct}
import org.globalforestwatch.features.FeatureId
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.model.PutObjectRequest

import scala.reflect.runtime.universe._


object Util {
  def uploadFile(file: File, uri: AmazonS3URI): Unit = {
    val body = RequestBody.fromFile(file)
    val putObjectRequest = PutObjectRequest.builder()
      .bucket(uri.getBucket)
      .key(uri.getKey)
      .build()
    S3ClientProducer.get().putObject(putObjectRequest, body)
  }

  def sortByZIndex[G <: Geometry, A](
    features: Seq[Feature[G, A]],
    rasterExtent: RasterExtent
  ): Seq[Feature[G, A]] = {
    def zindex(p: Point): Long = {
      val col = rasterExtent.mapXToGrid(p.getX)
      val row = rasterExtent.mapYToGrid(p.getY)
      Z2(col, row).z
    }

    features.sortBy { feature =>
      zindex(Extent(feature.geom.getEnvelopeInternal).northWest)
    }
  }

  def getKeyedFeatureRDD[FEATUREID <: FeatureId](featureRDD: RDD[Feature[Geometry, FEATUREID]],
                                                 windowLayout: LayoutDefinition,
                                                 partitioner: Partitioner) = {
    featureRDD
      .flatMap { feature: Feature[Geometry, FEATUREID] =>
        val keys: Set[SpatialKey] =
          windowLayout.mapTransform.keysForGeometry(feature.geom)
        keys.toSeq.map { key =>
          (key, feature)
        }
      }
      .partitionBy(partitioner)
  }

  def getAnyMapValue[T: Manifest](map: Map[String, Any], key: String): T = {
    map.getOrElse(key, None) match {
      case v: T => v
      case _ => throw new IllegalArgumentException("Wrong type")
    }

  }

  def convertBytesToHex(bytes: Seq[Byte]): String = {
    val sb = new StringBuilder
    for (b <- bytes) {
      sb.append(String.format("%02x", Byte.box(b)))
    }
    sb.toString
  }

  def countRecordsPerPartition[T](rdd: RDD[T], spark: SparkSession): Unit = {
    import spark.implicits._

    rdd
      .mapPartitionsWithIndex { case (i, rows) => Iterator((i, rows.size)) }
      .toDF("partition_number", "number_of_records")
      .orderBy("number_of_records")
      .show(100, false)


    rdd
      .mapPartitionsWithIndex { case (i, rows) => Iterator((i, rows.size)) }
      .toDF("partition_number", "number_of_records")
      .orderBy(col("number_of_records").desc)
      .show(100, false)
  }

  /** Select columns with same names as case class fields and group them into a struct */
  def colsFor[T: TypeTag]: Column = {
    val cols = typeOf[T].members.collect {
      case m: MethodSymbol if m.isCaseAccessor => col(m.name.toString)
    }.toSeq
    struct(cols: _*)
  }

  /** Select given fields from a struct column */
  def fieldsFromCol(col: Column, fields: List[String]): List[Column] =
    fields.map(name => col.getField(name).as(name))

}
