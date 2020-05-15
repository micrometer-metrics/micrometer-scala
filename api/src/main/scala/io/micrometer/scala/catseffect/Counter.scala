package io.micrometer.scala.catseffect

import cats.effect.Sync
import io.micrometer.core.instrument.{Counter => Delegate}

class Counter[F[_]: Sync](delegate: Delegate) {

  private val F = Sync[F]

  def increment: F[Unit] = increment(1.0)
  def increment(amount: Double): F[Unit] = F.delay(delegate.increment(amount))
  def count: F[Double] = F.delay(delegate.count())

}
