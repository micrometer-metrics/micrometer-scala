package io.micrometer.scala.design1

import cats.effect.Sync
import io.micrometer.core.instrument.Gauge

class ScalaGauge[F[_]: Sync](delegate: Gauge) {

  private val F = Sync[F]

  def value: F[Double] = F.delay(delegate.value)

}
