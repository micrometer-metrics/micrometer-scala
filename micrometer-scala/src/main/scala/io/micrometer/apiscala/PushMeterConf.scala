package io.micrometer.apiscala

import java.time.Duration

import io.micrometer.core.instrument.push.PushRegistryConfig
import pureconfig._

//import scala.jdk.DurationConverters._
import scala.compat.java8.DurationConverters._
import scala.concurrent.duration.FiniteDuration

class PushMeterConf(
    /*private[apiscala]*/ val meterConf: MeterConf,
    /*private[apiscala]*/ val stepOpt: Option[FiniteDuration],
    /*private[apiscala]*/ val enabledOpt: Option[Boolean],
    /*private[apiscala]*/ val numThreadsOpt: Option[Int],
    /*private[apiscala]*/ val batchSizeOpt: Option[Int]
) extends MeterConf(meterConf.enableBucketsByDefault)
    with PushRegistryConfig
    with ForceDefaults {

  override def step(): Duration   = stepOpt.map(_.toJava) getOrElse super.step()
  override def enabled(): Boolean = enabledOpt getOrElse super.enabled()
  override def numThreads(): Int  = numThreadsOpt getOrElse super.numThreads()
  override def batchSize(): Int   = batchSizeOpt getOrElse super.batchSize()
}

object PushMeterConf {
  implicit val configReader = ConfigReader.fromCursor[PushMeterConf] { cur =>
    for {
      c              <- cur.asObjectCursor
      meterConf      <- ConfigReader[MeterConf].from(c)
      stepConf       <- ConfigReader[Option[FiniteDuration]].from(c.atKeyOrUndefined("step"))
      enabledConf    <- ConfigReader[Option[Boolean]].from(c.atKeyOrUndefined("enabled"))
      numThreadsConf <- ConfigReader[Option[Int]].from(c.atKeyOrUndefined("numThreads"))
      batchSizeConf  <- ConfigReader[Option[Int]].from(c.atKeyOrUndefined("batchSize"))
    } yield new PushMeterConf(meterConf, stepConf, enabledConf, numThreadsConf, batchSizeConf)
  }
}

//trait PushMeterReg extends SupportedRegistry[PushMeterConf] {
//  import pureconfig._
//
//  val `type` = "abstract-push-registry"
//
//  override def configReader: ConfigReader[PushMeterConf] = ConfigReader[PushMeterConf]
//}
