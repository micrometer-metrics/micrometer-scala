package io.micrometer.scala.design2

import java.util.concurrent.TimeUnit

import cats.effect.{Bracket, Sync}
import io.micrometer.core.instrument.{Counter, Gauge, MeterRegistry, Tag, Timer}
import io.micrometer.scala.ToDouble

import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters._

object CatsEffect {

  implicit class MeterRegistryOps(val delegate: MeterRegistry) extends AnyVal {
    def counter1[F[_]: Sync](name: String, tags: String*): F[Counter] = Sync[F].delay(delegate.counter(name, tags: _*))

    def counter[F[_]: Sync](name: String, tags: Iterable[Tag]): F[Counter] = Sync[F].delay(delegate.counter(name, tags.asJava))

    def gauge1[F[_]: Sync, A: ToDouble](name: String, number: A): F[Gauge] = gauge(name, List.empty, number)

    def gauge[F[_]: Sync, A: ToDouble](name: String, tags: Iterable[Tag], number: A): F[Gauge] =
      Sync[F].delay {
        delegate.gauge[Double](name, tags.asJava, ToDouble[A].toDouble(number), (_: Double).doubleValue())
        delegate.get(name).gauge()
      }

    def timer1[F[_]: Sync](name: String, tags: String*): F[Timer] = Sync[F].delay(delegate.timer(name, tags: _*))

    def timer[F[_]: Sync](name: String, tags: Iterable[Tag]): F[Timer] = Sync[F].delay(delegate.timer(name, tags.asJava))

    def clear[F[_]: Sync]: F[Unit] = Sync[F].delay(delegate.clear())

    def close[F[_]: Sync]: F[Unit] = Sync[F].delay(delegate.close())
  }

  implicit class CounterOps(val counter: Counter) extends AnyVal {

    def increment1[F[_]: Sync]: F[Unit] = increment(1.0)
    def increment[F[_]: Sync](amount: Double): F[Unit] = Sync[F].delay(counter.increment(amount))
    def count1[F[_]: Sync]: F[Double] = Sync[F].delay(counter.count())

  }

  implicit class GaugeOps(val gauge: Gauge) extends AnyVal {
    def value1[F[_]: Sync]: F[Double] = Sync[F].delay(gauge.value)
  }

  implicit class TimerOps(val timer: Timer) extends AnyVal {

    def record[F[_]: Sync](duration: Duration): F[Unit] = Sync[F].delay(timer.record(duration.toNanos, TimeUnit.NANOSECONDS))

    def wrap[F[_]: Sync, A](f: F[A]): F[A] = {
      Bracket[F, Throwable].bracket(Sync[F].delay(System.nanoTime))(_ => f)(start =>
        Sync[F].delay(timer.record(System.nanoTime - start, TimeUnit.NANOSECONDS))
      )
    }

    def totalTime1[F[_]: Sync](unit: TimeUnit): F[Double] = Sync[F].delay(timer.totalTime(unit))

  }

}
