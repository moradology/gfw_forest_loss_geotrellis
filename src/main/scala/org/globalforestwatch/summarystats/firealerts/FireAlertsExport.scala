package org.globalforestwatch.summarystats.firealerts

import org.apache.spark.sql.{Column, DataFrame}
import org.globalforestwatch.summarystats.SummaryExport
import org.globalforestwatch.util.Util.getAnyMapValue

object FireAlertsExport extends SummaryExport {

  override protected def exportGadm(summaryDF: DataFrame,
                                    outputUrl: String,
                                    kwargs: Map[String, Any]): Unit = {

    val fireAlertType = getAnyMapValue[String](kwargs, "fireAlertType")
    val changeOnly: Boolean =
      getAnyMapValue[Boolean](kwargs, "changeOnly")

    val spark = summaryDF.sparkSession
    import spark.implicits._

    val viirsFireCols = List(
      $"fireId.lon" as "longitude",
      $"fireId.lat" as "latitude",
      $"fireId.alertDate" as "alert__date",
      $"fireId.alertTime" as "alert__time_utc",
      $"fireId.confidence" as "confidence__cat",
      $"fireId.brightTi4" as "bright_ti4__K",
      $"fireId.brightTi5" as "bright_ti5__K",
      $"fireId.frp" as "frp__MW"
    )

    val modisFireCols = List(
      $"fireId.lon" as "longitude",
      $"fireId.lat" as "latitude",
      $"fireId.alertDate" as "alert__date",
      $"fireId.alertTime" as "alert__time_utc",
      $"fireId.confidence" as "confidence__perc",
      $"fireId.brightness" as "brightness__K",
      $"fireId.brightT31" as "bright_t31__K",
      $"fireId.frp" as "frp__MW"
    )

    val featureCols =
      List($"featureId.iso" as "iso", $"featureId.adm1" as "adm1", $"featureId.adm2" as "adm2")

    val cols = featureCols ++ (fireAlertType match {
      case "viirs" => viirsFireCols
      case "modis" => modisFireCols
    })

    //val gadmDF =
    //  summaryDF.transform(FireAlertsDF.unpackValues(cols))

    val gadmDF =
      summaryDF.transform(FireAlertsDF.unpackValues(cols))
    summaryDF.unpersist()

    gadmDF.cache()
    gadmDF
      .coalesce(300)
      .write
      .options(csvOptions)
      .csv(path = outputUrl + "/all")

    if (!changeOnly) {
      exportWhitelist(gadmDF, outputUrl)
    }
    exportChange(gadmDF, outputUrl)
    gadmDF.unpersist()
  }

  private def exportWhitelist(df: DataFrame, outputUrl: String): Unit = {
    val adm2DF = df
      .transform(FireAlertsDF.whitelist(List("iso", "adm1", "adm2")))

    adm2DF
      .coalesce(1)
      .write
      .options(csvOptions)
      .csv(path = outputUrl + "/adm2/whitelist")

    val adm1DF = adm2DF
      .transform(FireAlertsDF.whitelist2(List("iso", "adm1")))

    adm1DF
      .coalesce(1)
      .write
      .options(csvOptions)
      .csv(path = outputUrl + "/adm1/whitelist")

    val isoDF = adm1DF
      .transform(FireAlertsDF.whitelist2(List("iso")))

    isoDF
      .coalesce(1)
      .write
      .options(csvOptions)
      .csv(path = outputUrl + "/iso/whitelist")

  }

  private def exportChange(df: DataFrame, outputUrl: String): Unit = {

    val adm2DailyDF = df
      .transform(FireAlertsDF.aggChangeDaily(List("iso", "adm1", "adm2")))

    adm2DailyDF
      .coalesce(130)
      .write
      .options(csvOptions)
      .csv(path = outputUrl + "/adm2/daily_alerts")

    val adm2DF = adm2DailyDF
      .transform(FireAlertsDF.aggChangeWeekly(List("iso", "adm1", "adm2")))

    adm2DF
      .coalesce(100)
      .write
      .options(csvOptions)
      .csv(path = outputUrl + "/adm2/weekly_alerts")

    val adm1DF = adm2DF
      .transform(FireAlertsDF.aggChangeWeekly2(List("iso", "adm1")))

    adm1DF
      .coalesce(75)
      .write
      .options(csvOptions)
      .csv(path = outputUrl + "/adm1/weekly_alerts")


    val isoDF = adm1DF
      .transform(FireAlertsDF.aggChangeWeekly2(List("iso")))


    isoDF
      .coalesce(50)
      .write
      .options(csvOptions)
      .csv(path = outputUrl + "/iso/weekly_alerts")
  }

