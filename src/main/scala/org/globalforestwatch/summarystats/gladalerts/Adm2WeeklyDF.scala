package org.globalforestwatch.summarystats.gladalerts

import com.github.mrpowers.spark.daria.sql.DataFrameHelpers.validatePresenceOfColumns
import org.apache.spark.sql._
import org.apache.spark.sql.functions._

object Adm2WeeklyDF {

  def sumAlerts(df: DataFrame): DataFrame = {

    val spark = df.sparkSession
    import spark.implicits._

    validatePresenceOfColumns(
      df,
      Seq(
        "iso",
        "adm1",
        "adm2",
        "alert__date",
        "is__confirmed_alert",
        "is__regional_primary_forest",
        "wdpa_protected_area__iucn_cat",
        "is__alliance_for_zero_extinction_site",
        "is__key_biodiversity_area",
        "is__landmark",
        "gfw_plantation__type",
        "is__gfw_mining",
        "is__gfw_logging",
        "rspo_oil_palm__certification_status",
        "is__gfw_wood_fiber",
        "is__peat_land",
        "is__idn_forest_moratorium",
        "is__gfw_oil_palm",
        "idn_forest_area",
        "per_forest_concession__type",
        "is__gfw_oil_gas",
        "is__mangroves_2016",
        "intact_forest_landscapes_2016",
        "bra_biome__name",
        "alert_count",
        "alert_area__ha",
        "aboveground_co2_emissions__Mg"
      )
    )

    df
      .select(
        $"iso",
        $"adm1",
        $"adm2",
        year($"alert__date") as "alert__year",
        weekofyear($"alert__date") as "alert__week",
        $"is__confirmed_alert",
        $"is__regional_primary_forest",
        $"wdpa_protected_area__iucn_cat",
        $"is__alliance_for_zero_extinction_site",
        $"is__key_biodiversity_area",
        $"is__landmark",
        $"gfw_plantation__type",
        $"is__gfw_mining",
        $"is__gfw_logging",
        $"rspo_oil_palm__certification_status",
        $"is__gfw_wood_fiber",
        $"is__peat_land",
        $"is__idn_forest_moratorium",
        $"is__gfw_oil_palm",
        $"idn_forest_area",
        $"per_forest_concession__type",
        $"is__gfw_oil_gas",
        $"is__mangroves_2016",
        $"intact_forest_landscapes_2016",
        $"bra_biome__name",
        $"alert_count",
        $"alert_area__ha",
        $"aboveground_co2_emissions__Mg"
      )
      .groupBy(
        $"iso",
        $"adm1",
        $"adm2",
        $"alert__year",
        $"alert__week",
        $"is__confirmed_alert",
        $"is__regional_primary_forest",
        $"wdpa_protected_area__iucn_cat",
        $"is__alliance_for_zero_extinction_site",
        $"is__key_biodiversity_area",
        $"is__landmark",
        $"gfw_plantation__type",
        $"is__gfw_mining",
        $"is__gfw_logging",
        $"rspo_oil_palm__certification_status",
        $"is__gfw_wood_fiber",
        $"is__peat_land",
        $"is__idn_forest_moratorium",
        $"is__gfw_oil_palm",
        $"idn_forest_area",
        $"per_forest_concession__type",
        $"is__gfw_oil_gas",
        $"is__mangroves_2016",
        $"intact_forest_landscapes_2016",
        $"bra_biome__name"
      )
      .agg(
        sum("alert_count") as "alert_count",
        sum("alert_area__ha") as "alert_area__ha",
        sum("aboveground_co2_emissions__Mg") as "aboveground_co2_emissions__Mg"
      )
  }
}
