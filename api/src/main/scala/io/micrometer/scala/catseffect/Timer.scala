package io.micrometer.scala.catseffect

import java.time.Duration
import java.util.concurrent.{Callable, TimeUnit}

import cats.effect.{Bracket, Sync}
import io.micrometer.core.instrument.{Timer => Delegate}

class Timer[F[_]: Sync](delegate: Delegate) {

  private val F = Sync[F]

  def record(amount: Long, unit: TimeUnit): F[Unit] = F.delay(delegate.record(amount, unit))

  def record(duration: Duration): F[Unit] = F.delay(delegate.record(duration))

  def record(duration: scala.concurrent.duration.Duration): F[Unit] = F.delay(delegate.record(duration.toNanos, TimeUnit.NANOSECONDS))

  def wrap[A](block: => A): F[A] =
    F.delay {
      delegate
        .wrap(new Callable[A] {
          override def call(): A = block
        })
        .call()
    }

  def wrap[A](f: F[A]): F[A] =
    Bracket[F, Throwable].bracket(F.delay(System.nanoTime))(_ => f)(start =>
      F.delay(delegate.record(System.nanoTime - start, TimeUnit.NANOSECONDS))
    )

  def count: F[Double] = F.delay(delegate.count().toDouble)

  def totalTime(unit: TimeUnit): F[Double] = F.delay(delegate.totalTime(unit))

}
