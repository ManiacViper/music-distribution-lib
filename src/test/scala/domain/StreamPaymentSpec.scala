package domain

import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.duration._

class StreamPaymentSpec extends AnyWordSpec with Matchers {

  "StreamPayment.canBeMonetized" should {
    "return true" when {
      "streaming for longer than 30 seconds" in {
        val songDetail = StreamPayment(UUID.randomUUID(), 31.seconds, None)
        songDetail.canBeMonetized mustBe true
      }
    }

    "return false" when {
      "streaming for 30 seconds" in {
        val songDetail = StreamPayment(UUID.randomUUID(), 30.seconds, None)
        songDetail.canBeMonetized mustBe false
      }

      "streaming for less than 30 seconds" in {
        val songDetail = StreamPayment(UUID.randomUUID(), 29.seconds, None)
        songDetail.canBeMonetized mustBe false
      }
    }
  }

  "StreamPayment.isPayable" should {
    "return true" when {
      "stream has not been paid yet" in {
        val songDetail = StreamPayment(UUID.randomUUID(), 31.seconds, None)
        songDetail.isPayable mustBe true
      }
    }

    "return false" when {
      "paid already for a stream" in {
        val songDetail = StreamPayment(UUID.randomUUID(), 30.seconds, Some(LocalDate.now()))
        songDetail.isPayable mustBe false
      }
    }
  }

}
