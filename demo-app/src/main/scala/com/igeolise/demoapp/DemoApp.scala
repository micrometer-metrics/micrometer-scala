package com.iGeolise.demoapp

import java.util.concurrent.ExecutorService

import com.typesafe.scalalogging.LazyLogging
import io.micrometer.core.instrument.{Clock, Metrics}
import io.micrometer.apiscala.{MetricId, Micrometer, MicrometerInit}
import io.micrometer.apiscala.syntax._
import io.micrometer.apiscala.opentsdb.OpenTSDBMeterReg
import io.micrometer.apiscala.prometheus.PrometheusMeterReg
import io.micrometer.core.instrument.util.NamedThreadFactory
import io.micrometer.core.ipc.http.OkHttpSender
import io.micrometer.opentsdb.OpenTSDBMeterRegistry
import okhttp3.OkHttpClient

import scala.collection._
import scala.concurrent.{ExecutionContext, Future}

object DemoApp extends LazyLogging {

  sealed trait InvocationTypes
  object InvocationTypes extends InvocationTypes {
    val tagName = "invocationType"
    case object TypeOne extends InvocationTypes {
      override def toString: String = "type.one"
    }
    case object TypeTwo extends InvocationTypes {
      override def toString: String = "type.two"
    }

    case object TypeThree extends InvocationTypes {
      override def toString: String = "type.three"
    }

    val invocationTypes = Array(TypeOne, TypeTwo, TypeThree)
  }

  def main(args: Array[String]): Unit = {

    val okHttpBuilder = new OkHttpClient.Builder()
    val httpClient    = new OkHttpSender(okHttpBuilder.build())
    val threadFactory = new NamedThreadFactory("demoapp-metrics-threadfactory")

    val micrometer = MicrometerInit(
      OpenTSDBMeterReg { (_, config) =>
        OpenTSDBMeterRegistry
          .builder(config)
          .clock(Clock.SYSTEM)
          .threadFactory(threadFactory)
          .httpClient(httpClient)
          .build()
      },
      PrometheusMeterReg { (name, config) =>
        null
      }
    )

    val startResult = micrometer.start()

    sys.addShutdownHook {
      import scala.jdk.CollectionConverters._
      Metrics.globalRegistry.getMeters.asScala.foreach { m =>
        logger info
          s"""=================================
             |
             |${m.getId.getType}
             |${m.getId.getName}
             |${m.getId.getDescription}
             |${m.getId.getBaseUnit}
             |${m.getId.getTags.asScala.mkString(";")}
             |""".stripMargin
      }
      micrometer.stop()
    }

    //    ExecutionContext.global match {
    //      case es: ExecutorService =>
    //        new ExecutorServiceMetrics(es, "ExecutionContext.global", Seq.empty.asJava).bindTo(Metrics.globalRegistry)
    //      case _ =>
    //        logger.info(s"ExecutionContext.global is not an instance of ExecutorService")
    //
    //    }

    val testCounter        = Micrometer.counter("test_counter.first")
    val commonPrefix       = Micrometer.Id("test_another", "x" -> "y")
    val anotherTestCounter = commonPrefix.withTags("a" -> "b").counter()
    //val testTimer = Metrics.timer("test_timer").record()

    val fCounter = Future {
      val r = new scala.util.Random()

      while (true) { //x =>
        val increment = r.nextInt(100)
        testCounter.increment(increment)
        Micrometer
          .counter(
            "test_counter.second",
            InvocationTypes.tagName -> InvocationTypes
              .invocationTypes(r.nextInt(InvocationTypes.invocationTypes.length))
              .toString
          )
          .increment(increment)
        Thread.sleep(1000)
        //if (x % 90 == 0) System.gc()
        logger info testCounter.count().toString
      }
      logger info "counter end"
      1
    }(ExecutionContext.global)

    val testHistogram = Micrometer.summary("test_distribution")
    val testTimer     = Micrometer.timer("testTimer")
    val fHistogram = Future {
      val r = new scala.util.Random()

      var i = 0
      while (true) {
        i += 1

//        import scala.concurrent.duration._
//        testTimer.record(30.seconds)

        testTimer.time {
          testHistogram.record(i % 1000)
          Thread.sleep(10)
        }
      }
    }(ExecutionContext.global)

//    val fStress = Future {}(ExecutionContext.global)
    val fStress = Future {
      Thread.sleep(10000)
      val blockSize = 1024 * 1024
      val l         = mutable.ListBuffer.empty[Array[Byte]]
      try {
        while (true) {
          l.append(Array.fill(blockSize)(1))
          if ((l.size % 1000) == 0) logger.info(s"list size ${l.size}")
        }
      } catch {
        case e: OutOfMemoryError =>
        //simply go on
      }
      l.remove(0)
      logger.info(s"mem full, list size ${l.size + 1}")
      var i = 0
      while (true) {
        i += 1
        logger.info(s"iteration: $i")
        try {
          l.append(Array.fill(blockSize)(1))
        } catch {
          case e: OutOfMemoryError =>
          //simply go on
          case e: Exception =>
            logger.info(e.toString)
        }
        l.remove(0)
        if ((i % 20) == 0) Thread.sleep(1000)
      }
      logger info "jvm stress end"
      1
    }(ExecutionContext.global)

    implicit val ctx = ExecutionContext.global
    for {
      cnt       <- fCounter
      histogram <- fHistogram
      jvmStress <- fStress
    } {
      micrometer.stop()
    }
  }
}
