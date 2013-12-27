package com.datayes.pms.sec

import javax.sql.DataSource

object Security {
  implicit def anyToOI(t: Any) = ObjectIdentity(t.hashCode, t.getClass.getName)
  private def auth = SecurityContext.authentication
  private def roles = auth.roles
  def sec[R <% ObjectIdentity](permissions: Permission*)(f: => List[R])(implicit ds: DataSource, ls: LookupStrategy): List[R] = {
    val lr = f
    val ois = lr.map { p => val i: ObjectIdentity = p; i}
    val acls = AclService.readAclsById(ois, roles)
    lr.filter {
      acls.get(_) match {
        case Some(acl) => acl.isGranted(permissions.toList, roles)
        case None => false
      }
    }
  }
  def grant[R <% ObjectIdentity, T](permissions: Permission*)(oi: R)(f: => T)(implicit ds: DataSource, ls: LookupStrategy) = {
    val acls = AclService.readAclsById(List(oi), roles)
    if (acls.get(oi) match {
      case Some(acl) => acl.isGranted(permissions.toList, roles)
      case None => false
    }) f
  }
  def role[R](sids: Sid*)(f: => R) = {
    if (sids.exists(roles.contains)) f
  }
}