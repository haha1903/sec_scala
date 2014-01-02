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

object K {
  case class B[+T](s: String)
  implicit val a = B[Int]("bbb")
  def a[A: B](a: A): String = {
    val ba = implicitly[B[A]]
    println(ba)
    "aaa"
  }
  println(a(3))
  def main(args: Array[String]) {
    val l = List(1, 2, 3)
    l.map(a =>
      a.toString)
  }
}