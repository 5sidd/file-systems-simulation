import java.io.File;

public class Chained extends FileManager {
    int[] pointers = new int[256]; // Pointers that indicate which blocks each block points to
    Helper helper;
    
    public Chained() {
        super();
        this.helper = new Helper();
        
        for (int i = 0; i < this.pointers.length; i++) {
            this.pointers[i] = -1;
        }
    }
    
    // Find blocks that can fit file being added
    private int[] findSpace(int blocksNeeded, char[] currentBitmap) {
        int blockCounts = 0;
        int[] openBlocks = new int[blocksNeeded];
        
        for (int i = 2; i < currentBitmap.length; i++) {
            if (currentBitmap[i] == '0') {
                openBlocks[blockCounts] = i;
                blockCounts += 1;
                
                if (blockCounts == blocksNeeded) {
                    break;
                }
            }
        }
        
        if (blockCounts < blocksNeeded) {
            return null;
        }
        
        return openBlocks;
    }
    
    // Write file to disk
    private void writeFileToDisk(Disk disk, int[] targetBlocks, byte[] fileBytes) {
        int currentBlock = 0;
        
        for (int i = 0; i < fileBytes.length; i = i + 512) {
            byte[] block = new byte[512];
            
            for (int j = 0; j < 512; j++) {
                if ((i + j) >= fileBytes.length) {
                    break;
                }
                
                block[j] = fileBytes[i + j];
            }
            
            disk.writeToDisk(targetBlocks[currentBlock], block);
            currentBlock += 1;
        }
    }
    
    // Update bitmap after adding a file
    private void updatedBitmapAdd(Disk disk, char[] bitmap, int[] targetBlocks) {
        for (int i = 0; i < targetBlocks.length; i++) {
            int blockNumber = targetBlocks[i];
            bitmap[blockNumber] = '1';
        }
        
        super.updateBitmap(disk, bitmap);
    }
    
    // Adjust pointers after writing blocks
    private void adjustPointersAdd(int[] targetBlocks) {
        for (int i = 0; i < targetBlocks.length - 1; i++) {
            int blockNumber = targetBlocks[i];
            int pointsTo = targetBlocks[i + 1];
            this.pointers[blockNumber] = pointsTo;
        }
    }
    
    // Update file table after adding a file
    private void addFileToFileTable(Disk disk, String fileName, int startingBlock, int length) {
        String currentFileTable = super.getFileTable(disk);
        
        String toAppend = fileName + "\t" + Integer.toString(startingBlock) + "\t" + Integer.toString(length) + "\n";
        String newFileTable = currentFileTable.concat(toAppend);
        
        byte[] newFileTableBytes = this.helper.stringToBytes(newFileTable);
        disk.writeToDisk(0, newFileTableBytes);
    }
    
    // Remove file from disk
    private void removeFileFromDisk(Disk disk, int[] targetBlocks) {
        for (int i = 0; i < targetBlocks.length; i++) {
            int blockNumber = targetBlocks[i];
            super.resetBlock(disk, blockNumber);
        }
    }
    
    // Update bitmap after removing a file
    private void updatedBitmapRemove(Disk disk, char[] bitmap, int[] targetBlocks) {
        for (int i = 0; i < targetBlocks.length; i++) {
            int blockNumber = targetBlocks[i];
            bitmap[blockNumber] = '0';
        }
        
        super.updateBitmap(disk, bitmap);
    }
    
    // Adjust pointers after removing blocks
    private void adjustPointersRemove(int[] targetBlocks) {
        for (int i = 0; i < targetBlocks.length; i++) {
            int blockNumber = targetBlocks[i];
            this.pointers[blockNumber] = -1;
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
    
    // Update file table after removing a file
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
    
    // Get the target blocks
    private int[] getTargetBlocks(int startingBlock, int length) {
        int[] targetBlocks = new int[length];
        targetBlocks[0] = startingBlock;
        
        int index = 1;
        int curr = startingBlock;
        
        while (this.pointers[curr] != -1) {
            int blockNumber = this.pointers[curr];
            targetBlocks[index] = blockNumber;
            index += 1;
            curr = this.pointers[curr];
        }
        
        return targetBlocks;
    }
    
    @Override
    protected void addFileToSimulation(Disk disk, String fileName, File file) {
        int blocksNeeded = super.getBlocksNeeded(file);
        char[] currentBitmap = super.getBitmap();
        int[] targetBlocks = this.findSpace(blocksNeeded, currentBitmap);
        
        if (targetBlocks == null) {
            System.out.println("There is currently not enough space to allocate this file using chained allocation.");
        }
        else {
            byte[] fileBytes = this.helper.fileToBytes(file);
            
            if (fileBytes == null) {
                System.out.println("There was an error while adding this file, please try again");
            }
            else {
                this.writeFileToDisk(disk, targetBlocks, fileBytes);
                this.addFileToFileTable(disk, fileName, targetBlocks[0], blocksNeeded);
                this.updatedBitmapAdd(disk, currentBitmap, targetBlocks);
                this.adjustPointersAdd(targetBlocks);
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
            int[] targetBlocks = this.getTargetBlocks(Integer.valueOf(parts[1]), Integer.valueOf(parts[2]));
            
            this.removeFileFromDisk(disk, targetBlocks);
            this.removeFileFromFileTable(disk, comparisonString, currentFileTable);
            
            char[] currentBitmap = super.getBitmap();
            this.updatedBitmapRemove(disk, currentBitmap, targetBlocks);
            
            this.adjustPointersRemove(targetBlocks);
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
            int[] targetBlocks = this.getTargetBlocks(Integer.valueOf(parts[1]), Integer.valueOf(parts[2]));
            
            String[] contents = new String[targetBlocks.length];
            
            for (int i = 0; i < contents.length; i++) {
                int blockNumber = targetBlocks[i];
                
                if (i == contents.length - 1) {
                    byte[] block = super.processBlock(disk.retrieveFromDisk(blockNumber));
                    contents[i] = this.helper.bytesToString(block);
                }
                else {
                    byte[] block = disk.retrieveFromDisk(blockNumber);
                    contents[i] = this.helper.bytesToString(block);
                }
            }
            
            return String.join("", contents);
        }
        
        return "";
    }
}
