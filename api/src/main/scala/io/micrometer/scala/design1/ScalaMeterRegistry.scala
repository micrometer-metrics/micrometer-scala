package io.micrometer.scala.design1

import cats.effect.Sync
import io.micrometer.core.instrument._
import io.micrometer.scala.ToDouble

import scala.jdk.CollectionConverters._

class ScalaMeterRegistry[F[_]: Sync](delegate: MeterRegistry) {

  private val F = Sync[F]

  def counter(name: String, tags: String*): F[ScalaCounter[F]] = F.delay(new ScalaCounter(delegate.counter(name, tags: _*)))

  def counter(name: String, tags: Iterable[Tag]): F[ScalaCounter[F]] = F.delay(new ScalaCounter(delegate.counter(name, tags.asJava)))

  def gauge[A: ToDouble](name: String, number: A): F[ScalaGauge[F]] = gauge(name, List.empty, number)

  def gauge[A: ToDouble](name: String, tags: Iterable[Tag], number: A): F[ScalaGauge[F]] = F.delay {
    delegate.gauge[Double](name, tags.asJava, ToDouble[A].toDouble(number), (_: Double).doubleValue())
    new ScalaGauge(delegate.get(name).gauge())
  }

  def timer(name: String, tags: String*): F[ScalaTimer[F]] = F.delay(new ScalaTimer(delegate.timer(name, tags: _*)))

  def timer(name: String, tags: Iterable[Tag]): F[ScalaTimer[F]] = F.delay(new ScalaTimer(delegate.timer(name, tags.asJava)))

  def clear: F[Unit] = F.delay(delegate.clear())

  def close: F[Unit] = F.delay(delegate.close())

}
