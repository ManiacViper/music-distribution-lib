package domain

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.duration._

case class ArtistDetails(id: UUID, name: String)
case class StreamPayment(id: UUID, streamed: Duration, lastPaymentDate: Option[LocalDate]) {
  def canBeMonetized: Boolean =
    streamed > 30.seconds

  def isPayable: Boolean =
    lastPaymentDate.isEmpty
}
case class SongDetails(id: UUID,
                       title: String,
                       streamPayments: List[StreamPayment])

case class Songs(artist: ArtistDetails, songs: List[SongDetails], proposedReleaseDate: LocalDate, isAgreedByRecordLabel: Boolean) {
  def canBeDistributed: Boolean = {
    lazy val today = LocalDate.now()
    isAgreedByRecordLabel && (proposedReleaseDate.isBefore(today) || proposedReleaseDate.isEqual(today))
  }
}
