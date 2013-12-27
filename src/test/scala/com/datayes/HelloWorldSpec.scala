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

  "The 'Hello world' string" should {

    "haha" in {
      implicit val ds = new BasicDataSource
      ds.setDriverClassName("com.mysql.jdbc.Driver")
      ds.setUrl("jdbc:mysql://localhost/security")
      ds.setUsername("root")
      ds.setPassword("")
      implicit val ls = BasicLookupStrategy
      val acls = AclService.readAclsById(ObjectIdentity(1, "com.datayes.paas.Foo") :: Nil, Sid("haha") :: Nil)
      println(acls)
      ok
    }
    "security" in {
      SecurityContext.authentication = Authentication("haha", "", List(Sid("ROLE_ADMIN")))
      def fr[R <% ObjectIdentity](f: => List[R]) = sec[R](Permission.READ)(f)
      def fw[R <% ObjectIdentity](f: => List[R]) = sec[R](Permission.WRITE)(f)

      val r = sec(Permission.READ) {
        List(1, 2)
      }
      println(r)

      println(fw {
        List(1, 2)
      })
      println(fr {

        List("s1", "s2")
      })

      grant(READ)(1) {
        println("read 1")
      }
      grant(READ)(2) {
        println("read 2")
      }
      grant(WRITE)(1) {
        println("write 1")
      }
      grant(WRITE)(2) {
        println("write 2")
      }
      role("ROLE_ADMIN", "ROLE_READER") {
        println("success")
      }
      role("ROLE_HAHA") {
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
        val granting: Boolean = true
      })
      ok
    }
  }
}