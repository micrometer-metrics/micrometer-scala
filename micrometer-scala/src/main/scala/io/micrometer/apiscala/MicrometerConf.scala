package io.micrometer.apiscala

import scala.collection._
import pureconfig._

case class MicrometerConf(
    bindDefaultCollectors: Boolean,
    registriesConfigs: immutable.Map[String, RegistryConfWithInitializers[_ <: MeterConf]],
    commonTags: immutable.Map[String, String]
)

trait RegistryLoader {
  def supportedRegistries: Seq[SupportedRegistry[_ <: MeterConf]]
  protected val mapping: immutable.Map[String, SupportedRegistry[_ <: MeterConf]] =
    supportedRegistries.map(x => (x.`type`, x)).toMap

  def extractByType(
      typ: String,
      objCur: ConfigObjectCursor
  ): ConfigReader.Result[RegistryConfWithInitializers[_ <: MeterConf]] = {
    mapping
      .get(typ)
      .map(x => x.getWithInitializers(objCur))
      .getOrElse(
        objCur.failed(
          error
            .CannotConvert(
              objCur.value.toString,
              "MeterRegistryConfig",
              s"type has value $typ instead of ${mapping.keys.mkString("|")}"
            )
        )
      )
  }

  implicit val registryConfigReader = ConfigReader.fromCursor { cur =>
    for {
      objCur  <- cur.asObjectCursor
      typeCur <- objCur.atKey("type")
      typeStr <- typeCur.asString
      regConf <- extractByType(typeStr, objCur)
    } yield regConf
  }
}

trait MicrometerConfLoader extends RegistryLoader {
  implicit val micrometerConfReader = ConfigReader.fromCursor { cur =>
    for {
      c       <- cur.asObjectCursor
      regsCur <- c.atKey("registries")
      regMap <- ConfigReader[immutable.Map[String, RegistryConfWithInitializers[_ <: MeterConf]]]
        .from(regsCur)
      bindDefaultCollsCur <- c.atKey("bind-default-collectors")
      bindDefaultColls    <- bindDefaultCollsCur.asBoolean
      tagsCur             <- c.atKey("tags")
      tags                <- ConfigReader[immutable.Map[String, String]].from(tagsCur)
    } yield MicrometerConf(bindDefaultColls, regMap, tags)
  }
}
