package t

import java.util.Comparator

object J {
  implicit val intComparator = new Comparator[Int] {
    def compare(o1: Int, o2: Int): Int = o1.hashCode - o2.hashCode
  }
  implicit val s1 = "hello"
  def max[T: Comparator](a: T, b: T)(implicit s: String) = {
    println(s1)
    val cp = implicitly[Comparator[T]]
    if (cp.compare(a, b) > 0) a else b
  }
  def main(args: Array[String]) {
    println(max(1, 2))
  }
}
object K extends App {
  class C1 {
    def id = 3
    def id_=(i: Int) {id = i}
  }
  val c = new C1
//  println(c.getClass.getDeclaredMethods.map(_.getName).mkString("\n"))
  println(c.getClass.getDeclaredFields.length)
  println(c.getClass.getDeclaredFields.map(_.getName).mkString("\n"))
}