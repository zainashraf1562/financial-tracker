package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * Capstone skeleton – personal finance tracker.
 * ------------------------------------------------
 * File format  (pipe-delimited)
 *     yyyy-MM-dd|HH:mm:ss|description|vendor|amount
 * A deposit has a positive amount; a payment is stored
 * as a negative amount.
 */
public class FinancialTracker {

    /* ------------------------------------------------------------------
       Shared data and formatters
       ------------------------------------------------------------------ */
    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean run = true;

        while (run) {
            System.out.println("Welcome!!");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> run = false;
                default -> System.out.println("Invalid option");
            }
        }
        scanner.close();
    }

    /* ------------------------------------------------------------------
       File I/O
       ------------------------------------------------------------------ */

    /**
     * Load transactions from FILE_NAME.
     * • If the file doesn’t exist, create an empty one so that future writes succeed.
     * • Each line looks like: date|time|description|vendor|amount
     */
    public static void loadTransactions(String fileName) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            String line;
            while ((line =reader.readLine()) !=null) {
                String[] tokens = line.split("\\|");
                LocalDate date = LocalDate.parse(tokens[0]);
                LocalTime time = LocalTime.parse(tokens[1]);
                String description = tokens[2];
                String vendor = tokens[3];
                double amount = Double.parseDouble(tokens[4]);

                Transaction transaction = new Transaction(date, time, description, vendor, amount);

                transactions.add(transaction);
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading file: " + FILE_NAME);
        }
    }

    /* ------------------------------------------------------------------
       Add new transactions
       ------------------------------------------------------------------ */

    /**
     * Prompt for ONE date+time string in the format
     * "yyyy-MM-dd HH:mm:ss", plus description, vendor, amount.
     * Validate that the amount entered is positive.
     * Store the amount as-is (positive) and append to the file.
     */
    private static void addDeposit(Scanner scanner) {
        LocalDate dateFormatted = null;
        LocalTime timeFormatted = null;
        boolean validDate = false;

        while(!validDate) {
            try {
                System.out.println("Date (yyyy-MM-dd):");
                String date = scanner.nextLine();
                dateFormatted = LocalDate.parse(date, DATE_FMT);
                validDate = true;
            } catch (Exception e) {
                System.out.println("invalid date \n");
            }
        }
        boolean validTime = false;

        while(!validTime) {
            try {
                System.out.println("Time(HH:mm:ss):");
                String time = scanner.nextLine();
                timeFormatted = LocalTime.parse(time, TIME_FMT);
                validTime = true;
            } catch (Exception e) {
                System.out.println("Invalid time. Please input time based on format (HH:mm:ss) \n");
            }
        }
        System.out.println("Enter Description:");
        String description = scanner.nextLine();

        System.out.println("Enter Vendor:");
        String vendor = scanner.nextLine();

        double positiveAmount = 0.0;
        boolean validAmount = false;

        while(!validAmount) {
            try {
                System.out.println("Amount (positive):");
                positiveAmount = Double.parseDouble(scanner.nextLine());
                if (positiveAmount <= 0){
                    System.out.println("Invalid number. Please enter a positive number greater than zero");
                } else {
                    validAmount = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number");
            }
        }

        transactions.add(new Transaction(dateFormatted, timeFormatted, description, vendor, positiveAmount));

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
            writer.write(dateFormatted.format(DATE_FMT) + "|" + timeFormatted.format(TIME_FMT)+ "|" + description + "|" + vendor + "|" + String.format("%.2f", positiveAmount));
            writer.newLine();
            System.out.println("Deposit Saved!");
            writer.close();
        } catch (Exception e) {
            System.err.println("Error writing to file");
        }
    }

    /**
     * Same prompts as addDeposit.
     * Amount must be entered as a positive number,
     * then converted to a negative amount before storing.
     */
    private static void addPayment(Scanner scanner) {
        System.out.println("FILL ALL VALUES!");
        LocalDate dateFormatted = null;
        LocalTime timeFormatted = null;
        boolean validDate = false;

        while(!validDate) {
            try {
                System.out.println("Enter Date (yyyy-MM-dd):");
                String date = scanner.nextLine();
                dateFormatted = LocalDate.parse(date, DATE_FMT);
                validDate = true;
            } catch (Exception e) {
                System.out.println("invalid date. (yyyy-MM-dd)");
            }
        }
        boolean validTime = false;

        while(!validTime) {
            try {
                System.out.println("Enter Time (HH:mm:ss):");
                String time = scanner.nextLine();
                timeFormatted = LocalTime.parse(time, TIME_FMT);
                validTime = true;
            } catch (Exception e) {
                System.out.println("Invalid time. (HH:mm:ss)");
            }
        }
        System.out.println("Enter Description:");
        String description = scanner.nextLine();

        System.out.println("Enter Vendor:");
        String vendor = scanner.nextLine();

        double amount = 0;
        boolean goodAmount = false;

        while(!goodAmount) {
            try {
                System.out.println("Enter Amount (greater than 0):");
                amount = Double.parseDouble(scanner.nextLine());
                if (amount <= 0){
                    System.out.println("Invalid number. Enter Positive.");
                } else {
                    goodAmount = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value");
            }
        }

        double negativeAmount = -Math.abs(amount);
        transactions.add(new Transaction(dateFormatted, timeFormatted, description, vendor, negativeAmount));
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
            writer.write(dateFormatted.format(DATE_FMT) + "|" + timeFormatted.format(TIME_FMT)+ "|" + description + "|" + vendor + "|" + String.format("%.2f", negativeAmount));
            writer.newLine();
            System.out.println("Payment recorded!");
            writer.close();
        } catch (IOException e) {
            System.err.print("Error writing to the file: " + FILE_NAME);
        }
    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() {
        System.out.println("Date----------Time---------Description--------------------Vendor--------------Amount");
        System.out.println("=======================================================================================");
        try {
            for (Transaction transaction : transactions) {
                System.out.printf("%-12s %-10s %-30s %-20s %10.2f \n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
            }
        } catch (Exception ex) {
            System.err.println("Error");
        } }

    private static void displayDeposits() {
        System.out.println("Date----------Time---------Description--------------------Vendor--------------Amount");
        System.out.println("=======================================================================================");

        for (Transaction transaction : transactions) {
            if (transaction.getAmount() >= 0) {
                System.out.printf("%-12s %-10s %-30s %-20s %10.2f \n", transaction.getDate(), transaction.getTime(), transaction.getDescription(), transaction.getVendor(), transaction.getAmount());
            }
        } }

    private static void displayPayments() { /* TODO – only amount < 0               */ }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Reports");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {/* TODO – month-to-date report */ }
                case "2" -> {/* TODO – previous month report */ }
                case "3" -> {/* TODO – year-to-date report   */ }
                case "4" -> {/* TODO – previous year report  */ }
                case "5" -> {/* TODO – prompt for vendor then report */ }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        // TODO – iterate transactions, print those within the range
    }

    private static void filterTransactionsByVendor(String vendor) {
        // TODO – iterate transactions, print those with matching vendor
    }

    private static void customSearch(Scanner scanner) {
        // TODO – prompt for any combination of date range, description,
        //        vendor, and exact amount, then display matches
    }

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {
        /* TODO – return LocalDate or null */
        return null;
    }

    private static Double parseDouble(String s) {
        /* TODO – return Double   or null */
        return null;
    }
}
