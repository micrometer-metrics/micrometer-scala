package io.micrometer.apiscala

import com.typesafe.scalalogging.LazyLogging
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.scalatest.EitherValues
import pureconfig._
import pureconfig.Derivation.Successful

import scala.collection._
import io.micrometer.apiscala.opentsdb.{OpenTSDBMeterConf, OpenTSDBMeterReg}
import io.micrometer.apiscala.prometheus.{PrometheusMeterConf, PrometheusMeterReg}

import io.micrometer.core.instrument.config.MeterRegistryConfig

class LoadRegistriesTest extends AnyFunSuite with Matchers with EitherValues with LazyLogging {
  test("simpleRegistryLoad") {
    val loaded = ConfigSource.default.at("micrometer.registries.localhost-opentsdb").load[OpenTSDBMeterConf]
    loaded.right.value shouldBe a[OpenTSDBMeterConf]
  }

  test("registriesLoad") {
    import pureconfig._
    val registriesLoader = new RegistryLoader {
      override def supportedRegistries: immutable.Seq[SupportedRegistry[_ <: MeterConf]] =
        immutable.Seq(OpenTSDBMeterReg { (name, conf) =>
          null
        }, PrometheusMeterReg { (name, conf) =>
          null
        })
    }
    implicit val registriesReader = registriesLoader.registryConfigReader
    val registriesResult =
      ConfigSource.default
        .at("micrometer.registries")
        .load(Successful(ConfigReader[immutable.Map[String, RegistryConfWithInitializers[_ <: MeterConf]]]))

    registriesResult.right.value should contain key "localhost-opentsdb"
    registriesResult.right.value should contain key "another-opentsdb"
    registriesResult.right.value should contain key "simple-prometheus"
    registriesResult.right.value("localhost-opentsdb") shouldBe a[RegistryConfWithInitializers[OpenTSDBMeterConf]]
    registriesResult.right.value("another-opentsdb") shouldBe a[RegistryConfWithInitializers[OpenTSDBMeterConf]]
    registriesResult.right.value("simple-prometheus") shouldBe a[RegistryConfWithInitializers[PrometheusMeterConf]]
  }

  test("micrometerCreation") {
    MicrometerInit(OpenTSDBMeterReg { (name, conf) =>
      null
    }, PrometheusMeterReg { (name, conf) =>
      null
    })
  }

}
