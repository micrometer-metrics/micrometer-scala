package io.micrometer.scala

trait ToDouble[-A] {

  def toDouble(value: A): Double

}

object ToDouble {

  def apply[A](implicit ev: ToDouble[A]): ToDouble[A] = ev

  implicit object IntToDouble extends ToDouble[Int] {
    override def toDouble(value: Int): Double = value.toDouble
  }

  implicit object NumberToDouble extends ToDouble[Number] {
    override def toDouble(value: Number): Double = value.doubleValue()
  }

}
