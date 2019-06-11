package org.globalforestwatch.layers

case class Bgc2000(grid: String) extends DoubleLayer with OptionalDLayer {
  val uri: String = s"$basePath/bgc_2000/$grid.tif"
}