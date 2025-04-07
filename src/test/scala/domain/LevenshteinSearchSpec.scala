package domain

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class LevenshteinSearchSpec extends AnyWordSpec with Matchers {
 "LevenshteinSearch.search" should {
   "return 0" when {
     "search term is identical to the value compared" in {
       val searchTerm = "some-song-title"
       val value = "some-song-title"

       val result = LevenshteinSearch.search(searchTerm, value)

       result mustBe 0
     }
   }

  "return insertion difference for the value compared" when {
   "search term has more characters" in {
     val searchTerm = "some-song-title-12"
     val value = "some-song-title"

     val result = LevenshteinSearch.search(searchTerm, value)

     result mustBe 3
   }
  }

  "return deletion differences for the value compared" when {
   "search term has more characters" in {
    val searchTerm = "some-song-title"
    val value = "some-song-title-123"

    val result = LevenshteinSearch.search(searchTerm, value)

    result mustBe 4
   }
  }

   "return substitution differences for the value compared" when {
     "search term has some different characters" in {
       val searchTerm = "somesone"
       val value = "somelong"

       val result = LevenshteinSearch.search(searchTerm, value)

       result mustBe 2
     }
   }

   "return any differences for the value compared" when {
     "search term has common characters" in {
       val searchTerm = "sitting"
       val value = "kitten"

       val result = LevenshteinSearch.search(searchTerm, value)

       result mustBe 3
     }
   }

 }
}
