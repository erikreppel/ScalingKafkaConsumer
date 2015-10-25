Scaling Kafka Consumer
=============

This project is a version of a high level consumer I wrote while interning at Intuitive Surgical. They were kind enough to let me open source it, so a big thanks to them, they are awesome!


#### Requirements:
  - **Kafka**: I recommend [Confluent Kafka](http://www.confluent.io/), its much easier to set up than Apache Kafka. They also have some nice [Docker images](https://github.com/confluentinc/docker-images) to make life even easier.

  - **Scala**: Development was done with 2.11.7.

    To install: [download](http://scala-lang.org/download/) and extract, then export the location as SCALA_HOME, and add $SCALA_HOME/bin to your path. Full instructions from [scala-lang.org](http://scala-lang.org/download/install.html).

  - **sbt**: I used 0.13.9 for development, but any version should work. Instructions on sbt [site](http://www.scala-sbt.org/download.html).

  - **Java 8**: Akka requires Java 8 and this project requires Akka.
    Install on Ubuntu:

    ```
    $ sudo add-apt-repository ppa:webupd8team/java
    $ sudo apt-get update
    $ sudo apt-get install oracle-java8-installer
    ```

#### Quick Start:

You should now have Kafka running at `localhost:9092` and zookeeper running at `localhost:2181`. If you are running them at another location, or are running multiple nodes, you must edit the `consumer.properties` file, located in `src/main/resources/consumer.properties`.

1. From the top directory of this repository (it contains a `build.sbt` file), run `sbt`. This can take a while the first time you use sbt as it will update its self and all dependencies for the project.

2. When sbt is up to date you should get a console to enter commands into. Type 'compile' and hit enter. This might take a little while too, as the dependencies also need to be compiled the first time and some (akka) are quite large. You may see a couple of warnings because of naming but no errors.

3. Enter `run` in the sbt console and hit enter. This runs Main.scala, which has nothing in it really. Its meant to be a place to put your code!

4. Go to `src/main/scala/examples` choose an example and uncomment `with App` from the function declaration at the top of the file (I recommend ProducerExample.scala first so you have some data in a topic, but you will need to change the `kafkaLoc` variable in ProducerExample.scala if you aren't running Kafka at `localhost:9092`). Then get back into the sbt console, compile, and run again. You should now see two options after running, `ScalingConsumer.Main`, and `examples.ProducerExample`, choose the number that corresponds to ProducerExample.

5. Try one of the consuming examples, and write your own!


#### Why?:

  This project was made because I could not find a good example of a Kafka consumer that automatically scales itself to take full advantage of the size of the cluster it is communicating with. Additionally I could not find anything simple, I want this to happen to every message in a topic, I don't want to worry about all the fiddly Kafka specific stuff, just let me write a function that gets applied to every message and thats it.

  And thus, the two design goals: scalable, simple.

#### How to use:

  There are two main files that handle the scaling for the consumer, HighLevelConsumer.scala and ActorConsumer.scala (both can be found in `src/main/scala/kafka/consumer`)...

  (this read me is in progress... chances are it will be updated in the next two days)
