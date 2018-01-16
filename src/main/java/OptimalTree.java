import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.util.Map;


public class OptimalTree {

  public static final int EPS_DECIMALS = 20;
  public static MathContext mc = new MathContext(50, RoundingMode.HALF_EVEN);
  public static MathContext mcEpsilon = new MathContext(EPS_DECIMALS, RoundingMode.HALF_UP);
//  public static MathContext mc = MathContext.DECIMAL128;

  private static Integer TAU;
  private static BigDecimal QU;
  private static BigDecimal COST;
  private static BigDecimal DELTA;
  private static List<BigDecimal> BETA; // 0.810137
  // 0.821699999999999999 for tau = 2 0.8, 0.7, 0.6
  // 0.52 - 1true and stop

  public static void main(String[] args) {
    System.out.println("Welcome!");
    ArrayList<BigDecimal> betas = new ArrayList<>(3);
    // rewrite to always use option labeles instead of converting 0<>1
    betas.add(new BigDecimal("0.7", mc));
    betas.add(new BigDecimal("0.6", mc));
    betas.add(new BigDecimal("0.5", mc));
    TreeNodeNek result = runCalculation(3, "0.7", "0.002", "0.98", betas);
    System.out.println("Generated tree: ");
    result.printOptimalTreeInfo();
    System.out.println("Max revenue: " + result.revenue.toString());

  }

  public static TreeNodeNek runCalculation(int tau, String qu, String cost, String delta, List<BigDecimal> betas) {
    TAU = tau;
    QU = new BigDecimal(qu, mc);
    COST = new BigDecimal(cost, mc);
    DELTA = new BigDecimal(delta, mc);
    BETA = betas;

//    System.out.println("Calculating for tau=" + TAU.toString() + " and initial BETAS=" + BETA.toString());

    TreeNodeNek root = new TreeNodeNek(-1, null, zero(), zero());
    ArrayList<TreeNodeNek> rootList = new ArrayList<>(1);
    rootList.add(root);

    TreeNodeNek result = generateTree(0, TAU, BETA.size(), rootList);

//    System.out.println("Generated tree:");
//    TreeNodeNek.printTree(result);
//    System.out.println("Calculating optimal subtrees...");

    updateChoiceRevenuesFromNode(root);

    return result;
//    System.out.println("Optimal sub trees: ");
//    TreeNodeNek.printOptimalTreeInfo(root);
//    System.out.println("Max revenue: " + root.revenue.toString());
  }

  private static BigDecimal updateChoiceRevenuesFromNode(TreeNodeNek node) {
    if (node.children.size() == 1) {
      return node.revenue;
    }
    for (int beta = 1; beta <= BETA.size(); beta++) {
      node.choiceRevenue.put(beta,updateChoiceRevenuesFromNode(node.getIthBetaChild(beta, true)).add(updateChoiceRevenuesFromNode(node.getIthBetaChild(beta, false))));
    }
    BigDecimal maxRevenue = zero();
    for (Map.Entry<Integer, BigDecimal> entry : node.choiceRevenue.entrySet()) {
      BigDecimal optionRevenue = entry.getValue();
      if (compareWithEpsilon(maxRevenue, optionRevenue) < 0) {
        maxRevenue = optionRevenue;
        node.bestChoices = new ArrayList<>();
        node.bestChoices.add(entry.getKey());
      } else if (compareWithEpsilon(maxRevenue, optionRevenue) == 0) {
        node.bestChoices.add(entry.getKey());
      }
    }
    node.revenue = maxRevenue;
    return maxRevenue;
  }

  private static int compareWithEpsilon(BigDecimal maxRevenue, BigDecimal optionRevenue) {
//      return maxRevenue.compareTo(optionRevenue);

    BigDecimal optionRevenueScaled = getTruncated(optionRevenue, EPS_DECIMALS);
    BigDecimal maxRevenueScaled = getTruncated(maxRevenue, EPS_DECIMALS);
    return maxRevenueScaled.subtract(optionRevenueScaled, mcEpsilon).compareTo(zero());
  }

  public static BigDecimal getTruncated(BigDecimal original, int decimals) {
    BigDecimal truncated = new BigDecimal(String.valueOf(original));
    truncated = truncated.setScale(decimals, BigDecimal.ROUND_HALF_UP);
    return truncated;
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
              .forEach(OptimalTree::addLastLevelTerminalNode);
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
    List<BigDecimal> beta_updated = new ArrayList<>(BETA);
    BigDecimal probability = node.calculateProbability(new ArrayList<>(BETA), QU);

    for (int i = 0; i < BETA.size(); i++) {
      List<Boolean> updates = node.getInvestigations(i+1);
      // order of updates doesn't matter - can only have counts, difference in couns  is the power of corresponding update
      for (Boolean update : updates) {
        BigDecimal old_beta = beta_updated.get(i);
        BigDecimal qBeta = QU.multiply(old_beta, mc);
        BigDecimal q1Beta = (one().subtract(QU, mc)).multiply(old_beta, mc);

        beta_updated.set(i, update ?
                qBeta.divide(qBeta.add((one().subtract(QU, mc)).multiply(one().subtract(old_beta, mc), mc), mc), mc) :
                q1Beta.divide(q1Beta.add(QU.multiply(one().subtract(old_beta, mc), mc), mc), mc) );
      }
    }

    BigDecimal maxBeta = zero();
    for (int i = 0; i < beta_updated.size(); i++) {
      maxBeta = maxBeta.compareTo(beta_updated.get(i)) < 0 ? beta_updated.get(i) : maxBeta;
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
    node.addChild(0, null, costs, expectedUtility); // node.option==2 && node.resolution==Boolean.TRUE
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
