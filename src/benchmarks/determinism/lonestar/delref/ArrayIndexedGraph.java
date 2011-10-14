/*
    Lonestar ArrayIndexedGraph: A directed graph class for trees and DAGs

    Author: Martin Burtscher
    Center for Grid and Distributed Computing
    The University of Texas at Austin

    Copyright (C) 2007, 2008, 2009 The University of Texas at Austin

    Licensed under the Eclipse Public License, Version 1.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.eclipse.org/legal/epl-v10.html

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    File: ArrayIndexedGraph.java
    Modified: Apr. 20, 2009 by Martin Burtscher (initial version)
*/

package benchmarks.determinism.lonestar.delref;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ArrayIndexedGraph<NodeData> implements IndexedGraph<NodeData> {
  protected Set<ArrayIndexedNode> nodes;
  protected final int numberOfNeighbors;

  public ArrayIndexedGraph(int numberOfNeighbors) {
    this.nodes = Collections.synchronizedSet(new HashSet<ArrayIndexedNode>());
    this.numberOfNeighbors = numberOfNeighbors;
  }


  public Node<NodeData> createNode(NodeData data) {
    ArrayIndexedNode node = new ArrayIndexedNode(data);
    return node;
  }


  public boolean addNode(Node<NodeData> node) {
    return nodes.add((ArrayIndexedNode) node);
  }


  public boolean removeNode(Node<NodeData> node) {
    ArrayIndexedNode inode = (ArrayIndexedNode) node;
    ArrayIndexedNode[] neighbors = inode.neighbors;
    Arrays.fill(neighbors, null);
    return nodes.remove(node);
  }


  public boolean containsNode(Node<NodeData> n) {
    return nodes.contains(n);
  }


  public Node<NodeData> getRandom() {
    return nodes.iterator().next();
  }

  @SuppressWarnings("unchecked")

  public Iterator iterator() {
    return nodes.iterator();
  }

  // This method is not supported in an IndexedGraph because it is
  // not clear which neighbor the added neighbor should become.
  public boolean addNeighbor(Node<NodeData> src, Node<NodeData> dst) {
    throw new UnsupportedOperationException(
        "ArrayIndexedGraph.addNeighbor(Node<NodeData>, Node<NodeData>) unimplemented");
  }

  protected final int neighborIndex(Node<NodeData> src, Node<NodeData> dst) {
    ArrayIndexedNode isrc = (ArrayIndexedNode) src;
    ArrayIndexedNode[] neighbors = isrc.neighbors;
    List<ArrayIndexedNode> neighborList = Arrays.asList(neighbors);
    return neighborList.indexOf(dst);
  }


  public boolean removeNeighbor(Node<NodeData> src, Node<NodeData> dst) {
    int idx = neighborIndex(src, dst);
    if (0 <= idx) {
      ArrayIndexedNode isrc = (ArrayIndexedNode) src;
      isrc.neighbors[idx] = null;
      return true;
    }
    return false;
  }


  public boolean hasNeighbor(Node<NodeData> src, Node<NodeData> dst) {
    return 0 <= neighborIndex(src, dst);
  }


  public Collection<? extends Node<NodeData>> getInNeighbors(Node<NodeData> node) {
    throw new UnsupportedOperationException(
        "ArrayIndexedGraph.getInNeighbors(Node<NodeData>) unimplemented");
  }


  public Collection<? extends Node<NodeData>> getOutNeighbors(Node<NodeData> node) {
    ArrayIndexedNode inode = (ArrayIndexedNode) node;
    return (Collection<? extends Node<NodeData>>) Collections
        .unmodifiableList(Arrays.asList(inode.neighbors));
  }


  public int getNumNodes() {
    return nodes.size();
  }


  public void setNeighbor(Node<NodeData> src, int idx, Node<NodeData> dst) {
    ArrayIndexedNode isrc = (ArrayIndexedNode) src;
    isrc.neighbors[idx] = (ArrayIndexedNode) dst;
  }


  public Node<NodeData> getNeighbor(Node<NodeData> node, int idx) {
    ArrayIndexedNode inode = (ArrayIndexedNode) node;
    return inode.neighbors[idx];
  }

  public void removeNeighbor(Node<NodeData> node, int idx) {
    ArrayIndexedNode inode = (ArrayIndexedNode) node;
    inode.neighbors[idx] = null;
  }


  public NodeData setNodeData(Node<NodeData> node, NodeData new_data) {
    ArrayIndexedNode ainode = (ArrayIndexedNode) node;
    NodeData old_data = (NodeData) ainode.data;
    ainode.data = new_data;
    return old_data;
  }


  public NodeData getNodeData(Node<NodeData> node) {
    return ((ArrayIndexedNode) node).data;
  }

  protected class ArrayIndexedNode implements Node<NodeData> {
    protected NodeData data;
    protected ArrayIndexedNode[] neighbors;

    public ArrayIndexedNode(NodeData data) {
      this.data = data;
      neighbors =
          (ArrayIndexedNode[]) Array.newInstance(this.getClass(), numberOfNeighbors);
      Arrays.fill(neighbors, null);
    }


    public NodeData getData() {
      return getNodeData(this);
    }


    public NodeData setData(NodeData node) {
      return setNodeData(this, node);
    }
  }
}
