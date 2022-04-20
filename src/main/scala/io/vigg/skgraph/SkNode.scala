package io.vigg.skgraph

import scala.collection.mutable

trait SkNode[N, E] {

  implicit val graph: SkGraph[N, E]

  val id: Int
  val data: N

  var in = mutable.HashSet[Int]()
  var out = mutable.HashSet[Int]()

  def addIn(edge: SkEdge[E]): Unit = {
    in += edge.in
  }

  def addOut(edge: SkEdge[E]): Unit = {
    out += edge.out
  }

  def removeIn(edge: SkEdge[E]): Unit = {
    in -= edge.in
  }

  def removeOut(edge: SkEdge[E]): Unit = {
    in -= edge.out
  }

  def children: mutable.Set[SkNode[N, E]] = out.map(x => graph.get(x))
  def parents: mutable.Set[SkNode[N, E]] = in.map(x => graph.get(x))

}
