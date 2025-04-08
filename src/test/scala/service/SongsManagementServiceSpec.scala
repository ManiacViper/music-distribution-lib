package service

import cats.effect.IO
import domain.{ArtistDetails, SongDetails, Songs, StreamPayment}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import repository.{DefaultSongsRepository, SongsRepository}
import service.SongsManagementServiceSpec.{artist, defaultSong, stubFailedRepository, stubSuccesfulRepository}

import java.time.LocalDate
import java.util.UUID
import scala.List
import scala.concurrent.duration.DurationInt

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
        val service = new DefaultSongsManagementService[IO](stubSuccesfulRepository())
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

  "SongsManagementService.fileForPayment" should {

    "update songs with payment date" when {
      "songs have no payment date and can be monetized" in {
        val payableStreamOne = StreamPayment(UUID.randomUUID(), 31.seconds, None)
        val payableStreamTwo = StreamPayment(UUID.randomUUID(), 45.seconds, None)
        val nonPayableStreamOne = StreamPayment(UUID.randomUUID(), 30.seconds, None)
        val streamPaymentsForFirstSong = List(
          payableStreamOne,
          nonPayableStreamOne
        )

        val paidStream = StreamPayment(UUID.randomUUID(), 60.seconds, Option(LocalDate.now().minusDays(2)))
        val nonPayableStreamTwo = StreamPayment(UUID.randomUUID(), 30.seconds, None)
        val streamPaymentsForSecondSong = List(
          nonPayableStreamTwo,
          payableStreamTwo,
          paidStream
        )
        val songs = Songs(
          artist,
          List(
            defaultSong.copy(streamPayments = streamPaymentsForFirstSong),
            defaultSong
              .copy(id = UUID.randomUUID())
              .copy(streamPayments = streamPaymentsForSecondSong)
          ),
          LocalDate.now(),
          isAgreedByRecordLabel = true
        )
        val service = new DefaultSongsManagementService[IO](stubSuccesfulRepository(Option(songs)))
        val _: Unit = service.add(songs).unsafeRunSync()
        val Some(result) = service.fileForPayment(songs.artist.id).unsafeRunSync()

        result.songs.flatMap(_.streamPayments) must contain theSameElementsAs List(
          payableStreamOne.copy(lastPaymentDate = Option(LocalDate.now())),
          nonPayableStreamOne,
          payableStreamTwo.copy(lastPaymentDate = Option(LocalDate.now())),
          nonPayableStreamTwo,
          paidStream
        )
      }
    }

  }

}

object SongsManagementServiceSpec {

  def stubSuccesfulRepository(songs: Option[Songs] = None): SongsRepository[IO] = new SongsRepository[IO] {
    override def addSongs(songs: Songs): IO[Unit] =
      IO(())

    override def getSongs(artistId: UUID): IO[Option[Songs]] =
      IO(songs)
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
      List.empty
    )
}
