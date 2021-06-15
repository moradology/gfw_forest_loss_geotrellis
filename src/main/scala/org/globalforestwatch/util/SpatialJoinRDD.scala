package org.globalforestwatch.util

import com.vividsolutions.jts.geom.Geometry
import org.apache.spark.api.java.JavaPairRDD
import org.datasyslab.geospark.enums.{GridType, IndexType}
import org.datasyslab.geospark.spatialOperator.JoinQuery
import org.datasyslab.geospark.spatialRDD.SpatialRDD
import org.globalforestwatch.summarystats.forest_change_diagnostic.ForestChangeDiagnosticAnalysis

import java.util

object SpatialJoinRDD {

  def spatialjoin[A <: Geometry, B <: Geometry](
                                                 querySpatialRDD: SpatialRDD[A],
                                                 valueSpatialRDD: SpatialRDD[B],
                                                 buildOnSpatialPartitionedRDD: Boolean = true, // Set to TRUE only if run join query
                                                 considerBoundaryIntersection: Boolean = false, // Only return gemeotries fully covered by each query window in queryWindowRDD
                                                 usingIndex: Boolean = false
                                               ): JavaPairRDD[A, util.HashSet[B]] = {

    querySpatialRDD.spatialPartitioning(GridType.QUADTREE)

    valueSpatialRDD.spatialPartitioning(querySpatialRDD.getPartitioner)

    if (usingIndex)
      querySpatialRDD.buildIndex(
        IndexType.QUADTREE,
        buildOnSpatialPartitionedRDD
      )

    JoinQuery.SpatialJoinQuery(
      valueSpatialRDD,
      querySpatialRDD,
      usingIndex,
      considerBoundaryIntersection
    )
  }

  def flatSpatialJoin[A <: Geometry, B <: Geometry](
                                                     largerSpatialRDD: SpatialRDD[A],
                                                     smallerSpatialRDD: SpatialRDD[B],
                                                     buildOnSpatialPartitionedRDD: Boolean = true, // Set to TRUE only if run join query
                                                     considerBoundaryIntersection: Boolean = false, // Only return gemeotries fully covered by each query window in queryWindowRDD
                                                     usingIndex: Boolean = false
                                                   ): JavaPairRDD[B, A] = {

    try {
      largerSpatialRDD.spatialPartitioning(GridType.QUADTREE)
      smallerSpatialRDD.spatialPartitioning(largerSpatialRDD.getPartitioner)
      if (usingIndex)
        largerSpatialRDD.buildIndex(
          IndexType.QUADTREE,
          buildOnSpatialPartitionedRDD
        )
    } catch {
      case _: java.lang.IllegalArgumentException =>
        ForestChangeDiagnosticAnalysis.logger.warn(
          "Skip spatial partitioning. Dataset too small."
        )

    }

    JoinQuery.SpatialJoinQueryFlat(
      largerSpatialRDD,
      smallerSpatialRDD,
      usingIndex,
      considerBoundaryIntersection
    )
  }

}
