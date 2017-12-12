import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class MainRunner {

  private static final int tau = 7;
  private static final String delta = "0.98";
  private static final String cost = "0.002";
  private static final String qu = "0.7";

  public static void main(String[] args) {
    checkspecificBetas();
//
//    long startTime = System.currentTimeMillis();
//
//    String s_step = "0.1"; //0.2 0.7 0.01  .. 0.5 0.99
//    BigDecimal first_step = getDec("0.00");
//    BigDecimal step = getDec(s_step);
//
//    HashMap<BigDecimal, ArrayList<BigDecimal>> revenueToBetas = new HashMap<>();
//    long count = 0;
//
//    for (BigDecimal beta3 = first_step; beta3.compareTo(one()) <= 0; beta3 = beta3.add(step) ) {
//      for (BigDecimal beta2 = beta3; beta2.compareTo(one()) <= 0; beta2 = beta2.add(step)) {
//        for (BigDecimal beta1= beta2; beta1.compareTo(one()) <= 0; beta1 = beta1.add(step)) {
//
//          ArrayList<BigDecimal> betas = new ArrayList<>(3);
//          betas.add(beta1);
//          betas.add(beta2);
//          betas.add(beta3);
//
//          TreeNodeNek result = OptimalTree.runCalculation(tau, qu, cost, delta, betas);
//          if (result.has3dOption(betas)) {
//            System.out.println("Yes for betas: " + betas.toString());
//            revenueToBetas.put(result.revenue, betas);
//          }
//
//          count++;
//          if (count%500 == 0) {
//            System.out.println(count);
//          }
//
//
//        }
//      }
//    }
//
//    System.out.println("Size: " + revenueToBetas.size());
//
//    long stopTime = System.currentTimeMillis();
//    long minutes = ((stopTime - startTime) / 1000)  / 60;
//    long seconds = ((stopTime - startTime) / 1000) % 60;
////    long seconds = TimeUnit.MILLISECONDS.toSeconds(stopTime - startTime);
//    System.out.println("Completed in: " + minutes + " min " + seconds + " seconds");
  }

  private static void checkspecificBetas() {
    ArrayList<BigDecimal> betas = new ArrayList<>(3);

//    betas.add(getDec("0.70"));
////    betas.add(getDec("0.475"));
//    betas.add(getDec("0.5"));
//    betas.add(getDec("0.50000"));

//    betas.add(getDec("0.474973000000000"));
//    betas.add(getDec("0.469290000000000"));
//    betas.add(getDec("0.464751000000000"));

    betas.add(getDec("0.475000000000"));
    betas.add(getDec("0.4690000000000"));
    betas.add(getDec("0.465000000000"));

    TreeNodeNek result = OptimalTree.runCalculation(tau, qu, cost, delta, betas);
    TreeNodeNek.printOptimalTreeInfo(result);
    if (result.has3dOption(betas)) {
      System.out.println("Yes for betas: " + betas.toString());
    }
    BigDecimal dec = getDec("0.3");
    System.out.println(dec.divide(getDec("0.7"), OptimalTree.mc));

  }

  private static BigDecimal getDec(String s) {
    return new BigDecimal(s, OptimalTree.mc);
  }

  private static BigDecimal zero() {
    return new BigDecimal("0", OptimalTree.mc);
  }

  private static BigDecimal one() {
    return new BigDecimal("1.0", OptimalTree.mc);
  }
}
