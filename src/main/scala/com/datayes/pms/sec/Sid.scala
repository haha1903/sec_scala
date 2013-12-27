package com.datayes.pms.sec

case class Sid(id: Long, sid: String)

object Sid {
  implicit def stringToSid(sid: String) = Sid(0, sid)
  def apply(sid: String): Sid = Sid(0, sid)
}