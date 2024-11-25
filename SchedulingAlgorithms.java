//package OS;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Scanner;
//
//public class SchedulingAlgorithms {
//
//    public static void calculateFCFS(List<Process> processes) {
//        int currentTime = 0;
//        for (Process p : processes) {
//            if (currentTime < p.arrivalTime) {
//                currentTime = p.arrivalTime; // Idle time
//            }
//            p.waitingTime = currentTime - p.arrivalTime;
//            currentTime += p.burstTime;
//            p.turnAroundTime = p.waitingTime + p.burstTime;
//        }
//    }
//
//    public static void calculateSJFNonPreemptive(List<Process> processes) {
//        int n = processes.size();
//        int currentTime = 0, completed = 0;
//        boolean[] visited = new boolean[n];
//
//        while (completed < n) {
//            int shortest = -1;
//            for (int i = 0; i < n; i++) {
//                if (!visited[i] && processes.get(i).arrivalTime <= currentTime) {
//                    if (shortest == -1 || processes.get(i).burstTime < processes.get(shortest).burstTime) {
//                        shortest = i;
//                    }
//                }
//            }
//            if (shortest != -1) {
//                visited[shortest] = true;
//                Process p = processes.get(shortest);
//                p.waitingTime = currentTime - p.arrivalTime;
//                currentTime += p.burstTime;
//                p.turnAroundTime = p.waitingTime + p.burstTime;
//                completed++;
//            } else {
//                currentTime++; // Idle time
//            }
//        }
//    }
//
//    public static void calculateSJFPreemptive(List<Process> processes) {
//        int n = processes.size();
//        int currentTime = 0, completed = 0;
//
//        while (completed < n) {
//            int shortest = -1;
//            int minRemaining = Integer.MAX_VALUE;
//
//            for (int i = 0; i < n; i++) {
//                Process p = processes.get(i);
//                if (p.arrivalTime <= currentTime && p.remainingTime > 0 && p.remainingTime < minRemaining) {
//                    shortest = i;
//                    minRemaining = p.remainingTime;
//                }
//            }
//            if (shortest != -1) {
//                Process p = processes.get(shortest);
//                p.remainingTime--;
//                currentTime++;
//                if (p.remainingTime == 0) {
//                    p.turnAroundTime = currentTime - p.arrivalTime;
//                    p.waitingTime = p.turnAroundTime - p.burstTime;
//                    completed++;
//                }
//            } else {
//                currentTime++; // Idle time
//            }
//        }
//    }
//
//    public static void displayResults(List<Process> processes, String algorithm) {
//        double totalWaitingTime = 0, totalTurnAroundTime = 0;
//
//        System.out.println("\nResults for " + algorithm + " Scheduling:");
//        System.out.println("Process\tArrival\tBurst\tWaiting\tTurnAround");
//        for (Process p : processes) {
//            System.out.printf("P%d\t%d\t%d\t%d\t%d\n", p.id, p.arrivalTime, p.burstTime, p.waitingTime, p.turnAroundTime);
//            totalWaitingTime += p.waitingTime;
//            totalTurnAroundTime += p.turnAroundTime;
//        }
//
//        System.out.printf("\nAverage Waiting Time: %.2f\n", totalWaitingTime / processes.size());
//        System.out.printf("Average Turn Around Time: %.2f\n", totalTurnAroundTime / processes.size());
//    }
//
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//
//        System.out.print("Enter the number of processes: ");
//        int n = sc.nextInt();
//        List<Process> processes = new ArrayList<>();
//
//        System.out.println("Enter Arrival Time and Burst Time for each process:");
//        for (int i = 0; i < n; i++) {
//            System.out.print("P" + (i + 1) + ": ");
//            int arrivalTime = sc.nextInt();
//            int burstTime = sc.nextInt();
//            processes.add(new Process(i + 1, arrivalTime, burstTime));
//        }
//
//        System.out.println("\nChoose Scheduling Algorithm:");
//        System.out.println("1. FCFS");
//        System.out.println("2. SJF (Non-Preemptive)");
//        System.out.println("3. SJF (Preemptive)");
//        System.out.print("Enter your choice: ");
//        int choice = sc.nextInt();
//
//        switch (choice) {
//            case 1:
//                processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
//                calculateFCFS(processes);
//                displayResults(processes, "FCFS");
//                break;
//
//            case 2:
//                processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
//                calculateSJFNonPreemptive(processes);
//                displayResults(processes, "SJF Non-Preemptive");
//                break;
//
//            case 3:
//                processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
//                calculateSJFPreemptive(processes);
//                displayResults(processes, "SJF Preemptive");
//                break;
//
//            default:
//                System.out.println("Invalid choice!");
//        }
//
//        sc.close();
//    }
//}
