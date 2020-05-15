package io.micrometer.scala.design1

import java.util.concurrent.TimeUnit.MILLISECONDS

import cats.effect.IO
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.micrometer.scala.CatsEffectMeterRegistry
import org.scalatest.funsuite.AsyncFunSuite

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

class CatsEffectMeterRegistryTest extends AsyncFunSuite {

  test("ScalaMeterRegistry") {
    val javaRegistry = new SimpleMeterRegistry()
    val scalaRegistry = new CatsEffectMeterRegistry[IO](javaRegistry)

    val io = for {
      counter <- scalaRegistry.counter("test-counter")
      _ <- counter.increment
      count <- counter.count
      gauge <- scalaRegistry.gauge("test-gauge", 123)
      gaugeValue <- gauge.value
      timer <- scalaRegistry.timer("test-timer")
      _ <- timer.wrap(IO.sleep(Duration(350, MILLISECONDS))(IO.timer(ExecutionContext.global)))
      time <- timer.totalTime(MILLISECONDS)
    } yield assert(count === 1.0 && gaugeValue === 123.0 && time >= 300 && time <= 400)

    io.unsafeToFuture()
  }

}
