import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.io.FileWriter;
import java.lang.Math;

public abstract class FileManager {
    private Helper helper;
    private char[] bitmap;
    
    public FileManager() {
        this.helper = new Helper();
        this.bitmap = new char[256];
    }
    
    protected void initializeFileManager(Disk disk) {
        // Initialize empty file table
        String emptyString = "";
        byte[] emptyStringBytes = this.helper.stringToBytes(emptyString);
        disk.writeToDisk(0, emptyStringBytes);
        
        // Initialize bitmap
        this.bitmap[0] = '1';
        this.bitmap[1] = '1';
        
        for (int i = 2; i < this.bitmap.length; i++) {
            this.bitmap[i] = '0';
        }
        
        String bitmapString = new String(this.bitmap);
        byte[] bitmapBytes = this.helper.stringToBytes(bitmapString);
        disk.writeToDisk(1, bitmapBytes);
    }
    
    // Get the current bitmap in char[] format
    protected char[] getBitmap() {
        return this.bitmap;
    }
    
    // Update bitmap and write it to the disk
    protected void updateBitmap(Disk disk, char[] newBitmap) {
        this.bitmap = newBitmap;
        String bitmapString = new String(this.bitmap);
        byte[] bitmapBytes = this.helper.stringToBytes(bitmapString);
        disk.writeToDisk(1, bitmapBytes);
    }
    
    // Get the file table
    public String getFileTable(Disk disk) {
        byte[] fileTableBytes = this.processBlock(disk.retrieveFromDisk(0));
        String currentFileTable = new String(fileTableBytes);
        return currentFileTable;
    }
    
    // Print a block
    public void printBlock(Disk disk, int blockNumber) {
        byte[] block = this.processBlock(disk.retrieveFromDisk(blockNumber));
        System.out.println(this.helper.bytesToString(block));
    }
    
    // Reset a block
    protected void resetBlock(Disk disk, int blockNumber) {
        byte[] resetBlock = new byte[512];
        disk.writeToDisk(blockNumber, resetBlock);
    }
    
    // Write to a file in the real system
    protected void writeToFile(String fileName, String content) {
        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(content);
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Find the number of blocks a file will occupy
    protected int getBlocksNeeded(File file) {
        int fileLength = (int) this.helper.getFileSizeBytes(file);
        int blocksNeeded = (int) Math.ceil(fileLength / 512.0);
        return blocksNeeded;
    }
    
    // Process a block to not include any null characters
    public byte[] processBlock(byte[] block) {
        int processedBlockLength = 0;
        for (int i = 0; i < block.length; i++) {
            if (block[i] != 0) {
                processedBlockLength += 1;
            }
        }
        
        byte[] processedBlock = new byte[processedBlockLength];
        for (int i = 0; i < processedBlockLength; i++) {
            processedBlock[i] = block[i];
        }
        
        return processedBlock;
    }
    
    // Add a file to the simulation
    protected abstract void addFileToSimulation(Disk disk, String fileName, File file);
    
    // Remove a file from the simulation
    protected abstract void removeFileFromSimulation(Disk disk, String fileName);
    
    // Reconstruct a file
    public abstract String reconstructFile(Disk disk, String fileName);
    
}
