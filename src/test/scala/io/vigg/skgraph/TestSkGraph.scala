package io.vigg.skgraph

import org.junit.Test
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(classOf[JUnit4])
class TestSkGraph {

  case class TestEdgeData(name: String)
  case class TestNodeData(name: String, age: Int)
  case class TestEdge(in: Int, out: Int, data: TestEdgeData)
      extends SkEdge[TestEdgeData]

  class TestNode(val id: Int, val data: TestNodeData)(implicit
      val graph: SkGraph[TestNodeData, TestEdgeData]
  ) extends SkNode[TestNodeData, TestEdgeData]

  class TestGraph extends SkGraph[TestNodeData, TestEdgeData]

  implicit val graph: TestGraph = new TestGraph

  @Before def initialize(): Unit = {

    graph.addVertex(new TestNode(12, TestNodeData("tanja", 20)))
    graph.addVertex(new TestNode(15, TestNodeData("james", 21)))
    graph.addVertex(new TestNode(17, TestNodeData("jaimie", 12)))
    graph.addVertex(new TestNode(21, TestNodeData("justin", 4)))

    graph.addEdge(TestEdge(12, 17, TestEdgeData("A")))
    graph.addEdge(TestEdge(15, 17, TestEdgeData("B")))
    graph.addEdge(TestEdge(17, 21, TestEdgeData("C")))

  }

  @Test def testGraphImplementation(): Unit = {

    val parents = graph.get(17).parents.toList

    assert(parents.size == 2)

    assert(parents(0).data.name == "tanja")
    assert(parents(1).data.name == "james")

    assert(parents(0).data.age == 20)
    assert(parents(1).data.age == 21)

    assert(parents(0).id == 12)
    assert(parents(1).id == 15)

  }

}
