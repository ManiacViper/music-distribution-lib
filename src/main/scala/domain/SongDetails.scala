package domain

import java.time.LocalDate
import java.util.UUID

case class ArtistDetails(id: UUID, name: String)

case class SongDetails(id: UUID,
                       title: String,
                       artist: ArtistDetails)

case class Songs(songs: List[SongDetails], proposedReleaseDate: LocalDate, isAgreedByRecordLabel: Boolean) {
  def canBeDistributed: Boolean = {
    lazy val today = LocalDate.now()
    isAgreedByRecordLabel && (proposedReleaseDate.isBefore(today) || proposedReleaseDate.isEqual(today))
  }
}
