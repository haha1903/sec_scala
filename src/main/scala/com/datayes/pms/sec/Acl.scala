package com.datayes.pms.sec

trait Acl {
  def entries: List[Ace]
  val objectIdentity: ObjectIdentity
  val owner: Sid
  val parentAcl: Acl
  val entriesInheriting: Boolean
  def isGranted(permissions: List[Permission], sids: List[Sid]): Boolean = permissions.exists { p =>
    sids.exists { sid =>
      entries.exists { ace => ace.sid == sid && ace.permission.mask == p.mask && ace.granting
      }
    }
  }
  def isSidLoaded(sids: List[Sid]): Boolean = sids.forall(entries.map(_.sid).contains)
}