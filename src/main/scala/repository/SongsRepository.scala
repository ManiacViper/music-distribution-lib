package repository

import domain.Songs
import cats.effect.Sync
import java.util.UUID
import scala.collection.mutable

trait SongsRepository[F[_]] {
  def addSongs(songs: Songs): F[Unit]
  def getSongs(artistId: UUID): F[Option[Songs]]
}

class DefaultSongsRepository[F[_]: Sync] extends SongsRepository[F] {
  private val artistToSongs: mutable.Map[UUID, Songs] = mutable.Map.empty
  override def addSongs(songs: Songs): F[Unit] =
    Sync[F].delay(
      if(songs.songs.isEmpty) {
        ()
      } else {
        artistToSongs.update(songs.artist.id, songs)
      }
    )
  override def getSongs(artistId: UUID): F[Option[Songs]] =
    Sync[F].delay(
      artistToSongs.get(artistId)
    )
}

