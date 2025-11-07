package org.example.codemart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import com.mongodb.MongoException;

public class DatabaseConnection {

    private static MongoDatabase database;
    private static MongoClient mongoClient;

    private static final String DATABASE_NAME = "codeMartDB";


    private static final String CONNECTION_URI =
            "mongodb+srv://admin:rkvUm3Aw2IZSP6t@codemartcluster.0kwnhtn.mongodb.net/?retryWrites=true&w=majority&appName=CodeMartCluster";


    public static void initialize() {
        try {
            if (mongoClient == null) {
                // Connect and force authentication check (ping command)
                mongoClient = MongoClients.create(CONNECTION_URI);
                mongoClient.getDatabase("admin").runCommand(new Document("ping", 1));

                database = mongoClient.getDatabase(DATABASE_NAME);
                System.out.println("✅ Connected to MongoDB Atlas successfully and authenticated!");
            }
        } catch (MongoException e) {
            mongoClient = null;
            database = null;
            System.err.println("❌ ERROR: Connection or Authentication failed. Check credentials and IP whitelist!");
            System.err.println("Underlying Error: " + e.getMessage());
        }
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            initialize();
        }
        return database;
    }

    // ... (loginUser and registerUser methods remain the same)
    public static boolean loginUser(String username, String password) {
        try {
            MongoCollection<Document> users = getDatabase().getCollection("users");
            Document user = users.find(Filters.and(
                    Filters.eq("username", username),
                    Filters.eq("password", password)
            )).first();
            return user != null;
        } catch (Exception e) {
            System.err.println("⚠️ Error during login: " + e.getMessage());
            return false;
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

            long count = users.countDocuments();
            Document newUser = new Document("user_id", count + 1)
                    .append("username", username)
                    .append("email", email)
                    .append("password", password);

            users.insertOne(newUser);
            System.out.println("✅ User registered successfully: " + username);
            return true;
        } catch (Exception e) {
            System.err.println("⚠️ Error during registration: " + e.getMessage());
            return false;
        }
    }
}