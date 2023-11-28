public class Disk {
    private byte[] diskContents;
    
    public Disk() {
        int diskSize = (256 * 512);
        this.diskContents = new byte[diskSize];
    }
    
    // Write a block to the disk
    public void writeToDisk(int blockNumber, byte[] blockContent) {
        int startingIndex = (blockNumber * 512);
        
        for (int i = 0; i < blockContent.length; i++) {
            int diskAddress = startingIndex + i;
            this.diskContents[diskAddress] = blockContent[i];
        }
    }
    
    // Retrieve a block from the disk
    public byte[] retrieveFromDisk(int blockNumber) {
        byte[] retrievedBlock = new byte[512];
        int startingIndex = (blockNumber * 512);
        
        for (int i = 0; i < 512; i++) {
            int diskAddress = startingIndex + i;
            retrievedBlock[i] = this.diskContents[diskAddress];
        }
        
        return retrievedBlock;
    }
}
