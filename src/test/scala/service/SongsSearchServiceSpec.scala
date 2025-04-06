package service

import domain.{ArtistDetails, SongDetails}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import service.SongsSearchServiceSpec.defaultSong

import java.time.LocalDate
import java.util.UUID

class SongsSearchServiceSpec extends AnyWordSpec with Matchers {

  "SongsSearchService.searchBy" should {
    "return a song" when {
      "song title matches one in the list passed" in {
        val songs = List(defaultSong.copy(title = "another song"), defaultSong.copy(title = "some other song"), defaultSong)
        val result = SongsSearchService.searchBy("some song",
          songs)
        result mustBe Some(defaultSong)
      }
    }

    "returns empty" when {
      "song title does not match any of the songs passed" in {
        val songs = List(defaultSong.copy(title = "another song"), defaultSong.copy(title = "some other song"), defaultSong)
        val result = SongsSearchService.searchBy("non existent song",
          songs)
        result mustBe None
      }
    }
  }

}

object SongsSearchServiceSpec {
  val artist = ArtistDetails(UUID.randomUUID(), "some artist")
  val today = LocalDate.now()
  val defaultSong =
      SongDetails(
        UUID.randomUUID(),
        "some song",
        artist
      )
}
