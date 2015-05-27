package controllers
import collection.JavaConversions.propertiesAsScalaMap

object Main {
  def printProps() {
    val props = System.getProperties.toMap
    val colLen = props.keys.map(_.length).max + 1
    for ((k, v) <- props) {
      println(k.padTo(colLen, ' ') + "|" + v)
    }
  }                                               //> printProps: ()Unit
}