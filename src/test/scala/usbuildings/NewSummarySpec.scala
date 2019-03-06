package usbuildings

import geotrellis.raster._
import geotrellis.vector._
import geotrellis.contrib.vlm.geotiff._
import org.scalatest._
import geotrellis.contrib.polygonal.PolygonalSummary.ops._
import geotrellis.contrib.polygonal.Implicits._
import geotrellis.contrib.polygonal._
import geotrellis.vector.io.wkt.WKT
import cats._

/* application specific accumulator for polygonal summary */
case class MyMean(total: Double, count: Long)

/**
 *  Contained example of how to add application specific accumulator for polygonal summary
 */
object MyMean {
  // Tell me how to add cell values to your accumulator
  implicit val myMeanIsCellAccumulator: CellAccumulator[MyMean] = {
    new CellAccumulator[MyMean] {
      def add(self: MyMean, v: Int): MyMean = {
        MyMean(self.total + v, self.count + 1)
      }

      def add(self: MyMean, v: Double): MyMean = {
        MyMean(self.total + v, self.count + 1)
      }
    }
  }

  // Tell me what is empty and how to combine them
  implicit val myMeanIsMonoid: Monoid[MyMean] =
    new Monoid[MyMean] {
      def empty: MyMean = MyMean(0, 0L)
      def combine(x: MyMean, y: MyMean): MyMean =
        MyMean(x.total + y.total, x.count + y.count)
    }
}

class NewSummarySpec extends FunSpec {
  val rasterSource = GeoTiffRasterSource("s3://gfw2-data/forest_change/hansen_2018/50N_080W.tif")
  val raster: Raster[Tile] = rasterSource.read(Extent(-72.97754892, 43.85921846, -72.80676639, 43.97153490)).get.mapTile(_.band(0))

  val nonIntersectingWkt = "POLYGON ((-73.175445 43.055058, -73.175373 43.055098, -73.175462 43.055181, -73.175534 43.055141, -73.175445 43.055058))"
  val nonIntersectingGeom: Geometry = WKT.read(nonIntersectingWkt)

  it("will perform a summary (non-intersecting)") {
    val Feature(_, mymean) =
      raster.polygonalSummary[Geometry, MyMean](nonIntersectingGeom)
    info(s"Result: $mymean")
  }

  val intersectingGeom = raster.extent.toPolygon
  it("will perform a summary (intersecting)") {
    val Feature(_, mymean) =
      raster.polygonalSummary[Polygon, MyMean](intersectingGeom)
    info(s"raster cols: ${raster.cols} rows: ${raster.rows}")
    info(s"Result: $mymean")
  }

}