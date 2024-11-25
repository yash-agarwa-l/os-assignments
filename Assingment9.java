package OS;
import java.util.*;
public class Assingment9 {
}



class MemoryBlock {
    int id;         // Block identifier
    int size;       // Size of the block
    boolean free;   // Is block free?
    int processId;  // Process ID using this block (-1 if free)

    public MemoryBlock(int id, int size) {
        this.id = id;
        this.size = size;
        this.free = true;
        this.processId = -1;
    }
}

class MemoryAllocator {
    private List<MemoryBlock> memory;
    private int totalMemory;
    private int nextProcessId;

    public MemoryAllocator(int[] initialChunks) {
        memory = new ArrayList<>();
        nextProcessId = 1;

        // Initialize memory with given chunks
        for(int i = 0; i < initialChunks.length; i++) {
            memory.add(new MemoryBlock(i, initialChunks[i]));
            totalMemory += initialChunks[i];
        }
    }

    // First Fit Algorithm
    public boolean firstFit(int requestSize) {
        for(MemoryBlock block : memory) {
            if(block.free && block.size >= requestSize) {
                allocateBlock(block, requestSize);
                return true;
            }
        }
        return false;
    }

    // Best Fit Algorithm
    public boolean bestFit(int requestSize) {
        MemoryBlock bestBlock = null;
        int minDifference = Integer.MAX_VALUE;

        for(MemoryBlock block : memory) {
            if(block.free && block.size >= requestSize) {
                int difference = block.size - requestSize;
                if(difference < minDifference) {
                    minDifference = difference;
                    bestBlock = block;
                }
            }
        }

        if(bestBlock != null) {
            allocateBlock(bestBlock, requestSize);
            return true;
        }
        return false;
    }

    // Worst Fit Algorithm
    public boolean worstFit(int requestSize) {
        MemoryBlock worstBlock = null;
        int maxDifference = -1;

        for(MemoryBlock block : memory) {
            if(block.free && block.size >= requestSize) {
                int difference = block.size - requestSize;
                if(difference > maxDifference) {
                    maxDifference = difference;
                    worstBlock = block;
                }
            }
        }

        if(worstBlock != null) {
            allocateBlock(worstBlock, requestSize);
            return true;
        }
        return false;
    }

    // Helper method to allocate a block
    private void allocateBlock(MemoryBlock block, int requestSize) {
        if(block.size > requestSize) {
            // Split the block
            MemoryBlock newBlock = new MemoryBlock(
                    memory.size(),
                    block.size - requestSize
            );
            block.size = requestSize;
            memory.add(memory.indexOf(block) + 1, newBlock);
        }

        block.free = false;
        block.processId = nextProcessId++;
    }

    // Method to deallocate memory
    public boolean deallocate(int processId) {
        boolean found = false;
        for(int i = 0; i < memory.size(); i++) {
            MemoryBlock block = memory.get(i);
            if(block.processId == processId) {
                block.free = true;
                block.processId = -1;
                found = true;

                // Merge with next block if it's free
                if(i < memory.size() - 1 && memory.get(i + 1).free) {
                    block.size += memory.get(i + 1).size;
                    memory.remove(i + 1);
                }

                // Merge with previous block if it's free
                if(i > 0 && memory.get(i - 1).free) {
                    memory.get(i - 1).size += block.size;
                    memory.remove(i);
                }
                break;
            }
        }
        return found;
    }

    // Display current memory state
    public void displayMemoryState() {
        System.out.println("\nCurrent Memory State:");
        System.out.println("Total Memory: " + totalMemory + " units");
        System.out.println("----------------------------------------");
        System.out.printf("%-8s %-8s %-8s %-8s\n", "Block ID", "Size", "Status", "Process");
        System.out.println("----------------------------------------");

        for(MemoryBlock block : memory) {
            System.out.printf("%-8d %-8d %-8s %-8s\n",
                    block.id,
                    block.size,
                    (block.free ? "Free" : "Allocated"),
                    (block.free ? "-" : "P" + block.processId)
            );
        }
        System.out.println("----------------------------------------");
    }


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Initialize memory with some chunks
        int[] initialChunks = {100, 500, 200, 300, 600};
        MemoryAllocator allocator = new MemoryAllocator(initialChunks);

        while(true) {
            System.out.println("\nMemory Allocation Simulator");
            System.out.println("1. First Fit Allocation");
            System.out.println("2. Best Fit Allocation");
            System.out.println("3. Worst Fit Allocation");
            System.out.println("4. Deallocate Memory");
            System.out.println("5. Display Memory State");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();

            switch(choice) {
                case 1:
                case 2:
                case 3:
                    System.out.print("Enter size to allocate: ");
                    int size = scanner.nextInt();
                    boolean allocated = false;

                    switch(choice) {
                        case 1:
                            allocated = allocator.firstFit(size);
                            System.out.println("\nUsing First Fit Strategy:");
                            break;
                        case 2:
                            allocated = allocator.bestFit(size);
                            System.out.println("\nUsing Best Fit Strategy:");
                            break;
                        case 3:
                            allocated = allocator.worstFit(size);
                            System.out.println("\nUsing Worst Fit Strategy:");
                            break;
                    }

                    if(allocated) {
                        System.out.println("Memory allocated successfully!");
                    } else {
                        System.out.println("Unable to allocate memory!");
                    }
                    allocator.displayMemoryState();
                    break;

                case 4:
                    System.out.print("Enter process ID to deallocate: ");
                    int processId = scanner.nextInt();
                    if(allocator.deallocate(processId)) {
                        System.out.println("Memory deallocated successfully!");
                    } else {
                        System.out.println("Process ID not found!");
                    }
                    allocator.displayMemoryState();
                    break;

                case 5:
                    allocator.displayMemoryState();
                    break;

                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
}
