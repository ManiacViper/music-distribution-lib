package repository

import cats.effect.IO
import domain.{ArtistDetails, SongDetails, Songs}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import repository.DefaultSongsRepositorySpec.{artist, defaultSong}

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.duration._

class DefaultSongsRepositorySpec extends AnyWordSpec with Matchers {

  "DefaultSongsRepository.addSongs" should {
    "save the songs" when {
      "the songs are provided" in {
        val songs = Songs(artist, List(defaultSong, defaultSong.copy(UUID.randomUUID())), LocalDate.now(), isAgreedByRecordLabel = true)
        val repo = new DefaultSongsRepository[IO]()

        val _ = repo.addSongs(songs).unsafeRunSync()
        val Some(result) =
          repo
          .getSongs(songs.artist.id)
          .unsafeRunSync()

        result mustBe songs
      }
    }

    "not save songs" when {
      "there are no songs" in {
        val songs = Songs(artist, List.empty, LocalDate.now(), isAgreedByRecordLabel = true)
        val repo = new DefaultSongsRepository[IO]()

        val _ = repo.addSongs(songs).unsafeRunSync()
        val result =
          repo
            .getSongs(songs.artist.id)
            .unsafeRunSync()

        result mustBe None
      }
    }

  }
}

object DefaultSongsRepositorySpec {
  val artist: ArtistDetails = ArtistDetails(UUID.randomUUID(), "some artist")
  val today: LocalDate = LocalDate.now()
  val defaultSong: SongDetails =
    SongDetails(
      UUID.randomUUID(),
      "some song",
      List.empty
    )
}
