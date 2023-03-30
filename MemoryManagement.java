/*CPCS361 project part 1(Memory Management-Main Memory).. Group 19
Khlood Alsofyani
Compiler name: NetBeans IDE 8.2
Operating System: Windows 11
------------------------------------------------------------------*/
import java.util.ArrayList;
import java.util.Scanner;

public class MemoryManagement {

    private int MemorySize;
    private ArrayList<Partition> Partitions; //to create memory ..

    public MemoryManagement() {
        this.MemorySize = 0;
        this.Partitions = new ArrayList<>();
    }

    public MemoryManagement(int MemorySize) {
        this.MemorySize = MemorySize;
        this.Partitions = new ArrayList<>();
        this.Partitions.add(new Partition(0, MemorySize, "Unused")); // create memory ..
    }

    public void Run() {
        // print command for the user..
        System.out.println("RQ (request), RL (release), C (compact), STAT (status \n"
                + "report), X (exit).");

        String Choice = "";
        Scanner scanner = new Scanner(System.in);

        while (!Choice.equalsIgnoreCase("X")) {
            System.out.print("allocator>");

            Choice = scanner.next();

            if (Choice.equalsIgnoreCase("RQ")) { // request a region for new process
                String status = scanner.next();
                int size; // region size .. 
                System.out.print(" ");
                size = scanner.nextInt();
                System.out.print(" ");
                String AlgorithmType = scanner.next(); // by which allocation strategy ? .. 

                if (!AllocateNewProccess(status, size, AlgorithmType)) {
                    System.out.println("Allocation failed.. there is no sufficient memory to allocating."); // failed allocation because AllocateNewProccess return false ..
                }

            } else if (Choice.equalsIgnoreCase("RL")) { // release process from memory .. 
                System.out.print("");
                String status = scanner.next();

                if (!ReleaseProcess(status)) {

                    System.err.println("\nRelease Faild"); // failed deallocation because ReleaseProcess return false ..

                }
            }

            if (Choice.equalsIgnoreCase("C")) { // compact the holes in one large holes ..
                CompactUnusedHoles();
            } else if (Choice.equalsIgnoreCase("X")) { // terminate the program ..

                System.exit(0);

            } else if (Choice.equalsIgnoreCase("STAT")) { // printing report for the regions that are allocated and unused ..
                ReportMemoryRegions();
                System.out.println("");
            }
        }

    }
    // METHODS ..........................................................................

    private boolean AllocateNewProccess(String status, int size, String AlgorithmType) {

        Scanner scanner = new Scanner(System.in);
        Partition New = new Partition(size, status); // region for new process with parameter contains partition size and status..

        int index = -1;
        if (AlgorithmType.equalsIgnoreCase("B")) { // Best fit strategy for allocation..

            index = BestFit(New); // if the user choose Best fit ...

        } else if (AlgorithmType.equalsIgnoreCase("W")) { // Worst fit strategy for allocation..

            index = WorstFit(New); // if the user choose Worst fit ...

        } else if (AlgorithmType.equalsIgnoreCase("F")) { // First fit strategy for allocation..

            index = FirstFit(New); // if the user choose First fit ...

        }

        if (index == -1) { // no free partition founded by strategy ..

            return false;

        } else {

            Partition Chosen = this.Partitions.get(index); // get the chosen partition returned from the strategy
            int start = Chosen.getStartAddress();
            New.setStartAddress(start);

            this.Partitions.remove(index); // check if the index released previously by partition ..
            this.Partitions.add(index, New);

            int remainingSize = Chosen.getSize() - size; // sub the new size from memory size .. to find remaining size ..
            if (remainingSize != 0) {
                int startFree = start + size;
                this.Partitions.add(index + 1, new Partition(startFree, remainingSize, "Unused")); // if there is enough memory size for the process 
            }

        }
        return true;
    }

    private int BestFit(Partition New) { // Best fit method .. find the must fit unused region for new proccess ..
        int index = -1, mn = 1000000;
        for (int i = 0; i < Partitions.size(); ++i) {
            if (Partitions.get(i).getStatus().equals("Unused")) {
                if (Partitions.get(i).getSize() >= New.getSize()
                        && Partitions.get(i).getSize() < mn) {
                    mn = Partitions.get(i).getSize();
                    index = i;
                }
            }
        }

        return index;
    }

