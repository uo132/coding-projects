//2333236

import java.util.Scanner;

public class DataManagementApp {
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to Data Management App");
        System.out.println("Do you want to (1) Login or (2) Sign Up?");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            Authenticate.Index.login();
        } else if (choice == 2) {
            Authenticate.Index.signUp();
        } else {
            System.out.println("Invalid choice. Please restart the application.");
        }
    }
}
