package com.datayes.pms.sec

object SecurityContext {
  private val tl = new ThreadLocal[Authentication]
  def authentication = tl.get()
  def authentication_=(authentication: Authentication) = tl.set(authentication)
}
