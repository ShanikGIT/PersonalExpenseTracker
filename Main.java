import java.util.*;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class Main {
    private static Map<String, User> users = new HashMap<>();
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser;

    public static void main(String[] args) {
        System.out.println("Welcome to the Personal Expense Tracker!");
        showInitialMenu();  // Changed to show a Sign-Up and Login option

        if (currentUser != null) {
            showMainMenu();
        }
    }
    private static void showInitialMenu() {
        while (true) {
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    authenticateUser();
                    if (currentUser != null) return;  // Proceed if login is successful
                    break;
                case "2":
                    signUpUser();  // Handle user sign-up
                    break;
                case "0":
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private static void authenticateUser() {
        System.out.print("Enter username:");
        String username = scanner.nextLine();
        System.out.print("Enter password:");
        String password = scanner.nextLine();

       currentUser = User.getUserFromDatabase(username);
        if (currentUser != null) {
            if (currentUser.authenticate(password)) {
                System.out.println("Login successful!");
            } else {
                System.out.println("Invalid password.");
                currentUser = null;
            }
        } else {
            System.out.println("User not found. Please sign up.");
        }
    }

    private static void signUpUser() {
        System.out.print("Enter a username:");
        String username = scanner.nextLine();

        // Check if the username already exists
        if (User.getUserFromDatabase(username) != null) {
            System.out.println("Username already exists. Please try a different username.");
            return;
        }

        System.out.print("Enter a password:");
        String password = scanner.nextLine();

        System.out.println("Enter your budget:");
        double budget;
        try {
            budget = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid budget. Please enter a valid number.");
            return;
        }

        // Create the user and add it to the database
        User newUser = new User(username, password, budget);
        if (newUser.saveToDatabase()) {
            System.out.println("Sign-up successful! You can now log in.");
        } else {
            System.out.println("Sign-up failed. Please try again.");
        }
    }

    private static void showMainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\nMain Menu:");
            System.out.println("0. User info");
            System.out.println("1. Add Expense");
            System.out.println("2. View Expenses");
            System.out.println("3. Edit Expense");
            System.out.println("4. Delete Expense");
            System.out.println("5. Filter Expenses by Category");
            System.out.println("6. Generate Monthly Report");
            System.out.println("7. Export to CSV");
            System.out.println("8. Edit Budget");
            System.out.println("9. Check Budget");
            System.out.println("10. Log Out");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 0:
                    userInfo();
                    break;
                case 1:
                    addExpense();
                    break;
                case 2:
                    viewExpenses();
                    break;
                case 3:
                    editExpense();
                    break;
                case 4:
                    deleteExpense();
                    break;
                case 5:
                    filter();
                    break;
                case 6:
                    generateReport();
                    break;
                case 7:
                    exportToCSV();
                    break;
                case 8:
                    EditB();
                    break;
                case 9:
                    checkBudget();
                    break;
                case 10:
                    logOut();
                    showInitialMenu();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addExpense() {
        System.out.print("Enter amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter date (yyyy-mm-dd): ");
        String dateStr = scanner.nextLine();
        Date date = Date.valueOf(dateStr);

        System.out.print("Enter category: ");
        String category = scanner.nextLine();

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        Expense expense = new Expense(0, amount, date, category, description);
        if (expense.saveToDatabase(currentUser.getUsername())) {
            System.out.println("Expense added successfully!");
        } else {
            System.out.println("Failed to add expense. Please try again.");
        }
    }

    private static void viewExpenses() {
        List<Expense> expenses = Expense.getExpensesForUser(currentUser.getUsername());

        if (expenses.isEmpty()) {
            System.out.println("No expenses found.");
        } else {
            for (Expense expense : expenses) {
                System.out.println("Amount: " + expense.getAmount() + ", Category: " + expense.getCategory()+ ", Description: " + expense.getDescription() + ", Date: " + expense.getDate());
            }
        }
    }


    private static void editExpense() {
        System.out.print("Enter expense ID to edit: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter new amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();  // Consume newline

        System.out.print("Enter new category: ");
        String category = scanner.nextLine();

        System.out.print("Enter new description: ");
        String description = scanner.nextLine();

        currentUser.getExpenseTracker().editExpenseInDatabase(id, amount, category, description);
        System.out.println("Expense updated.");
    }

    private static void deleteExpense() {
        System.out.print("Enter expense ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        currentUser.getExpenseTracker().deleteExpenseFromDatabase(id);
        System.out.println("Expense deleted.");
    }
    private static void filter() {
        System.out.print("Enter Category: ");
        String category = scanner.nextLine();
        scanner.nextLine();  // Consume newline
        System.out.println("Filtered By"+category);
        currentUser.getExpenseTracker().filterExpensesByCategory(category);

    }

    private static void generateReport() {
        System.out.print("Enter month (1-12): ");
        int month = scanner.nextInt();

        System.out.print("Enter year (yyyy): ");
        int year = scanner.nextInt();
        scanner.nextLine();  // Consume newline

        currentUser.getExpenseTracker().generateMonthlyReport(month, year);
    }

    private static void exportToCSV() {
        System.out.print("Enter file path to export: ");
        String filePath = scanner.nextLine();
        currentUser.getExpenseTracker().exportToCSV(filePath);
    }
    private static void EditB() {
        System.out.print("Enter the budget: ");
        double budget = scanner.nextDouble();
        currentUser.setBudget(budget);
    }
    private static void checkBudget() {
        currentUser.checkBudget();
    }
    private static void userInfo() {
        currentUser.printUserInfo();
    }

    private static void logOut() {
        System.out.println("Logging out...");
        currentUser = null;
    }
}
