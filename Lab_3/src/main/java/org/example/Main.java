package org.example;

import java.util.Scanner;

public class Main {

    static final String ALPHABET =
            "AĂÂBCDEFGHIÎJKLMNOPQRSȘTȚUVWXYZ";

    // The grid is 6x6 = 36 cells, so we add 5 extra symbols to fill it
    static final String GRID_CHARS = ALPHABET + "01234";

    static char[][] grid = new char[6][6];

    // Build the 6x6 matrix from the key
    static void buildGrid(String key) {
        String used = "";

        // first the key letters
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (used.indexOf(c) == -1) {
                used = used + c;
            }
        }

        // the rest of the alphabet
        for (int i = 0; i < GRID_CHARS.length(); i++) {
            char c = GRID_CHARS.charAt(i);
            if (used.indexOf(c) == -1) {
                used = used + c;
            }
        }

        // fill the matrix row by row
        int k = 0;
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                grid[r][c] = used.charAt(k);
                k++;
            }
        }
    }

    static void printGrid() {
        System.out.println("Encryption matrix:");
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                System.out.print(grid[r][c] + " ");
            }
            System.out.println();
        }
    }

    static int findRow(char ch) {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                if (grid[r][c] == ch) {
                    return r;
                }
            }
        }
        return -1;
    }

    static int findCol(char ch) {
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 6; c++) {
                if (grid[r][c] == ch) {
                    return c;
                }
            }
        }
        return -1;
    }

    // Prepare the message: insert X between double letters
    static String prepare(String text) {
        String result = "";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // if we are at the start of a pair and it would be a double letter
            if (result.length() % 2 == 1 && result.charAt(result.length() - 1) == c) {
                if (c == 'X') {
                    result = result + 'Z';
                } else {
                    result = result + 'X';
                }
            }
            result = result + c;
        }
        // odd length -> add one more letter
        if (result.length() % 2 == 1) {
            if (result.charAt(result.length() - 1) == 'X') {
                result = result + 'Z';
            } else {
                result = result + 'X';
            }
        }
        return result;
    }

    // shift = 1 for encryption, shift = 5 for decryption
    static String process(String text, int shift) {
        String result = "";
        for (int i = 0; i < text.length(); i = i + 2) {
            char a = text.charAt(i);
            char b = text.charAt(i + 1);

            int r1 = findRow(a);
            int c1 = findCol(a);
            int r2 = findRow(b);
            int c2 = findCol(b);

            if (r1 == r2) {
                // same row
                c1 = (c1 + shift) % 6;
                c2 = (c2 + shift) % 6;
            } else if (c1 == c2) {
                // same column
                r1 = (r1 + shift) % 6;
                r2 = (r2 + shift) % 6;
            } else {
                // rectangle: swap the columns
                int temp = c1;
                c1 = c2;
                c2 = temp;
            }

            result = result + grid[r1][c1] + grid[r2][c2];
        }
        return result;
    }

    static boolean onlyLatinLetters(String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < 'A' || c > 'Z') {
                return false;
            }
        }
        return true;
    }

    // Letters of the 31-letter alphabet (used for the ciphertext to decrypt)
    static boolean onlyAlphabetLetters(String text) {
        for (int i = 0; i < text.length(); i++) {
            if (ALPHABET.indexOf(text.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== Playfair cipher (31-letter Romanian alphabet) ===");

        // choose operation
        int choice = 0;
        while (choice != 1 && choice != 2) {
            System.out.println("Choose operation: 1 - Encrypt, 2 - Decrypt");
            String line = sc.nextLine().trim();
            if (line.equals("1")) {
                choice = 1;
            } else if (line.equals("2")) {
                choice = 2;
            } else {
                System.out.println("Please type 1 or 2.");
            }
        }

        // read key
        String key = "";
        while (true) {
            System.out.println("Enter the key (at least 7 letters, only A-Z or a-z):");
            key = sc.nextLine().replace(" ", "").toUpperCase();
            if (!onlyLatinLetters(key)) {
                System.out.println("Wrong characters! Use only letters from 'A' to 'Z' or 'a' to 'z'.");
            } else if (key.length() < 7) {
                System.out.println("The key is too short! It must have at least 7 letters.");
            } else {
                break;
            }
        }

        buildGrid(key);
        printGrid();

        // read text
        String text = "";
        while (true) {
            if (choice == 1) {
                System.out.println("Enter the message (only A-Z or a-z):");
            } else {
                System.out.println("Enter the ciphertext (letters of the alphabet):");
            }
            text = sc.nextLine().replace(" ", "").toUpperCase();

            if (text.length() == 0) {
                System.out.println("The text is empty!");
            } else if (choice == 1 && !onlyLatinLetters(text)) {
                System.out.println("Wrong characters! Use only letters from 'A' to 'Z' or 'a' to 'z'.");
            } else if (choice == 2 && !onlyAlphabetLetters(text)) {
                System.out.println("Wrong characters! The ciphertext can contain only letters of the 31-letter alphabet.");
            } else if (choice == 2 && text.length() % 2 == 1) {
                System.out.println("The ciphertext must have an even number of letters.");
            } else {
                break;
            }
        }

        if (choice == 1) {
            String prepared = prepare(text);
            System.out.println("Prepared message: " + prepared);
            System.out.println("Ciphertext: " + process(prepared, 1));
        } else {
            System.out.println("Decrypted message: " + process(text, 5));
            System.out.println("(Remove the extra X/Z letters and add the spaces by hand.)");
        }
    }
}