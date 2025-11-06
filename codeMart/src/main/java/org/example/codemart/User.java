// src/main/java/org/example/codemart/User.java

package org.example.codemart; // Add the package statement here too

public class User {
    private String name;
    private int age;

    // Default constructor is essential for MongoDB
    public User() {}

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters and Setters (IntelliJ can generate these easily!)
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
}