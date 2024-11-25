import java.util.*;

class Process {
    int id;
    int burstTime;
    int arrivalTime;
    int priority;
    int remainingTime;
    int waitingTime;
    int turnaroundTime;

    public Process(int id, int burstTime, int arrivalTime, int priority) {
        this.id = id;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        this.remainingTime = burstTime;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
    }
}

class CPUScheduler {
    private List<Process> processes;

    public CPUScheduler() {
        processes = new ArrayList<>();
    }

    public void addProcess(Process process) {
        processes.add(process);
    }

    // Non-preemptive Priority Scheduling
    public void priorityNonPreemptive() {
        int currentTime = 0;
        List<Process> completed = new ArrayList<>();
        List<Process> tempProcesses = new ArrayList<>(processes);

        while (!tempProcesses.isEmpty()) {
            List<Process> available = new ArrayList<>();
            for (Process p : tempProcesses) {
                if (p.arrivalTime <= currentTime) {
                    available.add(p);
                }
            }

            if (available.isEmpty()) {
                currentTime++;
                continue;
            }

            // Get process with highest priority (lower number = higher priority)
            Process selected = available.stream()
                    .min(Comparator.comparingInt(p -> p.priority))
                    .get();

            selected.waitingTime = currentTime - selected.arrivalTime;
            currentTime += selected.burstTime;
            selected.turnaroundTime = selected.waitingTime + selected.burstTime;

            completed.add(selected);
            tempProcesses.remove(selected);
        }

        displayResults(completed, "Non-preemptive Priority");
    }

    void nonPrempPriority() {
        int n = processes.size();
        int currentTime = 0, completed = 0;
        boolean[] visited = new boolean[n];

        while (completed < n) {
            int maxPriorityProcess = -1;
            int maxPriority = Integer.MIN_VALUE;

            // Find the process with the highest priority that is ready to execute
            for (int i = 0; i < n; i++) {
                Process p = processes.get(i);
                if (!visited[i] && p.arrivalTime <= currentTime && p.priority > maxPriority) {
                    maxPriorityProcess = i;
                    maxPriority = p.priority;
                }
            }

            if (maxPriorityProcess != -1) {
                Process p = processes.get(maxPriorityProcess);
                visited[maxPriorityProcess] = true; // Mark the selected process as visited
                p.waitingTime = currentTime - p.arrivalTime; // Calculate waiting time
                currentTime += p.burstTime; // Update current time after execution
                p.turnaroundTime = p.waitingTime + p.burstTime; // Calculate turnaround time
                completed++; // Increment the count of completed processes
            } else {
                currentTime++; // Increment time if no process is ready
            }
        }
    }



    // Preemptive Priority Scheduling
    public void priorityPreemptive() {
        int currentTime = 0;
        List<Process> completed = new ArrayList<>();
        List<Process> tempProcesses = new ArrayList<>();

        // Create deep copy of processes
        for (Process p : processes) {
            tempProcesses.add(new Process(p.id, p.burstTime, p.arrivalTime, p.priority));
        }

        while (!tempProcesses.isEmpty()) {
            List<Process> available = new ArrayList<>();
            for (Process p : tempProcesses) {
                if (p.arrivalTime <= currentTime) {
                    available.add(p);
                }
            }

            if (available.isEmpty()) {
                currentTime++;
                continue;
            }

            // Get process with highest priority
            Process selected = available.stream()
                    .min(Comparator.comparingInt(p -> p.priority))
                    .get();

            selected.remainingTime--;
            currentTime++;

            for (Process p : tempProcesses) {
                if (p != selected && p.arrivalTime <= currentTime) {
                    p.waitingTime++;
                }
            }

            if (selected.remainingTime == 0) {
                selected.turnaroundTime = currentTime - selected.arrivalTime;
                completed.add(selected);
                tempProcesses.remove(selected);
            }
        }

        displayResults(completed, "Preemptive Priority");
    }

