package domain

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDate
import java.util.UUID

class SongDetailsSpec extends AnyWordSpec with Matchers {

  "Songs.canBeDistributed" should {
    "return true" when {
      "proposed release date is in the past and record label has agreed to the proposed date" in {
        val artist = ArtistDetails(UUID.randomUUID(), "some artist")
        val releasableSongs = Songs(
          artist,
          List(SongDetails(UUID.randomUUID(), "some song", isStreamable = true, List.empty)),
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
          isStreamable = true,
          List.empty)),
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
          isStreamable = true,
          List.empty)),
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
          isStreamable = true,
          List.empty)),
          tomorrow,
          isAgreedByRecordLabel = true)

        val result = unreleasableSong.canBeDistributed

        result mustBe false
      }
    }
  }


}
