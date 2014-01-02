package com.datayes.pms.sec

trait Ace {
  val acl: Acl
  val id: Long = 0
  val permission: Permission
  val sid: Sid
  val granting: Boolean = true
}
