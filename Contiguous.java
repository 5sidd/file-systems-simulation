import java.io.File;

public class Contiguous extends FileManager {
    private Helper helper;
    
    public Contiguous() {
        super();
        this.helper = new Helper();
    }
    
    // Find a starting block to add a file
    private int findSpace(int blocksNeeded, char[] currentBitmap) {
        
        int contiguousEmptyBlockCounts = 0;
        int startingBlock = -1;
        
        for (int i = 2; i < currentBitmap.length; i++) {
            if (currentBitmap[i] == '0') {
                contiguousEmptyBlockCounts += 1;
                
                if (contiguousEmptyBlockCounts == blocksNeeded) {
                    startingBlock = i - blocksNeeded + 1;
                    break;
                }
            }
            else {
                contiguousEmptyBlockCounts = 0;
            }
        }
        
        return startingBlock;
    }
    
    // Write the file to the disk
    private void writeFileToDisk(Disk disk, int startingBlock, byte[] fileBytes) {
        int currentBlock = startingBlock;
        
        for (int i = 0; i < fileBytes.length; i = i + 512) {
            byte[] block = new byte[512];
                
            for (int j = 0; j < 512; j++) {
                if ((i + j) >= fileBytes.length) {
                    break;
                }
                
                block[j] = fileBytes[i + j];
            }
                
            disk.writeToDisk(currentBlock, block);
            currentBlock += 1;
        }
    }
    
    // Remove a file from the disk
    private void removeFileFromDisk(Disk disk, int startingBlock, int length) {
        int currentBlock = startingBlock;
        
        for (int i = 0; i < length; i++) {
            super.resetBlock(disk, currentBlock);
            currentBlock += 1;
        }
    }
    
    // Find a file
    private String findFile(String fileName, String currentFileTable) {
        String[] parts = currentFileTable.split("\n");
        
        for (int i = 0; i < parts.length; i++) {
            String[] parts2 = parts[i].split("\t");
            if (parts2[0].equals(fileName)) {
                return parts[i];
            }
        }
        
        return "-1";
    }
    
    // Update file table after adding a file
    private void addFileToFileTable(Disk disk, String fileName, int startingBlock, int length) {
        String currentFileTable = super.getFileTable(disk);
        
        String toAppend = fileName + "\t" + Integer.toString(startingBlock) + "\t" + Integer.toString(length) + "\n";
        String newFileTable = currentFileTable.concat(toAppend);
        
        byte[] newFileTableBytes = this.helper.stringToBytes(newFileTable);
        disk.writeToDisk(0, newFileTableBytes);
    }
    
    // Remove file from file table
    private void removeFileFromFileTable(Disk disk, String comparisonString, String currentFileTable) {
        String[] parts = currentFileTable.split("\n");
        String[] newFileTable = new String[parts.length - 1];
        
        int index = 0;
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].equals(comparisonString) == false) {
                newFileTable[index] = parts[i];
                index += 1;
            }
        }
        
        super.resetBlock(disk, 0);
        String newFileTableString = String.join("\n", newFileTable);
        String finalNewFileTableString = newFileTableString.concat("\n");
        byte[] newFileTableBytes = this.helper.stringToBytes(finalNewFileTableString);
        disk.writeToDisk(0, newFileTableBytes);
    }
    
    // Update bitmap after adding a file
    private void updatedBitmapAdd(Disk disk, char[] bitmap, int startingBlock, int length) {
        int currentBlock = startingBlock;
        
        for (int i = 0; i < length; i++) {
            bitmap[currentBlock] = '1';
            currentBlock += 1;
        }
        
        super.updateBitmap(disk, bitmap);
    }
    
    // Update bitmap after removing a file
    private void updatedBitmapRemove(Disk disk, char[] bitmap, int startingBlock, int length) {
        int currentBlock = startingBlock;
        
        for (int i = 0; i < length; i++) {
            bitmap[currentBlock] = '0';
            currentBlock += 1;
        }
        
        super.updateBitmap(disk, bitmap);
    }
    
    @Override
    protected void addFileToSimulation(Disk disk, String fileName, File file) {
        int blocksNeeded = super.getBlocksNeeded(file);
        char[] currentBitmap = super.getBitmap();
        int startingBlock = this.findSpace(blocksNeeded, currentBitmap);
        
        if (startingBlock == -1) {
            System.out.println("There is currently not enough space to contiguously allocate this file.");
        }
        else {
            byte[] fileBytes = this.helper.fileToBytes(file);
            
            if (fileBytes == null) {
                System.out.println("There was an error while adding this file, please try again");
            }
            else {
                this.writeFileToDisk(disk, startingBlock, fileBytes);
                this.addFileToFileTable(disk, fileName, startingBlock, blocksNeeded);
                this.updatedBitmapAdd(disk, currentBitmap, startingBlock, blocksNeeded);
            }
        }
    }
    
    @Override
    protected void removeFileFromSimulation(Disk disk, String fileName) {
        String currentFileTable = super.getFileTable(disk);
        String comparisonString = this.findFile(fileName, currentFileTable);
        
        if (comparisonString.equals("-1")) {
            System.out.println("This file does not exist in the simulation");
        }
        else {
            String[] parts = comparisonString.split("\t");
            
            this.removeFileFromDisk(disk, Integer.valueOf(parts[1]), Integer.valueOf(parts[2]));
            this.removeFileFromFileTable(disk, comparisonString, currentFileTable);
            
            char[] currentBitmap = super.getBitmap();
            this.updatedBitmapRemove(disk, currentBitmap, Integer.valueOf(parts[1]), Integer.valueOf(parts[2]));
        }
    }
    
    @Override
    public String reconstructFile(Disk disk, String fileName) {
        String currentFileTable = super.getFileTable(disk);
        String doesFileExist = this.findFile(fileName, currentFileTable);
        
        if (doesFileExist.equals("-1")) {
            System.out.println("This file does not exist in the simulation.");
        }
        else {
            String[] parts = doesFileExist.split("\t");
            int startingBlock = Integer.valueOf(parts[1]);
            int length = Integer.valueOf(parts[2]);
            
            String[] contents = new String[length];
            
            for (int i = 0; i < length; i++) {
                int blockAddress = startingBlock + i;
                
                if (i == length - 1) {
                    byte[] block = super.processBlock(disk.retrieveFromDisk(blockAddress));
                    contents[i] = this.helper.bytesToString(block);
                }
                else {
                    byte[] block = disk.retrieveFromDisk(blockAddress);
                    contents[i] = this.helper.bytesToString(block);
                }
            }
            
            return String.join("", contents);
        }
        
        return "";
    }
}