    public static void preemptivePriorityScheduling(List<Process> processes) {
        int n = processes.size();
        int currentTime = 0;
        int completed = 0;

        // Sort processes by arrival time initially
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        // Keep track of completed processes
        boolean[] completedProcesses = new boolean[n];

        while (completed < n) {
            // Find the process with the highest priority that is ready
            int highestPriorityProcess = -1;
            int maxPriority = Integer.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                Process p = processes.get(i);
                if (!completedProcesses[i] && p.arrivalTime <= currentTime && p.remainingTime > 0 && p.priority < maxPriority) {
                    highestPriorityProcess = i;
                    maxPriority = p.priority;
                }
            }

            if (highestPriorityProcess != -1) {
                // Execute the process for one unit of time
                Process p = processes.get(highestPriorityProcess);
                p.remainingTime--;

                // Update current time
                currentTime++;

                // If the process is completed
                if (p.remainingTime == 0) {
                    completed++;
                    completedProcesses[highestPriorityProcess] = true;
                    p.turnaroundTime = currentTime - p.arrivalTime;
                    p.waitingTime = p.turnaroundTime - p.burstTime;
                }
            } else {
                // No process is ready, increment time
                currentTime++;
            }
        }
    }

    public static void roundRobin(List<Process> processes, int timeQuantum) {
        int currentTime = 0;
        int completed = 0;
        int n = processes.size();
        Queue<Process> readyQueue = new LinkedList<>();

        // Sort processes by arrival time initially
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        // Add the first process(es) to the ready queue
        int processIndex = 0;
        while (processIndex < n && processes.get(processIndex).arrivalTime <= currentTime) {
            readyQueue.add(processes.get(processIndex));
            processIndex++;
        }

        while (completed < n) {
            if (!readyQueue.isEmpty()) {
                Process currentProcess = readyQueue.poll();

                // Execute the process for the time quantum or until completion
                int executionTime = Math.min(currentProcess.remainingTime, timeQuantum);
                currentProcess.remainingTime -= executionTime;
                currentTime += executionTime;

                // Add newly arrived processes to the queue during execution
                while (processIndex < n && processes.get(processIndex).arrivalTime <= currentTime) {
                    readyQueue.add(processes.get(processIndex));
                    processIndex++;
                }

                if (currentProcess.remainingTime > 0) {
                    // Re-add the process to the end of the queue if not completed
                    readyQueue.add(currentProcess);
                } else {
                    // Process is completed
                    completed++;
                    currentProcess.turnaroundTime = currentTime - currentProcess.arrivalTime;
                    currentProcess.waitingTime = currentProcess.turnaroundTime - currentProcess.burstTime;
                }
            } else {
                // No process is ready; increment time to the next arrival
                currentTime++;
                while (processIndex < n && processes.get(processIndex).arrivalTime <= currentTime) {
                    readyQueue.add(processes.get(processIndex));
                    processIndex++;
                }
            }
        }
    }

    // Round Robin Scheduling
    public void roundRobin(int timeQuantum) {
        int currentTime = 0;
        Queue<Process> readyQueue = new LinkedList<>();
        List<Process> completed = new ArrayList<>();
        List<Process> tempProcesses = new ArrayList<>();

        // Create deep copy of processes
        for (Process p : processes) {
            tempProcesses.add(new Process(p.id, p.burstTime, p.arrivalTime, p.priority));
        }

        while (!tempProcesses.isEmpty() || !readyQueue.isEmpty()) {
            // Add newly arrived processes to ready queue
            Iterator<Process> iterator = tempProcesses.iterator();
            while (iterator.hasNext()) {
                Process p = iterator.next();
                if (p.arrivalTime <= currentTime) {
                    readyQueue.add(p);
                    iterator.remove();
                }
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process current = readyQueue.poll();
            int executeTime = Math.min(timeQuantum, current.remainingTime);
            current.remainingTime -= executeTime;
            currentTime += executeTime;

            // Update waiting time for other processes
            for (Process p : readyQueue) {
                p.waitingTime += executeTime;
            }

            // Add newly arrived processes during this time quantum
            iterator = tempProcesses.iterator();
            while (iterator.hasNext()) {
                Process p = iterator.next();
                if (p.arrivalTime <= currentTime) {
                    readyQueue.add(p);
                    iterator.remove();
                }
            }

            if (current.remainingTime > 0) {
                readyQueue.add(current);
            } else {
                current.turnaroundTime = currentTime - current.arrivalTime;
                completed.add(current);
            }
        }

        displayResults(completed, "Round Robin (Time Quantum: " + timeQuantum + ")");
    }

    private void displayResults(List<Process> completed, String algorithm) {
        System.out.println("\nResults for " + algorithm + " Scheduling:");
        System.out.println("Process\tWaiting Time\tTurnaround Time");

        double totalWaitingTime = 0;
        double totalTurnaroundTime = 0;

        for (Process p : completed) {
            System.out.println("P" + p.id + "\t" + p.waitingTime + "\t\t" + p.turnaroundTime);
            totalWaitingTime += p.waitingTime;
            totalTurnaroundTime += p.turnaroundTime;
        }

        double avgWaitingTime = totalWaitingTime / completed.size();
        double avgTurnaroundTime = totalTurnaroundTime / completed.size();

        System.out.println("\nAverage Waiting Time: " + String.format("%.2f", avgWaitingTime));
        System.out.println("Average Turnaround Time: " + String.format("%.2f", avgTurnaroundTime));
    }
}

class Main2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        CPUScheduler scheduler = new CPUScheduler();

        System.out.print("Enter number of processes: ");
        int n = scanner.nextInt();

        for (int i = 0; i < n; i++) {
            System.out.println("\nProcess " + (i + 1) + ":");
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Priority (lower number = higher priority): ");
            int priority = scanner.nextInt();

            scheduler.addProcess(new Process(i + 1, burstTime, arrivalTime, priority));
        }

        while (true) {
            System.out.println("\nSelect Scheduling Algorithm:");
            System.out.println("1. Non-preemptive Priority");
            System.out.println("2. Preemptive Priority");
            System.out.println("3. Round Robin");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    scheduler.priorityNonPreemptive();
                    break;
                case 2:
                    scheduler.priorityPreemptive();
                    break;
                case 3:
                    System.out.print("Enter time quantum: ");
                    int timeQuantum = scanner.nextInt();
                    scheduler.roundRobin(timeQuantum);
                    break;
                case 4:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}