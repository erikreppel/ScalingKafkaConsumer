import org.scalatest.{FlatSpec, Matchers}
import kafka.consumer.HighLevelConsumer
import java.io.{StringWriter, PrintWriter}
import scala.io.Source
import java.util.Properties
import java.io.FileInputStream

class HLConsumerSpec extends FlatSpec with Matchers {

  "A consumer" should "get properties from the consumer.properties file" in {
    val consumer = new HighLevelConsumer()

    val readProperties = new Properties
    val propFileUrl = getClass.getResource("/consumer.properties")
    if (propFileUrl != null) {
      val source = Source.fromURL(propFileUrl)
      readProperties.load(source.bufferedReader())
    }
    val topic = readProperties.getProperty("topic")
    readProperties.remove("topic")
    consumer.properties should be (readProperties)
  }

  it should "poll Zookeeper to find the number of partitions for a topic" in {
    val consumer = new HighLevelConsumer()
    assert(consumer.nThreads > 0)
  }

  it should "Create a Whitelist for the topic specified in consumer.properties" in {
    val readProperties = new Properties
    val propFileUrl = getClass.getResource("/consumer.properties")
    if (propFileUrl != null) {
      val source = Source.fromURL(propFileUrl)
      readProperties.load(source.bufferedReader())
    }
    val topic = readProperties.getProperty("topic")

    val consumer = new HighLevelConsumer()
    consumer.topicWhitelist.toString() should be (topic)
  }
}
