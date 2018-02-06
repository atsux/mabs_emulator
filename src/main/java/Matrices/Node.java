package Matrices;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Node {
  private int option;
  private Boolean parentResolution;
  private Node parent;
  private Node plusResolution;
  private Node minusResolution;

  public Node(int option, Boolean parentResolution, Node parent) {
    this.option = option;
    this.parentResolution = parentResolution;
    this.parent = parent;
  }

  public int getDepth() {
    if (plusResolution == null && minusResolution == null) {
      return 0;
    }
    if (plusResolution == null ) {
      return minusResolution.getDepth() + 1;
    }
    if (minusResolution == null ) {
      return plusResolution.getDepth() + 1;
    }

    return Math.max(plusResolution.getDepth(), minusResolution.getDepth()) + 1;
  }

  public Set<Node> getIthTerminalChildren(int j) {
    if (!this.isRoot()) {
      System.out.println("Error: got children not from root");
    }
    if (j == 0) {
      return Collections.singleton(this);
    }
    Set<Node> rootChildren = new HashSet<>();
    rootChildren.add(this.getMinusResolution());
    rootChildren.add(this.getPlusResolution());

    return getIthTerminalChildren(j - 1, rootChildren);
  }

  private Set<Node> getIthTerminalChildren(int j, Set<Node> childrenToSpawn) {
    if (j <= 0) {
      return childrenToSpawn.stream()
              .filter(Node::isTerminal)
              .collect(Collectors.toSet());
    }

    Set<Node> nextLevelChildren = new HashSet<>();
    for (Node child: childrenToSpawn) {
      if (!child.isTerminal()) {
        nextLevelChildren.add(child.getPlusResolution());
        nextLevelChildren.add(child.getMinusResolution());
      }
    }

    return getIthTerminalChildren(j - 1, nextLevelChildren);
  }

  private boolean isTerminal() {
    return this.minusResolution == null && this.plusResolution == null;
  }

  public boolean isRoot() {
    return parentResolution == null;
  }

  public Boolean getParentResolution() {
    return parentResolution;
  }

  public int getOption() {
    return option;
  }

  public Node getPlusResolution() {
    return plusResolution;
  }

  public void setPlusResolution(Node plusResolution) {
    this.plusResolution = plusResolution;
  }

  public Node getMinusResolution() {
    return minusResolution;
  }

  public Node getParent() {
    return parent;
  }

  public void setMinusResolution(Node minusResolution) {
    this.minusResolution = minusResolution;
  }

  public void setResolutions(int plus, int minus) {
    this.setPlusResolution(new Node (plus, true, this));
    this.setMinusResolution(new Node(minus, false, this));
  }

}
