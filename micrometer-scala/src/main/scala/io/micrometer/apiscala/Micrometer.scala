package io.micrometer.apiscala

import io.micrometer.core.instrument.{Counter, DistributionSummary, Metrics, Tag, Timer}

import scala.concurrent.duration.FiniteDuration

import scala.compat.java8.FunctionConverters._
import scala.jdk.CollectionConverters._
import scala.compat.java8.DurationConverters._

trait MetricBridge {
  def counter(name: String, tags: (String, String)*): Counter = Metrics.counter(name, Tags(tags))

  def summary(name: String, tags: (String, String)*): DistributionSummary = Metrics.summary(name, Tags(tags))

  def timer(name: String, tags: (String, String)*): Timer = Metrics.timer(name, Tags(tags))

  // f(obj) must be thread-safe!!!
  def gauge[T](obj: T, name: String, tags: (String, String)*)(f: T => Double): T =
    Metrics.gauge(name, Tags(tags), obj, f.asJava)

  // v.doubleValue() must be thread-safe!!!
  def gauge[T <: Number](v: T, name: String, tags: (String, String)*): T =
    Metrics.gauge(name, Tags(tags), v, (value: T) => value.doubleValue())
}

case class MetricId(name: String, tags: Seq[(String, String)]) {
  def withTags(addTags: (String, String)*): MetricId = this.copy(name = this.name, tags = this.tags ++ addTags)
  def withSuffix(suffix: String): MetricId           = this.copy(name = this.name + suffix, tags = this.tags)

  def counter(): Counter = Micrometer.counter(name, tags: _*)

  def summary(): DistributionSummary = Micrometer.summary(name, tags: _*)

  def timer(): Timer = Micrometer.timer(name, tags: _*)

  // f(obj) must be thread-safe!!!
  def gauge[T](obj: T)(f: T => Double): T =
    Micrometer.gauge(obj, name, tags: _*)(f)

  // v.doubleValue() must be thread-safe!!!
  def gauge[T <: Number](v: T): T = Micrometer.gauge(v, name, tags: _*)
}

object Micrometer extends MetricBridge {
  def Id(name: String, tags: (String, String)*): MetricId = MetricId(name, tags)
}

object Tags {
  def apply(tags: Seq[(String, String)]) = tags.map { case (name, value) => Tag.of(name, value) }.asJava
}

object syntax {
  implicit class ToScalaTimer(x: Timer) {
    def time[T](f: => T): T             = x.record(() => f)
    def record(d: FiniteDuration): Unit = x.record(d.toJava)
  }
}
