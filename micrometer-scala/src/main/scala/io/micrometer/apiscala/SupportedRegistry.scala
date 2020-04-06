package io.micrometer.apiscala

import io.micrometer.core.instrument.{Meter, MeterRegistry}
import io.micrometer.core.instrument.config.{MeterFilter, MeterRegistryConfig}
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import pureconfig._

case class RegistryConfWithInitializers[C <: MeterConf](
    config: C,
    initializers: TypedRegistriesInitializer[C]
) {
  def runInit(name: String): MeterRegistry = {
    val createdRegistry = initializers(name, config)

    if (config.enableBucketsByDefault.contains(true)) {
      createdRegistry.config.meterFilter(new MeterFilter() {
        override def configure(id: Meter.Id, config: DistributionStatisticConfig): DistributionStatisticConfig = {
          DistributionStatisticConfig.builder.percentilesHistogram(true).build.merge(config)
        }
      })
    }

    createdRegistry
  }
}

trait SupportedRegistry[C <: MeterConf] {
  def `type`: String

  def initRegistries: TypedRegistriesInitializer[C]
  def configReader: ConfigReader[C]

  def getWithInitializers = { cc: ConfigObjectCursor =>
    configReader.from(cc).map(c => RegistryConfWithInitializers(c, initRegistries))

  }
}
