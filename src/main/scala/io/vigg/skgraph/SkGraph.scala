package io.vigg.skgraph

import scala.collection.mutable

trait SkGraph[N, E] {

  private val edges = mutable.ListBuffer[SkEdge[E]]()
  private val nodes = mutable.ListBuffer[SkNode[N, E]]()
  private val nodeMap = mutable.Map[Int, SkNode[N, E]]()

  def get(id: Int): SkNode[N, E] = nodeMap(id)

  def addVertex(n: SkNode[N, E]): Unit = {

    /* add node at current index */
    nodes.addOne(n)

    /* add node to node map */
    nodeMap += (n.id -> n)

  }

  def addEdge(edge: SkEdge[E]): Unit = {

    val in = nodeMap.getOrElse(edge.in, null)
    val out = nodeMap.getOrElse(edge.out, null)

    if (in == null || out == null) {
      throw new Exception("node for edge not found")
    }

    in.addOut(edge)
    out.addIn(edge)

    edges.addOne(edge)

  }

  def addVertices(vs: List[SkNode[N, E]]): Unit = {
    vs.foreach(x => {
      addVertex(x)
    })
  }

  def addEdges(es: List[SkEdge[E]]): Unit = {
    es.foreach(x => {
      addEdge(x)
    })
  }

}
