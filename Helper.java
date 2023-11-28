// This is a class of helper methods
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.io.FileWriter;

public class Helper {
    public Helper() {
        
    }
    
    // Convert string to bytes
    public byte[] stringToBytes(String inputString) {
        byte[] byteArray = inputString.getBytes();
        return byteArray;
    }
    
    // Convert bytes to String
    public String bytesToString(byte[] byteArray) {
        String s = new String(byteArray);
        return s;
    }
    
    // Get the size of a file in bytes
    public long getFileSizeBytes(File file) {
        return file.length();
    }
    
    // Convert a file to bytes
    public byte[] fileToBytes(File file) {
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            return bytes;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
