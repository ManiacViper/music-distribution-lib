package service

import cats.effect.Sync
import domain.Songs
import repository.SongsRepository

import java.util.UUID

trait SongsManagementService[F[_]] {
  def add(songs: Songs): F[Unit]
  def retrieve(artistId: UUID): F[Option[Songs]]
}

class DefaultSongsManagementService[F[_]: Sync](songsRepository: SongsRepository[F]) extends SongsManagementService[F] {
  override def add(songs: Songs): F[Unit] =
    songsRepository.addSongs(songs)

  override def retrieve(artistId: UUID): F[Option[Songs]] =
    songsRepository.getSongs(artistId)
}
