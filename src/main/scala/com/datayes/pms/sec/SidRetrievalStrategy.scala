package com.datayes.pms.sec
import javax.sql.DataSource

trait SidRetrievalStrategy {
  def getSids(authentication: Authentication)(implicit ds: DataSource): List[Sid]
}
