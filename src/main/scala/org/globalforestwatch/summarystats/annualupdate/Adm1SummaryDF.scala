package org.globalforestwatch.summarystats.annualupdate

import org.apache.spark.sql._
import org.apache.spark.sql.functions._

object Adm1SummaryDF {

  def sumArea(df: DataFrame): DataFrame = {

    val spark: SparkSession = df.sparkSession
    import spark.implicits._

    df.groupBy($"iso", $"adm1", $"threshold")
      .agg(
        sum("area_ha") as "area_ha",
        sum("extent_2000_ha") as "extent_2000_ha",
        sum("extent_2010_ha") as "extent_2010_ha",
        sum("gain_2000_2012_ha") as "gain_2000_2012_ha",
        sum("biomass_stock_2000_Mt") as "biomass_stock_2000_Mt",
        sum($"avg_biomass_per_ha_Mt" * $"extent_2000_ha") / sum(
          "extent_2000_ha"
        ) as "avg_biomass_per_ha_Mt",
        sum("co2_stock_2000_Mt") as "co2_stock_2000_Mt",
        sum("tc_loss_ha_2001") as "tc_loss_ha_2001",
        sum("tc_loss_ha_2002") as "tc_loss_ha_2002",
        sum("tc_loss_ha_2003") as "tc_loss_ha_2003",
        sum("tc_loss_ha_2004") as "tc_loss_ha_2004",
        sum("tc_loss_ha_2005") as "tc_loss_ha_2005",
        sum("tc_loss_ha_2006") as "tc_loss_ha_2006",
        sum("tc_loss_ha_2007") as "tc_loss_ha_2007",
        sum("tc_loss_ha_2008") as "tc_loss_ha_2008",
        sum("tc_loss_ha_2009") as "tc_loss_ha_2009",
        sum("tc_loss_ha_2010") as "tc_loss_ha_2010",
        sum("tc_loss_ha_2011") as "tc_loss_ha_2011",
        sum("tc_loss_ha_2012") as "tc_loss_ha_2012",
        sum("tc_loss_ha_2013") as "tc_loss_ha_2013",
        sum("tc_loss_ha_2014") as "tc_loss_ha_2014",
        sum("tc_loss_ha_2015") as "tc_loss_ha_2015",
        sum("tc_loss_ha_2016") as "tc_loss_ha_2016",
        sum("tc_loss_ha_2017") as "tc_loss_ha_2017",
        sum("tc_loss_ha_2018") as "tc_loss_ha_2018",
        sum("biomass_loss_Mt_2001") as "biomass_loss_Mt_2001",
        sum("biomass_loss_Mt_2002") as "biomass_loss_Mt_2002",
        sum("biomass_loss_Mt_2003") as "biomass_loss_Mt_2003",
        sum("biomass_loss_Mt_2004") as "biomass_loss_Mt_2004",
        sum("biomass_loss_Mt_2005") as "biomass_loss_Mt_2005",
        sum("biomass_loss_Mt_2006") as "biomass_loss_Mt_2006",
        sum("biomass_loss_Mt_2007") as "biomass_loss_Mt_2007",
        sum("biomass_loss_Mt_2008") as "biomass_loss_Mt_2008",
        sum("biomass_loss_Mt_2009") as "biomass_loss_Mt_2009",
        sum("biomass_loss_Mt_2010") as "biomass_loss_Mt_2010",
        sum("biomass_loss_Mt_2011") as "biomass_loss_Mt_2011",
        sum("biomass_loss_Mt_2012") as "biomass_loss_Mt_2012",
        sum("biomass_loss_Mt_2013") as "biomass_loss_Mt_2013",
        sum("biomass_loss_Mt_2014") as "biomass_loss_Mt_2014",
        sum("biomass_loss_Mt_2015") as "biomass_loss_Mt_2015",
        sum("biomass_loss_Mt_2016") as "biomass_loss_Mt_2016",
        sum("biomass_loss_Mt_2017") as "biomass_loss_Mt_2017",
        sum("biomass_loss_Mt_2018") as "biomass_loss_Mt_2018",
        sum("co2_emissions_Mt_2001") as "co2_emissions_Mt_2001",
        sum("co2_emissions_Mt_2002") as "co2_emissions_Mt_2002",
        sum("co2_emissions_Mt_2003") as "co2_emissions_Mt_2003",
        sum("co2_emissions_Mt_2004") as "co2_emissions_Mt_2004",
        sum("co2_emissions_Mt_2005") as "co2_emissions_Mt_2005",
        sum("co2_emissions_Mt_2006") as "co2_emissions_Mt_2006",
        sum("co2_emissions_Mt_2007") as "co2_emissions_Mt_2007",
        sum("co2_emissions_Mt_2008") as "co2_emissions_Mt_2008",
        sum("co2_emissions_Mt_2009") as "co2_emissions_Mt_2009",
        sum("co2_emissions_Mt_2010") as "co2_emissions_Mt_2010",
        sum("co2_emissions_Mt_2011") as "co2_emissions_Mt_2011",
        sum("co2_emissions_Mt_2012") as "co2_emissions_Mt_2012",
        sum("co2_emissions_Mt_2013") as "co2_emissions_Mt_2013",
        sum("co2_emissions_Mt_2014") as "co2_emissions_Mt_2014",
        sum("co2_emissions_Mt_2015") as "co2_emissions_Mt_2015",
        sum("co2_emissions_Mt_2016") as "co2_emissions_Mt_2016",
        sum("co2_emissions_Mt_2017") as "co2_emissions_Mt_2017",
        sum("co2_emissions_Mt_2018") as "co2_emissions_Mt_2018"
      )
  }

