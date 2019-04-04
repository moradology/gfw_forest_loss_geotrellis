package org.globalforestwatch.layers

class ProtectedAreas(grid: String) extends StringLayer with OptionalILayer {

  val uri: String = s"s3://wri-users/tmaschler/prep_tiles/wdpa/${grid}.tif"

  def lookup(value: Int): String = value match {
    case 1 => "Category Ia/b or II"
    case 2 => "Othe Category"
  }
}