

## SkGraph

_pronunciation: (es-kay-graf)_

SkGraph is a graph database implemented in Scala.

This database is intended to allow low latency access to wide sparsely connected graphs. There
are three basic primitives available, all implemented as traits.

### Concepts

**1.)** **SkEdge**

This represents connections between SkNodes, with optional metadata carried through
via the trait parameter:

``` 
trait SkEdge[E] {
  val in: Int
  val out: Int
}
```

Here "in" is the indentifier for the source of the connection, and "out" is the identifer 
for the destination of the connection. The trait parameter `E` can be any metadata that you
with to attached to the edge, such as the name, data created, or anything else you
can think of.

**2.) SkNode**

This is the vertices in the graph, with access methods (not shown here for brevity), and
chainable calls for children and parents of the node. 
``` 
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
```
Just like with SkEdge, you can carry
along arbitrary data with the node via the trait parameter `N`. The edge parameter is also required
when specifying the node, so that the node is aware of the metadata attached to an edge.

**3.) SkGraph**

SkGraph is a collection of nodes and edges, with the relations defined between them. This includes an map
of the connections for fast lookup, as well as searchable buffers. Specification of the node and 
edge metadata via trait parameters is required when instantiating graph.

```
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


```

### Example Implementation

An example graph implementation is included in `/src/test/scala/io/vigg/skgraph/TestSkGraph`. A basic
setup works as follows:

1.) Define the metadata classes for the edges and nodes. In this example, the edges have no extra data,
while the nodes store a name and age property of the node

``` 
case class TestEdgeData(name: String)
case class TestNodeData(name: String, age: Int)
```

2.) Define your node and edge types:

``` 
  case class TestEdge(
      in: Int, 
      out: Int, 
      data: TestEdgeData
  ) 
  extends SkEdge[TestEdgeData]

  class TestNode(
      val id: Int, 
      val data: TestNodeData
  )(
    implicit val graph: SkGraph[TestNodeData, TestEdgeData]
  ) 
  extends SkNode[TestNodeData, TestEdgeData]
  
```

This extends the basic node to hold the metadata.

3.) Lastly, define your graph:

``` 
    class TestGraph extends SkGraph[TestNodeData, TestEdgeData]
```

That's all you need to get started! You can of course bring your own data classes to
extends this to any arbitrary data. A simple graph instantiation and you're ready to 
start building!

``` 
    implicit val graph: TestGraph = new TestGraph
    
    graph.addVertex(new TestNode(12, TestNodeData("tanja", 20)))
    graph.addVertex(new TestNode(15, TestNodeData("james", 21)))
    graph.addVertex(new TestNode(17, TestNodeData("jaimie", 12)))
    graph.addVertex(new TestNode(21, TestNodeData("justin", 4)))

    graph.addEdge(TestEdge(12, 17, TestEdgeData("A")))
    graph.addEdge(TestEdge(15, 17, TestEdgeData("B")))
    graph.addEdge(TestEdge(17, 21, TestEdgeData("C")))
```