    private int WorstFit(Partition New) { // Worst fit method .. but the new partitions in the most bigger unused region ..
        int index = -1, mx = 0;
        for (int i = 0; i < Partitions.size(); ++i) {
            if (Partitions.get(i).getStatus().equals("Unused")) {
                if (Partitions.get(i).getSize() >= New.getSize()
                        && Partitions.get(i).getSize() > mx) {
                    mx = Partitions.get(i).getSize();
                    index = i;
                }
            }
        }

        return index;
    }

    private int FirstFit(Partition New) { // First fit method .. find first unused region for new partition if it match new process size or larger..
        for (int i = 0; i < Partitions.size(); ++i) {
            if (Partitions.get(i).getStatus().equals("Unused")) {
                if (Partitions.get(i).getSize() >= New.getSize()) {
                    return i;
                }
            }
        }

        return -1;

    }

    private boolean ReleaseProcess(String status) { // remove process from the memory ..

        int start = 0;

        for (int i = start; i < Partitions.size(); ++i) {

            if (Partitions.get(i).getStatus().equalsIgnoreCase(status)) {

                Partitions.get(i).setStatus("Unused");
                MergeAdjacentHoles(); // compact adjacent holes after releasing ..
                return true;
            }
        }

        return false;

    }

    private void MergeAdjacentHoles() { // used in ReleaseProcess method above to Merge adjacent holes ..

        for (int i = 0; i < this.Partitions.size() - 1; ++i) {

            if (this.Partitions.get(i).getStatus() == "Unused"
                    && this.Partitions.get(i + 1).getStatus() == "Unused") {

                this.Partitions.get(i).increaseSize(this.Partitions.get(i + 1).getSize()); // increase the hole size by next unused hole size 
                this.Partitions.remove(i + 1);                                             //to merge them and become larger size 
                                                                                           //to utilize the memory as much as possible
                i--;
            }

        }

    }

    private void CompactUnusedHoles() {  // compact the holes ..

        int AllfreeSpace = getUnusedSpaceinMemory(); // store free holes to compact them ..
        removeUnusedParitions(); // remove it after stor it ..
        int start = shiftAllocatedPartitions(); // variable to shift allocated processes after storing holes sizes .. 

        this.Partitions.add(new Partition(start, AllfreeSpace, "Unused")); // new large hole unused from prev holes ..

    }

    private int shiftAllocatedPartitions() { // shift processes so that holes become one large holes after processes ..

        int start = 0;

        for (int i = 0; i < this.Partitions.size(); ++i) {  

            if (this.Partitions.get(i).getStartAddress() != start) {
                this.Partitions.get(i).setStartAddress(start);
            }

            start = this.Partitions.get(i).getStartAddress() + this.Partitions.get(i).getSize();

        }
        return start;
    }

    private void removeUnusedParitions() { // remove unused regions after store it in variables to compact them ..

        for (int i = 0; i < this.Partitions.size(); ++i) {

            if (this.Partitions.get(i).getStatus() == "Unused") {

                this.Partitions.remove(i);
                i--;

            }

        }

    }

    private int getUnusedSpaceinMemory() {

        int sum = 0;

        for (int i = 0; i < this.Partitions.size(); ++i) {

            if (this.Partitions.get(i).getStatus() == "Unused") {
                sum += this.Partitions.get(i).getSize(); // store & sum unused sizes here ..
            }

        }

        return sum; // return sum of the sizes of unused patritions ..

    }

    private void ReportMemoryRegions() { // print memory regions ( processes & unused regions ) ..

        System.out.println("\nReport:");

        for (int i = 0; i < this.Partitions.size(); ++i) {

            int start = this.Partitions.get(i).getStartAddress();
            int size = this.Partitions.get(i).getSize();
            String status = this.Partitions.get(i).getStatus();
            if (status.equals("Unused")) { // print processes ..
                System.out.println(
                        "Addres[" + start
                        + ":" + (start + size - 1) + "]"
                        + status
                );
                } else{

                System.out.println( //print unused regions & holes..
                        "Addres[" + start
                        + ":" + (start + size - 1) + "]"
                        + "Process " + status
                );
            }
        }

    }

    private String getChoice() { // print the commands ..

        System.out.println("RQ (request), RL (release), C (compact), STAT (status \n"
                + "report), X (exit).");

        String Choice = "";
        Scanner scanner = new Scanner(System.in);

        while (!Choice.equals(null)) {
            System.out.print("allocator>");

            Choice = scanner.next();
        }

        return Choice;
    }

}