  override protected def exportWdpa(summaryDF: DataFrame,
                                    outputUrl: String,
                                    kwargs: Map[String, Any]): Unit = {

    val spark = summaryDF.sparkSession
    import spark.implicits._

    val groupByCols = List(
      "wdpa_protected_area__id",
      "wdpa_protected_area__name",
      "wdpa_protected_area__iucn_cat",
      "wdpa_protected_area__iso",
      "wdpa_protected_area__status"
    )
    val unpackCols = List(
      $"featureId.wdpaId" as "wdpa_protected_area__id",
      $"featureId.name" as "wdpa_protected_area__name",
      $"featureId.iucnCat" as "wdpa_protected_area__iucn_cat",
      $"featureId.iso" as "wdpa_protected_area__iso",
      $"featureId.status" as "wdpa_protected_area__status"
    )

    _export(summaryDF, outputUrl + "/wdpa", kwargs, groupByCols, unpackCols, wdpa = true)
  }

  override protected def exportFeature(summaryDF: DataFrame,
                                       outputUrl: String,
                                       kwargs: Map[String, Any]): Unit = {

    val spark = summaryDF.sparkSession
    import spark.implicits._

    val groupByCols = List("feature__id")
    val unpackCols = List($"featureId.featureId" as "feature__id")

    _export(summaryDF, outputUrl + "/feature", kwargs, groupByCols, unpackCols)
  }

  override protected def exportGeostore(summaryDF: DataFrame,
                                        outputUrl: String,
                                        kwargs: Map[String, Any]): Unit = {

    val spark = summaryDF.sparkSession
    import spark.implicits._


    val groupByCols = List("geostore__id")
    val unpackCols = List($"featureId.geostoreId" as "geostore__id")

    _export(summaryDF, outputUrl + "/geostore", kwargs, groupByCols, unpackCols)

  }

  private def _export(summaryDF: DataFrame,
                      outputUrl: String,
                      kwargs: Map[String, Any],
                      groupByCols: List[String],
                      unpackCols: List[Column],
                      wdpa: Boolean = false): Unit = {

    val changeOnly: Boolean = getAnyMapValue[Boolean](kwargs, "changeOnly")
    val fireAlertType = getAnyMapValue[String](kwargs, "fireAlertType")

    val spark = summaryDF.sparkSession
    import spark.implicits._

    val viirsFireCols = List(
      $"fireId.lon" as "longitude",
      $"fireId.lat" as "latitude",
      $"fireId.alertDate" as "alert__date",
      $"fireId.alertTime" as "alert__time_utc",
      $"fireId.confidence" as "confidence__cat",
      $"fireId.brightTi4" as "bright_ti4__K",
      $"fireId.brightTi5" as "bright_ti5__K",
      $"fireId.frp" as "frp__MW"
    )

    val modisFireCols = List(
      $"fireId.lon" as "longitude",
      $"fireId.lat" as "latitude",
      $"fireId.alertDate" as "alert__date",
      $"fireId.alertTime" as "alert__time_utc",
      $"fireId.confidence" as "confidence__perc",
      $"fireId.brightness" as "brightness__K",
      $"fireId.brightT31" as "bright_t31__K",
      $"fireId.frp" as "frp__MW"
    )

    val cols = groupByCols

    val unpackAllCols = unpackCols ++ (fireAlertType match {
      case "viirs" => viirsFireCols
      case "modis" => modisFireCols
    })

    val df = summaryDF.transform(
      FireAlertsDF.unpackValues(unpackAllCols, wdpa = wdpa)
    )

    df.cache()
    df.coalesce(200)
      .write
      .options(csvOptions)
      .csv(path = outputUrl + "/all")

    if (!changeOnly) {
      df.transform(FireAlertsDF.whitelist(cols, wdpa = wdpa))
        .coalesce(1)
        .write
        .options(csvOptions)
        .csv(path = outputUrl + "/whitelist")
    }

    df.transform(FireAlertsDF.aggChangeDaily(cols, wdpa = wdpa))
      .coalesce(100)
      .write
      .options(csvOptions)
      .csv(path = outputUrl + "/daily_alerts")

    df.transform(FireAlertsDF.aggChangeWeekly(cols, wdpa = wdpa))
      .coalesce(75)
      .write
      .options(csvOptions)
      .csv(path = outputUrl + "/weekly_alerts")

    df.unpersist()
    ()
  }
}
