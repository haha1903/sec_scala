package com.datayes.pms.sec
import javax.sql.DataSource

trait LookupStrategy {
  def readAclsById(ois: List[ObjectIdentity], sids: List[Sid])(implicit ds: DataSource): Map[ObjectIdentity, Acl]
}