  def roundValues(df: DataFrame): DataFrame = {

    val spark: SparkSession = df.sparkSession
    import spark.implicits._

    df.select(
      $"iso" as "country",
      $"adm1" as "subnational1",
      $"threshold",
      round($"area_ha") as "area_ha",
      round($"extent_2000_ha") as "extent_2000_ha",
      round($"extent_2010_ha") as "extent_2010_ha",
      round($"gain_2000_2012_ha") as "gain_2000_2012_ha",
      round($"biomass_stock_2000_Mt") as "biomass_stock_2000_Mt",
      round($"avg_biomass_per_ha_Mt") as "avg_biomass_per_ha_Mt",
      round($"co2_stock_2000_Mt") as "co2_stock_2000_Mt",
      round($"tc_loss_ha_2001") as "tc_loss_ha_2001",
      round($"tc_loss_ha_2002") as "tc_loss_ha_2002",
      round($"tc_loss_ha_2003") as "tc_loss_ha_2003",
      round($"tc_loss_ha_2004") as "tc_loss_ha_2004",
      round($"tc_loss_ha_2005") as "tc_loss_ha_2005",
      round($"tc_loss_ha_2006") as "tc_loss_ha_2006",
      round($"tc_loss_ha_2007") as "tc_loss_ha_2007",
      round($"tc_loss_ha_2008") as "tc_loss_ha_2008",
      round($"tc_loss_ha_2009") as "tc_loss_ha_2009",
      round($"tc_loss_ha_2010") as "tc_loss_ha_2010",
      round($"tc_loss_ha_2011") as "tc_loss_ha_2011",
      round($"tc_loss_ha_2012") as "tc_loss_ha_2012",
      round($"tc_loss_ha_2013") as "tc_loss_ha_2013",
      round($"tc_loss_ha_2014") as "tc_loss_ha_2014",
      round($"tc_loss_ha_2015") as "tc_loss_ha_2015",
      round($"tc_loss_ha_2016") as "tc_loss_ha_2016",
      round($"tc_loss_ha_2017") as "tc_loss_ha_2017",
      round($"tc_loss_ha_2018") as "tc_loss_ha_2018",
      round($"biomass_loss_Mt_2001") as "biomass_loss_Mt_2001",
      round($"biomass_loss_Mt_2002") as "biomass_loss_Mt_2002",
      round($"biomass_loss_Mt_2003") as "biomass_loss_Mt_2003",
      round($"biomass_loss_Mt_2004") as "biomass_loss_Mt_2004",
      round($"biomass_loss_Mt_2005") as "biomass_loss_Mt_2005",
      round($"biomass_loss_Mt_2006") as "biomass_loss_Mt_2006",
      round($"biomass_loss_Mt_2007") as "biomass_loss_Mt_2007",
      round($"biomass_loss_Mt_2008") as "biomass_loss_Mt_2008",
      round($"biomass_loss_Mt_2009") as "biomass_loss_Mt_2009",
      round($"biomass_loss_Mt_2010") as "biomass_loss_Mt_2010",
      round($"biomass_loss_Mt_2011") as "biomass_loss_Mt_2011",
      round($"biomass_loss_Mt_2012") as "biomass_loss_Mt_2012",
      round($"biomass_loss_Mt_2013") as "biomass_loss_Mt_2013",
      round($"biomass_loss_Mt_2014") as "biomass_loss_Mt_2014",
      round($"biomass_loss_Mt_2015") as "biomass_loss_Mt_2015",
      round($"biomass_loss_Mt_2016") as "biomass_loss_Mt_2016",
      round($"biomass_loss_Mt_2017") as "biomass_loss_Mt_2017",
      round($"biomass_loss_Mt_2018") as "biomass_loss_Mt_2018",
      round($"co2_emissions_Mt_2001") as "co2_emissions_Mt_2001",
      round($"co2_emissions_Mt_2002") as "co2_emissions_Mt_2002",
      round($"co2_emissions_Mt_2003") as "co2_emissions_Mt_2003",
      round($"co2_emissions_Mt_2004") as "co2_emissions_Mt_2004",
      round($"co2_emissions_Mt_2005") as "co2_emissions_Mt_2005",
      round($"co2_emissions_Mt_2006") as "co2_emissions_Mt_2006",
      round($"co2_emissions_Mt_2007") as "co2_emissions_Mt_2007",
      round($"co2_emissions_Mt_2008") as "co2_emissions_Mt_2008",
      round($"co2_emissions_Mt_2009") as "co2_emissions_Mt_2009",
      round($"co2_emissions_Mt_2010") as "co2_emissions_Mt_2010",
      round($"co2_emissions_Mt_2011") as "co2_emissions_Mt_2011",
      round($"co2_emissions_Mt_2012") as "co2_emissions_Mt_2012",
      round($"co2_emissions_Mt_2013") as "co2_emissions_Mt_2013",
      round($"co2_emissions_Mt_2014") as "co2_emissions_Mt_2014",
      round($"co2_emissions_Mt_2015") as "co2_emissions_Mt_2015",
      round($"co2_emissions_Mt_2016") as "co2_emissions_Mt_2016",
      round($"co2_emissions_Mt_2017") as "co2_emissions_Mt_2017",
      round($"co2_emissions_Mt_2018") as "co2_emissions_Mt_2018"
    )
  }
}
