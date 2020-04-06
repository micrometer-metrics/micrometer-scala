package io.micrometer.apiscala

//import com.typesafe.scalalogging.LazyLogging
import pureconfig._

import scala.collection._
import scala.jdk.CollectionConverters._
import io.micrometer.core.instrument.{MeterRegistry, Metrics, Tag}
import io.micrometer.core.instrument.binder.jvm.{ClassLoaderMetrics, JvmGcMetrics, JvmMemoryMetrics, JvmThreadMetrics}
import io.micrometer.core.instrument.binder.logging.LogbackMetrics
import io.micrometer.core.instrument.binder.system.{FileDescriptorMetrics, ProcessorMetrics, UptimeMetrics}
import io.github.mweirauch.micrometer.jvm.extras.{ProcessMemoryMetrics, ProcessThreadMetrics}
import io.micrometer.core.instrument.binder.MeterBinder

case class MicrometerInit(supportedRegistries: SupportedRegistry[_ <: MeterConf]*) extends MicrometerConfLoader {

  val config = ConfigSource.default.at("micrometer").load[MicrometerConf]

  lazy val defaultCollectors: immutable.Seq[MeterBinder] = {
    immutable.Seq(
      new JvmMemoryMetrics(),
      new JvmGcMetrics(),
      new JvmThreadMetrics(),
      new ClassLoaderMetrics(),
      new ProcessorMetrics(),
      new UptimeMetrics(),
      new FileDescriptorMetrics(),
      new LogbackMetrics(),
      new ProcessMemoryMetrics(),
      new ProcessThreadMetrics()
    )
  }

  def start(): Either[Exception, immutable.Map[String, MeterRegistry]] = {
    config match {
      case Right(conf) =>
        try {
          Metrics.globalRegistry
            .config()
            .commonTags(conf.commonTags.map { case (name, value) => Tag.of(name, value) }.asJava)

          if (conf.bindDefaultCollectors)
            defaultCollectors.foreach(_.bindTo(Metrics.globalRegistry))

          val registries: immutable.Map[String, MeterRegistry] = conf.registriesConfigs.map {
            case (name, configAndInits) => (name, configAndInits.runInit(name))
          }

          registries.values.foreach(Metrics.addRegistry)

          Right(registries)
        } catch {
          case e: Exception =>
            try {
              Metrics.globalRegistry.getRegistries.forEach(x => x.close())
              Left(e)
            } catch {
              case ex: Exception =>
                Left(e)
            }
        }

      case Left(fail) =>
        Left(new RuntimeException(s"error loading micrometer-scala configs:\n${fail.prettyPrint()}"))
    }
  }

  def stop(): Unit = {
    Metrics.globalRegistry.close()
  }
}
