package t

class B(implicit c: C) {
  def pc = {
    println(c)
  }
}
case class C(val s: String)