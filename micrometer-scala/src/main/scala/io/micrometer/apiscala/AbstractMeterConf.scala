package io.micrometer.apiscala

import io.micrometer.core.instrument.config.MeterRegistryConfig
import pureconfig._

import scala.concurrent.duration.FiniteDuration

trait ForceDefaults {
  // to force use behaviour of defaults in *Config interfaces
  def get(key: String): String = null
}

class MeterConf(
    val enableBucketsByDefault: Option[Boolean]
) extends MeterRegistryConfig
    with ForceDefaults {
  override def prefix(): String = ""
}

object MeterConf {
  implicit val configReader = ConfigReader.fromCursor[MeterConf] { cur =>
    for {
      c                      <- cur.asObjectCursor
      enableBucketsByDefault <- ConfigReader[Option[Boolean]].from(c.atKeyOrUndefined("enable-buckets-by-default"))
    } yield new MeterConf(enableBucketsByDefault)
  }
}
