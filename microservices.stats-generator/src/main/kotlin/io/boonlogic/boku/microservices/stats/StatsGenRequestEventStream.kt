package io.boonlogic.boku.microservices.stats

import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaSparkContext
import org.apache.spark.sql.SparkSession
import org.slf4j.LoggerFactory
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink

@EnableBinding(Sink::class)
class StatsGenRequestEventStream() {

    val log = LoggerFactory.getLogger(StatsGenRequestEventStream::class.java)

    @StreamListener(Sink.INPUT)
    fun listen(evt: String) {
        log.info("search event received: {}", evt)

        val spark = sparkSession()
        val sc = JavaSparkContext(spark.sparkContext())
        val result = sc.parallelize(listOf("alice", "hero", "shinchan", "peter"))
            .filter { it.length > 5 }
            .map { Pair(it.length, it) }
            .reduce { a, b ->
                if(b.first > a.first) b
                else a
            }
        println(result)

        //    val sql  = SparkSession.Builder().config(conf).getOrCreate().sqlContext();
        //    var df  = sql.read().format("org.elasticsearch.spark.sql").load("");
    }
}

fun sparkSession(): SparkSession {
    val master = "local"
    val conf = SparkConf().setMaster(master)
//    conf.set("es.index.auto.create", "true")
//    conf.set("es.nodes", "10.1.11.42")
//    conf.set("es.port", "9200")
//    conf.set("pushdown", "true")

    return SparkSession
        .builder()
        .appName("Boku Stats in Spark")
        .config(conf)
        .getOrCreate()
}
