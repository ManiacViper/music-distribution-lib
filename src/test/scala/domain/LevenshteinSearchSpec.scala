package domain

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class LevenshteinSearchSpec extends AnyWordSpec with Matchers {
 "LevenshteinSearch.search" should {
   "return 0" when {
     "search term is identical to the value being compared" in {
       val searchTerm = "some-song-title"
       val value = "some-song-title"

       val result = LevenshteinSearch.search(searchTerm, value)

       result mustBe 0
     }
   }

  "return insertion difference for the value being searched" when {
   "search term has more characters" in {
     val searchTerm = "some-song-title-12"
     val value = "some-song-title"

     val result = LevenshteinSearch.search(searchTerm, value)

     result mustBe 3
   }
  }

  "return deletion differences for the value being searched" when {
   "search term has more characters" in {
    val searchTerm = "some-song-title"
    val value = "some-song-title-123"

    val result = LevenshteinSearch.search(searchTerm, value)

    result mustBe 4
   }
  }

 }
}
