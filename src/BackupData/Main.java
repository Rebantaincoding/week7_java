package BackupData;

import java.util.*;
import java.io.*;
import org.json.JSONObject;
import com.opencsv.exceptions.CsvValidationException;


//Contains the main function to implement all the functionalities of BackupManager class
public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        BackupManager manager = new BackupManager();

        int choice = 0;

        while (choice != 8) {

            System.out.println("\n========== DATA BACKUP SYSTEM ==========");
            System.out.println("1. Create Backup");
            System.out.println("2. Restore Backup from Binary File");
            System.out.println("3. Restore Backup from Compressed File");
            System.out.println("4. Import Data from CSV");
            System.out.println("5. Import Data from JSON");
            System.out.println("6. List All Backup Versions");
            System.out.println("7. Cleanup Old Backups");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    case 1:
                    	//Implement backup data creation
                        System.out.print("Enter backup description: ");
                        String description = sc.nextLine();
                        
                        //To store data of backups
                        Map<String, Object> data = new HashMap<>();

                        System.out.print("Enter number of data items: ");
                        int n = Integer.parseInt(sc.nextLine());

                        for (int i = 0; i < n; i++) {

                            System.out.print("Enter key: ");
                            String key = sc.nextLine();

                            System.out.print(
                                "Enter data type (String/Integer/Double/Boolean): ");
                            String type = sc.nextLine();

                            System.out.print("Enter value: ");
                            String value = sc.nextLine();

                            Object obj;

                            switch (type.toLowerCase()) {

                                case "integer":
                                    obj = Integer.parseInt(value);
                                    break;

                                case "double":
                                    obj = Double.parseDouble(value);
                                    break;

                                case "boolean":
                                    if (!value.equalsIgnoreCase("true") &&
                                        !value.equalsIgnoreCase("false")) {
                                        throw new IllegalArgumentException(
                                            "Boolean must be true or false");
                                    }
                                    obj = Boolean.parseBoolean(value);
                                    break;

                                case "string":
                                    obj = value;
                                    break;

                                default:
                                    throw new IllegalArgumentException(
                                        "Unsupported data type: " + type);
                            }

                            data.put(key, obj);
                        }

                        String backupId = manager.createBackup(description, data);
                        System.out.println("Backup ID: " + backupId);
                        break;

                    case 2:
                    	//Restores data from binary file
                        System.out.print("Enter binary file path: ");
                        String binaryPath = sc.nextLine();

                        BackupData binaryBackup =
                            manager.restoreFromBinary(binaryPath);

                        System.out.println("\nBackup restored successfully!");
                        System.out.println(binaryBackup);
                        System.out.println("Data: " + binaryBackup.getData());
                        break;

                    case 3:
                    	//Restore data from compressed file
                        System.out.print("Enter compressed file path: ");
                        String compressedPath = sc.nextLine();

                        BackupData compressedBackup =
                            manager.restoreFromCompressed(compressedPath);

                        System.out.println("\nBackup restored successfully!");
                        System.out.println(compressedBackup);
                        System.out.println("Data: " + compressedBackup.getData());
                        break;

                    case 4:
                    	//Restore data from CSV file
                        System.out.print("Enter CSV file path: ");
                        String csvPath = sc.nextLine();

                        Map<String, Object> csvData =
                            manager.importFromCSV(csvPath);

                        System.out.println("\nCSV imported successfully!");
                        System.out.println("Imported Data: " + csvData);
                        break;

                    case 5:
                    	//Restore data from JSON file
                        System.out.print("Enter JSON file path: ");
                        String jsonPath = sc.nextLine();

                        JSONObject jsonData =
                            manager.importFromJSON(jsonPath);

                        System.out.println("\nJSON imported successfully!");
                        System.out.println(jsonData.toString(4));
                        break;

                    case 6:
                        List<BackupData> versions =
                            manager.listAllVersions();

                        if (versions.isEmpty()) {
                            System.out.println("No backup versions found.");
                        } else {
                            System.out.println("\n========== BACKUP VERSIONS ==========");

                            for (BackupData backup : versions) {
                                System.out.println(backup);
                                System.out.println("Data: " + backup.getData());
                                System.out.println("----------------------------");
                            }
                        }
                        break;

                    case 7:
                    	//Removes all old backups
                        System.out.print(
                            "Enter number of days to retain backups: ");

                        int days = Integer.parseInt(sc.nextLine());

                        if (days < 0) {
                            System.out.println("Days cannot be negative.");
                            break;
                        }

                        manager.cleanupOldBackups(days);
                        System.out.println("Backup cleanup completed.");
                        break;

                    case 8:
                        System.out.println("Exiting Data Backup System...");
                        break;

                    default:
                        System.out.println("Invalid choice! Try again.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid number entered.");
            } catch (IOException e) {
                System.out.println("File error: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("Error restoring backup: " + e.getMessage());
            } catch (CsvValidationException e) {
                System.out.println("CSV error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid input: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }

        sc.close();
    }
}

