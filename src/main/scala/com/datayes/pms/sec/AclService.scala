package com.datayes.pms.sec

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import scala.slick.jdbc.{StaticQuery => Q, StaticQuery0, SetParameter, GetResult, PositionedParameters}
import Q.interpolation
import javax.sql.DataSource

object AclService {
  private def auth = SecurityContext.authentication

  def readAclsById(ois: List[ObjectIdentity], sids: List[Sid])(implicit ds: DataSource, ls: LookupStrategy): Map[ObjectIdentity, Acl] = {
    ls.readAclsById(ois, sids)
  }

  def sid(name: String) = sql"select id from acl_sid where principal=1 and sid=$name".as[Long].firstOption

  def aclClass(oiClass: String) = sql"select id from acl_class where class=$oiClass".as[Long].firstOption

  def objectIdentity(oi: ObjectIdentity) = {
    val oiClass = oi.tpe
    val oiId = oi.id
    sql"""select acl_object_identity.id from acl_object_identity, acl_class
    where acl_object_identity.object_id_class = acl_class.id and
    acl_class.class=$oiClass and acl_object_identity.object_id_identity = $oiId""".as[Long].firstOption
  }
  def createAcl(oi: ObjectIdentity)(implicit ds: DataSource, ls: LookupStrategy) = {
    Database.forDataSource(ds).withDynSession {
      val oiClass = oi.tpe
      val oiId = oi.id
      objectIdentity(oi) match {
        case Some(x) =>
        case None =>
          sid(auth.name) match {
            case Some(ownerId) =>
              val oiClassId = aclClass(oiClass) match {
                case Some(x) => x
                case None => sqlu"insert into acl_class (class) values ($oiClass)".execute
                  sql"SELECT @@IDENTITY".as[Long].first
              }
              sqlu"""insert into acl_object_identity (object_id_class, object_id_identity, owner_sid, entries_inheriting)
              values ($oiClassId, $oiId, $ownerId, 1)""".first
            case None => println("sid not found: name = " + auth.name)
          }
      }
      readAclsById(List(oi), null).get(oi).get
    }
  }

  def createAce(ace: Ace)(implicit ds: DataSource, ls: LookupStrategy) = {
    Database.forDataSource(ds).withDynSession {
      val oi = ace.acl.objectIdentity
      val oiId = objectIdentity(oi).get
      val aceOrder = ace.acl.entries.length
      val sid2 = sid(ace.sid.sid).get
      val mask = ace.permission.mask
      sqlu"""insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure)
      values ($oiId, $aceOrder, $sid2, $mask, 1, 0, 0)""".execute
      readAclsById(List(oi), null).get(oi).get
    }
  }
}
