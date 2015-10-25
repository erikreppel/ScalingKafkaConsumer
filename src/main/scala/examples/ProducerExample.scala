package examples

import kafka.producer.KafkaProducer

object ProducerExample extends App {
  val topic = "testing.kafka"
  val kafkaLoc = "localhost:9092"
  val producer = new KafkaProducer(topic, kafkaLoc, synchronously=false)
  var counter = 0
  while(true) {
    var msg = s"This is test message: $counter"
    producer.send(msg)
    counter += 1
    if (counter % 10000 == 0 && counter != 0) println(s"Produced $counter messages to $topic")
  }
}
