package org.globalforestwatch.summarystats.forest_change_diagnostic

import cats.Semigroup

/** Summary data per class
  *
  * Note: This case class contains mutable values
  */
case class ForestChangeDiagnosticData(
                                       treeCoverLossTotalYearly: ForestChangeDiagnosticTCLYearly,
                                       treeCoverLossPrimaryForestYearly: ForestChangeDiagnosticTCLYearly,
                                       treeCoverLossPeatLandYearly: ForestChangeDiagnosticTCLYearly,
                                       treeCoverLossIntactForestYearly: ForestChangeDiagnosticTCLYearly,
                                       treeCoverLossProtectedAreasYearly: ForestChangeDiagnosticTCLYearly,
                                       treeCoverLossSEAsiaLandCoverYearly: ForestChangeDiagnosticTCLClassYearly,
                                       treeCoverLossIDNLandCoverYearly: ForestChangeDiagnosticTCLClassYearly,
                                       treeCoverLossSoyPlanedAreasYearly: ForestChangeDiagnosticTCLYearly,
                                       treeCoverLossIDNForestAreaYearly: ForestChangeDiagnosticTCLClassYearly
) {

  def merge(other: ForestChangeDiagnosticData): ForestChangeDiagnosticData = {

    ForestChangeDiagnosticData(
      treeCoverLossTotalYearly.merge(other.treeCoverLossTotalYearly),
      treeCoverLossPrimaryForestYearly.merge(
        other.treeCoverLossPrimaryForestYearly
      ),
      treeCoverLossPeatLandYearly.merge(other.treeCoverLossPeatLandYearly),
      treeCoverLossIntactForestYearly.merge(
        other.treeCoverLossIntactForestYearly
      ),
      treeCoverLossProtectedAreasYearly.merge(
        other.treeCoverLossProtectedAreasYearly
      ),
      treeCoverLossSEAsiaLandCoverYearly.merge(
        other.treeCoverLossSEAsiaLandCoverYearly
      ),
      treeCoverLossIDNLandCoverYearly.merge(
        other.treeCoverLossIDNLandCoverYearly
      ),
      treeCoverLossSoyPlanedAreasYearly.merge(
        other.treeCoverLossSoyPlanedAreasYearly
      ),
      treeCoverLossIDNForestAreaYearly.merge(
        other.treeCoverLossIDNForestAreaYearly
      )
    )
  }
}

object ForestChangeDiagnosticData {

  def empty: ForestChangeDiagnosticData = ForestChangeDiagnosticData(
    ForestChangeDiagnosticTCLYearly.empty,
    ForestChangeDiagnosticTCLYearly.empty,
    ForestChangeDiagnosticTCLYearly.empty,
    ForestChangeDiagnosticTCLYearly.empty,
    ForestChangeDiagnosticTCLYearly.empty,
    ForestChangeDiagnosticTCLClassYearly.empty,
    ForestChangeDiagnosticTCLClassYearly.empty,
    ForestChangeDiagnosticTCLYearly.empty,
    ForestChangeDiagnosticTCLClassYearly.empty
  )

  implicit val lossDataSemigroup: Semigroup[ForestChangeDiagnosticData] =
    new Semigroup[ForestChangeDiagnosticData] {
      def combine(x: ForestChangeDiagnosticData,
                  y: ForestChangeDiagnosticData): ForestChangeDiagnosticData =
        x.merge(y)
    }

}
