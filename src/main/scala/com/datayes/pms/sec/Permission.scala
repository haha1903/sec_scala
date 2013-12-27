package com.datayes.pms.sec

trait Permission {
  val mask: Int
}

case class BasePermission(val mask: Int, val code: String) extends Permission

object Permission {
  val READ = BasePermission(1 << 0, "R")
  val WRITE = BasePermission(1 << 1, "W")
  val CREATE = BasePermission(1 << 2, "C")
  val DELETE = BasePermission(1 << 3, "D")
  val ADMINISTRATION = BasePermission(1 << 4, "A")
}