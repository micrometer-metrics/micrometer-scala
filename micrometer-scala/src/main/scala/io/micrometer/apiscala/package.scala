package io.micrometer

import io.micrometer.core.instrument.MeterRegistry

package object apiscala {
  type TypedRegistriesInitializer[T <: MeterConf] = (String, T) => MeterRegistry
}
