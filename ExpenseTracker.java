import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ExpenseTracker {
    public void addExpenseToDatabase(Expense expense) {
        String sql = "INSERT INTO expenses (amount, date, category, description) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, expense.getAmount());
            pstmt.setDate(2, new java.sql.Date(expense.getDate().getTime()));
            pstmt.setString(3, expense.getCategory());
            pstmt.setString(4, expense.getDescription());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteExpenseFromDatabase(int id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editExpenseInDatabase(int id, double amount, String category, String description) {
        String sql = "UPDATE expenses SET amount = ?, category = ?, description = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, category);
            pstmt.setString(3, description);
            pstmt.setInt(4, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Expense> getExpensesFromDatabase() {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                double amount = rs.getDouble("amount");
                Date date = rs.getDate("date");
                String category = rs.getString("category");
                String description = rs.getString("description");
                expenses.add(new Expense(id, amount, date, category, description));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public void filterExpensesByCategory(String category) {
        List<Expense> filteredExpenses = new ArrayList<>();
        for (Expense expense : getExpensesFromDatabase()) {
            if (expense.getCategory().equalsIgnoreCase(category)) {
                filteredExpenses.add(expense);
            }
        }
        System.out.println(filteredExpenses);

    }

    public void generateMonthlyReport(int month, int year) {
        Map<String, Double> report = new HashMap<>();
        for (Expense expense : getExpensesFromDatabase()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(expense.getDate());
            int expenseMonth = cal.get(Calendar.MONTH) + 1;
            int expenseYear = cal.get(Calendar.YEAR);

            if (expenseMonth == month && expenseYear == year) {
                report.put(expense.getCategory(), report.getOrDefault(expense.getCategory(), 0.0) + expense.getAmount());
            }
        }

        System.out.println("Monthly Report for " + month + "/" + year);
        for (Map.Entry<String, Double> entry : report.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public void exportToCSV(String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Expense expense : getExpensesFromDatabase()) {
                writer.write(expense.getId() + "," + expense.getAmount() + "," + expense.getDate() + "," + expense.getCategory() + "," + expense.getDescription());
                writer.newLine();
            }
            System.out.println("Expenses exported successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
