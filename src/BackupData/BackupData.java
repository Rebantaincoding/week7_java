package BackupData;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import java.text.SimpleDateFormat;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import org.json.JSONObject;

//Backup data class to store backup attributes and functionalities
public class BackupData implements Serializable{
      private static final long serialVersionUID=1;
      //Stores backupId
      private String backupId;
      //Stores the time at which backup was created
      private Date backupTime;
      //HashMap containing data
      private Map<String,Object> data;
      //Description of backup
      private String description;
      
      //Parameterized constructor
      public BackupData(String description,Map<String,Object> data) {
    	  this.backupId=UUID.randomUUID().toString();
    	  backupTime=new Date();
    	  this.description=description;
    	  this.data=new HashMap<>(data);
      }
      
      //Saves backup as a binary file
      public void saveToBinaryFile(String filePath) throws IOException {
    	  try(
    		  ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(filePath))){
    		  oos.writeObject(this);
    		  oos.close();
    	  }
    	  
      }
      
      //Saves backup as a compressed file
      public void saveToCompressedFile(String filePath) throws IOException {
    	  try (
    		  ObjectOutputStream oos=new ObjectOutputStream(new GZIPOutputStream( new FileOutputStream(filePath)))){
    		  oos.writeObject(this);
    		  oos.close();
    	  }
    	  
      }
      
      //Saves backup as a CSV file
      public void exportToCSV(String filePath) throws IOException {

    		    try (CSVWriter csvWriter =
    		            new CSVWriter(new FileWriter(filePath))) {

    		        for (Map.Entry<String, Object> entry : data.entrySet()) {

    		            String[] set = {
    		                entry.getKey(),
    		                entry.getValue().toString(),
    		                entry.getValue().getClass().getSimpleName()
    		            };

    		            csvWriter.writeNext(set);
    		        }
    		    }
    		}
 
      //Saves backup as a JSON file
      public void exportToJSON(String filePath) throws IOException{
    	  
    	  try (FileWriter fw=new FileWriter(filePath)){
    		  JSONObject json = new JSONObject();

    	        for (Map.Entry<String, Object> entry : data.entrySet()) {

    	            JSONObject item = new JSONObject();

    	            item.put("value", entry.getValue());
    	            item.put("type",
    	                    entry.getValue().getClass().getSimpleName());

    	            json.put(entry.getKey(), item);
    	        }

    	        fw.write(json.toString(4));

    	        System.out.println("JSON exported successfully!");
                fw.close();
    	    }
      }
      
      //Getter methods
	  public String getBackupId() {
		  return backupId;
	  }
	  public Date getBackupTime() {
		  return backupTime;
	  }
	  public Map<String, Object> getData() {
		  return data;
	  }
	  public String getDescription() {
		  return description;
	  }
	  @Override
      public String toString() {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          return String.format("Backup ID: %s\nTime: %s\nDescription: %s\nData Items: %d",
                             backupId, sdf.format(backupTime), description, data.size());
      }
}
