package org.globalforestwatch.layers

import org.globalforestwatch.grids.GridTile

case class SEAsiaLandCover(gridTile: GridTile)
    extends StringLayer
    with OptionalILayer {

  //FIXME: Need to verify data lake path
  val uri: String =
    s"$basePath/south_east_asia_land_cover/v2015/raster/epsg-4326/${gridTile.gridSize}/${gridTile.rowCount}/name/gdal-geotiff/${gridTile.tileId}.tif"

  override val externalNoDataValue = "Unknown"

  def lookup(value: Int): String = value match {
    case 19 => "Bare land"
    case 18 => "Coastal fish pond"
    case 7 => "Rubber plantation"
    case 13 | 17 => "Agriculture"
    case 2 | 6 | 5 => "Secondary forest"
    case 15 | 11 => "Grassland/ shrub"
    case 20 => "Mining"
    case 10 => "Mixed tree crops"
    case 22 => "No data"
    case 8 => "Oil palm plantation"
    case 14 => "Settlements"
    case 16 | 12 => "Swamp"
    case 9 => "Timber plantation"
    case 1 | 4 | 3 => "Primary forest"
    case 21 => "Water bodies"


  }
}
