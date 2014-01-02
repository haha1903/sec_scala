package com.datayes.pms.sec

import javax.sql.DataSource

object Security {
  private def roles(implicit ds: DataSource, srs: SidRetrievalStrategy) = srs.getSids(SecurityContext.authentication)
  def secured[R <% ObjectIdentity](permissions: Permission*)(f: => List[R])(implicit ds: DataSource, ls: LookupStrategy, srs: SidRetrievalStrategy): List[R] = {
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
  def isGranted[R <% ObjectIdentity, T](oi: R, permissions: Permission*)(implicit ds: DataSource, ls: LookupStrategy, srs: SidRetrievalStrategy) = {
    val acls = AclService.readAclsById(List(oi), roles)
    acls.get(oi) match {
      case Some(acl) => acl.isGranted(permissions.toList, roles)
      case None => false
    }
  }
  def hasRole[R](sids: Sid*)(implicit ds: DataSource, srs: SidRetrievalStrategy) = sids.exists(roles.contains)
}