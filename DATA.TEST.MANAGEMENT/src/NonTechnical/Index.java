//2333236

package NonTechnical;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Index {
    private static final String COLLECTION_FOLDER = "collection";
    private static final String DEFAULT_JSON_FILE = COLLECTION_FOLDER + "/default.json";
    private static final List<String> ADJECTIVES = Arrays.asList("DOB", "animals", "GPAs", "names");

    public static void init() {
        ensureDefaultJsonExists();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("\n--- Welcome, Non-Technical User! ---");
                System.out.println("Please select an option:");
                System.out.println("1. View Available Data Packs");
                System.out.println("2. Load Data Pack and Mask/Subset");
                System.out.println("3. Generating data");
                System.out.println("0. Exit");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline

                switch (choice) {
                    case 1:
                        viewAvailableDataPacks();
                        break;
                    case 2:
                        loadAndProcessDataPack(scanner);
                        break;
                    case 3:
                        generatingdata(scanner);
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

    private static void ensureDefaultJsonExists() {
        File folder = new File(COLLECTION_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File defaultFile = new File(DEFAULT_JSON_FILE);
        if (!defaultFile.exists()) {
            try (FileWriter writer = new FileWriter(defaultFile)) {
                writer.write("{\"name\": \"john\", \"age\": 30}");
            } catch (IOException e) {
                System.out.println("Failed to create default JSON file: " + e.getMessage());
            }
        }
    }

    private static void viewAvailableDataPacks() {
        File folder = new File(COLLECTION_FOLDER);
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("No data packs available.");
            return;
        }

        System.out.println("\nAvailable Data Packs:");
        for (File file : files) {
            System.out.println("- " + file.getName());
        }
    }

    private static void loadAndProcessDataPack(Scanner scanner) {
        System.out.print("\nEnter the name of the data pack to load: ");
        String fileName = scanner.nextLine();
        File file = new File(COLLECTION_FOLDER + "/" + fileName);

        if (!file.exists()) {
            System.out.println("Data pack not found.");
            return;
        }

        Map<String, Object> jsonData = loadJsonFile(file.getAbsolutePath());
        if (jsonData == null) {
            System.out.println("Failed to load data pack.");
            return;
        }

        System.out.println("Data Loaded: " + jsonData);
        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Mask Data");
        System.out.println("2. Subset Data");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        switch (choice) {
            case 1:
                maskData(jsonData, scanner);
                break;
            case 2:
                jsonData = subsetData(jsonData, scanner);
                break;
            default:
                System.out.println("Invalid choice.");
        }

        saveJsonFile(jsonData, file.getAbsolutePath());
        System.out.println("Updated data pack saved.");
    }

    private static void maskData(Map<String, Object> jsonData, Scanner scanner) {
        System.out.print("Enter the key to mask: ");
        String key = scanner.nextLine();

        if (jsonData.containsKey(key)) {
            jsonData.put(key, "****");
            System.out.println("Data masked successfully.");
        } else {
            System.out.println("Key not found.");
        }
    }

    private static Map<String, Object> subsetData(Map<String, Object> jsonData, Scanner scanner) {
        System.out.print("Enter the key to subset by: ");
        String subsetKey = scanner.nextLine();

        if (jsonData.containsKey(subsetKey)) {
            Map<String, Object> subset = new HashMap<>();
            subset.put(subsetKey, jsonData.get(subsetKey));
            System.out.println("Subset completed.");
            return subset;
        } else {
            System.out.println("Key not found. No changes made.");
            return jsonData;
        }
    }

    private static void generatingdata(Scanner scanner) {
        System.out.println("\nAvailable Data Types for Dummy Data:");
        for (int i = 0; i < ADJECTIVES.size(); i++) {
            System.out.println((i + 1) + ". " + ADJECTIVES.get(i));
        }

        System.out.print("Select a data type (1-" + ADJECTIVES.size() + "): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        if (choice < 1 || choice > ADJECTIVES.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        System.out.print("How many entries would you like to generate? ");
        int count = scanner.nextInt();
        scanner.nextLine(); // Consume the newline

        String type = ADJECTIVES.get(choice - 1);
        List<Object> dummyData = generateDataByType(type, count);

        System.out.print("Enter the name of the file to save the dummy data: ");
        String fileName = scanner.nextLine();
        saveJsonFile(Collections.singletonMap(type, dummyData), COLLECTION_FOLDER + "/" + fileName);
        System.out.println("Dummy data saved successfully.");
    }

    private static List<Object> generateDataByType(String type, int count) {
        List<Object> data = new ArrayList<>();
        Random random = new Random();

        switch (type) {
            case "DOB":
                for (int i = 0; i < count; i++) {
                    int year = 1970 + random.nextInt(54);
                    int month = 1 + random.nextInt(12);
                    int day = 1 + random.nextInt(28);
                    data.add(String.format("%04d-%02d-%02d", year, month, day));
                }
                break;
            case "animals":
                String[] animals = {"Cat", "Dog", "Lion", "Tiger", "Elephant", "Fox", "Wolf"};
                for (int i = 0; i < count; i++) {
                    data.add(animals[random.nextInt(animals.length)]);
                }
                break;
            case "GPAs":
                for (int i = 0; i < count; i++) {
                    data.add(String.format("%.2f", 1.0 + (random.nextDouble() * 3.0)));
                }
                break;
            case "names":
                String[] names = {"John", "Jane", "Alice", "Bob", "Charlie", "David", "Eve"};
                for (int i = 0; i < count; i++) {
                    data.add(names[random.nextInt(names.length)]);
                }
                break;
        }
        return data;
    }

    private static Map<String, Object> loadJsonFile(String filePath) {
        try (Scanner fileScanner = new Scanner(new File(filePath))) {
            StringBuilder content = new StringBuilder();
            while (fileScanner.hasNextLine()) {
                content.append(fileScanner.nextLine());
            }
            return parseJsonToMap(content.toString());
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
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
            jsonString.append("\"").append(entry.getKey()).append("\": ").append(entry.getValue()).append(", ");
        }
        if (jsonString.length() > 1) {
            jsonString.setLength(jsonString.length() - 2);
        }
        jsonString.append("}");
        return jsonString.toString();
    }

    public static void main(String[] args) {
        init();
    }
}
