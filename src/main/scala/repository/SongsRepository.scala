package repository

import domain.SongDetails

trait SongsRepository[F[_]] {
  def addSongs(songs: Seq[SongDetails]): F[Unit]
}
