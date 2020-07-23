package org.globalforestwatch.util

import java.io.{File, FileInputStream}

import com.amazonaws.services.s3.AmazonS3URI
import com.amazonaws.services.s3.model.ObjectMetadata
import geotrellis.raster.RasterExtent
import geotrellis.layer.SpatialKey
import geotrellis.store.index.zcurve.Z2
import geotrellis.store.s3.S3ClientProducer
import geotrellis.layer.LayoutDefinition
import geotrellis.vector.{Feature, Geometry, Point, Polygon}
import org.apache.spark.Partitioner
import org.apache.spark.rdd.RDD
import org.globalforestwatch.features.FeatureId
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.model.PutObjectRequest

import scala.util.control.NonFatal

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
      val row = rasterExtent.mapXToGrid(p.getY)
      Z2(col, row).z
    }

    features.sortBy { feature =>
      zindex(feature.geom.getEnvelope.getCentroid)
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

  def getAnyMapValue[T: Manifest](map: Map[String, Any], key: String): T =
    map(key) match {
      case v: T => v
      case _ => throw new IllegalArgumentException("Wrong type")
    }
}
