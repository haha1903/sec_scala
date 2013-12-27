package com.datayes.pms.sec

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import scala.slick.jdbc.{StaticQuery => Q, SetParameter, GetResult, PositionedParameters}
import Q.interpolation
import javax.sql.DataSource

object BasicLookupStrategy extends LookupStrategy {

  implicit object SetObjectIdentitity extends SetParameter[ObjectIdentity] {
    def apply(v: ObjectIdentity, pp: PositionedParameters) {
      pp.setLong(v.id)
      pp.setString(v.tpe)
    }
  }

  val selectClause =
    """select acl_object_identity.object_id_identity,
      acl_entry.ace_order,
      acl_object_identity.id as acl_id,
      acl_object_identity.parent_object,
      acl_object_identity.entries_inheriting,
      acl_entry.id as ace_id,
      acl_entry.mask,
      acl_entry.granting,
      acl_entry.audit_success,
      acl_entry.audit_failure,
      acl_sid.principal as ace_principal,
      acl_sid.sid as ace_sid,
      acli_sid.principal as acl_principal,
      acli_sid.sid as acl_sid, acl_class.class
      from acl_object_identity left join acl_sid acli_sid on acli_sid.id = acl_object_identity.owner_sid left join acl_class on acl_class.id = acl_object_identity.object_id_class left join acl_entry on acl_object_identity.id = acl_entry.acl_object_identity left join acl_sid on acl_entry.sid = acl_sid.id
      where ( """
  val lookupPrimaryKeysWhereClause = "(acl_object_identity.id = ?)"
  val lookupObjectIdentitiesWhereClause = "(acl_object_identity.object_id_identity = ? and acl_class.class = ?)"
  val orderByClause = ") order by acl_object_identity.object_id_identity asc, acl_entry.ace_order asc"

  def readAclsById(ois: List[ObjectIdentity], sids: List[Sid])(implicit ds: DataSource): Map[ObjectIdentity, Acl] = {
    ois.map { identity =>
      val acl = lookupObjectIdentitiy(identity, sids)
      (identity, acl)
    }.filter(_._2 != null).toMap
  }

  def lookupObjectIdentitiy(identity: ObjectIdentity, sids: List[Sid])(implicit ds: DataSource): Acl = {
    val sql = selectClause + lookupObjectIdentitiesWhereClause + orderByClause
    Database.forDataSource(ds).withDynSession {
      case class AceRecord(objectIdIdentity: Long, aceOrder: Int, aclId: Long, parentObject: Option[Long], entriesInheriting: Boolean, aceId: Long,
                           mask: Option[Int], granting: Boolean, auditSuccess: Boolean, auditFailure: Boolean, acePrincipal: Boolean, aceSid: String,
                           aclPrincipal: Boolean, aclSid: String, clazz: String)
      implicit val getAceRecordResult = GetResult(r => AceRecord(r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
      val rs = (Q[ObjectIdentity, AceRecord] + sql).list(identity)
      if (!rs.isEmpty) {
        val acl = new Acl {
          self =>
          val objectIdentity = ObjectIdentity(rs(0).objectIdIdentity, rs(0).clazz)
          val parentAcl: Acl = null
          val entries = rs.map(r => {
            r.mask match {
              case Some(m) =>
                new Ace {
                  val granting: Boolean = r.granting
                  val acl = self
                  val sid = Sid(r.aceSid)
                  override val id = r.aceId
                  val permission: Permission = new Permission {
                    val mask: Int = m
                  }
                }
              case None => null
            }
          }).filter(_ != null)
          val entriesInheriting = rs(0).entriesInheriting
          val owner = Sid(rs(0).aclSid)
        }
        acl
      } else null
    }
  }
}
