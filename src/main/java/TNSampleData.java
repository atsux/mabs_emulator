import java.math.BigDecimal;

class TNSampleData {

  public static TreeNodeNek getSet1() {
    TreeNodeNek root = new TreeNodeNek(-1, null, new BigDecimal(0), new BigDecimal(0));
    {
      TreeNodeNek node0 = root.addChild(0, true);
      TreeNodeNek node1 = root.addChild(1, true);
      TreeNodeNek node2 = root.addChild(2, null);
      {
        TreeNodeNek node20 = node2.addChild(2, null);
        TreeNodeNek node21 = node2.addChild(21, null);
        {
          TreeNodeNek node210 = node21.addChild(210, null);
          TreeNodeNek node211 = node21.addChild(211, null);
        }
      }
      TreeNodeNek node3 = root.addChild(3, null);
      {
        TreeNodeNek node30 = node3.addChild(30, null);
      }
    }

    return root;
  }

//  public static TreeNodeNek getSetSOF() {
//    TreeNodeNek root = new TreeNodeNek("root");
//    {
//      TreeNodeNek node0 = root.addChild("node0");
//      TreeNodeNek node1 = root.addChild("node1");
//      TreeNodeNek node2 = root.addChild("node2");
//      {
//        TreeNodeNek node20 = node2.addChild(null);
//        TreeNodeNek node21 = node2.addChild("node21");
//        {
//          TreeNodeNek node210 = node20.addChild("node210");
//        }
//      }
//    }
//
//    return root;
//  }
}
