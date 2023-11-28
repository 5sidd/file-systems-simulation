import java.util.Arrays;
import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter the type of allocation method as a command line argument");
        }
        else {
            FileManager fm;

            if (args[0].equals("1")) {
                fm = new Contiguous();
            }
            else if (args[0].equals("2")) {
                fm = new Chained();
            }
            else {
                fm = new Indexed();
            }
            
            Disk disk = new Disk();
            Helper helper = new Helper();
            Scanner scanner = new Scanner(System.in);
            fm.initializeFileManager(disk);
            
            while (true) {
                System.out.println("Enter choice:");
                String choice = scanner.nextLine();
                
                if (choice.equals("1")) {
                    displayFile(scanner, fm, disk);
                }
                else if (choice.equals("2")) {
                    fm.printBlock(disk, 0);
                }
                else if (choice.equals("3")) {
                    fm.printBlock(disk, 1);
                }
                else if (choice.equals("4")) {
                    System.out.println("Select the block number you wish to display");
                    int blockNumber = Integer.valueOf(scanner.nextLine());
                    fm.printBlock(disk, blockNumber);
                }
                else if (choice.equals("5")) {
                    simulationToSystem(scanner, fm, disk);
                }
                else if (choice.equals("6")) {
                    systemToSimulation(scanner, fm, disk);
                }
                else if (choice.equals("7")) {
                    deleteFromSimulation(scanner, fm, disk);
                }
                else if (choice.equals("8")) {
                    System.out.println("Exiting Simulation...");
                    break;
                }
                else {
                    System.out.println("Error: Invalid choice number");
                }
                
                System.out.println("");
            }
        }
    }
    
    public static void displayFile(Scanner scanner, FileManager fm, Disk disk) {
        System.out.println("Enter the name of the file within the simulation you wish to display:");
        String fileName = scanner.nextLine();
        String reconstructedFile = fm.reconstructFile(disk, fileName);
            
        if (reconstructedFile.equals("") == false) {
            System.out.println(reconstructedFile);
        }
    }
    
    public static void simulationToSystem(Scanner scanner, FileManager fm, Disk disk) {
        System.out.println("Which file within the simulation do you wish to copy to the real system?");
        String simulationFile = scanner.nextLine();
        
        String reconstructedFile = fm.reconstructFile(disk, simulationFile);
        
        if (reconstructedFile.equals("") == false) {
            System.out.println("Which file in the real system do you wish to copy the selected file to?");
            String systemFile = scanner.nextLine();
            String systemFilePath = "./" + systemFile;
            
            File f = new File(systemFilePath);
            
            if (f.exists() && !f.isDirectory()) {
                fm.writeToFile(systemFilePath, reconstructedFile);
            }
            else {
                System.out.println("Error: could not find specified file within the real system");
            }
            
        }
    }
    
    public static void systemToSimulation(Scanner scanner, FileManager fm, Disk disk) {
        System.out.println("Copy from:");
        String systemFile = scanner.nextLine();
        String systemFilePath = "./" + systemFile;
        
        File f = new File(systemFilePath);
        
        if (f.exists() && !f.isDirectory()) {
            int blocksNeeded = fm.getBlocksNeeded(f);
            
            if (blocksNeeded > 10) {
                System.out.println("Error: Could not add file to simulation since it is longer than 10 blocks");
            }
            else {
                System.out.println("Copy as:");
                String simulationFile = scanner.nextLine();
                fm.addFileToSimulation(disk, simulationFile, f);
            }
        }
        else {
            System.out.println("Error: could not find specified file within the real system");
        }
    }
    
    public static void deleteFromSimulation(Scanner scanner, FileManager fm, Disk disk) {
        System.out.println("Which file do you wish to delete from the simulation?");
        String simulationFile = scanner.nextLine();
        fm.removeFileFromSimulation(disk, simulationFile);
    }
}
