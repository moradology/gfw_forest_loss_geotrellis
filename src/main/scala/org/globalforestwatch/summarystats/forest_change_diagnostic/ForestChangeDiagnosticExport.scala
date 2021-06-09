package org.globalforestwatch.summarystats.forest_change_diagnostic

import org.apache.spark.sql.DataFrame
import org.globalforestwatch.summarystats.SummaryExport

object ForestChangeDiagnosticExport extends SummaryExport {

  override val csvOptions: Map[String, String] = Map(
    "header" -> "true",
    "delimiter" -> "\t",
    "quote" -> "\u0000",
    "escape" -> "\u0000",
    "quoteMode" -> "NONE",
    "nullValue" -> null,
    "emptyValue" -> null
  )

  override def export(featureType: String,
                      summaryDF: DataFrame,
                      outputUrl: String,
                      kwargs: Map[String, Any]): Unit = {

    featureType match {

      case "feature" => exportFeature(summaryDF, outputUrl, kwargs)
      case "intermediate" => exportIntermediateList(summaryDF, outputUrl)
      case _ =>
        throw new IllegalArgumentException(
          "Feature type must be one of 'feature', 'intermediate'"
        )
    }
  }

  override protected def exportFeature(summaryDF: DataFrame,
                                       outputUrl: String,
                                       kwargs: Map[String, Any]): Unit = {

    summaryDF
      .coalesce(1)
      .write
      .options(csvOptions)
      .csv(path = outputUrl + "/final")

  }

  def exportIntermediateList(intermediateListDF: DataFrame,
                             outputUrl: String): Unit = {

    intermediateListDF
      .coalesce(1)
      .write
      .options(csvOptions)
      .csv(path = outputUrl + "/intermediate")
  }

}
