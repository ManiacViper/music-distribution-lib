package domain

object LevenshteinSearch {
  def search(searchTerm: String, value: String): Int = {
    if(searchTerm == value) {
      0
    } else {
      val insertions = if(searchTerm.length > value.length) searchTerm.length - value.length else 0
      val deletions =  if(searchTerm.length < value.length) value.length - searchTerm.length else 0
      val substitutions =
        if(insertions > 0) {
          val searchTermWithoutExtraChars = searchTerm.slice(0, value.length)
          searchTermWithoutExtraChars.length - value.intersect(searchTermWithoutExtraChars).length
        } else if(deletions > 0) {
          val valueWithoutDeletions = value.slice(0, searchTerm.length)
          searchTerm.length - searchTerm.intersect(valueWithoutDeletions).length
        } else
          searchTerm.length - searchTerm.intersect(value).length
      insertions + deletions + substitutions
    }
  }
}