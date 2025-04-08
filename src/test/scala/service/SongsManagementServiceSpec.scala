package service

import cats.effect.IO
import domain.{ArtistDetails, SongDetails, Songs}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import repository.{DefaultSongsRepository, SongsRepository}
import service.SongsManagementServiceSpec.{artist, defaultSong, stubFailedRepository, stubSuccesfulRepository}

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.duration._

class SongsManagementServiceSpec extends AnyWordSpec with Matchers {

  "SongsManagementService.add" should {
    "save songs" when {
      "songs are provided" in {
        val songs = Songs(
          artist,
          List(defaultSong,
          defaultSong.copy(id = UUID.randomUUID())),
          LocalDate.now(),
          isAgreedByRecordLabel = true
        )
        val service = new DefaultSongsManagementService[IO](stubSuccesfulRepository)
        val result: Unit = service.add(songs).unsafeRunSync()

        result mustBe ()

      }

    }

    "return errors" when {
      "there are problems saving the songs" in {
        val songs = Songs(
          artist,
          List(defaultSong,
            defaultSong.copy(id = UUID.randomUUID())),
          proposedReleaseDate = LocalDate.now(),
          isAgreedByRecordLabel = true
        )
        val service = new DefaultSongsManagementService[IO](stubFailedRepository)
        val result: RuntimeException = intercept[RuntimeException] {
          service.add(songs).unsafeRunSync()
        }

        result.getMessage mustBe "some repository error"
      }

    }
  }

  "SongsManagementService.retrieve" should {
    "retrieve songs by artist" when {
      "songs exist for an artist" in {
        val songs = Songs(
          artist,
          List(defaultSong,
            defaultSong.copy(id = UUID.randomUUID())),
          proposedReleaseDate = LocalDate.now(),
          isAgreedByRecordLabel = true
        )

        //was a bit lazy by using the actual repository, would be using a stubRepo here with Map but as ive done the same in repository it is a stub essentially for now
        val service = new DefaultSongsManagementService[IO](new DefaultSongsRepository)
        val _ = service.add(songs).unsafeRunSync()
        val Some(result) = service.retrieve(artist.id).unsafeRunSync()

        result mustBe songs
      }
    }

    "no songs are returned" when {
      "there are no songs saved for an artist" in {
        val songs = Songs(
          artist,
          List(defaultSong,
            defaultSong.copy(id = UUID.randomUUID())),
          proposedReleaseDate = LocalDate.now(),
          isAgreedByRecordLabel = true
        )

        //was a bit lazy by using the actual repository, would be using a stubRepo here with Map but as ive done the same in repository it is a stub essentially for now
        val service = new DefaultSongsManagementService[IO](new DefaultSongsRepository)
        val result = service.retrieve(artist.id).unsafeRunSync()

        result mustBe None
      }
    }

    "errors are returned" when {
      "repository throws an error to retrieve songs for an artist" in {
        val songs = Songs(
          artist,
          List(defaultSong,
            defaultSong.copy(id = UUID.randomUUID())),
          proposedReleaseDate = LocalDate.now(),
          isAgreedByRecordLabel = true
        )

        val service = new DefaultSongsManagementService[IO](stubFailedRepository)
        val result = intercept[RuntimeException](service.retrieve(artist.id).unsafeRunSync())

        result.getMessage mustBe "some repository error for getSongs"
      }
    }

  }

}

object SongsManagementServiceSpec {

  val stubSuccesfulRepository: SongsRepository[IO] = new SongsRepository[IO] {
    override def addSongs(songs: Songs): IO[Unit] =
      IO(())

    override def getSongs(artistId: UUID): IO[Option[Songs]] = ???
  }

  val stubFailedRepository: SongsRepository[IO] = new SongsRepository[IO] {
    override def addSongs(songs: Songs): IO[Unit] =
      IO.raiseError(new RuntimeException("some repository error"))

    override def getSongs(artistId: UUID): IO[Option[Songs]] =
      IO.raiseError(new RuntimeException("some repository error for getSongs"))

  }
  val artist: ArtistDetails = ArtistDetails(UUID.randomUUID(), "some artist")
  val today: LocalDate = LocalDate.now()
  val defaultSong: SongDetails =
    SongDetails(
      UUID.randomUUID(),
      "some song",
      0.seconds
    )
}
