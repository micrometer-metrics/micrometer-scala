package io.micrometer.apiscala
package prometheus

import java.time.Duration

//import scala.jdk.DurationConverters._
import scala.compat.java8.DurationConverters._
import io.micrometer.prometheus.{HistogramFlavor, PrometheusConfig}

import scala.concurrent.duration.FiniteDuration
import pureconfig._

case class PrometheusMeterConf(
    meterConf: MeterConf,
    stepOpt: Option[FiniteDuration],
    descriptionsOpt: Option[Boolean],
    histogramFlavorOpt: Option[HistogramFlavor]
) extends MeterConf(meterConf.enableBucketsByDefault)
    with PrometheusConfig
    with ForceDefaults {
  override def descriptions(): Boolean            = descriptionsOpt getOrElse super.descriptions()
  override def step(): Duration                   = stepOpt.map(_.toJava) getOrElse super.step()
  override def histogramFlavor(): HistogramFlavor = histogramFlavorOpt getOrElse super.histogramFlavor()

  override def prefix(): String = ""
}

object PrometheusMeterConf {

  implicit val configReader =
    ConfigReader.fromCursor[PrometheusMeterConf] { cur =>
      for {
        c                   <- cur.asObjectCursor
        meterConf           <- ConfigReader[MeterConf].from(c)
        stepConf            <- ConfigReader[Option[FiniteDuration]].from(c.atKeyOrUndefined("step"))
        descriptionsConf    <- ConfigReader[Option[Boolean]].from(c.atKeyOrUndefined("descriptions"))
        histogramFlavorConf <- ConfigReader[Option[HistogramFlavor]].from(c.atKeyOrUndefined("histogramFlavor"))
      } yield new PrometheusMeterConf(meterConf, stepConf, descriptionsConf, histogramFlavorConf)
    }

}

case class PrometheusMeterReg(initRegistries: TypedRegistriesInitializer[PrometheusMeterConf])
    extends SupportedRegistry[PrometheusMeterConf] {
  val `type` = "prometheus"

  val configReader: ConfigReader[PrometheusMeterConf] = ConfigReader[PrometheusMeterConf]
}
