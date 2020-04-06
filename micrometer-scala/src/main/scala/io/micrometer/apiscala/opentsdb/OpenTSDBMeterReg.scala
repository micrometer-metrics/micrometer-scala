package io.micrometer.apiscala
package opentsdb

import io.micrometer.opentsdb.{OpenTSDBConfig, OpenTSDBFlavor}
import pureconfig._

case class OpenTSDBMeterConf(
    pushRegistryConf: PushMeterConf,
    override val uri: String,
    userNameOpt: Option[String],
    passwordOpt: Option[String],
    flavorOpt: Option[OpenTSDBFlavor]
) extends PushMeterConf(
      pushRegistryConf.meterConf,
      pushRegistryConf.stepOpt,
      pushRegistryConf.enabledOpt,
      pushRegistryConf.numThreadsOpt,
      pushRegistryConf.batchSizeOpt
    )
    with OpenTSDBConfig {

  override def userName(): String       = userNameOpt getOrElse super.userName()
  override def password(): String       = passwordOpt getOrElse super.password()
  override def flavor(): OpenTSDBFlavor = flavorOpt getOrElse super.flavor()

  override def prefix(): String = ""
}

object OpenTSDBMeterConf {
  implicit val configReader =
    ConfigReader.fromCursor[OpenTSDBMeterConf] { cur =>
      for {
        c           <- cur.asObjectCursor
        pushRegConf <- ConfigReader[PushMeterConf].from(c)
        uriCur      <- c.atKey("uri")
        uri         <- uriCur.asString
        user        <- ConfigReader[Option[String]].from(c.atKeyOrUndefined("userName"))
        password    <- ConfigReader[Option[String]].from(c.atKeyOrUndefined("password"))
        flavor      <- ConfigReader[Option[OpenTSDBFlavor]].from(c.atKeyOrUndefined("flavor"))
      } yield new OpenTSDBMeterConf(pushRegConf, uri, user, password, flavor)
    }

}

case class OpenTSDBMeterReg(initRegistries: TypedRegistriesInitializer[OpenTSDBMeterConf])
    extends SupportedRegistry[OpenTSDBMeterConf] {
  val `type` = "opentsdb"

  val configReader: ConfigReader[OpenTSDBMeterConf] = ConfigReader[OpenTSDBMeterConf]
}

//object OpenTSDBMeterReg {
//  def apply(f: ())
//}
