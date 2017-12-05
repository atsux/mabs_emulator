import java.util.Iterator;

public class TreeNodeIter implements Iterator<TreeNodeNek> {

  enum ProcessStages {
    ProcessParent, ProcessChildCurNode, ProcessChildSubNode
  }

  private TreeNodeNek treeNode;

  public TreeNodeIter(TreeNodeNek treeNode) {
    this.treeNode = treeNode;
    this.doNext = ProcessStages.ProcessParent;
    this.childrenCurNodeIter = treeNode.children.iterator();
  }

  private ProcessStages doNext;
  private TreeNodeNek next;
  private Iterator<TreeNodeNek> childrenCurNodeIter;
  private Iterator<TreeNodeNek> childrenSubNodeIter;

  @Override
  public boolean hasNext() {

    if (this.doNext == ProcessStages.ProcessParent) {
      this.next = this.treeNode;
      this.doNext = ProcessStages.ProcessChildCurNode;
      return true;
    }

    if (this.doNext == ProcessStages.ProcessChildCurNode) {
      if (childrenCurNodeIter.hasNext()) {
        TreeNodeNek childDirect = childrenCurNodeIter.next();
        childrenSubNodeIter = childDirect.iterator();
        this.doNext = ProcessStages.ProcessChildSubNode;
        return hasNext();
      }

      else {
        this.doNext = null;
        return false;
      }
    }

    if (this.doNext == ProcessStages.ProcessChildSubNode) {
      if (childrenSubNodeIter.hasNext()) {
        this.next = childrenSubNodeIter.next();
        return true;
      }
      else {
        this.next = null;
        this.doNext = ProcessStages.ProcessChildCurNode;
        return hasNext();
      }
    }

    return false;
  }

  @Override
  public TreeNodeNek next() {
    return this.next;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}
