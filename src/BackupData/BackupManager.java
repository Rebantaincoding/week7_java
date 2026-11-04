package BackupData;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import java.text.SimpleDateFormat;
import org.json.JSONObject;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

//Backup Manager class containing all the utility methods
public class BackupManager {
	     private static final String BACKUP_DIR = "backups/";
	     private static final String VERSION_DIR = "versions/";
	     public BackupManager() {
	        createDirectories();
	      }
	    
	     //Creating directories where backup would be stored
	    private void createDirectories() {
	        new File(BACKUP_DIR).mkdirs();
	        new File(VERSION_DIR).mkdirs();
	    }
	    
	    //Creating versions of backup
	    private void createVersion(BackupData backup) throws IOException {
	        String versionFile = VERSION_DIR + "version_" + backup.getBackupId() + ".dat";
	        backup.saveToBinaryFile(versionFile);
	    }
	    
	    //Creating a new backup
	    public String createBackup(String description, Map<String, Object> data) throws IOException {
	        BackupData backup = new BackupData(description, data);
	        
	        // Save in multiple formats
	        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	        String baseName = BACKUP_DIR + "backup_" + timestamp;
	        
	        // Binary format
	        backup.saveToBinaryFile(baseName + ".dat");
	        
	        // Compressed format
	        backup.saveToCompressedFile(baseName + ".dat.gz");
	        
	        // CSV format
	        backup.exportToCSV(baseName + ".csv");
	        
	        backup.exportToJSON(baseName + ".json");
	        
	        // Create version
	        createVersion(backup);
	        
	        System.out.println("Backup created successfully!");
	        System.out.println("Files created:");
	        System.out.println("  • " + baseName + ".dat");
	        System.out.println("  • " + baseName + ".dat.gz");
	        System.out.println("  • " + baseName + ".csv");
	        System.out.println("  • " + baseName + ".json");
	        
	        return backup.getBackupId();
	    }
	    
	    //Restore data from a saved binary file
	    public BackupData restoreFromBinary(String filePath) throws IOException, ClassNotFoundException {
	        try (ObjectInputStream ois = new ObjectInputStream(
	                new FileInputStream(filePath))) {
	            return (BackupData) ois.readObject();
	        }
	    }
	    
	  //Restore data from a saved compressed file
	    public BackupData restoreFromCompressed(String filePath) throws IOException, ClassNotFoundException {
	        try (ObjectInputStream ois = new ObjectInputStream(
	                new GZIPInputStream(new FileInputStream(filePath)))) {
	            return (BackupData) ois.readObject();
	        }
	    }
	    
	  //Restore data from a saved CSV file
	    public Map<String, Object> importFromCSV(String filePath)
	            throws IOException, CsvValidationException {

	        Map<String, Object> result = new HashMap<>();

	        try (CSVReader reader =
	                new CSVReader(new FileReader(filePath))) {
                 String[] set;

	            while ((set = reader.readNext()) != null) {

	                String key = set[0];
	                String value = set[1];
	                String type = set[2];
                    Object obj;

	                switch (type) {
	                    case "Integer":
	                        obj = Integer.parseInt(value);
	                        break;
	                    case "Double":
	                        obj = Double.parseDouble(value);
	                        break;
	                    case "Boolean":
	                        obj = Boolean.parseBoolean(value);
	                        break;
	                    case "String":
	                        obj = value;
	                        break;
	                    default:
	                        throw new IllegalArgumentException(
	                            "Unsupported type: " + type);
	                }

	                result.put(key, obj);
	            }
	        }

	        return result;
	    }
	    
	  //Restore data from a saved JSON file
	    public JSONObject importFromJSON(String filePath)
	            throws IOException {

	        String content = Files.readString(Path.of(filePath));

	        return new JSONObject(content);
	    }
	    
	    //Lists all versions of the backup created
	    public List<BackupData> listAllVersions() throws IOException, ClassNotFoundException {
	        List<BackupData> versions = new ArrayList<>();
	        File versionDir = new File(VERSION_DIR);
	        
	        if(versionDir.exists()) {
	            File[] files = versionDir.listFiles((dir, name) -> name.endsWith(".dat"));
	            if(files != null) {
	                for(File file : files) {
	                    versions.add(restoreFromBinary(file.getPath()));
	                }
	            }
	        }
	        
	        // Sort by backup time (newest first)
	        versions.sort((b1, b2) -> b2.getBackupTime().compareTo(b1.getBackupTime()));
	        return versions;
	    }
	    
	    //Removes all existing backups
	    public void cleanupOldBackups(int keepDays) {
	        File backupDir = new File(BACKUP_DIR);
	        if(!backupDir.exists()) return;
	        
	        File[] files = backupDir.listFiles();
	        if(files == null) return;
	        
	        long cutoffTime = System.currentTimeMillis() - (keepDays * 24L * 60 * 60 * 1000);
	        
	        for(File file : files) {
	            if(file.lastModified() < cutoffTime) {
	                if(file.delete()) {
	                    System.out.println("Deleted old backup: " + file.getName());
	                }
	            }
	        }
	    }
}
