val grid = "10N_010E"

case class Corner(coord: Int, nsew: String) {
  override def toString: String = {
    if (nsew == "N" || nsew == "S") "%02d".format(coord) + nsew
    else "%03d".format(coord) + nsew
  }
}

def getBottom(top: Corner): Corner = {
  val coord: Int = if (top.nsew == "N") top.coord - 10 else -top.coord - 10
  val nsew: String = if (coord >= 0) "N" else "S"
  Corner(Math.abs(coord), nsew)
}

def getRight(left: Corner): Corner = {
  val coord: Int = if (left.nsew == "E") left.coord + 10 else -left.coord + 10
  val nsew: String = if (coord >= 0) "E" else "W"
  Corner(Math.abs(coord), nsew)
}

val coord = ("""\d+""".r findAllIn grid).toList
val nsew = ("""[^0-9,_]""".r findAllIn grid).toList

val top = Corner(coord(0).toInt, nsew(0))
val left = Corner(coord(1).toInt, nsew(1))

val bottom = getBottom(top)
val right = getRight(left)

Array(left, bottom, right, top).mkString("_")