/*CPCS361 project part 1(Memory Management-Main Memory).. Group 19
Khlood Alsofyani
Compiler name: NetBeans IDE 8.2
Operating System: Windows 11
-----------------------------------------------------------*/
import java.awt.Desktop;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        int MemorySize;
        System.out.print("./allocator ");
        // input memory size ..
        Scanner scanner = new Scanner(System.in);
        MemorySize = scanner.nextInt(); // Input memory size by user ..
        MemoryManagement MS = new MemoryManagement(MemorySize); // object form MemorySimulator, to design & simulate memory..

     MS.Run(); // run class ..

    }

}
