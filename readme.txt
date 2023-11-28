How to run:

You run this project through the Main class using the following commands:
To compile: javac Main.java
To run: java Main <allocation type>

<allocation type> options:
1 for Contiguous allocation --> java Main 1
2 for Chained allocation --> java Main 2
3 for Indexed alloaction --> java Main 3

Commands/Choices:
 1 - Display a file
 2 - Display the file table
 3 - Display the free space bitmap
 4 - Display a disk block
 5 - Copy a file from the simulation to a file on the real system
 6 - Copy a file from the real system to a file in the simulation
 7 - Delete a file
 8 - Exit

Make sure to not enter any spaces after entering a command/file name

When entering file name for a file in the real system, just enter the name of the file, not the path
Example:
    Enter Choice:
    6
    Copy from:
    abc.txt --> just the file name, not the path
    Copy to:
    abc

The program does not recognize any real system files outside of the directory it is running in

When copying a file from simulation to the real system, there can be a delay (usually file contents are copied once you have exited the simulation)

Please DO NOT use duplicate file names for files being stored in the simulation

I recommend file content to only include ASCII characters

File tables are structured in the same way as in the slides
    Contiguous & Chained File Table Structure:
    <file name>     <start block>     <length>

    Indexed File Table Structure:
    <file name>     <index block>

Please ignore the following files within the project, I used them to test my program
    abc.txt
    abc2.txt
    abc3.txt
    text.txt
