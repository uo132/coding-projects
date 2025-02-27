//2333236

package Technical;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;


import java.nio.file.Paths;
import java.util.*;

public class Index {
    private static final String COLLECTION_FOLDER = "collection";
    private static final String DEFAULT_JSON_FILE = COLLECTION_FOLDER + "/default.json";

    public static void init() {
        ensureDefaultJsonExists();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n--- Welcome, Technical User! ---");
                System.out.println("Please select an option:");
                System.out.println("1. Create New Data Pack");
                System.out.println("2. AI Chatbox");
                System.out.println("3. Search for Created Packs");
                System.out.println("4. Subset Data");
                System.out.println("5. Mask Data");
                System.out.println("0. Exit");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline

                switch (choice) {
                    case 1:
                        createNewDataPack(scanner);
                        break;
                    case 2:
                        openAiChatbox();
                        break;
                    case 3:
                        searchCreatedPacks(scanner);
                        break;
                    case 4:
                        subsetData(scanner);
                        break;
                    case 5:
                        maskData(scanner);
                        break;
                    case 0:
                        System.out.println("Exiting... Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        }
    }
    public static void searchCreatedPacks(Scanner scanner) {
        File folder = new File(COLLECTION_FOLDER);
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("No data found in the collection folder.");
            return;
        }

        // List all files in the collection folder
        System.out.println("Available Packs:");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        // Ask the user to select a file
        System.out.print("Select a pack by entering the corresponding number: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > files.length) {
                System.out.println("Invalid choice. Exiting.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Exiting.");
            return;
        }

        // Display the selected file's content
        File selectedFile = files[choice - 1];
        System.out.println("\nDisplaying content of: " + selectedFile.getName());
        try {
            String content = new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())));
            System.out.println(content);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private static void createNewDataPack(Scanner scanner) {
        System.out.println("\n--- Create New Data Pack ---");

        // Select Database
        System.out.print("Enter the Database Name: ");
        String database = scanner.nextLine();

        // Select Platform
        System.out.print("Enter the Platform (e.g., MySQL, PostgreSQL): ");
        String platform = scanner.nextLine();

        // Add Data Source
        System.out.print("Enter the Data Source (JSON file path or press Enter to use default): ");
        String dataSourcePath = scanner.nextLine();
        if (dataSourcePath.isEmpty()) {
            dataSourcePath = DEFAULT_JSON_FILE;
        }

        // Load JSON Data manually
        Map<String, Object> jsonData = loadJsonFile(dataSourcePath);
        if (jsonData == null) {
            System.out.println("Failed to load JSON data. Aborting.");
            return;
        }

        // Replace values in the JSON file
        System.out.println("\nCurrent JSON Data: " + jsonData);
        System.out.print("Enter the key you want to replace: ");
        String keyToReplace = scanner.nextLine();
        System.out.print("Enter the new value: ");
        String newValue = scanner.nextLine();
        if (jsonData.containsKey(keyToReplace)) {
            jsonData.put(keyToReplace, newValue);
            System.out.println("Value replaced successfully.");
        } else {
            System.out.println("Key not found in JSON data.");
        }

        // Ask if user wants to subset
        System.out.print("Do you want to subset the data? (yes/no): ");
        boolean subset = scanner.nextLine().equalsIgnoreCase("yes");
        if (subset) {
            System.out.println("Subsetting data...");
            jsonData = subsetData(jsonData, scanner); // Pass scanner here
        }

        // Save the updated JSON to the collection folder
        saveJsonFile(jsonData, dataSourcePath);

        System.out.println("\nData Pack Created Successfully!");
        System.out.println("Database: " + database);
        System.out.println("Platform: " + platform);
        System.out.println("Data Source: " + dataSourcePath);
    }

