import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.math.BigDecimal;
import java.util.Map;


public class Main {

  public static MathContext mc = new MathContext(10, RoundingMode.HALF_EVEN);

  private static final Integer TAU = 2;
  private static final BigDecimal QU = new BigDecimal("0.7", mc);
  private static final BigDecimal COST = new BigDecimal("0.002", mc);
  private static final BigDecimal DELTA = new BigDecimal("0.98", mc);
  private static BigDecimal[] BETA = {new BigDecimal("0.700", mc), new BigDecimal("0.60", mc), new BigDecimal("0.500", mc)}; // 0.810137
  // 0.821699999999999999 for tau = 2 0.8, 0.7, 0.6
  // 0.52 - 1true and stop

  public static void main(String[] args) {
    System.out.println("Welcome!");
    calculate();
  }

  private static void calculate() {
    System.out.println("Calculating for tau=" + TAU.toString() + " and initial BETAS=" + Arrays.toString(BETA));

    TreeNodeNek root = new TreeNodeNek(-1, null, zero(), zero());
    ArrayList<TreeNodeNek> rootList = new ArrayList<>(1);
    rootList.add(root);

    TreeNodeNek result = generateTree(0, TAU, BETA.length, rootList);

    System.out.println("Generated tree:");
    TreeNodeNek.printTree(result);
    System.out.println("Calculating optimal subtrees...");

    updateChoiceRevenuesFromNode(root);

    System.out.println("Optimal sub trees: ");
    TreeNodeNek.printOptimalTreeInfo(root);
    System.out.println("Max revenue: " + root.revenue.toString());
  }

  private static BigDecimal updateChoiceRevenuesFromNode(TreeNodeNek node) {
    if (node.children.size() == 1) {
      return node.revenue;
    }
    for (int beta = 1; beta <= BETA.length; beta++) {
      node.choiceRevenue.put(beta,updateChoiceRevenuesFromNode(node.getIthBetaChild(beta, true)).add(updateChoiceRevenuesFromNode(node.getIthBetaChild(beta, false))));
    }
    BigDecimal maxRevenue = zero();
    for (Map.Entry<Integer, BigDecimal> entry : node.choiceRevenue.entrySet()) {
      BigDecimal optionRevenue = entry.getValue();
      if (maxRevenue.compareTo(optionRevenue) < 0) {
        maxRevenue = optionRevenue;
        node.bestChoices = new ArrayList<>();
        node.bestChoices.add(entry.getKey());
      } else if (maxRevenue.compareTo(optionRevenue) == 0) {
        node.bestChoices.add(entry.getKey());
      }
    }
    node.revenue = maxRevenue;
    return maxRevenue;
  }


  private static TreeNodeNek generateTree (int lvl, int tau, int betaLength, List<TreeNodeNek> nodesToGrow) {
    BigDecimal thisLvlCost;

//    if (lvl == 0) {
//      thisLvlCost = zero();
//    } else {
      thisLvlCost = COST.multiply(DELTA.pow(lvl));
//    }

    if (lvl >= tau) {
      nodesToGrow.stream()
              .filter(node -> !node.isTerminalNode())
              .forEach(Main::addLastLevelTerminalNode);
      return nodesToGrow.get(0).getRoot();
    }

    List<TreeNodeNek> childrenToPopulate = new ArrayList<TreeNodeNek>(nodesToGrow.size()*(betaLength*2 + 1));

    for (TreeNodeNek node : nodesToGrow) {
      if (node.isTerminalNode()) {
        continue;
      }
      for (int i = 0; i <= betaLength; i++) {
        if (i == 0) {
          addLastLevelTerminalNode(node);
          continue;
        }
        node.addChild(i, true, thisLvlCost);
        node.addChild(i, false, thisLvlCost);
      }
      childrenToPopulate.addAll(node.children);
    }
      return generateTree(lvl + 1, tau, betaLength, childrenToPopulate);
  }

  private static void addLastLevelTerminalNode(TreeNodeNek node) {
    BigDecimal[] beta_updated = BETA.clone();
    BigDecimal probability = node.calculateProbability(BETA.clone(), QU);



    for (int i = 0; i < BETA.length; i++) {
      List<Boolean> updates = node.getInvestigations(i+1);
      // order of updates doesn't matter - can only have counts, difference in couns  is the power of corresponding update
      for (Boolean update : updates) {
        BigDecimal old_beta = beta_updated[i];
        BigDecimal qBeta = QU.multiply(old_beta, mc);
        BigDecimal q1Beta = (one().subtract(QU, mc)).multiply(old_beta, mc);

        beta_updated[i] = update ?
                qBeta.divide(qBeta.add((one().subtract(QU, mc)).multiply(one().subtract(old_beta, mc), mc), mc), mc) :
                q1Beta.divide(q1Beta.add(QU.multiply(one().subtract(old_beta, mc), mc), mc), mc) ;
      }
    }
    BigDecimal maxBeta = zero();
    for (int i = 0; i < beta_updated.length; i++) {
      maxBeta = maxBeta.compareTo(beta_updated[i]) < 0 ? beta_updated[i] : maxBeta;
    }

    final BigDecimal costs = node.calculateCosts();
    final BigDecimal delta;
//    if (node.getLevel() == 0) {
//      delta = one();
//    } else {
      delta = DELTA.pow(node.getLevel());
//    }
    final BigDecimal discountedRevenue = maxBeta.multiply(delta, mc);
    final BigDecimal expectedUtility = (discountedRevenue.subtract(costs, mc)) .multiply(probability, mc);
    node.addChild(0, null, costs, expectedUtility);
    node.revenue = expectedUtility;
    node.choiceRevenue.put(0, expectedUtility);
  }

  private static BigDecimal one() {
    return new BigDecimal("1", mc);
  }

  private static BigDecimal zero() {
    return new BigDecimal("0", mc);
  }
}
