package org.example;

import java.util.Random;
import java.util.Scanner;

// DES: K+ is given. Find the round key Ki for a given i.
public class DesRoundKey {

    // Permuted choice 2 (PC-2): 56 bits -> 48 bits
    static final int[] PC2 = {
            14, 17, 11, 24,  1,  5,
            3, 28, 15,  6, 21, 10,
            23, 19, 12,  4, 26,  8,
            16,  7, 27, 20, 13,  2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32
    };

    // Number of left shifts for each round 1..16
    static final int[] SHIFTS = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};

    // Circular left shift of a bit string
    static String leftShift(String bits, int n) {
        return bits.substring(n) + bits.substring(0, n);
    }

    // Apply a permutation table to a bit string
    static String permute(String bits, int[] table) {
        String result = "";
        for (int i = 0; i < table.length; i++) {
            result = result + bits.charAt(table[i] - 1);
        }
        return result;
    }

    // Put a space after every "size" bits, to make the output readable
    static String group(String bits, int size) {
        String result = "";
        for (int i = 0; i < bits.length(); i++) {
            if (i > 0 && i % size == 0) {
                result = result + " ";
            }
            result = result + bits.charAt(i);
        }
        return result;
    }

    static String binToHex(String bits) {
        String hex = "";
        for (int i = 0; i < bits.length(); i = i + 4) {
            int value = Integer.parseInt(bits.substring(i, i + 4), 2);
            hex = hex + Integer.toHexString(value).toUpperCase();
        }
        return hex;
    }

    static String hexToBin(String hex) {
        String bits = "";
        for (int i = 0; i < hex.length(); i++) {
            String b = Integer.toBinaryString(Character.digit(hex.charAt(i), 16));
            while (b.length() < 4) {
                b = "0" + b;
            }
            bits = bits + b;
        }
        return bits;
    }

    static boolean isBinary(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != '0' && s.charAt(i) != '1') {
                return false;
            }
        }
        return true;
    }

    static boolean isHex(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.digit(s.charAt(i), 16) == -1) {
                return false;
            }
        }
        return true;
    }

    static void printTables() {
        System.out.println("Table PC-2 (56 bits -> 48 bits):");
        for (int i = 0; i < 48; i++) {
            System.out.printf("%3d", PC2[i]);
            if (i % 6 == 5) {
                System.out.println();
            }
        }
        System.out.println();
        System.out.println("Table of left shifts:");
        System.out.print("Round:");
        for (int r = 1; r <= 16; r++) {
            System.out.printf("%3d", r);
        }
        System.out.println();
        System.out.print("Shift:");
        for (int r = 0; r < 16; r++) {
            System.out.printf("%3d", SHIFTS[r]);
        }
        System.out.println();
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== DES, task 2.3: round key Ki from K+ ===");

        // 1. choose how K+ is obtained
        int choice = 0;
        while (choice != 1 && choice != 2) {
            System.out.println("Choose: 1 - enter K+ manually, 2 - generate K+ randomly");
            String line = sc.nextLine().trim();
            if (line.equals("1")) {
                choice = 1;
            } else if (line.equals("2")) {
                choice = 2;
            } else {
                System.out.println("Please type 1 or 2.");
            }
        }

        String kPlus = "";
        if (choice == 1) {
            while (true) {
                System.out.println("Enter K+ (56 bits, or 14 hexadecimal digits):");
                String s = sc.nextLine().replace(" ", "").toUpperCase();
                if (s.length() == 56 && isBinary(s)) {
                    kPlus = s;
                    break;
                } else if (s.length() == 14 && isHex(s)) {
                    kPlus = hexToBin(s);
                    break;
                } else {
                    System.out.println("Wrong input! K+ must be exactly 56 bits (only 0 and 1) "
                            + "or 14 hexadecimal digits (0-9, A-F).");
                }
            }
        } else {
            Random rnd = new Random();
            for (int i = 0; i < 56; i++) {
                kPlus = kPlus + rnd.nextInt(2);
            }
            System.out.println("Random K+ generated.");
        }

        // 2. read the round number i
        int round = 0;
        while (round < 1 || round > 16) {
            System.out.println("Enter the round number i (1 - 16):");
            String s = sc.nextLine().trim();
            try {
                round = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                round = 0;
            }
            if (round < 1 || round > 16) {
                System.out.println("Wrong value! i must be an integer from 1 to 16.");
            }
        }

        System.out.println();
        printTables();

        // 3. steps
        System.out.println("Step 1. K+ (56 bits):");
        System.out.println("K+  = " + group(kPlus, 7));
        System.out.println();

        String c = kPlus.substring(0, 28);
        String d = kPlus.substring(28);
        System.out.println("Step 2. Split K+ into two halves of 28 bits:");
        System.out.println("C0  = " + group(c, 7));
        System.out.println("D0  = " + group(d, 7));
        System.out.println();

        System.out.println("Step 3. Circular left shifts (rounds 1.." + round + "):");
        for (int j = 1; j <= round; j++) {
            c = leftShift(c, SHIFTS[j - 1]);
            d = leftShift(d, SHIFTS[j - 1]);
            System.out.println("Round " + j + ": shift by " + SHIFTS[j - 1]);
            System.out.println("  C" + j + " = " + group(c, 7));
            System.out.println("  D" + j + " = " + group(d, 7));
        }
        System.out.println();

        String cd = c + d;
        System.out.println("Step 4. Concatenation C" + round + "D" + round + " (56 bits):");
        System.out.println("C" + round + "D" + round + " = " + group(cd, 7));
        System.out.println();

        String key = permute(cd, PC2);
        System.out.println("Step 5. Apply PC-2 to C" + round + "D" + round + ":");
        System.out.println("K" + round + " (binary, groups of 6) = " + group(key, 6));
        System.out.println("K" + round + " (hexadecimal)         = " + binToHex(key));
    }
}