    private static void openAiChatbox() {
        System.out.println("\n--- AI Chatbox ---");
        System.out.println("Redirecting to OpenAI ChatGPT...");

        try {
            String url = "https://chat.openai.com/";
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                // Windows
                Runtime.getRuntime().exec(new String[] { "cmd", "/c", "start", url });
            } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                // Mac
                Runtime.getRuntime().exec(new String[] { "open", url });
            } else if (System.getProperty("os.name").toLowerCase().contains("nix") ||
                    System.getProperty("os.name").toLowerCase().contains("nux")) {
                // Linux
                Runtime.getRuntime().exec(new String[] { "xdg-open", url });
            }
            System.out.println("Browser opened successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while opening the browser: " + e.getMessage());
        }
    }

    private static Map<String, Object> subsetData(Map<String, Object> jsonData, Scanner scanner) {
        System.out.print("Enter the key to subset by: ");
        String subsetKey = scanner.nextLine(); // Use the passed scanner

        if (jsonData.containsKey(subsetKey)) {
            Object subsetValue = jsonData.get(subsetKey);
            Map<String, Object> subset = new HashMap<>();
            subset.put(subsetKey, subsetValue);
            System.out.println("Subset completed successfully.");
            return subset;
        } else {
            System.out.println("Key not found. Subset aborted.");
            return jsonData;
        }
    }

    public static void subsetData(Scanner scanner) {
        System.out.print("Enter the JSON file path to load: ");
        String filePath = scanner.nextLine();
        Map<String, Object> jsonData = loadJsonFile(filePath);

        if (jsonData == null) {
            System.out.println("Failed to load JSON data. Aborting.");
            return;
        }

        System.out.println("\nLoaded JSON Data: " + jsonData);
        System.out.print("Enter keys to subset (comma-separated): ");
        String[] keys = scanner.nextLine().split(",");

        Map<String, Object> subset = new HashMap<>();
        for (String key : keys) {
            key = key.trim();
            if (jsonData.containsKey(key)) {
                subset.put(key, jsonData.get(key));
            } else {
                System.out.println("Key not found: " + key);
            }
        }

        System.out.println("Subset Data: " + subset);
        System.out.print("Do you want to save this subset? (yes/no): ");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            saveJsonFile(subset, COLLECTION_FOLDER + "/subset.json");
            System.out.println("Subset saved successfully.");
        }
    }

    public static void maskData(Scanner scanner) {
        System.out.print("Enter the JSON file path to load: ");
        String filePath = scanner.nextLine();
        Map<String, Object> jsonData = loadJsonFile(filePath);

        if (jsonData == null) {
            System.out.println("Failed to load JSON data. Aborting.");
            return;
        }

        System.out.println("\nLoaded JSON Data: " + jsonData);
        System.out.print("Enter keys to mask (comma-separated): ");
        String[] keys = scanner.nextLine().split(",");

        System.out.print("Enter the masking value (e.g., *****): ");
        String maskValue = scanner.nextLine();

        for (String key : keys) {
            key = key.trim();
            if (jsonData.containsKey(key)) {
                jsonData.put(key, maskValue);
            } else {
                System.out.println("Key not found: " + key);
            }
        }

        System.out.println("Masked Data: " + jsonData);
        System.out.print("Do you want to save this masked data? (yes/no): ");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            saveJsonFile(jsonData, COLLECTION_FOLDER + "/masked.json");
            System.out.println("Masked data saved successfully.");
        }
    }

    private static void ensureDefaultJsonExists() {
        File folder = new File(COLLECTION_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File defaultFile = new File(DEFAULT_JSON_FILE);
        if (!defaultFile.exists()) {
            try (FileWriter writer = new FileWriter(defaultFile)) {
                writer.write("{\"name\": \"mary\", \"age\": 25, \"city\": \"New York\"}");
            } catch (IOException e) {
                System.out.println("Failed to create default JSON file: " + e.getMessage());
            }
        }
    }

    private static Map<String, Object> loadJsonFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Scanner fileScanner = new Scanner(reader);
            StringBuilder content = new StringBuilder();
            while (fileScanner.hasNextLine()) {
                content.append(fileScanner.nextLine());
            }
            fileScanner.close();

            String jsonContent = content.toString();
            return parseJsonToMap(jsonContent);
        } catch (IOException e) {
            System.out.println("Error reading JSON file: " + e.getMessage());
            return null;
        }
    }

    private static Map<String, Object> parseJsonToMap(String jsonContent) {
        Map<String, Object> map = new HashMap<>();
        try {
            String[] entries = jsonContent.replace("{", "").replace("}", "").split(",");
            for (String entry : entries) {
                String[] keyValue = entry.split(":");
                map.put(keyValue[0].trim().replace("\"", ""), keyValue[1].trim().replace("\"", ""));
            }
        } catch (Exception e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
        }
        return map;
    }

    private static void saveJsonFile(Map<String, Object> jsonData, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(mapToJsonString(jsonData));
            System.out.println("JSON data saved to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error saving JSON file: " + e.getMessage());
        }
    }

    private static String mapToJsonString(Map<String, Object> map) {
        StringBuilder jsonString = new StringBuilder("{");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            jsonString.append("\"").append(entry.getKey()).append("\": \"").append(entry.getValue()).append("\", ");
        }
        if (jsonString.length() > 1) {
            jsonString.setLength(jsonString.length() - 2); // Remove last comma and space
        }
        jsonString.append("}");
        return jsonString.toString();
    }

    public static void main(String[] args) {
        init();
    }
}
