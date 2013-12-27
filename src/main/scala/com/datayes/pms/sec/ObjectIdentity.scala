package com.datayes.pms.sec

trait ObjectIdentity {
  def id: Long
  def tpe: String = getClass.getName
}

object ObjectIdentity {
  case class ObjectIdentityImpl(id: Long, type1: String) extends ObjectIdentity {
    override def tpe: String = type1
  }
  def apply(id1: Long, type1: String) = ObjectIdentityImpl(id1, type1)
}
