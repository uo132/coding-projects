//2333236

package  Authenticate;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Index {
    private static final ArrayList<User> users = new ArrayList<>();
    private static final String USERS_FILE = "collection/users.json";
    private static final Scanner scanner = new Scanner(System.in);

    static {
        loadUsers(); // Load existing users from the JSON file
    }

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nWelcome! Please choose an option:");
            System.out.println("1. Login");
            System.out.println("2. Sign Up");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            switch (choice) {
                case 1:
                    login();
                    break;
                case 2:
                    signUp();
                    break;
                case 3:
                    System.out.println("Exiting... Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    public static void login() {
        System.out.println("\nLogin:");
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = authenticate(email, password);
        if (user != null) {
            System.out.println("Login successful!");

            // Confirm and call respective dashboard
            System.out.println("Are you a (1) Technical or (2) Non-Technical user?");
            int roleChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (roleChoice == 1 && "technical".equalsIgnoreCase(user.getRole())) {
                Technical.Index.init();
            } else if (roleChoice == 2 && "non-technical".equalsIgnoreCase(user.getRole())) {
                NonTechnical.Index.init();
            } else {
                System.out.println("Invalid role choice. Please restart the application.");
            }
        } else {
            System.out.println("Invalid email or password. Please try again.");
        }
    }

    private static User authenticate(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public static void signUp() {
        System.out.println("\nSign Up:");
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        if (userExists(email)) {
            System.out.println("Email already exists. Please try logging in.");
            return;
        }

        System.out.print("Enter your password: ");
        String password = scanner.nextLine();

        System.out.print("Are you a (1) Technical or (2) Non-Technical user? ");
        int roleChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        String role = roleChoice == 1 ? "technical" : "non-technical";
        User newUser = new User(email, password, role);
        users.add(newUser);
        saveUsers(); // Save users to JSON file
        System.out.println("Sign-up successful! Please login to continue.");
        // Redirect to log in after sign-up
        login();
    }

    private static boolean userExists(String email) {
        for (User user : users) {
            if (user.getEmail().equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

    private static void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line.trim());
            }

            String[] userEntries = content.toString()
                    .replace("[", "")
                    .replace("]", "")
                    .split("},\\s*\\{");

            for (String entry : userEntries) {
                if (!entry.trim().isEmpty()) {
                    String cleanEntry = entry.replace("{", "").replace("}", "");
                    String[] fields = cleanEntry.split(",");
                    String email = null, password = null, role = null;

                    for (String field : fields) {
                        String[] keyValue = field.split(":");
                        String key = keyValue[0].replace("\"", "").trim();
                        String value = keyValue[1].replace("\"", "").trim();

                        switch (key) {
                            case "email":
                                email = value;
                                break;
                            case "password":
                                password = value;
                                break;
                            case "role":
                                role = value;
                                break;
                        }
                    }

                    if (email != null && password != null && role != null) {
                        users.add(new User(email, password, role));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private static void saveUsers() {
        StringBuilder jsonContent = new StringBuilder("[\n");

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            jsonContent.append("  {")
                    .append("\"email\":\"").append(user.getEmail()).append("\", ")
                    .append("\"password\":\"").append(user.getPassword()).append("\", ")
                    .append("\"role\":\"").append(user.getRole()).append("\"}")
                    .append(i < users.size() - 1 ? ",\n" : "\n");
        }
        jsonContent.append("]");

        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            writer.write(jsonContent.toString());
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
}

class User {
    private String email;
    private String password;
    private String role;

    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
