/*CPCS361 project part 1(Memory Management-Main Memory).. Group 19
Khlood Alsofyani
Compiler name: NetBeans IDE 8.2
Operating System: Windows 11
------------------------------------------------------*/

public class Partition {
    // Attributes needed for creating, deleting, and all memory allocation proccesses ..
    private int StartAddress;
    private int Size;
    private String Status;
    
    
 // constructors for creating objects ....
    public Partition() {
        this.StartAddress = -1;
        this.Status = "";
        this.Size = 0;
    }

    public Partition(int StartAddress, int Size, String Status) {
        this.StartAddress = StartAddress;
        this.Status = Status;
        this.Size = Size;
    }

    public Partition(int Size, String Status) {
        this.StartAddress = -1;
        this.Status = Status;
        this.Size = Size;
    }
// Methods .. Set & get
    public void setStartAddress(int StartAddress) { 
        this.StartAddress = StartAddress;
    }

    public void setStatus(String Status) { 
        this.Status = Status;
    }

    public int getStartAddress() { 
        return this.StartAddress;
    }

    public String getStatus() {
        return this.Status;
    }

    public void setSize(int Size) {
        this.Size = Size;
    }

    public int getSize() {
        return this.Size;
    }

    public void increaseSize(int additionalSize) { // used in release partition in MemoryManagement class to merge the holes after releasing ..
        this.Size += additionalSize;
    }

}
