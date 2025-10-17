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
    public static final String DEFAULT = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean run = true;

        while (run) {
            System.out.println(BLUE + "╔═══════════════════════════════════════════════╗");
            System.out.println(       "║  ┏━╸╻┏┓╻┏━┓┏┓╻┏━╸╻┏━┓╻  ╺┳╸┏━┓┏━┓┏━╸╻┏ ┏━╸┏━┓ ║\n" +
                                      "║  ┣╸ ┃┃┗┫┣━┫┃┗┫┃  ┃┣━┫┃   ┃ ┣┳┛┣━┫┃  ┣┻┓┣╸ ┣┳┛ ║\n" +
                                      "║  ╹  ╹╹ ╹╹ ╹╹ ╹┗━╸╹╹ ╹┗━╸ ╹ ╹┗╸╹ ╹┗━╸╹ ╹┗━╸╹┗  ║\n" +
                                      "╚═══════════════════════════════════════════════╝" + DEFAULT  );
            System.out.println("Choose An Option:");
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
                default -> System.out.println(RED + "INVALID OPTION");
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
            System.err.println(RED + "ERROR READING FILE: " + FILE_NAME);
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
                System.out.println("Enter Date (yyyy-MM-dd):");
                String date = scanner.nextLine();
                dateFormatted = LocalDate.parse(date, DATE_FMT);
                validDate = true;
            } catch (Exception e) {
                System.out.println(RED + "INVALID DATE \n");
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
                System.out.println(RED + "Invalid Time. Use Format (HH:mm:ss) \n");
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
                System.out.println("Enter Amount :");
                positiveAmount = Double.parseDouble(scanner.nextLine());
                if (positiveAmount <= 0){
                    System.out.println(RED + "Invalid number. Enter Positive Number");
                } else {
                    validAmount = true;
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid Input. Please Enter A Numeric Value");
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
            System.err.println(RED + "Error Writing To File");
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
                System.out.println(RED + "Invalid Date. Use Format (yyyy-MM-dd)");
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
                System.out.println(RED + "Invalid Time. Use Format (HH:mm:ss)");
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
                    System.out.println(RED + "Invalid Number. Enter Positive.");
                } else {
                    goodAmount = true;
                }
            } catch (NumberFormatException e) {
                System.out.println(RED + "Invalid Input. Please Enter A Numeric Value");
            }
        }

        double negativeAmount = -Math.abs(amount);
        transactions.add(new Transaction(dateFormatted, timeFormatted, description, vendor, negativeAmount));
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
            writer.write(dateFormatted.format(DATE_FMT) + "|" + timeFormatted.format(TIME_FMT)+ "|" + description + "|" + vendor + "|" + String.format("%.2f", negativeAmount));
            writer.newLine();
            System.out.println("Payment Recorded!");
            writer.close();
        } catch (IOException e) {
            System.err.print(RED + "Error Writing To The File: " + FILE_NAME);
        }
    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println();
            System.out.println(GREEN + "| LEDGER MENU |" + DEFAULT);
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
                default -> System.out.println(RED + "Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() {
        System.out.println();
        System.out.println(GREEN + "| ALL TRANSACTIONS |" + DEFAULT);
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
            System.err.println(RED + "Error");
        } }

    private static void displayDeposits() {
        System.out.println();
        System.out.println(GREEN + "| DEPOSITS |" + DEFAULT);
        System.out.println("Date----------Time---------Description--------------------Vendor--------------Amount");
        System.out.println("=======================================================================================");

        for (Transaction transaction : transactions) {
            if (transaction.getAmount() >= 0) {
                System.out.printf("%-12s %-10s %-30s %-20s %10.2f \n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
            }
        }
    }

    private static void displayPayments() {
        System.out.println();
        System.out.println(GREEN + "| PAYMENTS |" + DEFAULT);
        System.out.println("Date----------Time---------Description--------------------Vendor--------------Amount");
        System.out.println("=======================================================================================");

        for (Transaction transaction : transactions) {
            if (transaction.getAmount() < 0) {
                System.out.printf("%-12s %-10s %-30s %-20s %10.2f \n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
            }
        }
    }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println(GREEN + "| REPORTS MENU |" +DEFAULT);
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
                case "1" -> {
                    LocalDate start = LocalDate.now().withDayOfMonth(1);
                    LocalDate end = LocalDate.now();
                    filterTransactionsByDate(start, end); }
                case "2" -> {
                    LocalDate start = LocalDate.now().minusMonths(1).withDayOfMonth(1);
                    LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
                    filterTransactionsByDate(start, end);
                }
                case "3" -> {
                    LocalDate start = LocalDate.now().withDayOfYear(1);
                    LocalDate end = LocalDate.now();
                    filterTransactionsByDate(start, end);
                }
                case "4" -> {
                    LocalDate start = LocalDate.now().minusYears(1).withDayOfYear(1);
                    LocalDate end = start.withDayOfYear(start.lengthOfYear());
                    filterTransactionsByDate(start, end);
                }
                case "5" -> {
                    System.out.println("Enter Vendor: ");
                    String vendor = scanner.nextLine();
                    filterTransactionsByVendor(vendor);
                }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println(RED + "Invalid Option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        System.out.println();
        System.out.println(GREEN + "| TRANSACTIONS BY DATE |" + DEFAULT);
        System.out.println("Date----------Time---------Description--------------------Vendor--------------Amount");
        System.out.println("=======================================================================================");

        for (Transaction transaction : transactions) {
            LocalDate date = transaction.getDate();
            if (!date.isBefore(start) && !date.isAfter(end)){
                System.out.printf("%-12s %-10s %-30s %-20s %10.2f \n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
            }
        }
    }

    private static void filterTransactionsByVendor(String vendor) {
        System.out.println();
        System.out.println(GREEN + "| TRANSACTIONS BY VENDOR |" + DEFAULT);
        System.out.println("Date----------Time---------Description--------------------Vendor--------------Amount");
        System.out.println("=======================================================================================");

        for (Transaction transaction : transactions) {
            String vendor2 = transaction.getVendor();
            if (vendor2.equalsIgnoreCase(vendor)){
                System.out.printf("%-12s %-10s %-30s %-20s %10.2f \n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
            }
        }
    }

    private static void customSearch(Scanner scanner) {
        System.out.println();
        System.out.println(GREEN + "| CUSTOM-SEARCH MENU |" + DEFAULT);

        System.out.print("Start date (yyyy-MM-dd, Leave Empty for None): ");
        String stringStartDate = scanner.nextLine().trim();

        System.out.print("Enter End date (yyyy-MM-dd, Leave Empty for None): ");
        String stringEndDate = scanner.nextLine().trim();

        System.out.print("Description (Leave Empty for None): ");
        String description = scanner.nextLine();

        System.out.print("Vendor (Leave Empty for None): ");
        String vendor = scanner.nextLine();

        System.out.print("Amount (Leave Empty for None): ");
        String amount = scanner.nextLine();

        Double finalAmount = null;
        if (!amount.isEmpty()){
            finalAmount = Double.parseDouble(amount);
        }
        LocalDate startDate = null;
        if (!stringStartDate.isEmpty()) {
            startDate = LocalDate.parse(stringStartDate);
        }
        LocalDate endDate = null;
        if (!stringEndDate.isEmpty()){
            endDate = LocalDate.parse(stringEndDate);
        }
        System.out.println();
        System.out.println(GREEN + "| CUSTOM SEARCH |" + DEFAULT);
        System.out.println("Date----------Time---------Description--------------------Vendor--------------Amount");
        System.out.println("=======================================================================================");
        try {
            for (Transaction transaction : transactions) {
                if (startDate != null && transaction.getDate().isBefore(startDate)){ continue; }
                if (endDate != null && transaction.getDate().isAfter(endDate)) { continue; }
                if (!description.isEmpty() && !description.equalsIgnoreCase(transaction.getDescription())) { continue; }
                if (!vendor.isEmpty() && !vendor.equalsIgnoreCase(transaction.getVendor())) { continue; }
                if (finalAmount != null && transaction.getAmount() != finalAmount) { continue; }

                System.out.printf("%-12s %-10s %-30s %-20s %10.2f \n",
                        transaction.getDate(),
                        transaction.getTime(),
                        transaction.getDescription(),
                        transaction.getVendor(),
                        transaction.getAmount());
            }
        } catch (Exception ex){
            System.err.println(RED + "Error");
        }
    }

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {
        /* TODO – return LocalDate or null */
        return null;
    }

    private static Double parseDouble(String s) {
        /* TODO – return Double or null */
        return null;
    }
}
