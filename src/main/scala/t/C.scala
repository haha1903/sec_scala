package t

object E extends App {
  implicit val c = C("haha")
  val b = new B
  b.pc
}
