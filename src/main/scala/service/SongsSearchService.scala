package service

import domain.{LevenshteinSearch, SongDetails}

import java.util.UUID

trait SongsSearchService {
  def searchBy(title: String, songs: List[SongDetails]): Option[SongDetails]
}

object SongsSearchService extends SongsSearchService {
  override def searchBy(searchTerm: String, songs: List[SongDetails]): Option[SongDetails] = {
    val searchResults = songs
      .map { song =>
        val diff = LevenshteinSearch.search(searchTerm, song.title)
        SearchResult(song.id, song.title, diff)
      }.sortBy(searchResult => searchResult.diff)

    searchResults
      .headOption
      .flatMap { result =>
        songs.find(_.id == result.id)
      }
  }
}

private case class SearchResult(id: UUID, title: String, diff: Int)

