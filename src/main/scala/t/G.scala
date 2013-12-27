package t

import com.datayes.pms.sec._
import javax.sql.DataSource

object G {
  def f1[T <% ObjectIdentity](i: Int)(f2: => List[T]) = {
    val r = f2
    r.foreach(p => println("id : " + p.id))
  }
  def sec[R <% ObjectIdentity](permissions: Permission*)(f: => List[R])(implicit ds: DataSource, ls: LookupStrategy): List[R] = {
    val lr = f
    lr.foreach(p => println(p.id))
    lr
  }
  def f2[K](i: Int) = f3[K] _
  def f3[T](p: => T) = p

//  def f4[T](i: Int) = (p1: (() => String)) => p1
//  val a = (p: (=> String)) => "haha"

//  f4(3)(() => "asdfa")

  def k1(i: Int) = (s: String) => println(s)
  def k2 = k1(3)
  1.to(10)
}


import G._

object H {
  def main(args: Array[String]) {
    implicit def sToC1(s: String) = ObjectIdentity(s.toLong, "string")
    f1(33)(List("123"))
    val f10: String = f2(3) {
      "888"
    }
    def f4 = f2[Any](3)
    val f = f4 {
      5
    }
    println(f)
    def f8 = f1[String](44) _
    f8(List("123"))
  }
}