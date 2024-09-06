import java.sql.*;

public class User {
    private String username;
    private String password;
    private double budget;
    private ExpenseTracker expenseTracker;

    public User(String username, String password, double budget) {
        this.username = username;
        this.password = password;
        this.expenseTracker = new ExpenseTracker();
        this.budget = budget;
    }

    public String getUsername() {
        return username;
    }

    public boolean authenticate(String password) {
        return this.password.equals(password);
    }

    public ExpenseTracker getExpenseTracker() {
        return expenseTracker;
    }

    public void setBudget(double budget) {
        this.budget = budget;
        updateUserBudgetInDatabase();
    }

    public void checkBudget() {
        double total = getExpenseTracker().getExpensesFromDatabase().stream().mapToDouble(Expense::getAmount).sum();
        if (total > budget) {
            System.out.println("You have exceeded your budget by " + (total - budget));
        } else {
            System.out.println("You are within your budget. Remaining: " + (budget - total));
        }
    }

    public void printUserInfo() {
        System.out.println("User: " + username);
        System.out.println("Budget: " + budget);
    }

    public boolean saveToDatabase() {
        String sql = "INSERT INTO users (username, password, budget) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, this.username);
            pstmt.setString(2, this.password);
            pstmt.setDouble(3, this.budget);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User getUserFromDatabase(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String password = rs.getString("password");
                    double budget = rs.getDouble("budget");
                    return new User(username, password, budget);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateUserBudgetInDatabase() {
        String sql = "UPDATE users SET budget = ? WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, this.budget);
            pstmt.setString(2, this.username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
