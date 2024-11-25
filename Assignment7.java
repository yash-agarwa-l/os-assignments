package OS;
import java.util.*;
public class Assignment7 {
}



class BankersAlgorithm {
    private int numberOfProcesses;
    private int numberOfResources;
    private int[][] maxDemand;
    private int[][] currentAllocation;
    private int[][] remainingNeed;
    private int[] available;
    private boolean[] finished;

    public BankersAlgorithm(int processes, int resources) {
        this.numberOfProcesses = processes;
        this.numberOfResources = resources;
        this.maxDemand = new int[processes][resources];
        this.currentAllocation = new int[processes][resources];
        this.remainingNeed = new int[processes][resources];
        this.available = new int[resources];
        this.finished = new boolean[processes];
    }

    public void setMaxDemand(int[][] max) {
        for(int i = 0; i < numberOfProcesses; i++) {
            for(int j = 0; j < numberOfResources; j++) {
                maxDemand[i][j] = max[i][j];
            }
        }
    }

    public void setAllocation(int[][] allocation) {
        for(int i = 0; i < numberOfProcesses; i++) {
            for(int j = 0; j < numberOfResources; j++) {
                currentAllocation[i][j] = allocation[i][j];
            }
        }
    }

    public void setAvailable(int[] avail) {
        for(int i = 0; i < numberOfResources; i++) {
            available[i] = avail[i];
        }
    }

    private void calculateNeed() {
        for(int i = 0; i < numberOfProcesses; i++) {
            for(int j = 0; j < numberOfResources; j++) {
                remainingNeed[i][j] = maxDemand[i][j] - currentAllocation[i][j];
            }
        }
    }

