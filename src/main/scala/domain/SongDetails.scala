package domain

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.duration._

case class ArtistDetails(id: UUID, name: String)

case class SongDetails(id: UUID,
                       title: String,
                       streamed: Duration) {
  def canBeMonetized: Boolean =
    streamed > 30.seconds
}

case class Songs(artist: ArtistDetails, songs: List[SongDetails], proposedReleaseDate: LocalDate, isAgreedByRecordLabel: Boolean) {
  def canBeDistributed: Boolean = {
    lazy val today = LocalDate.now()
    isAgreedByRecordLabel && (proposedReleaseDate.isBefore(today) || proposedReleaseDate.isEqual(today))
  }
}
