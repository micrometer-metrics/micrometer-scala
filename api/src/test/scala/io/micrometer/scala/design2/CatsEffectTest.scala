package io.micrometer.scala.design2

import java.util.concurrent.TimeUnit.MILLISECONDS

import cats.effect.IO
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import io.micrometer.scala.design2.CatsEffect._
import org.scalatest.funsuite.AsyncFunSuite

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration

class CatsEffectTest extends AsyncFunSuite {

  test("CatsEffect") {
    val registry = new SimpleMeterRegistry()

    val io = for {
      counter <- registry.counter1[IO]("test-counter")
      _ <- counter.increment1[IO]
      count <- counter.count1[IO]
      gauge <- registry.gauge1[IO, Int]("test-gauge", 123)
      gaugeValue <- gauge.value1[IO]
      timer <- registry.timer1[IO]("test-timer")
      _ <- timer.wrap(IO.sleep(Duration(350, MILLISECONDS))(IO.timer(ExecutionContext.global)))
      time <- timer.totalTime1[IO](MILLISECONDS)
    } yield assert(count === 1.0 && gaugeValue === 123.0 && time >= 300 && time <= 400)

    io.unsafeToFuture()
  }

}
