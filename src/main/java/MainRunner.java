import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MainRunner {

  private static final int tau = 3;
  private static final String delta = "0.98";
  private static final String cost = "0.002";
  private static final String qu = "0.7";
  private static final int BETAS_SIZE = 4;

  public static void main(String[] args) {
    checkspecificBetas();

//    runWithBetasInRange();

//    runWithRandomBetas(10000);

  }

  private static void runWithRandomBetas(Integer rounds) {
    Path file = Paths.get("betas");
    Path fileUnique = Paths.get("betas_unique");

    for (int i = 0; i < rounds; i++) {
      List<BigDecimal> betas = generateRandomBetas(BETAS_SIZE);
      TreeNodeNek result = OptimalTree.runCalculation(tau, qu, cost, delta, betas);

      if (result.has3dOption()) {
        List<String> message = Arrays.asList(betas.toString(), result.getRoot().getChoicesString());

        try {
          Files.write(file, message, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
          if (result.hasOnly3dOption(betas)) {
            Files.write(fileUnique, message, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      if (i%100 == 0) {
        System.out.println(i);
      }

    }

  }


  public static BigDecimal generateRandomBigDecimalFromRange(BigDecimal min, BigDecimal max) {
    BigDecimal randomBigDecimal = min.add(new BigDecimal(Math.random(), OptimalTree.mc).multiply(max.subtract(min)));
    // uncomment to return scaled to 0.0000
    return randomBigDecimal.setScale(5,BigDecimal.ROUND_HALF_UP);
  }

  private static BigDecimal getRandomBeta(String min, String max) {
    return generateRandomBigDecimalFromRange(
            new BigDecimal(min, OptimalTree.mc),
            // uncomment to return scaled to 0.0000
            new BigDecimal(max, OptimalTree.mc).setScale(5, BigDecimal.ROUND_HALF_UP)
    );

  }

  private static List<BigDecimal> generateRandomBetas(int betasSize) {
    List<BigDecimal> betas = new ArrayList<>(betasSize);
    betas.add(getRandomBeta("0.0", "1.0"));
    // skewed betas
    for (int i = 1; i < betasSize; i++) {
      BigDecimal beta = generateRandomBigDecimalFromRange(zero(), betas.get(betas.size() - 1));
      betas.add(beta);
    }
    // uniformly distributed betas
//    for (int i = 1; i < betasSize; i++) {
//      betas.add(getRandomBeta("0.0", "1.0"));
//    }
//    betas = betas.stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
    return betas;
  }

  private static void runWithBetasInRange() {
    long startTime = System.currentTimeMillis();

    String s_step = "0.1"; //0.2 0.7 0.01  .. 0.5 0.99
    BigDecimal first_step = getDec("0.00");
    BigDecimal step = getDec(s_step);

    HashMap<BigDecimal, ArrayList<BigDecimal>> revenueToBetas = new HashMap<>();
    long count = 0;

    for (BigDecimal beta3 = first_step; beta3.compareTo(one()) <= 0; beta3 = beta3.add(step) ) {
      for (BigDecimal beta2 = beta3; beta2.compareTo(one()) <= 0; beta2 = beta2.add(step)) {
        for (BigDecimal beta1= beta2; beta1.compareTo(one()) <= 0; beta1 = beta1.add(step)) {

          ArrayList<BigDecimal> betas = new ArrayList<>(3);
          betas.add(beta1);
          betas.add(beta2);
          betas.add(beta3);

          TreeNodeNek result = OptimalTree.runCalculation(tau, qu, cost, delta, betas);
          if (result.hasOnly3dOption(betas)) {
            System.out.println("Yes for betas: " + betas.toString());
            revenueToBetas.put(result.revenue, betas);
          }

          count++;
          if (count%500 == 0) {
            System.out.println(count);
          }


        }
      }
    }

    System.out.println("Size: " + revenueToBetas.size());

    long stopTime = System.currentTimeMillis();
    long minutes = ((stopTime - startTime) / 1000)  / 60;
    long seconds = ((stopTime - startTime) / 1000) % 60;
//    long seconds = TimeUnit.MILLISECONDS.toSeconds(stopTime - startTime);
    System.out.println("Completed in: " + minutes + " min " + seconds + " seconds");
  }

  private static void checkspecificBetas() {
    ArrayList<BigDecimal> betas = new ArrayList<>(4);

    double[] betasArray = {0.65983, 0.65911, 0.65635, 0.21174};
    for (double beta : betasArray) {
      betas.add(getDec(beta));
    }

//    betas.add(getDec("0.04660"));
//    betas.add(getDec("0.02650"));
//    betas.add(getDec("0.02498"));
//    betas.add(getDec("0.00948"));

    TreeNodeNek result = OptimalTree.runCalculation(tau, qu, cost, delta, betas);
    System.out.println("For betas: " + betas.stream()
            .map(beta -> OptimalTree.getTruncated(beta, 5))
            .collect(Collectors.toList())
            .toString());
//    result.printOptimalTreeInfo();
    result.printOptimalPlansInfo();
    if (result.hasOnly3dOption(betas)) {
      System.out.println("Yes for betas: " + betas.toString());
    }

  }

  private static BigDecimal getDec(String s) {
    return new BigDecimal(s, OptimalTree.mc);
  }

  private static BigDecimal getDec(double d) {
    return new BigDecimal(d, OptimalTree.mc);
  }

  private static BigDecimal zero() {
    return new BigDecimal("0", OptimalTree.mc);
  }

  private static BigDecimal one() {
    return new BigDecimal("1.0", OptimalTree.mc);
  }
}
