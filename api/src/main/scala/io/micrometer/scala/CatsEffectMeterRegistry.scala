package io.micrometer.scala

import cats.effect.Sync
import io.micrometer.core.instrument._
import io.micrometer.core.instrument.search.{RequiredSearch, Search}
import io.micrometer.scala.CatsEffectMeterRegistry.CollectionSizeToDouble
import io.micrometer.scala.catseffect.{Counter, Gauge, Timer}

import scala.collection.immutable.Seq
import scala.jdk.CollectionConverters._

class CatsEffectMeterRegistry[F[_]: Sync](delegate: MeterRegistry) {

  private val F = Sync[F]

  def meters: F[Seq[Meter]] = F.delay(delegate.getMeters.asScala.toSeq)

  def foreach(f: Meter => Unit): Unit = delegate.forEachMeter(m => f(m))

  def config: MeterRegistry#Config = delegate.config()

  def find(name: String): F[Option[Search]] = F.delay(Option(delegate.find(name)))

  def get(name: String): F[RequiredSearch] = F.delay(delegate.get(name))

  def counter(name: String, tags: Iterable[Tag]): F[Counter[F]] = F.delay(new Counter(delegate.counter(name, tags.asJava)))

  def counter(name: String, tags: Tag*): F[Counter[F]] = counter(name, tags)

  def summary(name: String, tags: Iterable[Tag]): F[DistributionSummary] = F.delay(delegate.summary(name, tags.asJava))

  def summary(name: String, tags: Tag*): F[DistributionSummary] = summary(name, tags)

  def timer(name: String, tags: Iterable[Tag]): F[Timer[F]] = F.delay(new Timer(delegate.timer(name, tags.asJava)))

  def timer(name: String, tags: Tag*): F[Timer[F]] = timer(name, tags)

  def gauge[A: ToDouble](name: String, tags: Iterable[Tag], numberLike: A): F[Gauge[F]] =
    F.delay {
      delegate.gauge[Double](name, tags.asJava, ToDouble[A].toDouble(numberLike), (_: Double).doubleValue())
      new Gauge(delegate.get(name).gauge())
    }

  def gauge[A: ToDouble](name: String, numberLike: A): F[Gauge[F]] = gauge(name, List.empty, numberLike)

  def gaugeCollectionSize[A <: Iterable[_]](name: String, tags: Iterable[Tag], collection: A): F[Gauge[F]] =
    gauge(name, tags, collection)(CollectionSizeToDouble)

  def remove(meter: Meter): F[Option[Meter]] = F.delay(Option(delegate.remove(meter)))

  def remove(meterId: Meter.Id): F[Option[Meter]] = F.delay(Option(delegate.remove(meterId)))

  def clear: F[Unit] = F.delay(delegate.clear())

  def close: F[Unit] = F.delay(delegate.close())

  def isClosed: F[Boolean] = F.delay(delegate.isClosed)

}

private object CatsEffectMeterRegistry {

  object CollectionSizeToDouble extends ToDouble[Iterable[_]] {
    override def toDouble(value: Iterable[_]): Double = value.size.toDouble
  }

}
