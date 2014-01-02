package com.datayes.pms.sec

import scala.slick.driver.JdbcDriver.backend.Database
import Database.dynamicSession
import scala.slick.jdbc.{StaticQuery => Q, _}
import Q.interpolation
import javax.sql.DataSource
import scala.Some
import com.datayes.pms.sec.model.CycleInRoleHierarchyException

object SidRetrievalStrategyImpl extends SidRetrievalStrategy {

  def getSids(auth: Authentication)(implicit ds: DataSource): List[Sid] = {
    (Sid(auth.name) :: roles(auth.name).flatMap { name =>
      rolesMap.get(name) match {
        case Some(sids) => sids.map(Sid(_)) + Sid(name)
        case None => Set(Sid(name))
      }
    }).distinct
  }
  def roles(name: String)(implicit ds: DataSource): List[String] = {
    Database.forDataSource(ds).withDynSession {
      sql"""select authority from authorities where username = $name""".as[String].list()
    }
  }

  def children(map: Map[String, Set[String]], higher: String, visited: Set[String], toVisit: Set[String]): Set[String] = {
    toVisit.foldLeft(Set[String]())((visiting, visit) => {
      map.get(visit) match {
        case Some(lowers) => if (lowers.contains(higher) || !(lowers & visited).isEmpty || !(lowers & visiting).isEmpty) throw new CycleInRoleHierarchyException()
          children(map, higher, visited ++ lowers, lowers)
        case None => visited
      }
    })
  }

  def rolesMap(implicit ds: DataSource): Map[String, Set[String]] = {
    Database.forDataSource(ds).withDynSession {
      val oneStep = sql"""select higher.sid higher_sid, lower.sid lower_sid from acl_sid_include i join acl_sid higher on(i.higher = higher.id)
      join acl_sid lower on(i.lower = lower.id)""".as[(String, String)].list().foldLeft(Map[String, Set[String]]())((map, r) => {
        val (higher, lower) = r
        map + (map.get(higher) match {
          case Some(lowers) => (higher -> (lowers + lower))
          case None => (higher -> Set(lower))
        })
      })
      oneStep.foldLeft(Map[String, Set[String]]())((map, r) => {
        val (higher, lowers) = r
        map + (higher -> children(oneStep, higher, lowers, lowers))
      })
    }
  }
}
