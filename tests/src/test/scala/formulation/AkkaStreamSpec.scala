package formulation

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{Matchers, WordSpec}
import formulation.akkastreams._
import scala.concurrent.ExecutionContext.Implicits.global

class AkkaStreamSpec extends WordSpec with GeneratorDrivenPropertyChecks with Matchers with ArbitraryHelpers {

  private implicit val system: ActorSystem = ActorSystem()
  private implicit val materializer: Materializer = ActorMaterializer()

  "akka-stream" should {
    "encode and decode symmterically" in {
      forAll { (progress: List[UserV1]) =>
        Source(progress)
          .via(encoder[UserV1])
          .via(decoder[UserV1])
          .runWith(TestSink.probe(system))
          .request(progress.size)
          .expectNextN(progress.size) == progress
      }
    }
  }
}
