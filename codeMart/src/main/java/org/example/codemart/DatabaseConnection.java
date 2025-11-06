package org.example.codemart;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.MongoException;
import org.bson.Document;

public class DatabaseConnection {

    private static MongoDatabase database;
    private static MongoClient mongoClient;
    private static final String DATABASE_NAME = "codeMartDB";
    private static final String CONNECTION_URI = "mongodb://localhost:27017";

    public static void initialize() {
        try {
            mongoClient = MongoClients.create(CONNECTION_URI);
            database = mongoClient.getDatabase(DATABASE_NAME);
            System.out.println("Successfully connected to MongoDB!");
        } catch (MongoException e) {
            System.err.println("Error connecting to MongoDB: " + e.getMessage());
        }
    }

    public static MongoDatabase getDatabase() {
        if (database == null) {
            initialize();
        }
        return database;
    }

    // --- LOGIN METHOD (Unchanged from last version) ---
    public static boolean loginUser(String username, String password) {
        try {
            MongoDatabase db = getDatabase();
            if (db == null) return false;

            MongoCollection<Document> users = db.getCollection("users");

            Document userDocument = users.find(
                    Filters.and(
                            Filters.eq("username", username),
                            Filters.eq("password", password)
                    )
            ).first();

            if (userDocument != null) {
                System.out.println("Login successful for user: " + username);
                return true;
            } else {
                System.out.println("Login failed: Invalid credentials.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("An error occurred during login: " + e.getMessage());
            return false;
        }
    }

    // --- NEW REGISTRATION METHOD ---
    public static boolean registerUser(String username, String password) {
        try {
            MongoDatabase db = getDatabase();
            if (db == null) return false;

            MongoCollection<Document> users = db.getCollection("users");

            // Check if user already exists
            Document existingUser = users.find(Filters.eq("username", username)).first();
            if (existingUser != null) {
                System.out.println("Registration failed: User already exists.");
                return false;
            }

            // Insert the new user
            Document newUser = new Document("username", username).append("password", password);
            users.insertOne(newUser);
            System.out.println("User registered successfully: " + username);
            return true;
        } catch (Exception e) {
            System.err.println("An error occurred during registration: " + e.getMessage());
            return false;
        }
    }
}