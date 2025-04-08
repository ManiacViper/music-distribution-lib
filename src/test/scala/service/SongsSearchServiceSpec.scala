package service

import domain.{ArtistDetails, SongDetails}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import service.SongsSearchServiceSpec.defaultSong

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.duration._

class SongsSearchServiceSpec extends AnyWordSpec with Matchers {

  "SongsSearchService.searchBy" should {
    "return a song" when {
      "song title matches one in the list passed" in {
        val songs = List(
          defaultSong.copy(id = UUID.randomUUID(), title = "another song"),
          defaultSong.copy(id = UUID.randomUUID(), title = "some other song"),
          defaultSong)
        val result = SongsSearchService.searchBy("some song",
          songs)
        result mustBe Some(defaultSong)
      }
    }

    "returns empty" when {
      "song title does not match any of the songs passed" in {
        val expected = defaultSong.copy(id = UUID.randomUUID(), title = "another song")
        val songs = List(
          expected,
          defaultSong.copy(id = UUID.randomUUID(), title = "some other song"),
          defaultSong)
        val result = SongsSearchService.searchBy("similar song",
          songs)
        result mustBe Some(expected)
      }
    }
  }

}

object SongsSearchServiceSpec {
  val today = LocalDate.now()
  val defaultSong =
      SongDetails(
        UUID.randomUUID(),
        "some song",
        0.seconds
      )
}
