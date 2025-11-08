package org.example.codemart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import com.mongodb.MongoException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;

public class DatabaseConnection {

    private static MongoDatabase database;
    private static MongoClient mongoClient;

    private static final String DATABASE_NAME = "codeMartDB";
    private static final String CONNECTION_URI =
            "mongodb+srv://admin:rkvUm3Aw2IZSP6t@codemartcluster.0kwnhtn.mongodb.net/?retryWrites=true&w=majority&appName=CodeMartCluster";

    // -------------------- Initialize Connection --------------------
    public static void initialize() {
        try {
            if (mongoClient == null) {
                mongoClient = MongoClients.create(CONNECTION_URI);
                mongoClient.getDatabase("admin").runCommand(new Document("ping", 1));
                database = mongoClient.getDatabase(DATABASE_NAME);
                System.out.println("✅ Connected to MongoDB Atlas successfully!");
            }
        } catch (MongoException e) {
            mongoClient = null;
            database = null;
            System.err.println("❌ ERROR: Connection failed. Check credentials/IP whitelist!");
            System.err.println("Underlying Error: " + e.getMessage());
        }
    }

    public static MongoDatabase getDatabase() {
        if (database == null) initialize();
        return database;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean registerUser(String username, String email, String password) {
        try {
            MongoCollection<Document> users = getDatabase().getCollection("users");

            Document existing = users.find(Filters.or(
                    Filters.eq("username", username),
                    Filters.eq("email", email)
            )).first();

            if (existing != null) {
                System.out.println("❌ Registration failed: Username or email already exists.");
                return false;
            }

            String hashedPassword = hashPassword(password);

            long count = users.countDocuments();
            Document newUser = new Document("user_id", count + 1)
                    .append("username", username)
                    .append("email", email)
                    .append("password", hashedPassword);

            users.insertOne(newUser);
            System.out.println("✅ User registered successfully: " + username);
            return true;
        } catch (Exception e) {
            System.err.println("⚠️ Error during registration: " + e.getMessage());
            return false;
        }
    }

    public static boolean loginUser(String username, String password) {
        try {
            MongoCollection<Document> users = getDatabase().getCollection("users");
            Document user = users.find(Filters.eq("username", username)).first();

            if (user != null) {
                String storedHash = user.getString("password");
                String inputHash = hashPassword(password);
                return storedHash.equals(inputHash);
            }
            return false;
        } catch (Exception e) {
            System.err.println("⚠️ Error during login: " + e.getMessage());
            return false;
        }
    }

    public static boolean saveOrder(String username, List<CategoryController.Product> items, double total, String paymentMethod, String transactionId) {
        try {
            MongoCollection<Document> orders = getDatabase().getCollection("orders");

            Document order = new Document()
                    .append("username", username)
                    .append("transactionId", transactionId) // NEW: store transaction ID
                    .append("items", items.stream()
                            .map(p -> new Document("name", p.name)
                                    .append("quantity", p.quantity)
                                    .append("price", p.price))
                            .toList())
                    .append("total", total)
                    .append("payment_method", paymentMethod)
                    .append("timestamp", Instant.now().toString());

            orders.insertOne(order);
            System.out.println("✅ Order saved successfully for user: " + username + " | Transaction ID: " + transactionId);
            return true;
        } catch (Exception e) {
            System.err.println("⚠️ Error saving order: " + e.getMessage());
            return false;
        }
    }
}
