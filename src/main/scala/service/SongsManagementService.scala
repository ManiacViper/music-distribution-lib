package service

import cats.effect.Sync
import domain.{SongDetails, Songs, StreamPayment}
import repository.SongsRepository
import cats.implicits._

import java.time.LocalDate
import java.util.UUID

trait SongsManagementService[F[_]] {
  def add(songs: Songs): F[Unit]
  def retrieve(artistId: UUID): F[Option[Songs]]
  def remove(artistId: UUID, disabledSongIds: List[UUID]): F[Unit]
  def fileForPayment(artistId: UUID): F[Option[Songs]]
}

class DefaultSongsManagementService[F[_]: Sync](songsRepository: SongsRepository[F]) extends SongsManagementService[F] {
  override def add(songs: Songs): F[Unit] =
    songsRepository.addSongs(songs)

  override def remove(artistId: UUID, disabledSongIds: List[UUID]): F[Unit] =
    for {
      maybeSongs <- songsRepository.getSongs(artistId)
      _ <- maybeSongs match {
          case Some(songs) =>
            val songsDetails = songs.songs.map(song => if(disabledSongIds.contains(song.id)) song.copy(isStreamable = false) else song)
            val updatedSongs = songs.copy(songs = songsDetails)
            songsRepository.addSongs(updatedSongs)
          case None =>
            Sync[F].unit
        }
    } yield ()

  override def retrieve(artistId: UUID): F[Option[Songs]] =
    songsRepository.getSongs(artistId)

  override def fileForPayment(artistId: UUID): F[Option[Songs]] = {
    for {
      maybeExistingSongs <- songsRepository.getSongs(artistId)
      updatedSongs <- Sync[F].pure(
          maybeExistingSongs.map {
            songs =>
              val updatedPaymentSongs =
                songs
                  .songs
                  .map(updateStreamPayments)
              songs.copy(songs = updatedPaymentSongs)
          }
        )
      _ <- Sync[F].delay(updatedSongs.foreach(songsRepository.addSongs))
    } yield updatedSongs
  }

  private def updateStreamPayments(song: SongDetails): SongDetails = {
      val payments: List[StreamPayment] =
        song
        .streamPayments
        .filter(payment => payment.canBeMonetized && payment.isPayable)
        .map(payment => payment.copy(lastPaymentDate = Option(LocalDate.now())))

    val alreadyPaidStreams = song.streamPayments.filterNot(payment => payments.exists(_.id == payment.id))

    song
        .copy(streamPayments = payments ++ alreadyPaidStreams)
  }
}