    private boolean checkSafety() {
        int[] work = available.clone();
        boolean[] finish = new boolean[numberOfProcesses];
        List<Integer> safeSequence = new ArrayList<>();

        // Initialize finish array
        for(int i = 0; i < numberOfProcesses; i++) {
            finish[i] = false;
        }

        // Find a process that can be allocated resources
        boolean found;
        do {
            found = false;
            for(int i = 0; i < numberOfProcesses; i++) {
                if(!finish[i]) {
                    boolean canAllocate = true;
                    // Check if all resources can be allocated
                    for(int j = 0; j < numberOfResources; j++) {
                        if(remainingNeed[i][j] > work[j]) {
                            canAllocate = false;
                            break;
                        }
                    }

                    if(canAllocate) {
                        // Add resources back to work
                        for(int j = 0; j < numberOfResources; j++) {
                            work[j] += currentAllocation[i][j];
                        }
                        finish[i] = true;
                        found = true;
                        safeSequence.add(i);
                    }
                }
            }
        } while(found);

        // Check if all processes are finished
        boolean safe = true;
        for(int i = 0; i < numberOfProcesses; i++) {
            if(!finish[i]) {
                safe = false;
                break;
            }
        }

        if(safe) {
            System.out.println("System is in safe state!");
            System.out.print("Safe sequence is: ");
            for(int i = 0; i < safeSequence.size(); i++) {
                System.out.print("P" + safeSequence.get(i));
                if(i < safeSequence.size() - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.println();
        } else {
            System.out.println("System is NOT in safe state!");
        }

        return safe;
    }

    public boolean requestResources(int processId, int[] request) {
        // Check if request is valid
        for(int i = 0; i < numberOfResources; i++) {
            if(request[i] > remainingNeed[processId][i]) {
                System.out.println("Error: Process has exceeded its maximum claim!");
                return false;
            }
            if(request[i] > available[i]) {
                System.out.println("Error: Resources are not available!");
                return false;
            }
        }

        // Try to allocate resources
        for(int i = 0; i < numberOfResources; i++) {
            available[i] -= request[i];
            currentAllocation[processId][i] += request[i];
            remainingNeed[processId][i] -= request[i];
        }

        // Check if system is safe after allocation
        if(checkSafety()) {
            return true;
        } else {
            // If not safe, rollback changes
            for(int i = 0; i < numberOfResources; i++) {
                available[i] += request[i];
                currentAllocation[processId][i] -= request[i];
                remainingNeed[processId][i] += request[i];
            }
            return false;
        }
    }

    public void displayState() {
        calculateNeed();

        System.out.println("\nCurrent System State:");
        System.out.println("\nAvailable Resources:");
        for(int i = 0; i < numberOfResources; i++) {
            System.out.print("R" + i + ": " + available[i] + " ");
        }

        System.out.println("\n\nMaximum Demand:");
        for(int i = 0; i < numberOfProcesses; i++) {
            System.out.print("P" + i + ": ");
            for(int j = 0; j < numberOfResources; j++) {
                System.out.print(maxDemand[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("\nCurrent Allocation:");
        for(int i = 0; i < numberOfProcesses; i++) {
            System.out.print("P" + i + ": ");
            for(int j = 0; j < numberOfResources; j++) {
                System.out.print(currentAllocation[i][j] + " ");
            }
            System.out.println();
        }

        System.out.println("\nRemaining Need:");
        for(int i = 0; i < numberOfProcesses; i++) {
            System.out.print("P" + i + ": ");
            for(int j = 0; j < numberOfResources; j++) {
                System.out.print(remainingNeed[i][j] + " ");
            }
            System.out.println();
        }

        checkSafety();
    }
}

class Main {
    public static void main(String[] args) {
        // Create a system with 5 processes and 4 resources
        BankersAlgorithm banker = new BankersAlgorithm(5, 4);

        // Example 1: Safe State
        System.out.println("EXAMPLE 1: SAFE STATE");

        // Set available resources
        int[] available = {3, 3, 2, 1};
        banker.setAvailable(available);

        // Set maximum demand for each process
        int[][] maxDemand = {
                {7, 5, 3, 2},  // P0
                {3, 2, 2, 2},  // P1
                {9, 0, 2, 2},  // P2
                {2, 2, 2, 2},  // P3
                {4, 3, 3, 3}   // P4
        };
        banker.setMaxDemand(maxDemand);

        // Set current allocation
        int[][] allocation = {
                {0, 1, 0, 0},  // P0
                {2, 0, 0, 1},  // P1
                {3, 0, 2, 0},  // P2
                {2, 1, 1, 0},  // P3
                {0, 0, 2, 2}   // P4
        };
        banker.setAllocation(allocation);

        // Display current state and check if safe
        banker.displayState();

        // Try resource request (should be successful)
        System.out.println("\nTrying resource request for P1...");
        int[] request1 = {1, 0, 2, 0};
        boolean result = banker.requestResources(1, request1);
        System.out.println("Request " + (result ? "GRANTED" : "DENIED"));

        // Example 2: Unsafe State
        System.out.println("\n\nEXAMPLE 2: UNSAFE STATE");

        // Create new banker's algorithm instance
        banker = new BankersAlgorithm(5, 4);

        // Set available resources (less available resources)
        int[] available2 = {1, 1, 1, 1};
        banker.setAvailable(available2);

        // Set maximum demand (higher demands)
        int[][] maxDemand2 = {
                {7, 5, 3, 2},  // P0
                {3, 2, 2, 2},  // P1
                {9, 0, 2, 2},  // P2
                {2, 2, 2, 2},  // P3
                {4, 3, 3, 3}   // P4
        };
        banker.setMaxDemand(maxDemand2);

        // Set current allocation (more resources allocated)
        int[][] allocation2 = {
                {0, 1, 0, 0},  // P0
                {2, 0, 0, 1},  // P1
                {3, 0, 2, 0},  // P2
                {2, 1, 1, 0},  // P3
                {0, 0, 2, 2}   // P4
        };
        banker.setAllocation(allocation2);

        // Display current state and check if safe
        banker.displayState();

        // Try resource request (should be denied)
        System.out.println("\nTrying resource request for P1...");
        int[] request2 = {1, 1, 0, 0};
        result = banker.requestResources(1, request2);
        System.out.println("Request " + (result ? "GRANTED" : "DENIED"));
    }
}
