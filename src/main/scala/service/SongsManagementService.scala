package service

import cats.effect.Sync
import domain.Songs
import repository.SongsRepository

trait SongsManagementService[F[_]] {
  def add(songs: Songs): F[Unit]
}

class DefaultSongsManagementService[F[_]: Sync](songsRepository: SongsRepository[F]) extends SongsManagementService[F] {
  override def add(songs: Songs): F[Unit] =
    songsRepository.addSongs(songs)
}
