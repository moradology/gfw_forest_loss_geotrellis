package org.globalforestwatch.carbonflux

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.monovore.decline.{CommandApp, Opts}
import org.apache.log4j.Logger
import org.apache.spark._
import org.apache.spark.rdd._
import org.apache.spark.sql._
import cats.implicits._
import geotrellis.vector.{Feature, Geometry}
import org.globalforestwatch.features.{GadmFeatureFilter, GadmFeature, GadmFeatureId}

object CarbonFluxSummaryMain
    extends CommandApp(
      name = "geotrellis-carbon-flux-summary",
      header = "Compute statistics on carbon flux",
      main = {

        val featuresOpt =
          Opts.options[String]("features", "URI of features in TSV format")

        val outputOpt =
          Opts.option[String]("output", "URI of output dir for CSV files")

        // Can be used to increase the level of job parallelism
        val intputPartitionsOpt = Opts
          .option[Int]("input-partitions", "Partition multiplier for input")
          .withDefault(16)

        // Can be used to consolidate output into fewer files
        val outputPartitionsOpt = Opts
          .option[Int](
            "output-partitions",
            "Number of output partitions / files to be written"
          )
          .orNone

        val limitOpt = Opts
          .option[Int]("limit", help = "Limit number of records processed")
          .orNone

        val isoFirstOpt =
          Opts
            .option[String](
              "iso_first",
              help = "Filter by first letter of ISO code"
            )
            .orNone

        val isoStartOpt =
          Opts
            .option[String](
              "iso_start",
              help = "Filter by ISO code larger than or equal to given value"
            )
            .orNone

        val isoEndOpt =
          Opts
            .option[String](
              "iso_end",
              help = "Filter by ISO code smaller than given value"
            )
            .orNone

        val isoOpt =
          Opts.option[String]("iso", help = "Filter by country ISO code").orNone

        val admin1Opt = Opts
          .option[String]("admin1", help = "Filter by country Admin1 code")
          .orNone

        val admin2Opt = Opts
          .option[String]("admin2", help = "Filter by country Admin2 code")
          .orNone

        val logger = Logger.getLogger("CarbonFluxSummaryMain")

        (
          featuresOpt,
          outputOpt,
          intputPartitionsOpt,
          outputPartitionsOpt,
          limitOpt,
          isoOpt,
          isoFirstOpt,
          isoStartOpt,
          isoEndOpt,
          admin1Opt,
          admin2Opt
        ).mapN {
          (featureUris,
           outputUrl,
           inputPartitionMultiplier,
           maybeOutputPartitions,
           limit,
           iso,
           isoFirst,
           isoStart,
           isoEnd,
           admin1,
           admin2) =>
            val spark: SparkSession = CarbonFluxSparkSession()
            import spark.implicits._

            // ref: https://github.com/databricks/spark-csv
            val featuresDF: DataFrame = spark.read
              .options(Map("header" -> "true", "delimiter" -> "\t"))
              .csv(featureUris.toList: _*)
              .transform(
                GadmFeatureFilter.filter(
                  isoFirst,
                  isoStart,
                  isoEnd,
                  iso,
                  admin1,
                  admin2,
                  limit
                )(spark)
              )

            /* Transition from DataFrame to RDD in order to work with GeoTrellis features */
            val featureRDD: RDD[Feature[Geometry, GadmFeatureId]] =
              featuresDF.rdd.mapPartitions({ iter: Iterator[Row] =>
                for {
                  i <- iter
                  if GadmFeature.isValidGeom(i)
                } yield {
                  GadmFeature.getFeature(i)
                }
              }, preservesPartitioning = true)

            val part = new HashPartitioner(
              partitions = featureRDD.getNumPartitions * inputPartitionMultiplier
            )

            val summaryRDD: RDD[(GadmFeatureId, CarbonFluxSummary)] =
              CarbonFluxRDD(featureRDD, CarbonFluxGrid.blockTileGrid, part)

            val summaryDF =
              summaryRDD
                .flatMap {
                  case (id, treeLossSummary) =>
                    treeLossSummary.stats.map {
                      case (lossDataGroup, lossData) => {

                        val admin1: Integer = id.adm1ToInt
                        val admin2: Integer = id.adm2ToInt

                        CarbonFluxRow(
                          CarbonFluxRowFeatureId(id.country, admin1, admin2),
                          lossDataGroup.threshold,
                          CarbonFluxRowLayers(
                            lossDataGroup.gain,
                            lossDataGroup.mangroveBiomassExtent,
                            lossDataGroup.drivers,
                            lossDataGroup.ecozones,
                            lossDataGroup.landRights,
                            lossDataGroup.wdpa,
                            lossDataGroup.intactForestLandscapes,
                            lossDataGroup.plantations
                          ),
                          lossData.extent2000,
                          lossData.totalArea,
                          lossData.totalBiomass,
                          lossData.biomassHistogram.mean(),
                          lossData.totalGrossAnnualRemovalsCarbon,
                          lossData.grossAnnualRemovalsCarbonHistogram.mean(),
                          lossData.totalGrossCumulRemovalsCarbon,
                          lossData.grossCumulRemovalsCarbonHistogram.mean(),
                          lossData.totalNetFluxCo2,
                          lossData.netFluxCo2Histogram.mean(),
                          lossData.totalAgcEmisYear,
                          lossData.agcEmisYearHistogram.mean(),
                          lossData.totalBgcEmisYear,
                          lossData.bgcEmisYearHistogram.mean(),
                          lossData.totalDeadwoodCarbonEmisYear,
                          lossData.deadwoodCarbonEmisYearHistogram.mean(),
                          lossData.totalLitterCarbonEmisYear,
                          lossData.litterCarbonEmisYearHistogram.mean(),
                          lossData.totalSoilCarbonEmisYear,
                          lossData.soilCarbonEmisYearHistogram.mean(),
                          lossData.totalCarbonEmisYear,
                          lossData.totalCarbonEmisYearHistogram.mean(),
                          lossData.totalAgc2000,
                          lossData.agc2000Histogram.mean(),
                          lossData.totalBgc2000,
                          lossData.bgc2000Histogram.mean(),
                          lossData.totalDeadwoodCarbon2000,
                          lossData.deadwoodCarbon2000Histogram.mean(),
                          lossData.totalLitterCarbon2000,
                          lossData.litterCarbon2000Histogram.mean(),
                          lossData.totalSoil2000Year,
                          lossData.soilCarbon2000Histogram.mean(),
                          lossData.totalCarbon2000,
                          lossData.totalCarbon2000Histogram.mean(),
                          lossData.totalGrossEmissionsCo2,
                          lossData.grossEmissionsCo2Histogram.mean(),
                          CarbonFluxYearDataMap.toList(lossData.lossYear)
                        )
                      }
                    }
                }
                .toDF(
                  "feature_id",
                  "threshold",
                  "layers",
                  "extent_2000",
                  "total_area",
                  "total_biomass",
                  "avg_biomass_per_ha",
                  "gross_annual_removals_carbon",
                  "avg_gross_annual_removals_carbon_ha",
                  "gross_cumul_removals_carbon",
                  "avg_gross_cumul_removals_carbon_ha",
                  "net_flux_co2",
                  "avg_net_flux_co2_ha",
                  "agc_emissions_year",
                  "avg_agc_emissions_year",
                  "bgc_emissions_year",
                  "avg_bgc_emissions_year",
                  "deadwood_carbon_emissions_year",
                  "avg_deadwood_carbon_emissions_year",
                  "litter_carbon_emissions_year",
                  "avg_litter_carbon_emissions_year",
                  "soil_carbon_emissions_year",
                  "avg_soil_carbon_emissions_year",
                  "total_carbon_emissions_year",
                  "avg_carbon_emissions_year",
                  "agc_2000",
                  "avg_agc_2000",
                  "bgc_2000",
                  "avg_bgc_2000",
                  "deadwood_carbon_2000",
                  "avg_deadwood_carbon_2000",
                  "litter_carbon_2000",
                  "avg_litter_carbon_2000",
                  "soil_2000_year",
                  "avg_soil_carbon_2000",
                  "total_carbon_2000",
                  "avg_carbon_2000",
                  "gross_emissions_co2",
                  "avg_gross_emissions_co2",
                  "year_data"
                )

            val runOutputUrl = outputUrl + "/carbonflux_" +
              DateTimeFormatter
                .ofPattern("yyyyMMdd_HHmm")
                .format(LocalDateTime.now)

            val outputPartitionCount =
              maybeOutputPartitions.getOrElse(featureRDD.getNumPartitions)

            summaryDF.repartition($"feature_id", $"threshold")

            val csvOptions = Map(
              "header" -> "true",
              "delimiter" -> "\t",
              "quote" -> "\u0000",
              "quoteMode" -> "NONE",
              "nullValue" -> "\u0000"
            )

            summaryDF
              .transform(ApiDF.unpackValues(spark))
              // .transform(ApiDF.setNull)
              .coalesce(1)
              .orderBy($"iso", $"adm1", $"adm2", $"threshold")
              .write
              .options(csvOptions)
              .csv(path = runOutputUrl + "/summary/adm2")

            spark.stop
        }
      }
    )
