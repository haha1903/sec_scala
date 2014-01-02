import com.datayes.pms.sec._
import com.datayes.pms.sec.Sid
import org.apache.commons.dbcp.BasicDataSource
import org.specs2.mutable._
import com.datayes.pms.sec.Security._
import Permission._

class HelloWorldSpec extends Specification {

  implicit val ds = new BasicDataSource
  ds.setDriverClassName("com.mysql.jdbc.Driver")
  ds.setUrl("jdbc:mysql://localhost/security")
  ds.setUsername("root")
  ds.setPassword("")
  implicit val ls = BasicLookupStrategy
  implicit val srs = SidRetrievalStrategyImpl

  "The 'Hello world' string" should {

    "haha" in {
      implicit var ds = new BasicDataSource
      ds.setDriverClassName("com.mysql.jdbc.Driver")
      ds.setUrl("jdbc:mysql://localhost/security")
      ds.setUsername("root")
      ds.setPassword("")
      implicit var ls = BasicLookupStrategy
      var acls = AclService.readAclsById(ObjectIdentity(1, "com.datayes.paas.Foo") :: Nil, Sid("haha") :: Nil)
      println(acls)
      ds = new BasicDataSource
      ds.setDriverClassName("com.mysql.jdbc.Driver")
      ds.setUrl("jdbc:mysql://localhost/security2")
      ds.setUsername("root")
      ds.setPassword("")
      ls = BasicLookupStrategy
      acls = AclService.readAclsById(ObjectIdentity(1, "com.datayes.paas.Foo") :: Nil, Sid("haha") :: Nil)
      println(acls)
      ok
    }
    "security" in {
      SecurityContext.authentication = Authentication("haha", "", List(Sid("ROLE_ADMIN")))
      def fr[R <% ObjectIdentity](f: => List[R]) = secured[R](Permission.READ)(f)
      def fw[R <% ObjectIdentity](f: => List[R]) = secured[R](Permission.WRITE)(f)

      val r = secured(Permission.READ) {
        List(1, 2)
      }
      println(r)

      println(fr {
        List(1, 2)
      })
      println(fw {
        List(1, 2)
      })
      implicit def intToOi(i: Int) = ObjectIdentity(i + 1, "java.lang.Integer")
      println(fr {
        List(1, 2)
      })
      println(fw {
        List(1, 2)
      })
      println(fr {
        List("s1", "s2")
      })
      println(fw {
        List("s1", "s2")
      })

      if (isGranted(1, READ)) {
        println("read 1")
      }
      if (isGranted(2, READ)) {
        println("read 2")
      }
      if (isGranted(1, WRITE)) {
        println("write 1")
      }
      if (isGranted(2, WRITE)) {
        println("write 2")
      }
      if (isGranted(2, WRITE)) {
        println("write 2")
      } else {
        println("no write 2")
      }
      if (hasRole("ROLE_ADMIN", "ROLE_READER")) {
        println("success")
      }
      if (hasRole("ROLE_HAHA")) {
        println("failure")
      }
      ok
    }
    "create acl" in {
      SecurityContext.authentication = Authentication("haha", "", List(Sid("ROLE_ADMIN")))
      val acl1 = AclService.createAcl(ObjectIdentity(101, "hehe"))
      val acl2 = AclService.createAce(new Ace {
        val acl = acl1
        val permission = Permission.WRITE
        val sid = Sid("haha")
      })
      ok
    }
  }
}