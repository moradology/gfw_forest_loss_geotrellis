package org.globalforestwatch.summarystats.gladalerts

import com.github.mrpowers.spark.daria.sql.DataFrameHelpers.validatePresenceOfColumns
import org.apache.spark.sql.functions._
import org.apache.spark.sql._

object IsoWeeklyDF {

  def sumAlerts(df: DataFrame): DataFrame = {

    val spark = df.sparkSession
    import spark.implicits._

    validatePresenceOfColumns(
      df,
      Seq(
        "iso",
        "year",
        "week",
        "primary_forest",
        "wdpa",
        "aze",
        "kba",
        "landmark",
        "plantations",
        "mining",
        "managed_forests",
        "rspo",
        "wood_fiber",
        "peatlands",
        "idn_forest_moratorium",
        "oil_palm",
        "idn_forest_area",
        "per_forest_concession",
        "oil_gas",
        "mangroves_2016",
        "ifl_2016",
        "bra_biomes",
        "alert_count",
        "alert_area_ha",
        "co2_emissions_Mt",
        "total_area_ha"
      )
    )

    df.groupBy(
      $"iso",
      $"year",
      $"week",
      $"is_confirmed",
      $"primary_forest",
      $"wdpa",
      $"aze",
      $"kba",
      $"landmark",
      $"plantations",
      $"mining",
      $"managed_forests",
      $"rspo",
      $"wood_fiber",
      $"peatlands",
      $"idn_forest_moratorium",
      $"oil_palm",
      $"idn_forest_area",
      $"per_forest_concession",
      $"oil_gas",
      $"mangroves_2016",
      $"ifl_2016",
      $"bra_biomes"
    )
      .agg(
        sum("alert_count") as "alert_count",
        sum("alert_area_ha") as "alert_area_ha",
        sum("co2_emissions_Mt") as "co2_emissions_Mt",
        sum("total_area_ha") as "total_area_ha"
      )
  }
}