package org.globalforestwatch.layers

case class GrossCumulRemovalsCarbon(grid: String)
    extends DoubleLayer
    with RequiredDLayer {
  val uri: String = s"$basePath/gross_cumul_removals_carbon/$grid.tif"
}
