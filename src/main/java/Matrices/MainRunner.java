package Matrices;

import java.util.Arrays;
import java.util.Set;

public class MainRunner {


  public static void main(String[] args) {
    Node preRoot = new Node (3, null, null);

    Node rootT = new Node(1, true, preRoot);

    rootT.setResolutions(1, 1);

    rootT.getMinusResolution().setResolutions(1,2);

    Node rootF = new Node(1, false, preRoot);

    rootF.setResolutions(1, 1);

    rootF.getMinusResolution().setResolutions(1,2);

    preRoot.setPlusResolution(rootT);
    preRoot.setMinusResolution(rootF);

    printMatrix(preRoot);
  }

  private static void printMatrix(Node root) {
    final int size = 3;
    final int maxDepth = root.getDepth();

    System.out.println("" + maxDepth);

    for (int i = 0; i < Math.pow(2, size); i++) {
      final String stateOfTheWorld = addLeadingZeroes(Integer.toBinaryString(i), size);
      System.out.println(stateOfTheWorld + ":");

      for (int j = 1; j <= maxDepth; j++) {
        Set<Node> ithChildren = root.getIthTerminalChildren(j);

        System.out.print("T," + (j == 1 ? "" : j) + "C: ");
        StringBuilder resultT = new StringBuilder();
        for (Node child : ithChildren) {
          if (isGoodOption(child.getOption(), stateOfTheWorld)) {
            resultT.append(getProbability(child, stateOfTheWorld, "")).append("+");
          }
        }
        System.out.print(resultT);
        System.out.println();

        System.out.print("F," + (j == 1 ? "" : j) + "C: ");
        StringBuilder resultF = new StringBuilder();
        for (Node child : ithChildren) {
          if (isBadOption(child.getOption(), stateOfTheWorld)) {
            resultF.append(getProbability(child, stateOfTheWorld, "")).append("+");
          }
        }
        System.out.print(resultF);
        System.out.println();
      }


    }



  }

  private static String getProbability(Node child, String stateOfTheWorld, String acc) {
    if (child.isRoot()) {
      return acc;
    }
    int parentOption = child.getParent().getOption();
    Boolean parentResolution = child.getParentResolution();

    if (isGoodOption(parentOption, stateOfTheWorld)) {
      if (parentResolution) {
        acc += "q";
      } else {
        acc += "(1-q)";
      }
    }

    if (isBadOption(parentOption, stateOfTheWorld)) {
      if (parentResolution) {
        acc += "(1-q)";
      } else {
        acc += "q";
      }
    }

    return getProbability(child.getParent(), stateOfTheWorld, acc);
  }

  private static boolean isBadOption(int option, String stateOfTheWorld) {
    return stateOfTheWorld.charAt(option - 1) == '0';
  }

  private static boolean isGoodOption(int option, String stateOfTheWorld) {
    return stateOfTheWorld.charAt(option - 1) == '1';
  }

  private static String addLeadingZeroes(String s, int size) {
    int zeroes = s.length() % size;
    if (zeroes == 0) {
      return s;
    }
    byte[] bzero = new byte[size - zeroes];
    Arrays.fill(bzero, (byte)0x30);
    return new StringBuilder(new String(bzero)).append(s).toString();
  }
}
