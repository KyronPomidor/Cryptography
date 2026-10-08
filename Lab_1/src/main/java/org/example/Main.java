package org.example;

import java.util.Locale;
import java.util.Scanner;

public class Main {
    static final String ALPHABET = "AĂÂBCDEFGHIÎJKLMNOPQRSȘTȚUVWXYZ";
    static final Scanner input = new Scanner(System.in, "UTF-8");

    public static void main(String[] args) {
        int version = readNumber("Version (1 = Caesar, 2 = keyword): ", 1, 2);
        int operation = readNumber("Operation (1 = encrypt, 2 = decrypt): ", 1, 2);
        int key = readNumber("Key (1-30): ", 1, 30);

        String alphabet = ALPHABET;
        if (version == 2) {
            String keyword = readText("Keyword (at least 7 letters): ", true);
            StringBuilder reordered = new StringBuilder();
            for (char letter : (keyword + ALPHABET).toCharArray()) {
                if (reordered.indexOf(String.valueOf(letter)) == -1) {
                    reordered.append(letter);
                }
            }
            alphabet = reordered.toString();
            System.out.println("Permuted alphabet: " + alphabet);
        }

        String text = readText("Text: ", false);
        int shift = operation == 1 ? key : -key;
        StringBuilder result = new StringBuilder();
        for (char letter : text.toCharArray()) {
            int position = alphabet.indexOf(letter);
            int newPosition = (position + shift + 31) % 31;
            result.append(alphabet.charAt(newPosition));
        }
        System.out.println("Result: " + result);
    }

    static int readNumber(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int number = Integer.parseInt(input.nextLine().trim());
                if (number >= min && number <= max) return number;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            System.out.println("Enter an integer from " + min + " to " + max + ".");
        }
    }

    static String readText(String prompt, boolean keyword) {
        while (true) {
            System.out.print(prompt);
            String text = input.nextLine();
            StringBuilder cleaned = new StringBuilder();
            boolean valid = true;

            for (char letter : text.toCharArray()) {
                if (letter == ' ' && !keyword) continue;
                String upper = String.valueOf(letter).toUpperCase(Locale.ROOT)
                        .replace('Ş', 'Ș').replace('Ţ', 'Ț');
                if (upper.length() != 1 || !ALPHABET.contains(upper)) {
                    System.out.println("Invalid character: '" + letter + "'. Allowed letters: "
                            + ALPHABET + " (uppercase or lowercase)"
                            + (keyword ? "; no spaces." : ", plus spaces."));
                    valid = false;
                    break;
                }
                cleaned.append(upper);
            }

            if (!valid) continue;
            if (keyword && cleaned.length() < 7) {
                System.out.println("The keyword must contain at least 7 Romanian letters.");
            } else if (cleaned.length() == 0) {
                System.out.println("Enter at least one Romanian letter.");
            } else {
                return cleaned.toString();
            }
        }
    }
}
