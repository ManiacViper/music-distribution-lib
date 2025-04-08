package domain

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.duration._

class SongDetailsSpec extends AnyWordSpec with Matchers {

  "Songs.canBeDistributed" should {
    "return true" when {
      "proposed release date is in the past and record label has agreed to the proposed date" in {
        val artist = ArtistDetails(UUID.randomUUID(), "some artist")
        val releasableSongs = Songs(
          artist,
          List(SongDetails(
            UUID.randomUUID(),
            "some song",
            0.seconds)),
          LocalDate.now().minusDays(1),
          isAgreedByRecordLabel = true)

        val result = releasableSongs.canBeDistributed

        result mustBe true
      }

      "proposed release date is today and record label has agreed to the proposed date" in {
        val artist = ArtistDetails(UUID.randomUUID(), "some artist")
        val today = LocalDate.now()
        val releasableSong = Songs(artist, List(SongDetails(
          UUID.randomUUID(),
          "some song",
          0.seconds)),
          today,
          isAgreedByRecordLabel = true)

        val result = releasableSong.canBeDistributed

        result mustBe true
      }
    }

    "return false" when {
      "record label has not agreed to proposed released date" in {
        val artist = ArtistDetails(UUID.randomUUID(), "some artist")
        val today = LocalDate.now()
        val unreleasableSong = Songs(artist, List(SongDetails(
          UUID.randomUUID(),
          "some song",
          0.seconds)),
          today,
          isAgreedByRecordLabel = false)

        val result = unreleasableSong.canBeDistributed

        result mustBe false
      }

      "proposed release date is in the future" in {
        val artist = ArtistDetails(UUID.randomUUID(), "some artist")
        val tomorrow = LocalDate.now().plusDays(1)
        val unreleasableSong = Songs(artist, List(SongDetails(
              UUID.randomUUID(),
              "some song",
              0.seconds)),
          tomorrow,
          isAgreedByRecordLabel = true)

        val result = unreleasableSong.canBeDistributed

        result mustBe false
      }
    }
  }

  "SongDetails.canBeDistributed" should {
    "return true" when {
      "streaming for longer than 30 seconds" in {
        val songDetail = SongDetails(UUID.randomUUID(), "some song", 31.seconds)
        songDetail.canBeMonetized mustBe true
      }
    }

    "return false" when {
      "streaming for 30 seconds" in {
        val songDetail = SongDetails(UUID.randomUUID(), "some song", 30.seconds)
        songDetail.canBeMonetized mustBe false
      }

      "streaming for less than 30 seconds" in {
        val songDetail = SongDetails(UUID.randomUUID(), "some song", 29.seconds)
        songDetail.canBeMonetized mustBe false
      }
    }
  }

}
