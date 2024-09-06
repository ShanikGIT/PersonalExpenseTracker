import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Expense {
    private int id;
    private double amount;
    private Date date;
    private String category;
    private String description;

    public Expense(int id, double amount, Date date, String category, String description) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.description = description;
    }

    public boolean saveToDatabase(String username) {
        String sql = "INSERT INTO expenses (amount, date, category, description, user_id) " + "VALUES (?, ?, ?, ?, (SELECT username FROM users WHERE username = ?))";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, this.amount);
            stmt.setDate(2, this.date);
            stmt.setString(3, this.category);
            stmt.setString(4, this.description);
            stmt.setString(5, username);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Expense> getExpensesForUser(String username) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT * FROM expenses WHERE user_id = (SELECT username FROM users WHERE username = ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    double amount = rs.getDouble("amount");
                    Date date = rs.getDate("date");
                    String category = rs.getString("category");
                    String description = rs.getString("description");
                    expenses.add(new Expense(id, amount, date, category, description));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    // Getters and setters
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public Date getDate() { return date; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return "Expense [id=" + id + ", amount=" + amount + ", date=" + date + ", category=" + category + ", description=" + description + "]";
    }
}
